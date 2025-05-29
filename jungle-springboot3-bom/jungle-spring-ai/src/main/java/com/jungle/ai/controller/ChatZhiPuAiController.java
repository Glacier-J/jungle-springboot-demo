package com.jungle.ai.controller;

import com.jungle.ai.dto.WeatherRequest;
import com.jungle.ai.dto.WeatherResponse;
import com.jungle.ai.enums.Unit;
import com.jungle.ai.tools.DateTimeTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 智普AI
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

    private final ChatModel zhiPuAiChatModel;

    private final ToolCallbackProvider tools;

    private final ChatClient chatClient;


    public ChatZhiPuAiController(@Qualifier("zhiPuAiChatModel") ChatModel zhiPuAiChatModel, ToolCallbackProvider tools) {
        this.tools = tools;
        this.zhiPuAiChatModel = zhiPuAiChatModel;
        this.chatClient = ChatClient.builder(zhiPuAiChatModel)
                .defaultToolCallbacks(tools)
                .build();
    }

    @Value("classpath:/prompts/system-message.md")
    private org.springframework.core.io.Resource systemResource;

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
     * chat 文本聊天（同步）prompt来自文件
     *
     * @param input
     * @return
     */
    @GetMapping("/with/text/by/resource")
    public ChatResponse chatWithResource(@RequestParam("input") String input) {
        //自定义option或使用默认配置

        ZhiPuAiChatOptions zhipuAiChatOptions = ZhiPuAiChatOptions.builder()
                .model(ZhiPuAiApi.ChatModel.GLM_4_Flash.getValue())
                .temperature(0.8)
                .build();
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
        Prompt prompt1 = new Prompt(input, zhipuAiChatOptions);
        Prompt prompt = prompt1.augmentUserMessage(systemPromptTemplate.getTemplate());
        return zhiPuAiChatModel.call(prompt);
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

    /**
     * chat 提供大模型调用工具（函数）
     *
     * @param input     对话内容
     * @param modelName 模型名称
     * @return
     */
    @GetMapping("/with/text/tool")
    public String chatWithTool(@RequestParam(value = "input", required = false) String input,
                               @RequestParam(value = "modelName", required = false) String modelName) {

        ZhiPuAiChatOptions openAiChatOptions = ZhiPuAiChatOptions.builder()
                .model(StringUtils.isNotBlank(modelName) ? modelName : ZhiPuAiApi.ChatModel.GLM_4_Flash.value)
//                .toolCallbacks()
                .build();

        // 从当前时间开始，帮我设置一个1小时后的闹钟
        Prompt prompt = new Prompt(StringUtils.isNotBlank(input) ? input : "Jungle喜欢什么?", openAiChatOptions);
        // ask "成都现在天气怎么样" ，向 ChatClient 添加该工具，只对特定聊天请求生效
        ToolCallback toolCallback = FunctionToolCallback
//                .builder("currentWeather", new WeatherService())
                .builder("currentWeather", (request) -> {
                    System.out.println("获取当前位置的天气");
                    return new WeatherResponse(33.0, Unit.C);
                })
                .description("获取当前位置的天气")
                .inputType(WeatherRequest.class)
                .toolMetadata(ToolMetadata.builder().returnDirect(true).build()) //将函数调用内容直接返回
                .build();

        ToolCallback[] toolCallbacks = ToolCallbacks.from(new DateTimeTools());

        List<ToolCallback> list = new ArrayList<>(Arrays.asList(toolCallbacks));
        list.add(toolCallback);

        return ChatClient.create(zhiPuAiChatModel)
                .prompt(prompt)
                .toolCallbacks(toolCallbacks)
                .toolCallbacks(list)
//                .tools(new DateTimeTools())
                .call()
                .content();
    }

    public void testMethod(String input) {

        ZhiPuAiChatOptions openAiChatOptions = ZhiPuAiChatOptions.builder()
                .model(ZhiPuAiApi.ChatModel.GLM_4_Flash.value)
//                .toolCallbacks()
                .toolNames("currentWeather")
                .build();

        // 从当前时间开始，帮我设置一个1小时后的闹钟
        Prompt prompt = new Prompt(StringUtils.isNotBlank(input) ? input : "Jungle喜欢什么?", openAiChatOptions);


        ChatClient chatClient = ChatClient.builder(zhiPuAiChatModel)
//                .defaultToolCallbacks(list)
                .defaultToolNames("currentWeather")
                .build();
        chatClient.prompt(prompt)
//                .toolCallbacks(list)
                .toolContext(Map.of("tenantId", "acme")) //上下文数据，不会发送到 AI 模型
                .call()
                .content();

//        向ChatOptions和使用FunctionToolCallback加tool适用于向特定请求生效
    }


    /**
     * chat 文本聊天（同步） mcp
     *
     * @param input
     * @return
     */
    @GetMapping("/with/mcp")
    public String chatWithMcp(@RequestParam("input") String input) {

        String content = ChatClient.create(zhiPuAiChatModel)
                .prompt()
                .user(input)
                .toolCallbacks(tools)
                .call()
                .content();
        System.out.println(" >>>> " + content);


        String result1 = zhiPuAiChatModel.call("现在是北京时间什么时候");
        String result2 = chatClient.prompt("现在是北京时间什么时候").call().content();
        return "无 mcp >>>" + result1 + "\n\n有 mcp >>> " + result2;
    }

}
