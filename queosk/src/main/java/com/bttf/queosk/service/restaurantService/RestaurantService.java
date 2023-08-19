package com.bttf.queosk.service.restaurantService;

import com.bttf.queosk.config.emailSender.EmailSender;
import com.bttf.queosk.config.springSecurity.JwtTokenProvider;
import com.bttf.queosk.dto.enumerate.OperationStatus;
import com.bttf.queosk.dto.restaurantDto.*;
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
import java.util.UUID;

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
    private final EmailSender emailSender;

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

    @Transactional
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

    @Transactional
    public void resetRestaurantPassword(String email, String ownerName) {
        Restaurant restaurant = restaurantRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(INVALID_RESTAURANT));
        if (!restaurant.getOwnerName().equals(ownerName)) {
            throw new CustomException(OWNER_NAME_NOT_MATCH);
        }

        String randomPassword = UUID.randomUUID().toString().substring(0, 10);
        String encryptedPassword = passwordEncoder.encode(randomPassword);

        emailSender.sendEmail(
                restaurant.getEmail(),
                "비밀번호 초기화 완료",
                "새로운 비밀번호 : " + randomPassword + "를 입력해 로그인 해주세요."
        );

        restaurant.changePassword(encryptedPassword);

        restaurantRepository.save(restaurant);
    }

    @Transactional
    public void updateRestaurantPassword(Long id, RestaurantUpdatePasswordForm updatePassword) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new CustomException(INVALID_RESTAURANT));

        if (!passwordEncoder.matches(updatePassword.getOldPassword(), restaurant.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        restaurant.changePassword(passwordEncoder.encode(updatePassword.getNewPassword()));

        restaurantRepository.save(restaurant);

    }

    public void deleteRestaurant(String token) {
        Restaurant restaurant = restaurantRepository
                .findById(jwtTokenProvider.getIdFromToken(token))
                .orElseThrow(() -> new CustomException(INVALID_RESTAURANT));
        restaurant.delete();
        restaurantRepository.save(restaurant);
    }
}
