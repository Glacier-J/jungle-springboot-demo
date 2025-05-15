//package com.jungle.mcp.client.spring.ai.demo;
//
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.openai.OpenAiChatOptions;
//import org.springframework.ai.openai.api.OpenAiApi;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RequiredArgsConstructor
//@Slf4j
//@RestController
//@RequestMapping("/mcp")
//public class McpClientDemoController {
//
//    private final ChatModel openAiChatModel;
//
//    /**
//     * chat 文本聊天（同步）
//     *
//     * @param input 输入
//     * @return
//     */
//    @GetMapping("/openai/chat/with/text")
//    public String chatWithChatModelCustom(@RequestParam("input") String input) {
//        //自定义option或使用默认配置
//        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
//                .model(OpenAiApi.ChatModel.GPT_4_O_MINI.getValue())
//                .temperature(0.8)
//                .build();
//
//        Prompt prompt = new Prompt(input, openAiChatOptions);
//
//        return ChatClient.create(openAiChatModel)
//                .prompt(prompt)
//                .call()
//                .content();
//    }
//
//}