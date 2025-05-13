package com.jungle.ai.controller;

import groovy.util.logging.Slf4j;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/ai2")
public class Chat2Controller {

    public void chat(){
//        var openAiApi = OpenAiApi.builder()
//                .apiKey(System.getenv("OPENAI_API_KEY"))
//                .build();
//        var openAiChatOptions = OpenAiChatOptions.builder()
//                .model("gpt-3.5-turbo")
//                .temperature(0.4)
//                .maxTokens(200)
//                .build();
//        var chatModel = new OpenAiChatModel(openAiApi, openAiChatOptions);
//
//        ChatResponse response = chatModel.call(
//                new Prompt("Generate the names of 5 famous pirates."));

// Or with streaming responses
//        Flux<ChatResponse> response = chatModel.stream(
//                new Prompt("Generate the names of 5 famous pirates."));


        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();

        OpenAiApi.ChatCompletionMessage chatCompletionMessage =
                new OpenAiApi.ChatCompletionMessage("Hello world", OpenAiApi.ChatCompletionMessage.Role.USER);

// Sync request
//        ResponseEntity<OpenAiApi.ChatCompletion> response = openAiApi.chatCompletionEntity(
//                new OpenAiApi.ChatCompletionRequest(List.of(chatCompletionMessage), "gpt-3.5-turbo", 0.8, false));

// Streaming request
//        Flux<OpenAiApi.ChatCompletionChunk> streamResponse = openAiApi.chatCompletionStream(
//                new OpenAiApi.ChatCompletionRequest(List.of(chatCompletionMessage), "gpt-3.5-turbo", 0.8, true));
    }
}
