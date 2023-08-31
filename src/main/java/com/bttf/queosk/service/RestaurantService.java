package com.bttf.queosk.service;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.*;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.RefreshToken;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.enumerate.OperationStatus;
import com.bttf.queosk.enumerate.UserRole;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.mapper.TokenDtoMapper;
import com.bttf.queosk.repository.MenuRepository;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
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
    private final KakaoGeoAddressService kakaoGeoAddressService;
    private final ImageService imageService;
    private final EmailSender emailSender;
    private final MenuRepository menuRepository;

    @Transactional
    public void signUp(RestaurantSignUpForm restaurantSignUpForm) throws Exception {
        if (restaurantRepository.existsByEmail(restaurantSignUpForm.getEmail())) {
            throw new CustomException(EXISTING_USER);
        }

        double x = kakaoGeoAddressService.addressToCoordinate(restaurantSignUpForm.getAddress(), "x");
        double y = kakaoGeoAddressService.addressToCoordinate(restaurantSignUpForm.getAddress(), "y");

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
                        .latitude(y)
                        .longitude(x)
                        .region(kakaoGeoAddressService.coordinateToZone(x, y))
                        .operationStatus(OperationStatus.CLOSED)
                        .isDeleted(false)
                        .userRole(UserRole.ROLE_RESTAURANT)
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
                        .email(restaurant.getEmail())
                        .token(refreshToken)
                        .build()
        );

        return RestaurantSignInDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .imageUrl(restaurant.getImageUrl())
                .build();
    }

    @Transactional
    public void imageUpload(String token, MultipartFile image) throws IOException {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        Restaurant restaurant = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new CustomException(INVALID_USER_ID));

        if (restaurant.getImageUrl() != null) {
            imageService.deleteFile(restaurant.getImageUrl());
        }
        restaurant.updateImage(imageService.saveFile(image, "restaurant/" + UUID.randomUUID().toString().substring(0, 6)));

        restaurantRepository.save(restaurant);
    }


    public RestaurantDto getRestaurantInfoFromToken(String token) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));
        return RestaurantDto.of(restaurant);
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

    @Transactional
    public void deleteRestaurant(String token) {
        Restaurant restaurant = restaurantRepository
                .findById(jwtTokenProvider.getIdFromToken(token))
                .orElseThrow(() -> new CustomException(INVALID_RESTAURANT));
        restaurant.delete();
        restaurantRepository.save(restaurant);

    }

    @Transactional
    public RestaurantDto updateRestaurantInfo(String token, UpdateRestaurantInfoForm updateRestaurantInfoForm) {
        Restaurant restaurant = restaurantRepository
                .findById(jwtTokenProvider.getIdFromToken(token))
                .orElseThrow(() -> new CustomException(INVALID_RESTAURANT));

        restaurant.updateRestaurantInfo(updateRestaurantInfoForm);

        restaurant.setGeoPoint(
                kakaoGeoAddressService.addressToCoordinate(restaurant.getAddress(), "x"),
                kakaoGeoAddressService.addressToCoordinate(restaurant.getAddress(), "y"));

        double x = kakaoGeoAddressService.addressToCoordinate(updateRestaurantInfoForm.getAddress(), "x");
        double y = kakaoGeoAddressService.addressToCoordinate(updateRestaurantInfoForm.getAddress(), "y");

        restaurant.setRegion(kakaoGeoAddressService.coordinateToZone(x, y));

        restaurantRepository.save(restaurant);

        return RestaurantDto.of(restaurant);
    }

    public Page<RestaurantDto> getCoordRestaurantInfoForm(RestaurantInfoGetCoordForm restaurantInfoGetCoordForm) {
        Pageable pageable = PageRequest.of(restaurantInfoGetCoordForm.getPage(), restaurantInfoGetCoordForm.getSize());
        double x = restaurantInfoGetCoordForm.getX();
        double y = restaurantInfoGetCoordForm.getY();

        return restaurantRepository.getRestaurantListByDistance(kakaoGeoAddressService.coordinateToZone(x, y), x, y, pageable).map(RestaurantDto::of);
    }

    public RestaurantInfoMenuGetDto getRestaurantInfoAndMenu(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new CustomException(INVALID_RESTAURANT));
        List<Menu> menu = menuRepository.findByRestaurantId(restaurantId);

        return RestaurantInfoMenuGetDto.of(restaurant, menu);
    }
}
