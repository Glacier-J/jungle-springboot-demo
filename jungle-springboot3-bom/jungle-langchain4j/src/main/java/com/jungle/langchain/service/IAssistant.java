package com.jungle.langchain.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface IAssistant {

    @SystemMessage("You are a polite assistant")
    String chat(String userMessage);
}
