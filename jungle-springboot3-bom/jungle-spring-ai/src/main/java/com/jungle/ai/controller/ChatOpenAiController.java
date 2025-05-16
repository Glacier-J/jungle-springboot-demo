package com.jungle.ai.controller;

import com.jungle.ai.entity.ActorsFilms;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * OpenAI
 */
@RestController
@RequestMapping("/ai")
//@RequiredArgsConstructor
public class ChatOpenAiController {

    private final ChatModel openAiChatModel;

//    private final ChatClient chatClient;

    public ChatOpenAiController(/*ChatClient.Builder builder, */@Qualifier(value = "openAiChatModel") ChatModel openAiChatModel) {
//        this.chatClient = builder.build();
        this.openAiChatModel = openAiChatModel;
    }

//    @GetMapping("/openai/chat1")
//    public String chatWithChatClient(@RequestParam("input") String input) {
//        return this.chatClient.prompt()
//                .user(input)
//                .call()
//                .content();
//    }



    /**
     * chat 文本聊天（同步）
     * @param input
     * @return
     */
    @GetMapping("/openai/chat/with/text")
    public ChatResponse chatWithChatModelCustom(@RequestParam("input") String input) {
        //自定义option或使用默认配置
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_4_O_MINI.getValue())
                .temperature(0.8)
                .build();

        return openAiChatModel.call(new Prompt(input, openAiChatOptions));
    }


    //自定义option 或 使用默认配置
    public static final OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
            .model(OpenAiApi.ChatModel.GPT_4_O_MINI.getValue())
            .temperature(0.8)
            .build();


    /**
     * chat 文本聊天(流式)
     *
     * @param input
     * @return
     */
    @GetMapping("/openai/chat/with/text/stream")
    public Flux<ChatResponse> chatWithChatModelStream(@RequestParam("input") String input) {
        //自定义option或使用默认配置
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_4_O_MINI.getValue())
                .temperature(0.8)
                .build();

        return openAiChatModel.stream(new Prompt(input, openAiChatOptions));
    }


    /**
     * chat 多模态功能聊天示例 Vision  输入图像、Audio  输入音频 、Output Audio  输出音频
     *
     * @param input
     * @param imageUri
     * @return
     */
    @GetMapping("/openai/chat/with/images")
    public ChatResponse chatWithImages(@RequestParam("input") String input,
                                       @RequestParam(value = "imageUri", required = false) String imageUri) {
        Media media;
        if (StringUtils.isNotBlank(imageUri)) {
            try {
                URI uri = new URI(imageUri);
                media = new Media(MimeTypeUtils.IMAGE_PNG, uri);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            var imageResource = new ClassPathResource("/multimodal.test.png");
            media = new Media(MimeTypeUtils.IMAGE_PNG, imageResource);
        }

        UserMessage userMessage = UserMessage.builder()
                .text(input)
                .media(media)
                .build();

        return openAiChatModel.call(new Prompt(userMessage, openAiChatOptions));
    }

    /**
     * chat 结构化输出聊天
     *
     * @param input
     * @return
     */
    @GetMapping("/openai/chat/with/so")
    public ChatResponse chatWithStructuredOutputs(@RequestParam(value = "input",required = false) String input) {

        String jsonSchema = """
                {
                    "type": "object",
                    "properties": {
                        "steps": {
                            "type": "array",
                            "items": {
                                "type": "object",
                                "properties": {
                                    "explanation": { "type": "string" },
                                    "output": { "type": "string" }
                                },
                                "required": ["explanation", "output"],
                                "additionalProperties": false
                            }
                        },
                        "final_answer": { "type": "string" }
                    },
                    "required": ["steps", "final_answer"],
                    "additionalProperties": false
                }
                """;

        String contents = StringUtils.isNotBlank(input) ? input : "how can I solve 8x + 7 = -23";
        Prompt prompt = new Prompt(contents,
                OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.GPT_4_O_MINI.getValue())
                        .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                        .build());

        return openAiChatModel.call(prompt);
    }

    /**
     * chat 结构化输出聊天
     *
     * @param input
     * @return
     */
    @GetMapping("/openai/chat/with/so1")
    public ActorsFilms chatWithStructuredOutputs1(@RequestParam(value = "input", required = false) String input) {
//        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
//                .responseFormat(ResponseFormat.builder().type(ResponseFormat.Type.JSON_OBJECT).build())
//                .build();
//
//        Prompt prompt = new Prompt(input, openAiChatOptions);
        ChatClient.CallResponseSpec call = ChatClient.create(openAiChatModel)
                .prompt()
//                .options()
                .user(u -> {
                    u.text("Generate the game of 5 movies for {actor}.").param("actor", "Rockstar Games");
                })
                .call();
//        System.out.println(call.content());

//                Map<String, Object> mapContent = call.entity(new ParameterizedTypeReference<Map<String, Object>>() {
//        });

        //告诉LLM按照指定格式输出json，底层利用 ObjectMapper 将json串就行反序列化Java对象实例
        return call.entity(ActorsFilms.class);

    }


    //Low-level OpenAiApi Client
    public void chatLowLevel() {
//        var openAiApi = OpenAiApi.builder()
//                .apiKey(System.getenv("OPENAI_API_KEY"))
//                .build();
//        var openAiChatOptions = OpenAiChatOptions.builder()
//                .model("gpt-3.5-turbo")
//                .temperature(0.4)
//                .maxTokens(200)
//                .build();
//        var chatModel = new OpenAiChatModel(openAiApi, openAiChatOptions);
//
//        ChatResponse response = chatModel.call(
//                new Prompt("Generate the names of 5 famous pirates."));

// Or with streaming responses
//        Flux<ChatResponse> response = chatModel.stream(
//                new Prompt("Generate the names of 5 famous pirates."));


        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();

        OpenAiApi.ChatCompletionMessage chatCompletionMessage =
                new OpenAiApi.ChatCompletionMessage("Hello world", OpenAiApi.ChatCompletionMessage.Role.USER);

// Sync request
//        ResponseEntity<OpenAiApi.ChatCompletion> response = openAiApi.chatCompletionEntity(
//                new OpenAiApi.ChatCompletionRequest(List.of(chatCompletionMessage), "gpt-3.5-turbo", 0.8, false));

// Streaming request
//        Flux<OpenAiApi.ChatCompletionChunk> streamResponse = openAiApi.chatCompletionStream(
//                new OpenAiApi.ChatCompletionRequest(List.of(chatCompletionMessage), "gpt-3.5-turbo", 0.8, true));
    }
}
