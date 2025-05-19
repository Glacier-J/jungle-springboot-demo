package com.jungle.langchain.controller;

import com.jungle.langchain.service.assistant.IAssistant;
import com.jungle.langchain.service.assistant.IAssistant2;
import com.jungle.langchain.service.assistant.IAssistant3;
import com.jungle.langchain.service.assistant.IAssistant4;
import com.jungle.langchain.store.PersistentChatMemoryStore;
import dev.langchain4j.data.message.*;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static dev.langchain4j.model.LambdaStreamingResponseHandler.onPartialResponse;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * lanchain4j-demo-controller
 */
@RestController
@RequestMapping("/lc4j/chat")
public class ChatBaseController {

    @Autowired
    private ChatModel openAiChatModel;

    @Autowired
    private StreamingChatModel openAiStreamingChatModel;

    @Autowired
    private IAssistant assistant;

//    @Autowired
//    private IAssistant2 assistant2;

    /**
     * 聊天 sync
     *
     * @param message
     * @return
     */
    @GetMapping
    public String chat(@RequestParam("message") String message) {
        return assistant.chat(message);
    }



    /**
     * 聊天-chatModel-实现聊天记忆
     *
     * @param message
     * @return
     */
    @GetMapping("/with/multiple/message")
    public String modelMultiMessage(@RequestParam(value = "message", defaultValue = "Hello") String message) {
//        UserMessage firstUserMessage = UserMessage.from("Hello, my name is Klaus");
//        AiMessage firstAiMessage = chatModel.chat(firstUserMessage).aiMessage(); // Hi Klaus, how can I help you?
//        UserMessage secondUserMessage = UserMessage.from("What is my name?");
//        AiMessage secondAiMessage = chatModel.chat(firstUserMessage, firstAiMessage, secondUserMessage).aiMessage(); // Klaus
        //难以管理，可使用 chat Memory


        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id("12345")
                .maxMessages(10)
                //自定义存储
                .chatMemoryStore(new PersistentChatMemoryStore())
                .build();

        IAssistant3 assistant = AiServices.builder(IAssistant3.class)
                .chatModel(openAiChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
//                .chatMemoryProvider(memoryId -> chatMemory)
                .build();

        System.out.println(assistant.chatWithMemory(1, "Hello, my name is Klaus"));
        // Hi Klaus! How can I assist you today?

        System.out.println(assistant.chatWithMemory(2, "Hello, my name is Francine"));
        // Hello Francine! How can I assist you today?

        System.out.println(assistant.chatWithMemory(1, "What is my name?"));
        // Your name is Klaus.

        System.out.println(assistant.chatWithMemory(2, "What is my name?"));
        // Your name is Francine.

        return "secondAiMessage.text()";
    }


    /**
     * 聊天-chatModel-带图片
     *
     * @param message
     * @return
     */
    @GetMapping("/with/image")
    public ChatResponse chatWithImage(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        UserMessage userMessage = UserMessage.from(
                TextContent.from("解释一下这个logo的含义"),
                ImageContent.from("https://docs.langchain4j.dev/img/logo.svg"));
        return openAiChatModel.chat(userMessage);
    }


    /**
     * 聊天-流式
     *
     * @param message
     * @return
     */
    @GetMapping("/token/stream")
    public ChatResponse chatFlux(@RequestParam("message") String message) {
        IAssistant2 assistant = AiServices.create(IAssistant2.class, openAiStreamingChatModel);

        TokenStream tokenStream = assistant.chatFlux(message);

        CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();

        tokenStream.onPartialResponse(System.out::print)
                .onCompleteResponse(futureResponse::complete)
                .onError(futureResponse::completeExceptionally)
                .start();

        try {
            return futureResponse.get(30, SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 聊天-流式
     *
     * @param message
     * @return
     */
    @GetMapping("/flux/stream")
    public Flux<String> chatFlux1(@RequestParam("message") String message) {
        // 创建 AI 服务 或者 使用 @AiService 交由Spring统一管理
        // 原理：将接口的 Class 以及底层组件提供给 AiServices 会创建一个实现此接口的代理对象。目前使用反射
        //template cannot be null or blank ?
        IAssistant4 assistant = AiServices.create(IAssistant4.class, openAiStreamingChatModel);
        return assistant.chatFlux(message);
    }

    /**
     * 聊天-StreamingChat-流式传输
     *
     * @param message
     * @return
     */
    @GetMapping("/with/streaming")
    public void chatStreaming(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        SystemMessage systemMessage = new SystemMessage("你是一个Java后端的专业的高级开发工程师,务必以更容易让人理解的话语回答专业领域的知识。");
//        UserMessage userMessage = UserMessage.from(TextContent.from("Spring 源码中涉及到哪些设计模式？"));
        UserMessage userMessage = UserMessage.from(TextContent.from("详细解释一下Spring中的设计模式-观察者模式"));
        List<ChatMessage> chatMessages = List.of(systemMessage, userMessage);

//        openAiStreamingChatModel.chat(Collections.singletonList(userMessage), new MyStreamingChatResponseHandler());
        //更简洁的流式传输响应-LambdaStreamingResponseHandler
        openAiStreamingChatModel.chat(chatMessages, onPartialResponse(System.out::print));

    }


}
