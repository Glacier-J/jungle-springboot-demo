package com.jungle.mcp.client.service;

import reactor.core.publisher.Mono;

public interface IChatService {

    /**
     * 聊天
     *
     * @param userInput 用户输入内容
     * @return ai 生成内容
     */
    Mono<String> chat(String userInput);


    public String chat1(String userInput);
}
