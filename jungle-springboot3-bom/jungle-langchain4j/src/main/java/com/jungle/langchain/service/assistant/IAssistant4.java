package com.jungle.langchain.service.assistant;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.memory.ChatMemoryAccess;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 避免内存泄漏，清除不再需要的对话，对于单个对话的ChatMemory实例，并在对话结束时将其删除。需要配置M
 */
public interface IAssistant4 extends ChatMemoryAccess {

    @SystemMessage("You are a grumpy assistant")
    TokenStream chatTokenStream(String userMessage);

    @SystemMessage("You are a grumpy assistant")
    Result<List<String>> chat(String userMessage);

    @SystemMessage("You are a grumpy assistant")
    Flux<String> chatFlux(String userMessage);

}
