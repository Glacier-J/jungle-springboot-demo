package com.jungle.langchain.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.time.Duration.ofSeconds;

@Configuration
public class ChatConfig {
    //https://github.com/chatanywhere/GPT_API_free?tab=readme-ov-file
    public static final String BASE_URL = "https://api.chatanywhere.tech";
    public static final String API_KEY = "sk-pyWQ3wgQKHsZNc4DF8J0BhCo0ZF8a7sos7jYR7oEnUID0aDR";
    public static final String MODEL_NAME = "gpt-4o";

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl(BASE_URL)
                .modelName(MODEL_NAME)
                .apiKey(API_KEY/*System.getenv("OPENAI_API_KEY")*/)
                .temperature(0.3)
                .timeout(ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

}
