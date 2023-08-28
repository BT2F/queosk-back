package com.bttf.queosk.service.kakaoservice;

import com.bttf.queosk.dto.*;
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

    private static String CID = "TC0ONETIME";

    @Value("${kakao.adminKey}")
    private String ADMIN_KEY;

    @Value("${url}")
    private String MAIN_URL;

    public KakaoPaymentReadyDto kakaoPaymentReady(Long userId, KakaoPaymentReadyForm kakaoPaymentReadyForm) {

        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        String randomUuid = UUID.randomUUID().toString().replace("-", "");

        parameter.add("cid", CID);
        parameter.add("partner_order_id", randomUuid);
        parameter.add("partner_user_id", userId.toString());
        parameter.add("item_name", kakaoPaymentReadyForm.getItemName());
        parameter.add("item_code", kakaoPaymentReadyForm.getItemCode());
        parameter.add("quantity", String.valueOf(kakaoPaymentReadyForm.getQuantity()));
        parameter.add("total_amount", String.valueOf(kakaoPaymentReadyForm.getTotalAmount()));
        parameter.add("tax_free_amount", String.valueOf(kakaoPaymentReadyForm.getTaxFreeAmount()));
        parameter.add("vat_amount", String.valueOf(kakaoPaymentReadyForm.getVatAmount()));
        parameter.add("green_defosit", String.valueOf(kakaoPaymentReadyForm.getGreenDeposit()));
        parameter.add("approval_url", MAIN_URL + "/payment/approve");
        parameter.add("cancel_url", MAIN_URL + "/payment/cancel");
        parameter.add("fail_url", MAIN_URL + "/payment/fail");
        parameter.add("install_month", String.valueOf(kakaoPaymentReadyForm.getInstallMonth()));

        String postString = restApiPost(parameter, "https://kapi.kakao.com/v1/payment/ready");

        JsonObject json = JsonParser.parseString(postString).getAsJsonObject();
        KakaoPaymentReadyDto kakaoPaymentReadyDto = KakaoPaymentReadyDto.builder()
                .tid(json.get("tid").getAsString())
                .OrderId(randomUuid)
                .nextRedirectPcUrl(json.get("next_redirect_pc_url").getAsString())
                .nextRedirectMobileUrl(json.get("next_redirect_mobile_url").getAsString())
                .createdAt(LocalDateTime.parse((json.get("created_at").getAsString())))
                .build();

        return kakaoPaymentReadyDto;

    }

    public KakaoPaymentApproveDto kakaoPaymentApprove(Long userId, String pgToken, KakaoPaymentApproveForm kakaoPaymentApproveForm) {
        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        parameter.add("cid", CID);
        parameter.add("tid", kakaoPaymentApproveForm.getTid());
        parameter.add("partner_order_id", kakaoPaymentApproveForm.getPartnerOrderId());
        parameter.add("partner_user_id", userId.toString());
        parameter.add("pg_token", pgToken);
        parameter.add("payload", kakaoPaymentApproveForm.getPayload());
        parameter.add("total_amount", kakaoPaymentApproveForm.getTotalAmount().toString());

        String postString = restApiPost(parameter, "https://kapi.kakao.com/v1/payment/approve");

        JsonObject jsonObject = JsonParser.parseString(postString).getAsJsonObject();
        JsonObject amountJson = jsonObject.get("amount").getAsJsonObject();

        return KakaoPaymentApproveDto.builder()
                .aid(jsonObject.get("aid").getAsString())
                .tid(jsonObject.get("tid").getAsString())
                .cid(jsonObject.get("cid").getAsString())
                .partnerOrderId(jsonObject.get("partner_order_id").getAsString())
                .partnerUserId(jsonObject.get("partner_user_id").getAsString())
                .paymentMethodType(jsonObject.get("payment_method_type").getAsString())
                .itemName(jsonObject.get("item_name").getAsString())
                .itemCode(jsonObject.get("item_code").getAsString())
                .quantity(jsonObject.get("quantity").getAsInt())
                .amount(KakaoAmount.builder()
                        .total(amountJson.get("total").getAsInt())
                        .taxFree(amountJson.get("tax_free").getAsInt())
                        .vat(amountJson.get("vat").getAsInt())
                        .point(amountJson.get("point").getAsInt())
                        .discount(amountJson.get("discount").getAsInt())
                        .greenDeposit(amountJson.get("green_deposit").getAsInt())
                        .build())
                .createdAt(LocalDateTime.parse(jsonObject.get("created_at").getAsString()))
                .approvedAt(LocalDateTime.parse(jsonObject.get("approved_at").getAsString()))
                .payload(jsonObject.get("payload").getAsString())
                .build();
    }

    private String restApiPost(MultiValueMap<String, String> parameter, String url) {
        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<>(parameter, this.getHeaders());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        return restTemplate.postForObject(url, requestEntity, String.class);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        String auth = "KakaoAK " + ADMIN_KEY;
        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return httpHeaders;
    }
}
