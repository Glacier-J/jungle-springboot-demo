/*
 * Copyright 2025-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author brianxiadong
 */
package com.jungle.mcp.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        /*
        其实这是 Spring AI 目前的一个 BUG，Spring AI 提供了两个自动配置类去生成客户端工具处理 MCP 服务中 Tool 的获取，分别是 SseHttpClientTransportAutoConfiguration 和 SseWebFluxTransportAutoConfiguration。这两个自动配置类提供了同步和异步两种方式，本身应该是互斥的，但是 Spring AI 对于互斥的处理上出了问题，导致两个自动配置类都会加载。两个自动配置类加载之后，就会向提供 SSE 服务的 MCP 服务申请 Tool，这样就导致同样的 Tool 被申请了两次，自然就会重复了。解决方案也非常简单，在启动类上排除 SseHttpClientTransportAutoConfiguration 实现就可以了。
         */
        org.springframework.ai.autoconfigure.mcp.client.SseHttpClientTransportAutoConfiguration.class
})
public class McpSSEClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpSSEClientApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner predefinedQuestions(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools,
//                                                 ConfigurableApplicationContext context) {
//
//        return args -> {
//
//            var chatClient = chatClientBuilder
//                    .defaultTools(tools)
//                    .build();
//            String userInput = "北京的天气如何？";
//            System.out.println("\n>>> QUESTION: " + userInput);
//            System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
//
//            context.close();
//        };
//    }
}