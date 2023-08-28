package com.bttf.queosk.service.kakaoservice;

import com.bttf.queosk.dto.KakaoPaymentReadyDto;
import com.bttf.queosk.dto.KakaoPaymentReadyForm;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class KakaoPaymentService {

    private String cid = "TC0ONETIME";

    @Value("${kakao.adminKey}")
    private String ADMIN_KEY;

    @Value("${url}")
    private String MAIN_URL;

    public KakaoPaymentReadyDto kakaoPaymentReady(Long userId, KakaoPaymentReadyForm kakaoPaymentReadyForm) {

        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        String randomUuid = UUID.randomUUID().toString().replace("-", "");

        parameter.add("cid", cid);
        parameter.add("partner_order_id", randomUuid);
        parameter.add("partner_user_id", userId.toString());
        parameter.add("item_name", kakaoPaymentReadyForm.getItemName());
        parameter.add("item_code", kakaoPaymentReadyForm.getItemCode());
        parameter.add("quantity", String.valueOf(kakaoPaymentReadyForm.getQuantity()));
        parameter.add("total_amount", String.valueOf(kakaoPaymentReadyForm.getTotalAmount()));
        parameter.add("tax_free_amount", String.valueOf(kakaoPaymentReadyForm.getTaxFreeAmount()));
        parameter.add("vat_amount", String.valueOf(kakaoPaymentReadyForm.getVatAmount()));
        parameter.add("green_defosit", String.valueOf(kakaoPaymentReadyForm.getGreenDeposit()));
        parameter.add("approval_url", MAIN_URL + "/approve");
        parameter.add("cancel_url", MAIN_URL + "/cancel");
        parameter.add("fail_url", MAIN_URL + "/fail");
        parameter.add("install_month", String.valueOf(kakaoPaymentReadyForm.getInstallMonth()));

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<>(parameter, this.getHeaders());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        String postString = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/ready",
                requestEntity,
                String.class);
        JsonObject json = JsonParser.parseString(postString).getAsJsonObject();
        KakaoPaymentReadyDto kakaoPaymentReadyDto = KakaoPaymentReadyDto.builder()
                .tid(json.get("tid").toString().replace("\"", ""))
                .nextRedirectPcUrl(json.get("next_redirect_pc_url").toString().replace("\"", ""))
                .nextRedirectMobileUrl(json.get("next_redirect_mobile_url").toString().replace("\"", ""))
                .createdAt(LocalDateTime.parse((json.get("created_at").toString().replace("\"", ""))))
                .build();

        return kakaoPaymentReadyDto;

    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        String auth = "KakaoAK " + ADMIN_KEY;
        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return httpHeaders;
    }
}
