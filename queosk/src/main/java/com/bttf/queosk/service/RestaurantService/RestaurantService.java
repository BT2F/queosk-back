package com.bttf.queosk.service.RestaurantService;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.enumerate.OperationStatus;
import com.bttf.queosk.dto.restaurantDto.RestaurantSignInDTO;
import com.bttf.queosk.dto.restaurantDto.RestaurantSignInForm;
import com.bttf.queosk.dto.restaurantDto.RestaurantSignUpForm;
import com.bttf.queosk.dto.tokenDto.TokenDto;
import com.bttf.queosk.entity.RefreshToken;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.mapper.RestaurantSignInMapper;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.service.ImageService.ImageService;
import com.bttf.queosk.util.KakaoGeoAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static com.bttf.queosk.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoGeoAddress kakaoGeoAddress;
    private final ImageService imageService;

    @Transactional
    public void signUp(RestaurantSignUpForm restaurantSignUpForm) throws Exception {
        if (restaurantRepository.existsByEmail(restaurantSignUpForm.getEmail())) {
            throw new CustomException(EXISTING_USER);
        }
        Restaurant restaurant =
                Restaurant.builder()
                        .ownerId(restaurantSignUpForm.getOwnerId())
                        .ownerName(restaurantSignUpForm.getOwnerName())
                        .password(passwordEncoder.encode(restaurantSignUpForm.getPassword()))
                        .email(restaurantSignUpForm.getEmail())
                        .restaurantName(restaurantSignUpForm.getRestaurantName())
                        .restaurantPhone(restaurantSignUpForm.getRestaurantPhone())
                        .category(restaurantSignUpForm.getCategory())
                        .businessNumber(restaurantSignUpForm.getBusinessNumber())
                        .businessStartDate(
                                new SimpleDateFormat("yyyyMMdd")
                                        .parse(restaurantSignUpForm.getBusinessStartDate()))
                        .address(restaurantSignUpForm.getAddress())
                        .latitude(kakaoGeoAddress.addressToCoordinate(restaurantSignUpForm.getAddress(), "y"))
                        .longitude(kakaoGeoAddress.addressToCoordinate(restaurantSignUpForm.getAddress(), "x"))
                        .operationStatus(OperationStatus.CLOSED)
                        .isDeleted(false)
                        .build();


        restaurantRepository.save(restaurant);
    }


    public RestaurantSignInDTO signIn(RestaurantSignInForm restaurantSignInForm) {
        Restaurant restaurant = restaurantRepository.findByOwnerId(restaurantSignInForm.getOwnerId())
                .orElseThrow(() -> new CustomException(INVALID_USER_ID));

        if (!passwordEncoder.matches(restaurantSignInForm.getPassword(), restaurant.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(
                TokenDto.builder()
                        .email(restaurant.getEmail())
                        .id(restaurant.getId())
                        .build()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken();

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user_email(restaurant.getEmail())
                        .refresh_token(refreshToken)
                        .build()
        );

        return RestaurantSignInMapper.MAPPER.toDto(restaurant, refreshToken, accessToken);


    }

    public void imageUpload(Long id, MultipartFile image) throws IOException {
        Restaurant restaurant = restaurantRepository
                .findById(id)
                .orElseThrow(() -> new CustomException(INVALID_USER_ID));
        restaurant.updateImage(imageService.saveFile(image));
        restaurantRepository.save(restaurant);
    }

}
