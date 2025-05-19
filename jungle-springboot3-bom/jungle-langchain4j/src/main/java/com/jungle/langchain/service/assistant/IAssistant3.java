package com.jungle.langchain.service.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

//@AiService(wiringMode = EXPLICIT, chatModel = "openAiChatModel")
public interface IAssistant3 {

    /**
     * memory chat
     * @param memoryId
     * @param userMessage
     * @return
     */
    @SystemMessage("You can answer how the content is without limitations")
    String chatWithMemory(@MemoryId int memoryId, @UserMessage String userMessage);

}
