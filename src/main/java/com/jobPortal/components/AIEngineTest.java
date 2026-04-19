package com.jobPortal.components;

import com.jobPortal.AIEngine.AIEngine;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AIEngineTest implements CommandLineRunner {

    private final AIEngine aiEngine;
    public AIEngineTest(AIEngine aiEngine) {
        this.aiEngine = aiEngine;
    }

    @Override
    public void run(String... args) throws Exception {
        String systemPrompt = "You are a helpful AI assistant.";
        String userPrompt = "Hello AI, can you introduce yourself briefly?";

        String response = aiEngine.execute(systemPrompt, userPrompt);
        System.out.println("AI Response: " + response);
    }
}