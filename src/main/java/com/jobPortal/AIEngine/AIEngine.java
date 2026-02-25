package com.jobPortal.AIEngine;

import com.jobPortal.Config.AIProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIEngine {

    private final AIProperties properties;
    private final WebClient webClient = WebClient.builder().build();

    public String execute(String systemPrompt, String userPrompt) {

        String url = "https://api.openai.com/v1/chat/completions";

        Map<String, Object> request = Map.of(
                "model", properties.getModel(),
                "temperature", properties.getTemperature(),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        Map response = webClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + properties.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map choice = ((List<Map>) response.get("choices")).get(0);
        Map message = (Map) choice.get("message");
        return (String) message.get("content");
    }
}
