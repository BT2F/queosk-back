package com.bttf.queosk.service.restaurantService;

import com.bttf.queosk.config.springSecurity.JwtTokenProvider;
import com.bttf.queosk.dto.enumerate.OperationStatus;
import com.bttf.queosk.dto.restaurantDto.RestaurantDto;
import com.bttf.queosk.dto.restaurantDto.RestaurantSignInDto;
import com.bttf.queosk.dto.restaurantDto.RestaurantSignInForm;
import com.bttf.queosk.dto.restaurantDto.RestaurantSignUpForm;
import com.bttf.queosk.entity.RefreshToken;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.mapper.RestaurantDtoMapper;
import com.bttf.queosk.mapper.RestaurantSignInMapper;
import com.bttf.queosk.mapper.userMapper.TokenDtoMapper;
import com.bttf.queosk.model.userModel.UserRole;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.service.imageService.ImageService;
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
                        .userRole(UserRole.ROLE_ADMIN)
                        .build();


        restaurantRepository.save(restaurant);
    }


    public RestaurantSignInDto signIn(RestaurantSignInForm restaurantSignInForm) {
        Restaurant restaurant = restaurantRepository.findByOwnerId(restaurantSignInForm.getOwnerId())
                .orElseThrow(() -> new CustomException(INVALID_USER_ID));

        if (!passwordEncoder.matches(restaurantSignInForm.getPassword(), restaurant.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(
                TokenDtoMapper.INSTANCE.restaurantToTokenDto(restaurant)
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

    public void imageUpload(String token, MultipartFile image) throws IOException {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        Restaurant restaurant = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new CustomException(INVALID_USER_ID));
        restaurant.updateImage(imageService.saveFile(image));
        restaurantRepository.save(restaurant);
    }


    public RestaurantDto getRestaurantInfoFromToken(String token) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));
        return RestaurantDtoMapper.MAPPER.toDto(restaurant);
    }
}
