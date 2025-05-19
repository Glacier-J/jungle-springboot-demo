package com.jungle.langchain.service.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

@AiService
public interface IAssistant4 {

    @SystemMessage("You are a grumpy assistant")
    Flux<String> chatFlux(@UserMessage String userMessage);

}
