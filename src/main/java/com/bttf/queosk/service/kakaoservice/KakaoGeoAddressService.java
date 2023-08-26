package com.bttf.queosk.service.kakaoservice;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoGeoAddressService {

    @Value("${kakao.apiKey}")
    private String apiKey;

    public double addressToCoordinate(String address, String xOrY) {
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;
        WebClient webClient = WebClient.create(url);
        String response = webClient.get().uri(url).header("Authorization", "KakaoAK " + apiKey).retrieve().bodyToMono(String.class).block();
        JsonObject jobj = (JsonObject) JsonParser.parseString(response);
        JsonArray doc = (JsonArray) jobj.get("documents");
        JsonObject addressJson = (JsonObject) doc.get(0);
        return addressJson.get(xOrY).getAsDouble();
    }

    public String coordinateToZone(double x, double y) {
        String url = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=" + x + "&y=" + y;
        WebClient webClient = WebClient.create(url);
        String response = webClient.get().uri(url).header("Authorization", "KakaoAK " + apiKey).retrieve().bodyToMono(String.class).block();
        JsonObject jobj = (JsonObject) JsonParser.parseString(response);
        JsonArray doc = (JsonArray) jobj.get("documents");
        JsonObject docinJobj = (JsonObject) doc.get(1);
        return docinJobj.get("region_3depth_name").toString().replace("\"", "");
    }

}
