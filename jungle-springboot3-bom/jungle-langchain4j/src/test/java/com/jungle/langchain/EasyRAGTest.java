package com.jungle.langchain;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

@SpringBootTest
public class EasyRAGTest {

    @Test
    public void testEasyRAG() {
        //加载目录下的所有文档 loadDocumentsRecursively 从所有子目录加载文档
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("C:\\Users\\Admin\\IdeaProjects\\jungle-demo\\jungle-langchain4j\\src\\main\\resources\\documentation");
//        System.out.println(documents);

        //将文档预处理并存储在专门的嵌入存储中，也称为矢量数据库,这里使用内存中的嵌入存储
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
        interface Assistant {

            String chat(String userMessage);
        }

        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_O_MINI)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        String answer = assistant.chat("How to do Easy RAG with LangChain4j?");
        System.out.println(">>>>>>>>>>>>>>> \n"+answer);
    }


}
