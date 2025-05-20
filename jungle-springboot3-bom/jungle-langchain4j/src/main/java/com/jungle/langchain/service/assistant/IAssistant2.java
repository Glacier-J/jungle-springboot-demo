package com.jungle.langchain.service.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

//@AiService
public interface IAssistant2 {

    @SystemMessage("You are a good friend of mine. Answer using slang.")
    TokenStream chatFlux(String userMessage);

    @SystemMessage("You are a grumpy assistant")
    String chat(String userMessage);
}
