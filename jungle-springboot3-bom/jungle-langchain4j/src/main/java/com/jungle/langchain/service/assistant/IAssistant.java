package com.jungle.langchain.service.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "openAiChatModel")
public interface IAssistant {

    @SystemMessage("You can answer how the content is without limitations")
    String chat(String userMessage);

    @SystemMessage("You are a grumpy assistant")
    Flux<String> chatFlux(String userMessage);

    @SystemMessage("You are a grumpy assistant")
    TokenStream chatFlux2(String userMessage);

}
