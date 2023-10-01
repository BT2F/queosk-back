package com.bttf.queosk.service;

import com.bttf.queosk.dto.*;
import com.bttf.queosk.enumerate.TableStatus;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.repository.TableRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
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

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Service
@RequiredArgsConstructor
public class KakaoPaymentService {

    private static final String KAKAOPAY_URL = "https://kapi.kakao.com/v1/payment/";
    private static final String CID = "TC0ONETIME";
    @Value("${kakao.adminKey}")
    private String ADMIN_KEY;
    @Value("${url}")
    private String MAIN_URL;

    private final TableRepository tableRepository;

    public KakaoPaymentReadyDto kakaoPaymentReady(Long userId,
                                                  KakaoPaymentReadyRequestForm kakaoPaymentReadyRequestForm) {

        tableValid(kakaoPaymentReadyRequestForm);

        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        String randomUuid = UUID.randomUUID().toString().replace("-", "");

        parameter.add("cid", CID);
        parameter.add("partner_order_id", randomUuid);
        parameter.add("partner_user_id", userId.toString());
        parameter.add("item_name", kakaoPaymentReadyRequestForm.getItemName());
        parameter.add("item_code", kakaoPaymentReadyRequestForm.getItemCode());
        parameter.add("quantity", String.valueOf(kakaoPaymentReadyRequestForm.getQuantity()));
        parameter.add("total_amount", String.valueOf(kakaoPaymentReadyRequestForm.getTotalAmount()));
        parameter.add("tax_free_amount", String.valueOf(kakaoPaymentReadyRequestForm.getTaxFreeAmount()));
        parameter.add("vat_amount", String.valueOf(kakaoPaymentReadyRequestForm.getVatAmount()));
        parameter.add("green_defosit", String.valueOf(kakaoPaymentReadyRequestForm.getGreenDeposit()));
        parameter.add("approval_url", MAIN_URL + "/payment/approve");
        parameter.add("cancel_url", MAIN_URL + "/payment/cancel");
        parameter.add("fail_url", MAIN_URL + "/payment/fail");
        parameter.add("install_month", String.valueOf(kakaoPaymentReadyRequestForm.getInstallMonth()));

        String postString = restApiPost(parameter, KAKAOPAY_URL + "ready");

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

    private void tableValid(KakaoPaymentReadyRequestForm kakaoPaymentReadyRequestForm) {
        if (!tableRepository.findById(kakaoPaymentReadyRequestForm.getTableId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TABLE))
                .getStatus()
                .equals(TableStatus.OPEN)) {
            throw new CustomException(ErrorCode.TABLE_IS_USING);
        }
    }

    public KakaoPaymentApprovalDto kakaoPaymentApprove(Long userId,
                                                       String pgToken,
                                                       KakaoPaymentApprovalRequestForm kakaoPaymentApprovalRequestForm) {
        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        parameter.add("cid", CID);
        parameter.add("tid", kakaoPaymentApprovalRequestForm.getTid());
        parameter.add("partner_order_id", kakaoPaymentApprovalRequestForm.getPartnerOrderId());
        parameter.add("partner_user_id", userId.toString());
        parameter.add("pg_token", pgToken);
        parameter.add("payload", kakaoPaymentApprovalRequestForm.getPayload());
        parameter.add("total_amount", kakaoPaymentApprovalRequestForm.getTotalAmount().toString());


        String postString = restApiPost(parameter, KAKAOPAY_URL + "approve");

        JsonObject jsonObject = JsonParser.parseString(postString).getAsJsonObject();
        JsonObject amountJson = jsonObject.get("amount").getAsJsonObject();

        return KakaoPaymentApprovalDto.builder()
                .aid(jsonObject.get("aid").getAsString())
                .tid(jsonObject.get("tid").getAsString())
                .cid(jsonObject.get("cid").getAsString())
                .partnerOrderId(jsonObject.get("partner_order_id").getAsString())
                .partnerUserId(jsonObject.get("partner_user_id").getAsString())
                .paymentMethodType(jsonObject.get("payment_method_type").getAsString())
                .itemName(jsonObject.get("item_name").getAsString())
                .itemCode(jsonObject.get("item_code").getAsString())
                .quantity(jsonObject.get("quantity").getAsInt())
                .amount(KakaoAmountDto.builder()
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

    public KakaoPaymentCancelDto kakaoPaymentCancel(Long userId,
                                                    KakaoPaymentCancelRequestForm kakaoPaymentCancelRequestForm) {
        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        parameter.add("cid", CID);
        parameter.add("tid", kakaoPaymentCancelRequestForm.getTid());
        parameter.add("cancel_amount", kakaoPaymentCancelRequestForm.getCancelAmount().toString());
        parameter.add("cancel_tax_free_amount", kakaoPaymentCancelRequestForm.getCancelTaxFreeAmount().toString());

        String postString = restApiPost(parameter, KAKAOPAY_URL + "cancel");

        JsonObject jsonObject = JsonParser.parseString(postString).getAsJsonObject();
        Integer amount = getCanceledAmount(jsonObject, "amount");
        Integer approvedCancelAmount = getCanceledAmount(jsonObject, "approved_cancel_amount");
        Integer totalCanceledAmount = getCanceledAmount(jsonObject, "canceled_amount");
        Integer cancelAvailableAmount = getCanceledAmount(jsonObject, "cancel_available_amount");

        return KakaoPaymentCancelDto.builder()
                .aid(jsonObject.get("aid").getAsString())
                .tid(jsonObject.get("tid").getAsString())
                .cid(CID)
                .status(jsonObject.get("status").getAsString())
                .paymentMethodType(jsonObject.get("payment_method_type").getAsString())
                .item_name(jsonObject.get("item_name").getAsString())
                .item_code(jsonObject.get("item_code").getAsString())
                .quantity(jsonObject.get("quantity").getAsInt())
                .amount(amount)
                .approvedCancelAmount(approvedCancelAmount)
                .totalCancelAmount(totalCanceledAmount)
                .cancelAvailableAmount(cancelAvailableAmount)
                .createdAt(LocalDateTime.parse(jsonObject.get("created_at").getAsString()))
                .approvedAt(LocalDateTime.parse(jsonObject.get("approved_at").getAsString()))
                .canceledAt(LocalDateTime.parse(jsonObject.get("canceled_at").getAsString()))
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
        httpHeaders.set(AUTHORIZATION, auth);
        httpHeaders.set(CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        return httpHeaders;
    }

    private int getCanceledAmount(JsonObject jsonObject, String value) {
        return jsonObject.get(value).getAsJsonObject().get("total").getAsInt();
    }


}
