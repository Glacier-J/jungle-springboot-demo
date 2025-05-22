package com.jungle.langchain.service.assistant;

import com.jungle.langchain.enums.Priority;
import com.jungle.langchain.pojo.Person;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "openAiChatModel")
public interface IAssistantSO {

    @UserMessage("Extract information about a person from {{it}}")
    Person extractPersonFrom(String text);

    @UserMessage("Analyze the priority of the following issue: {{it}}")
    Priority analyzePriority(String issueDescription);

    @UserMessage("Does {{it}} has a positive sentiment?")
    boolean isPositive(String text);

}