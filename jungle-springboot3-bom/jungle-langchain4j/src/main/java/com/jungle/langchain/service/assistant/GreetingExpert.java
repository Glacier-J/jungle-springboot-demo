package com.jungle.langchain.service.assistant;

import dev.langchain4j.service.UserMessage;

public interface GreetingExpert {

    /**
     * 判断是否为问候语
     * @param text 对话输入内容
     * @return
     */
    @UserMessage("Is the following text a greeting? Text: {{it}}")
    boolean isGreeting(String text);
}