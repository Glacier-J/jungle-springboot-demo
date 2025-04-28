package com.jungle.langchain;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static java.time.Duration.ofSeconds;

@Slf4j
@SpringBootTest
public class OpenAITest {

    //https://github.com/chatanywhere/GPT_API_free?tab=readme-ov-file
    public static final String BASE_URL = "https://api.chatanywhere.tech";
    public static final String API_KEY = "sk-pyWQ3wgQKHsZNc4DF8J0BhCo0ZF8a7sos7jYR7oEnUID0aDR";
    public static final String MODEL_NAME = "gpt-3.5-turbo";

    @Test
    public void testOpenAIChat() {
//        String apiKey = System.getenv("OPENAI_API_KEY"); //获取环境变量中apikey
        OpenAiChatModel model = createOpenAIModel();
        String answer = model.chat("解释一下人工智能领域的 RAG");
        System.out.println(answer); // Hello World

        System.out.println("------------");
    }

    private static OpenAiChatModel createOpenAIModel() {
        return OpenAiChatModel.builder()
                .baseUrl(BASE_URL)
                .modelName(MODEL_NAME)
                .apiKey(API_KEY/*System.getenv("OPENAI_API_KEY")*/)
                .temperature(0.3)
                .timeout(ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Test
    public void testEmbedding() {
        OpenAiEmbeddingModel embeddingModel = new OpenAiEmbeddingModel
                .OpenAiEmbeddingModelBuilder()
                .apiKey(API_KEY)
                .baseUrl(BASE_URL)
                .build();
        log.info("当前的模型是: {}", embeddingModel.modelName());
        String text = "两只眼睛";
        Embedding embedding = embeddingModel.embed(text).content();
        log.info("它是{}维的向量", embedding.dimension());
        log.info("文本:{}的嵌入结果是:\n{}", text, embedding.vectorAsList());
    }

    @Test
    public void testCustomize() {
        SystemMessage systemMessage = new SystemMessage("");
        ChatRequest request = ChatRequest.builder()
                .messages(UserMessage.from())
                .parameters(ChatRequestParameters.builder()
                        .temperature(0.5)
                        .toolSpecifications()
                        .build())
                .build();
    }

    @Test
    public void testChatResponse() {
        OpenAiChatModel model = createOpenAIModel();

        List<Content> contentList = new ArrayList<>();
        //new TextContent("文本内容"); new ImageContent("图像内容url"); AudioContent  音频内容、 VideoContent  视频内容、 PdfFileContent  PdfFileContent （英文 ）
        contentList.add(new TextContent("文本内容"));
        UserMessage userMessageContents = new UserMessage(contentList);

        UserMessage userMessage = UserMessage.from(
                TextContent.from("Describe the following image"),
                ImageContent.from("https://example.com/cat.jpg")
        );
        ChatResponse response = model.chat(userMessage);

        UserMessage userMessageText = new UserMessage("泥耗！");
        //multiple chatMessage
        UserMessage firstUserMessage = UserMessage.from("Hello, my name is Klaus");
        AiMessage firstAiMessage = model.chat(firstUserMessage).aiMessage(); // Hi Klaus, how can I help you?
        UserMessage secondUserMessage = UserMessage.from("What is my name?");
        AiMessage secondAiMessage = model.chat(firstUserMessage, firstAiMessage, secondUserMessage).aiMessage(); // Klaus

        //ChatMemory 充当 ChatMessage 的容器
    }

    @Test
    public void testPolicy() {
        //驱逐策略(An eviction policy)是必要的

        //MessageWindowChatMemory 充当滑动窗口，保留 N 条最近的消息并驱逐不再适合的旧消息

        //更复杂的选项是 TokenWindowChatMemory，它也作为滑动窗口运行，但专注于保留 N 个最新的令牌 。 根据需要驱逐较旧的消息。 信息是不可分割的。如果消息不合适，则会将其完全逐出。 TokenWindowChatMemory 需要 Tokenizer 对每个 ChatMessage 中的令牌进行计数。
    }
//    public void testJd(){
//        //提示模板（Prompt Templates）是用于生成语言模型提示的预定义配方，可以包括指令、少样本示例[16]和特定上下文
//        PromptTemplate promptTemplate = PromptTemplate
//                .from("Tell me a {{adjective}} joke about {{content}}..");
//        Map<String, Object> variables = new HashMap<>();
//        variables.put("adjective", "funny");
//        variables.put("content", "computers");
//        Prompt prompt = promptTemplate.apply(variables);
//
//
//        //若需要能够从内存中读取信息以增强用户输入，同时还需要将当前运行的输入和输出写入内存
//        ChatMemory chatMemory = TokenWindowChatMemory
//                .withMaxTokens(300, new OpenAiTokenizer(GPT_3_5_TURBO));
//        chatMemory.add(userMessage("你好，我叫 Kumar"));
//        AiMessage answer = model.generate(chatMemory.messages()).content();
//        System.out.println(answer.text()); // 你好 Kumar！今天我能为您做些什么？
//        chatMemory.add(answer);
//        chatMemory.add(userMessage("我叫什么名字？"));
//        AiMessage answerWithName = model.generate(chatMemory.messages()).content();
//        System.out.println(answerWithName.text()); // 您的名字是 Kumar。
//        chatMemory.add(answerWithName);
//
//
//        //文档加载器 FileSystemDocumentLoader，用于从存储位置检索文档。然后，LangChain 还提供了转换器，用于进一步处理文档，例如将大型文档分割成更小的块
//        Document document = FileSystemDocumentLoader.loadDocument("simpson's_adventures.txt");
//        DocumentSplitter splitter = DocumentSplitters.recursive(100, 0,
//                new OpenAiTokenizer(GPT_3_5_TURBO));
//        List<TextSegment> segments = splitter.split(document);
//    }


}
