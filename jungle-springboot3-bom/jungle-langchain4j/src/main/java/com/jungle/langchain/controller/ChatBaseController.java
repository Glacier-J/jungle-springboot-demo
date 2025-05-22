package com.jungle.langchain.controller;

import cn.hutool.core.util.StrUtil;
import com.jungle.langchain.enums.Priority;
import com.jungle.langchain.handler.MilesOfSmiles;
import com.jungle.langchain.pojo.Person;
import com.jungle.langchain.service.assistant.*;
import com.jungle.langchain.store.PersistentChatMemoryStore;
import com.jungle.langchain.tools.SongTools;
import dev.langchain4j.data.message.*;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.jungle.langchain.constant.ChatConstant.OPENAI_BASE_URL;
import static com.jungle.langchain.constant.ChatConstant.OPENAI_API_KEY;
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

    @Autowired
    private IAssistantSO iAssistantSO;

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
     * 聊天-流式 - TokenStream
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
//        tokenStream.onPartialResponse(System.out::print)
//                .onRetrieved(System.out::println)
//                .onToolExecuted(System.out::println)
//                .onCompleteResponse((t)->{
//                    System.out.println("Complete response..");
//                })
//                .onError((t)->{
//                    System.out.println("Error");
//                })
//                .start();
        try {
            return futureResponse.get(30, SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 聊天-流式 Flux 代替 TokenStream 需导入 langchain4j-reactor 依赖
     *
     * @param message
     * @return Flux
     */
    @GetMapping("/flux/stream")
    public Flux<String> chatFlux1(@RequestParam("message") String message) {
        // 创建 AI 服务 或者 使用 @AiService 交由Spring统一管理
        // 原理：将接口的 Class 以及底层组件提供给 AiServices 会创建一个实现此接口的代理对象。目前使用反射
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

    /**
     * 聊天 service builder
     *
     * @param message
     * @return Result<List < String>>
     */
    @GetMapping("/with/service/builder")
    public List<String> chatServiceBuilder(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        IAssistant4 assistant = AiServices.builder(IAssistant4.class)
                .chatModel(openAiChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .systemMessageProvider(chatMemoryId -> "You are a good friend of mine. Answer using slang.")
                .build();
        Result<List<String>> result = assistant.chat(message);
        List<String> content = result.content();
        TokenUsage tokenUsage = result.tokenUsage();
        List<Content> sources = result.sources();
        List<ToolExecution> toolExecutions = result.toolExecutions();
        FinishReason finishReason = result.finishReason();
        return content;
    }

    /**
     * 聊天 structure output 结构化输出-boolean-enum-pojo-json
     *
     * @param message
     * @return Result<List < String>>
     */
    @GetMapping("/with/structure/output")
    public Person chatStructureOutput(@RequestParam(value = "message", required = false) String message) {
        //IAssistantSO IAssistantSO = AiServices.create(IAssistantSO.class, openAiChatModel);
        //提取自定义 POJO，实际上是 JSON，然后解析为 POJO，建议在模型配置中启用“JSON 模式 https://docs.langchain4j.dev/tutorials/ai-services#json-mode
        String text = """
                In 1968, amidst the fading echoes of Independence Day,
                a child named John arrived under the calm evening sky.
                This newborn, bearing the surname Doe, marked the start of a new journey.
                He was welcomed into the world at 345 Whispering Pines Avenue
                a quaint street nestled in the heart of Springfield
                an abode that echoed with the gentle hum of suburban dreams and aspirations.
                """;

        Person person = iAssistantSO.extractPersonFrom(StrUtil.isBlank(message) ? text : message);

        System.out.println(person); // Person { firstName = "John", lastName = "Doe", birthDate = 1968-07-04, address = Address { ... } }

        Priority priority = iAssistantSO.analyzePriority("The main payment gateway is down, and customers cannot process transactions.");
        System.out.println(priority); // CRITICAL

        boolean positive = iAssistantSO.isPositive("It's wonderful!");
        System.out.println(positive); // true

        return person;
    }


    /**
     * 聊天 添加工具函数 tools
     *
     * @param message
     * @return Result<List < String>>
     */
    @GetMapping("/with/tools")
    public SseEmitter chatWithTools(@RequestParam(value = "message", required = false) String message) {
        IAssistant4 assistant = AiServices.builder(IAssistant4.class)
                .streamingChatModel(openAiStreamingChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(new SongTools())
                .build();

        TokenStream tokenStream = assistant.chatTokenStream(StrUtil.isBlank(message) ? "把ID为20210522153941的歌曲给我" : message);
        SseEmitter sseEmitter = new SseEmitter();

        CompletableFuture<ChatResponse> future = new CompletableFuture<>();
        tokenStream.onPartialResponse((s)-> {
                    try {
                        sseEmitter.send(s, MediaType.APPLICATION_JSON);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onCompleteResponse(future::complete)
                .onError(future::completeExceptionally)
                .start();
        try {
            ChatResponse chatResponse = future.get(30, SECONDS);

//            return chatResponse.id()+", "+chatResponse.modelName()+", "+chatResponse.aiMessage()+", "+chatResponse.tokenUsage();
            return sseEmitter;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 聊天 Multiple Service
     *
     * @param message
     * @return
     */
    @GetMapping("/with/multiple/service")
    public void chatWithMultipleService(@RequestParam(value = "message", required = false) String message) {
        GreetingExpert greetingExpert = AiServices.create(GreetingExpert.class, openAiChatModel);
        EmbeddingStore<TextSegment> embeddingStore  = new InMemoryEmbeddingStore<>();
        EmbeddingModel embeddingModel = new OpenAiEmbeddingModel(OpenAiEmbeddingModel.builder().baseUrl(OPENAI_BASE_URL).apiKey(OPENAI_API_KEY));

        ContentRetriever contentRetriever = new EmbeddingStoreContentRetriever(embeddingStore, embeddingModel);
        ChatBot chatBot = AiServices.builder(ChatBot.class)
                .chatModel(openAiChatModel)
                .contentRetriever(contentRetriever)
                .build();

        MilesOfSmiles milesOfSmiles = new MilesOfSmiles(greetingExpert, chatBot);

        String greeting = milesOfSmiles.handle("Hello");
        System.out.println(greeting); // Greetings from Miles of Smiles! How can I make your day better?

        String answer = milesOfSmiles.handle("Which services do you provide?");
        System.out.println(answer); // At Miles of Smiles, we provide a wide range of services ...

    }

}
