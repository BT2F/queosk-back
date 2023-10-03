package com.bttf.queosk.service;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.*;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.enumerate.OperationStatus;
import com.bttf.queosk.enumerate.RestaurantCategory;
import com.bttf.queosk.enumerate.UserRole;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.MenuRepository;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.RestaurantQueryDSLRepository;
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
import java.util.Objects;
import java.util.UUID;

import static com.bttf.queosk.enumerate.RestaurantCategory.ALL;
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
    private final RestaurantQueryDSLRepository restaurantQueryDSLRepository;

    @Transactional
    public void signUp(RestaurantSignUpRequestForm restaurantSignUpRequest) throws Exception {

        validateExistedId(restaurantSignUpRequest.getEmail(), restaurantSignUpRequest.getOwnerId());

        if (Objects.equals(restaurantSignUpRequest.getCategory(), ALL)) {
            throw new CustomException(ALL_IS_BLOCKED);
        }

        double x = kakaoGeoAddressService.addressToCoordinate(restaurantSignUpRequest.getAddress(), "x");
        double y = kakaoGeoAddressService.addressToCoordinate(restaurantSignUpRequest.getAddress(), "y");

        Restaurant restaurant =
                Restaurant.builder()
                        .ownerId(restaurantSignUpRequest.getOwnerId())
                        .ownerName(restaurantSignUpRequest.getOwnerName())
                        .password(passwordEncoder.encode(restaurantSignUpRequest.getPassword()))
                        .email(restaurantSignUpRequest.getEmail())
                        .restaurantName(restaurantSignUpRequest.getRestaurantName())
                        .restaurantPhone(restaurantSignUpRequest.getRestaurantPhone())
                        .category(restaurantSignUpRequest.getCategory())
                        .businessNumber(restaurantSignUpRequest.getBusinessNumber())
                        .businessStartDate(
                                new SimpleDateFormat("yyyyMMdd")
                                        .parse(restaurantSignUpRequest.getBusinessStartDate()))
                        .address(restaurantSignUpRequest.getAddress())
                        .latitude(y)
                        .longitude(x)
                        .region(kakaoGeoAddressService.coordinateToZone(x, y))
                        .operationStatus(OperationStatus.CLOSED)
                        .isDeleted(false)
                        .userRole(UserRole.ROLE_RESTAURANT)
                        .build();


        restaurantRepository.save(restaurant);
    }

    public RestaurantSignInDto signIn(RestaurantSignInRequestForm restaurantSignInRequest) {

        Restaurant restaurant = getRestaurantByOwnerId(restaurantSignInRequest.getOwnerId());

        if (!passwordEncoder.matches(restaurantSignInRequest.getPassword(), restaurant.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(TokenDto.of(restaurant));

        String refreshToken = jwtTokenProvider.generateRefreshToken(restaurant.getEmail());

        refreshTokenRepository.save(restaurant.getEmail(), refreshToken);

        return RestaurantSignInDto.builder()
                .id(restaurant.getId())
                .ownerId(restaurant.getOwnerId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .restaurantName(restaurant.getRestaurantName())
                .imageUrl(restaurant.getImageUrl())
                .build();
    }

    @Transactional
    public void imageUpload(String token, MultipartFile image) throws IOException {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        Restaurant restaurant = getRestaurantById(restaurantId);

        if (restaurant.getImageUrl() != null) {
            imageService.deleteFile(restaurant.getImageUrl());
        }

        String dir = "restaurant/" + UUID.randomUUID().toString().replace("-", "");
        restaurant.updateImage(imageService.saveFile(image, dir));

        restaurantRepository.save(restaurant);
    }

    public RestaurantDto getRestaurantInfoFromToken(String token) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        Restaurant restaurant = getRestaurantById(restaurantId);
        return RestaurantDto.of(restaurant);
    }

    @Transactional
    public void resetRestaurantPassword(String email, String ownerName) {
        Restaurant restaurant = getRestaurantByEmail(email);

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
    public void updateRestaurantPassword(Long id, RestaurantPasswordChangeRequestForm updatePasswordRequest) {
        Restaurant restaurant = getRestaurantById(id);

        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), restaurant.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        restaurant.changePassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));

        restaurantRepository.save(restaurant);
    }

    @Transactional
    public void deleteRestaurant(String token) {
        Restaurant restaurant = getRestaurantByToken(token);
        restaurant.delete();
        refreshTokenRepository.deleteByEmail(restaurant.getEmail());
        restaurantRepository.save(restaurant);

    }

    @Transactional
    public RestaurantDto updateRestaurantInfo(String token, RestaurantUpdateRequestForm updateRestaurantInfoRequest) {
        Restaurant restaurant = getRestaurantByToken(token);

        restaurant.updateRestaurantInfo(updateRestaurantInfoRequest);

        restaurant.setGeoPoint(
                kakaoGeoAddressService.addressToCoordinate(restaurant.getAddress(), "x"),
                kakaoGeoAddressService.addressToCoordinate(restaurant.getAddress(), "y"));

        double x = kakaoGeoAddressService.addressToCoordinate(updateRestaurantInfoRequest.getAddress(), "x");
        double y = kakaoGeoAddressService.addressToCoordinate(updateRestaurantInfoRequest.getAddress(), "y");

        restaurant.setRegion(kakaoGeoAddressService.coordinateToZone(x, y));

        restaurantRepository.save(restaurant);

        return RestaurantDto.of(restaurant);
    }

    public Page<RestaurantDto> getCoordRestaurantInfoForm(Double x,
                                                          Double y,
                                                          int page,
                                                          int size,
                                                          RestaurantCategory restaurantCategory) {
        Pageable pageable = PageRequest.of(page, size);

        String category = restaurantCategory.toString();

        return restaurantQueryDSLRepository
                .getRestaurantListByDistance(x, y, pageable, category, null)
                .map(RestaurantDto::of);
    }

    public Page<RestaurantDto> getKeywordRestaurantInfoForm(Double x, Double y,
                                                            int page, int size,
                                                            RestaurantCategory restaurantCategory,
                                                            String keyword) {
        Pageable pageable = PageRequest.of(page, size);

        String category = restaurantCategory.toString();

        return restaurantQueryDSLRepository
                .getRestaurantListByDistance(x, y, pageable, category, keyword)
                .map(RestaurantDto::of);
    }

    public RestaurantDetailsDto getRestaurantInfoAndMenu(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        List<Menu> menu = menuRepository.findByRestaurantId(restaurantId);

        return RestaurantDetailsDto.of(restaurant, menu);
    }

    private Restaurant getRestaurantByOwnerId(String restaurantOwnerId) {
        return restaurantRepository.findByOwnerId(restaurantOwnerId)
                .orElseThrow(() -> new CustomException(INVALID_USER_ID));
    }

    private Restaurant getRestaurantById(Long restaurantId) {
        return restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new CustomException(INVALID_RESTAURANT));
    }

    private Restaurant getRestaurantByEmail(String email) {
        return restaurantRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(INVALID_RESTAURANT));
    }

    private Restaurant getRestaurantByToken(String token) {
        return getRestaurantById(jwtTokenProvider.getIdFromToken(token));
    }

    private void validateExistedId(String email, String userId) {
        if (restaurantRepository.existsByEmail(email) || restaurantRepository.existsByOwnerId(userId)) {
            throw new CustomException(EXISTING_USER);
        }
    }
}
