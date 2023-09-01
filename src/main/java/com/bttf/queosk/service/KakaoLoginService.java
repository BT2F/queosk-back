package com.bttf.queosk.service;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.KaKaoLoginForm;
import com.bttf.queosk.dto.TokenDto;
import com.bttf.queosk.dto.UserSignInDto;
import com.bttf.queosk.entity.KakaoAuth;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.KakaoAuthRepository;
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

    private final static String KAKAO_AUTH_URI = "https://kauth.kakao.com";
    private final static String KAKAO_API_URI = "https://kapi.kakao.com";
    private final UserRepository userRepository;
    private final KakaoAuthRepository kakaoAuthRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    @Value("${kakao.clientId}")
    private String KAKAO_CLIENT_ID;
    @Value("${kakao.secretId}")
    private String KAKAO_CLIENT_SECRET;
    @Value("${kakao.redirectUrl}")
    private String KAKAO_REDIRECT_URL;

    public String getKakaoLogin() {
        return KAKAO_AUTH_URI + "/oauth/authorize"
                + "?client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URL
                + "&response_type=code";
    }

    public void getKakaoLogout(String email) {
        KakaoAuth kakaoAuth = kakaoAuthRepository.findById(email).get();

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

        String responseBody = response.getBody();
        assert responseBody != null;
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

        if (response.getStatusCode().equals(HttpStatus.OK) &&
                jsonResponse.get("id").getAsString().equals(kakaoAuth.getKakaoId())) {
            log.info("카카오톡 소셜로그인 로그아웃 완료");
        }

        kakaoAuthRepository.deleteById(email);
    }

    public UserSignInDto getKakaoInfo(KaKaoLoginForm kaKaoLoginForm) {
        String accessToken = "";
        String refreshToken = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add(GRANT_TYPE, "authorization_code");
            params.add(CLIENT_ID, KAKAO_CLIENT_ID);
            params.add(CLIENT_SECRET, KAKAO_CLIENT_SECRET);
            params.add(CODE, kaKaoLoginForm.getCode());
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

            assert responseBody != null;
            JsonObject jsonObj = JsonParser.parseString(responseBody).getAsJsonObject();

            accessToken = jsonObj.get("access_token").getAsString();
            refreshToken = jsonObj.get("refresh_token").getAsString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(KAKAO_LOGIN_FAILED);
        }

        return getUserInfoWithToken(accessToken, refreshToken);
    }

    private UserSignInDto getUserInfoWithToken(String accessToken, String refreshToken) {
        //httpHeader 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        //httpHeader 담기
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_API_URI + "/v2/user/me",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        // response 파싱
        String responseBody = response.getBody();
        assert responseBody != null;
        JsonObject jsonObj = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonObject account = jsonObj.get("kakao_account").getAsJsonObject();
        JsonObject profile = account.get("profile").getAsJsonObject();

        String kakaoId = jsonObj.get("id").getAsString();
        String email = account.get("email").getAsString();
        String nickName = profile.get("nickname").getAsString();

        // 신규회원일 경우 가입 진행
        if (!userRepository.findByEmail(email).isPresent()) {

            // 비밀번호용 10자리 임의 UUID생성
            String password = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 10);

            String encodedPassword = passwordEncoder.encode(password);

            userRepository.save(User.of(email,nickName,encodedPassword));
        }

        // 레디스에 카카오 토큰 저장
        kakaoAuthRepository.save(KakaoAuth.of(email,kakaoId,refreshToken,accessToken));

        User user = userRepository.findByEmail(email).get();

        // Queosk 엑세스 토큰 생성
        String userAccessToken =
                jwtTokenProvider.generateAccessToken(TokenDto.of(user));

        // Queosk 리프레시 토큰 생성
        String userRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        log.info("카카오톡 소셜로그인 성공");

        return UserSignInDto.of(user, userRefreshToken, userAccessToken);
    }
}
