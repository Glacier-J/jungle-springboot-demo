package com.jungle.langchain.controller;

import cn.hutool.core.util.StrUtil;
import com.jungle.langchain.tools.DataFetchTools;
import dev.langchain4j.community.web.search.searxng.SearXNGWebSearchEngine;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.web.search.WebSearchEngine;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;

import static com.jungle.langchain.constant.ChatConstant.*;

/**
 * lanchain4j-rag demo
 * <a href="https://docs.langchain4j.dev/tutorials/rag">RAG</a>
 */
@RestController
@RequestMapping("/lc4j/chat/rag")

public class ChatRAGController {

    public static final String DIRECTORY_PATH = "C:\\Users\\Admin\\IdeaProjects\\jungle-springboot-demo\\jungle-springboot3-bom\\jungle-langchain4j\\src\\main\\resources\\documentation";

    @Autowired
    private ChatModel openAiChatModel;

    @Autowired
    private StreamingChatModel openAiStreamingChatModel;


    @Resource(name = "zhipuAiChatModel")
    private ChatModel zhipuAiChatModel;

    /**
     * 普通同步聊天
     *
     * @param message
     * @return
     */
    @GetMapping
    public String chatNormal(@RequestParam(value = "message", required = false) String message) {
        //创建一个AI服务
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(zhipuAiChatModel)
                .build();

        return assistant.chat(StrUtil.isNotBlank(message) ? message : "How to do Easy RAG with LangChain4j?中文回答");
    }

    /**
     * 聊天 加载文件作为上下文
     *
     * @param message
     * @return
     */
    @GetMapping("/easy/rag")
    public String chat(@RequestParam(value = "message", required = false) String message) {
        //正则表达式过滤文档
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.yml");
        //加载指定目录下的文档，从所有子目录加载文档，可以使用 loadDocumentsRecursively 方法,
        // 默认使用 TextDocumentParser 文档解析器
        // langchain4j-document-parser-apache-pdfbox 模块的 ApachePdfBoxDocumentParser
        // langchain4j-document-parser-apache-poi 模块的 ApachePoiDocumentParser
        // langchain4j-document-parser-apache-tika 模块的 ApacheTikaDocumentParser
        List<Document> documents = FileSystemDocumentLoader.loadDocuments(DIRECTORY_PATH, pathMatcher);

        //对文档进行预处理，使用内存存储
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        //创建一个AI服务
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(openAiChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        return assistant.chat(StrUtil.isNotBlank(message) ? message : "How to do Easy RAG with LangChain4j?中文回答");
    }

    /**
     * 嵌入
     *
     * @param message
     * @return
     */
    @GetMapping("/embedding")
    public String chatEmbedding(@RequestParam(value = "message", required = false) String message) {
        List<Document> documents = FileSystemDocumentLoader.loadDocuments(DIRECTORY_PATH, FileSystems.getDefault().getPathMatcher("glob:*.md"));

        //对文档进行预处理，使用内存存储,将文档内容提取处理后嵌入上下文中
        EmbeddingStore<TextSegment> inMemoryEmbeddingStore = new InMemoryEmbeddingStore<>();
//        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        OpenAiEmbeddingModel openAiEmbeddingModel = OpenAiEmbeddingModel.builder()
                .baseUrl(OPENAI_BASE_URL)
                .apiKey(OPENAI_API_KEY)
                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
                .build();

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(openAiEmbeddingModel)
                .embeddingStore(inMemoryEmbeddingStore)
                // 将userId元数据条目添加到每个文档中，以便以后能够根据它进行筛选
                .documentTransformer(document -> {
                    document.metadata().put("userId", "12345");
                    return document;
                })

                // 拆分为较小的 TextSegment, 提高相似性搜索的质量,减少成本 ， 将每个文档分割成每个1000个标记的TextSegments，并具有200个标记的重叠
                .documentSplitter(DocumentSplitters.recursive(1000, 200, new OpenAiTokenCountEstimator("gpt-4o-mini")))

                // 转换 将文档的名称添加到每个TextSegment中以提高搜索质量
                .textSegmentTransformer(textSegment -> TextSegment.from(
                        textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
                        textSegment.metadata()
                ))
                .build();

        IngestionResult ingestionResult = ingestor.ingest(documents);


//        //动态指定
//        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
//                .embeddingModel(openAiEmbeddingModel)
//                .embeddingStore(inMemoryEmbeddingStore)
//                .maxResults(3)
//                .minScore(0.75)
//                .dynamicMaxResults(query -> 3)
//                .dynamicMinScore(query -> 0.75)
//                .filter(metadataKey("userId").isEqualTo("12345"))
//                .dynamicFilter(query -> {
//                    String userId = query.metadata().chatMemoryId() + "_";
//                    return metadataKey("userId").isEqualTo(userId);
//                })
//                .build();
        // 创建一个AI服务
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(openAiChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
//                .contentRetriever(contentRetriever/*EmbeddingStoreContentRetriever.from(inMemoryEmbeddingStore)*/)
//                .retrievalAugmentor() //增强当前的 UserMessage
                .build();
//        CompressingQueryTransformer compressingQueryTransformer = new CompressingQueryTransformer() //会询问涉及之前问题或答案的后续问题时

        return assistant.chat(StrUtil.isNotBlank(message) ? message : "How to do Easy RAG with LangChain4j?中文回答");
    }

    /**
     * 嵌入
     *
     * @param message
     * @return
     */
    @GetMapping("/web/search")
    public String chatWebSearch(@RequestParam(value = "message", required = false) String message) {
        Assistant assistant = createAssistant();
        return assistant.chat(StrUtil.isNotBlank(message) ? message : "时间是今天，获取今天的实时热点新闻排行榜");
    }

    private Assistant createAssistant() {
        // Let's create our embedding store content retriever.
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();

        ContentRetriever embeddingStoreContentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(new InMemoryEmbeddingStore<TextSegment>())
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.6)
                .build();

//        WebSearchEngine googleSearchEngine = GoogleCustomWebSearchEngine.builder()
//                .apiKey(GOOGLE_API_KEY)
//                .csi(GOOGLE_SEARCH_ENGINE_ID)
//                .build();
        // 创建 Web 搜索内容检索器
//        WebSearchEngine webSearchEngine = TavilyWebSearchEngine.builder()
//                .apiKey(TAVILY_API_KEY) // get a free key: https://app.tavily.com/sign-in
//                .build();

        WebSearchEngine webSearchEngine = SearXNGWebSearchEngine.builder()
                .baseUrl(SEARXNG_SEARCH_BASE_URL)
                .logRequests(true)
                .logResponses(true)
                .build();

        ContentRetriever webSearchContentRetriever = WebSearchContentRetriever.builder()
                .webSearchEngine(webSearchEngine)
                .maxResults(3)
                .build();

        // 创建一个查询路由器，它将每个查询路由到两个检索器。
        QueryRouter queryRouter = new DefaultQueryRouter(embeddingStoreContentRetriever, webSearchContentRetriever);

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .build();

        return AiServices.builder(Assistant.class)
                .chatModel(zhipuAiChatModel)
                .tools(new DataFetchTools())
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "zhipuAiChatModel")
    interface Assistant {

        @SystemMessage("You are a person of humor")
        String chat(String message);

        TokenStream chatTokenStream(String message);

    }
}
