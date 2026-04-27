package com.jobPortal.AIEngine;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class AiEngine {

    private final ChatClient chatClient;

    // ChatClient.Builder ko Spring automatically inject kar dega
    public AiEngine(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String chat(String message) {
        String response =  chatClient.prompt()
                .user(message)
                .call()
                .content();
        System.out.println(response);

        return response;

    }
}
