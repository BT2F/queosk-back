package com.bttf.queosk.util;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@UtilityClass
public class KakaoGeoAddress {

    public double addressToCoordinate(String address, String xOrY) {
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;
        WebClient webClient = WebClient.create(url);
        String response = webClient.get().uri(url).header("Authorization", "KakaoAK afd1424272e23370e80792c29e7834c7").retrieve().bodyToMono(String.class).block();
        JsonObject jobj = (JsonObject) JsonParser.parseString(response);
        JsonArray doc = (JsonArray) jobj.get("documents");
        JsonObject addressJson = (JsonObject) doc.get(0);
        return addressJson.get(xOrY).getAsDouble();
    }
}
