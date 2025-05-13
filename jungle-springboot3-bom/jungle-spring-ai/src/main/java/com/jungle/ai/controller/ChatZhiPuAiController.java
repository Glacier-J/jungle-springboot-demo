package com.jungle.ai.controller;

import groovy.util.logging.Slf4j;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * 智普免费模型
 * GLM-4-Flash-250414   语言模型
 * GLM-4V-Flash         图像理解
 * CogView-3-Flash      图像生成
 * CogVideoX-Flash      视频生成
 * GLM-Z1-Flash         推理模型
 */
@Slf4j
@RestController
@RequestMapping("/ai/zhipu")
public class ChatZhiPuAiController {

    @Resource(name = "zhiPuAiChatModel")
    private ChatModel zhiPuAiChatModel;

    /**
     * chat 文本聊天（同步）
     *
     * @param input
     * @return
     */
    @GetMapping("/with/text")
    public ChatResponse chatWithChatModel(@RequestParam("input") String input) {
        //自定义option或使用默认配置

        ZhiPuAiChatOptions zhipuAiChatOptions = ZhiPuAiChatOptions.builder()
                .model(ZhiPuAiApi.ChatModel.GLM_3_Turbo.getValue())
                .temperature(0.8)
                .build();

        return zhiPuAiChatModel.call(new Prompt(input, zhipuAiChatOptions));
    }


    /**
     * chat 文本聊天（同步）
     *
     * @param input
     * @return
     */
    @GetMapping("/with/text/custom")
    public ChatResponse chatWithChatModelCustom(@RequestParam("input") String input,
                                                @RequestParam("modelName") String modelName) {
        //自定义option或使用默认配置
        ZhiPuAiChatOptions zhipuAiChatOptions = ZhiPuAiChatOptions.builder()
                .model(modelName)
                .temperature(0.8)
                .build();

        return zhiPuAiChatModel.call(new Prompt(input, zhipuAiChatOptions));
    }

    /**
     * chat 图片（同步）
     *
     * @param input 对话内容
     * @param modelName 模型名称
     * @return
     */
    @GetMapping("/with/text/image")
    public ChatResponse chatWithChatModelImage(@RequestParam(value = "input", required = false) String input,
                                               @RequestParam(value = "modelName", required = false) String modelName) {

        UserMessage userMessage = UserMessage.builder()
                .text(StringUtils.isNotBlank(input) ? input : "Explain what do you see on this picture?")
                .media(new Media(MimeTypeUtils.IMAGE_PNG,
                        URI.create("https://docs.spring.io/spring-ai/reference/_images/multimodal.test.png")))
                .build();
        ZhiPuAiChatOptions openAiChatOptions = ZhiPuAiChatOptions.builder()
                .model(StringUtils.isNotBlank(modelName) ? modelName : ZhiPuAiApi.ChatModel.GLM_4_Flash.value)
                .build();
        Prompt prompt = new Prompt(userMessage,
                openAiChatOptions);
        return zhiPuAiChatModel.call(prompt);
    }
}
