package com.jungle.mcp.client.service.impl;

import com.jungle.mcp.client.service.IChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {

    private final ChatClient chatClient;

    @Override
    public Mono<String> chat(String userInput) {
        log.info("\n>>> QUESTION: {}", userInput);
//        ChatClient chatClient = chatClientBuilder.defaultTools(tools).build();
        String content = chatClient.prompt(userInput).call().content();
        log.info("\n>>> ASSISTANT: {}", content);

        return Mono.empty();
    }

    @Override
    public String chat1(String userInput) {
        log.info("\n>>> QUESTION: {}", userInput);
//        ChatClient chatClient = chatClientBuilder.defaultTools(tools).build();
        String content = chatClient.prompt(userInput).call().content();
        log.info("\n>>> ASSISTANT: {}", content);

        return content;
    }
}
