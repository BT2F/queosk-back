package com.bttf.queosk.service;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.KakaoLoginForm;
import com.bttf.queosk.dto.TokenDto;
import com.bttf.queosk.dto.UserSignInDto;
import com.bttf.queosk.entity.KakaoAuth;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.KakaoAuthRepository;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.UserRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static com.bttf.queosk.enumerate.KaKaoParams.*;
import static com.bttf.queosk.exception.ErrorCode.KAKAO_LOGIN_FAILED;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoLoginService {
    private static final String KAKAO_AUTH_URI = "https://kauth.kakao.com";
    private static final String KAKAO_API_URI = "https://kapi.kakao.com";

    private final UserRepository userRepository;
    private final KakaoAuthRepository kakaoAuthRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${kakao.clientId}")
    private String KAKAO_CLIENT_ID;
    @Value("${kakao.secretId}")
    private String KAKAO_CLIENT_SECRET;
    @Value("${kakao.redirectUrl}")
    private String KAKAO_REDIRECT_URL;

    public void logoutKakaoUser(String email) {
        KakaoAuth kakaoAuth = kakaoAuthRepository.findById(email);

        if (kakaoAuth != null) {
            sendKakaoLogoutRequest(kakaoAuth);
            kakaoAuthRepository.deleteByEmail(email);
        }
    }

    public UserSignInDto getUserInfoFromKakao(KakaoLoginForm.Request kaKaoLoginRequest) throws CustomException {
        String accessToken = "";
        String refreshToken = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add(GRANT_TYPE, "authorization_code");
            params.add(CLIENT_ID, KAKAO_CLIENT_ID);
            params.add(CLIENT_SECRET, KAKAO_CLIENT_SECRET);
            params.add(CODE, kaKaoLoginRequest.getCode());
            params.add(REDIRECT_URI, KAKAO_REDIRECT_URL);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_AUTH_URI + "/oauth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            String responseBody = response.getBody();

            JsonObject jsonObj = JsonParser.parseString(responseBody).getAsJsonObject();

            accessToken = jsonObj.get("access_token").getAsString();
            refreshToken = jsonObj.get("refresh_token").getAsString();

        } catch (Exception e) {
            log.error("Kakao login failed: " + e.getMessage());
            throw new CustomException(KAKAO_LOGIN_FAILED);
        }

        return getUserInfoWithToken(accessToken, refreshToken);
    }

    //임시 서비스
    public UserSignInDto getUserInfoFromKakaoTest(KakaoLoginForm.Request kaKaoLoginRequest) throws CustomException {
        String accessToken = "";
        String refreshToken = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add(GRANT_TYPE, "authorization_code");
            params.add(CLIENT_ID, KAKAO_CLIENT_ID);
            params.add(CLIENT_SECRET, KAKAO_CLIENT_SECRET);
            params.add(CODE, kaKaoLoginRequest.getCode());
            params.add(REDIRECT_URI, "http://localhost:3000/auth/kakao/callback");

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_AUTH_URI + "/oauth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            String responseBody = response.getBody();

            JsonObject jsonObj = JsonParser.parseString(responseBody).getAsJsonObject();

            accessToken = jsonObj.get("access_token").getAsString();
            refreshToken = jsonObj.get("refresh_token").getAsString();

        } catch (Exception e) {
            log.error("Kakao login failed: " + e.getMessage());
            throw new CustomException(KAKAO_LOGIN_FAILED);
        }

        return getUserInfoWithToken(accessToken, refreshToken);
    }


    private UserSignInDto getUserInfoWithToken(String accessToken, String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_API_URI + "/v2/user/me",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        String responseBody = response.getBody();
        assert responseBody != null;
        JsonObject jsonObj = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonObject account = jsonObj.get("kakao_account").getAsJsonObject();
        JsonObject profile = account.get("profile").getAsJsonObject();

        String kakaoId = jsonObj.get("id").getAsString();
        String email = account.get("email").getAsString();
        String nickName = profile.get("nickname").getAsString();

        // If it's a new user, proceed with registration
        if (!userRepository.findByEmail(email).isPresent()) {
            String password = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 10);

            String encodedPassword = passwordEncoder.encode(password);

            userRepository.save(User.of(email, nickName, encodedPassword));
        }

        kakaoAuthRepository.save(email, KakaoAuth.of(kakaoId, refreshToken, accessToken));

        User user = userRepository.findByEmail(email).get();

        String userAccessToken = jwtTokenProvider.generateAccessToken(TokenDto.of(user));
        String userRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        refreshTokenRepository.save(email, userRefreshToken);

        log.info("Kakao social login successful");

        return UserSignInDto.of(user, userRefreshToken, userAccessToken);
    }

    private void sendKakaoLogoutRequest(KakaoAuth kakaoAuth) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAuth.getAccess());

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_API_URI +
                        "/v1/user/logout?" + "target_id_type=user_id&target_id=" +
                        kakaoAuth.getKakaoId(),
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        JsonObject jsonResponse = JsonParser.parseString(response.getBody()).getAsJsonObject();

        if (response.getStatusCode().equals(HttpStatus.OK) &&
                jsonResponse.get("id").getAsString().equals(kakaoAuth.getKakaoId())) {
            log.info("Kakao logout complete");
        }
    }
}


