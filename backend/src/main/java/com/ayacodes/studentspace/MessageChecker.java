package com.ayacodes.studentspace;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageChecker {
    private static final String API_URL = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=";
    public static final String apiKey = System.getenv("PERSPECTIVE_API_KEY");

    public double getToxicityScore(String message) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("comment", Map.of("text", message));
        requestBody.put("languages", List.of("en"));
        requestBody.put("requestedAttributes", Map.of("TOXICITY", new HashMap<>()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                API_URL + apiKey,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map attributeScores = (Map) response.getBody().get("attributeScores");
        Map toxicity = (Map) attributeScores.get("TOXICITY");
        Map summaryScore = (Map) toxicity.get("summaryScore");

        return ((Number) summaryScore.get("value")).doubleValue();
    }
}
