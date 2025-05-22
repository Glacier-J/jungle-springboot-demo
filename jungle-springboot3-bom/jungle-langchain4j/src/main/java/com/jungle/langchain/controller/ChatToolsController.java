package com.jungle.langchain.controller;

import com.jungle.langchain.pojo.Song;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * lanchain4j-tool工具支持 demo
 * https://docs.langchain4j.dev/tutorials/tools
 */
@RestController
@RequestMapping("/lc4j/chat/tool")
public class ChatToolsController {

    @Autowired
    private ChatModel openAiChatModel;

    @Autowired
    private StreamingChatModel openAiStreamingChatModel;

    /**
     * 聊天 手动构建 ToolSpecification
     *
     * @param message
     * @return
     */
    @GetMapping("/manually")
    public String chat(@RequestParam(value = "message", required = false) String message) {
        ToolSpecification toolSpecification = ToolSpecification.builder()
                .name("getWeather")
                .description("返回提供城市的天气预报")
                .parameters(JsonObjectSchema.builder()
                        .addStringProperty("city", "应返回天气预报的城市")
                        .addEnumProperty("temperatureUnit", List.of("CELSIUS", "FAHRENHEIT"))
                        .required("city") // the required properties should be specified explicitly
                        .build())
                .build();
//
//        List<ToolSpecification> toolSpecification = ToolSpecifications.toolSpecificationsFrom(WeatherTools.class);
//
////        ToolExecutionResultMessage toolExecutionResultMessage = ToolExecutionResultMessage.from(toolExecutionRequest, result);
//        ChatRequest chatRequest = ChatRequest.builder()
//                .messages(List.of(UserMessage.from(Optional.ofNullable(message).orElse("明天伦敦的天气会怎样？")),toolExecutionResultMessage))
//                .toolSpecifications(toolSpecification)
//                .build();
//        AiMessage aiMessage = openAiChatModel.chat(chatRequest).aiMessage();
//        System.out.println("是否使用到了工具："+aiMessage.hasToolExecutionRequests());
//        return aiMessage.text();


        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
//            Map<String, Object> arguments = fromJson(toolExecutionRequest.arguments());
//            String bookingNumber = arguments.get("bookingNumber").toString();
            Song booking = new Song("1", "qwe", "asd", "asd")/*getBooking(bookingNumber)*/;
            return booking.toString();
        };

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(openAiChatModel)
                .tools(Map.of(toolSpecification, toolExecutor))
                .build();
        TokenStream chat = assistant.chat(message);
        chat.onPartialResponse(System.out::print)
                .onCompleteResponse(System.out::println)
                .onError(t -> t.printStackTrace())
                .start();

        return assistant.toString();

    }


    interface Assistant {

        TokenStream chat(String message);
    }
}
