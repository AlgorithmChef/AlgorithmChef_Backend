package com.webservice.algorithmchef.client;

import com.webservice.algorithmchef.dto.recipe.CookRcpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FoodSafetyApiClient {

    private final RestTemplate restTemplate;

    public FoodSafetyApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${foodsafety.cookrcp.base-url}")
    private String baseUrl;

    @Value("${foodsafety.cookrcp.service-key}")
    private String serviceKey;

    @Value("${foodsafety.cookrcp.svc-no}")
    private String svcNo;

    public int getTotalCount() {
        String url = String.format("%s/%s/%s/json/1/1",
                baseUrl, serviceKey, svcNo);

        CookRcpResponse res = restTemplate.getForObject(url, CookRcpResponse.class);

        if (res == null || res.getCOOKRCP01() == null) return 0;

        try {
            return Integer.parseInt(res.getCOOKRCP01().getTotalCount());
        } catch (Exception e) {
            return 0;
        }
    }

    public List<CookRcpResponse.Item> fetchRange(int start, int end) {
        String url = String.format("%s/%s/%s/json/%d/%d",
                baseUrl, serviceKey, svcNo, start, end);

        CookRcpResponse res = restTemplate.getForObject(url, CookRcpResponse.class);

        return Optional.ofNullable(res)
                .map(CookRcpResponse::getCOOKRCP01)
                .map(CookRcpResponse.Body::getRow)
                .orElse(List.of());
    }

    public List<CookRcpResponse.Item> fetchAll() {
        int total = getTotalCount();
        if (total <= 0) return List.of();

        int pageSize = 200;
        List<CookRcpResponse.Item> all = new ArrayList<>(total);

        for (int start = 1; start <= total; start += pageSize) {
            int end = Math.min(start + pageSize - 1, total);
            all.addAll(fetchRange(start, end));
        }

        return all;
    }
}
