package com.jungle.ai.service.impl;

import com.jungle.ai.service.VectorService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

//@Service
public class VectorServiceImpl implements VectorService {



    //    @Autowired
    private VectorStore milvusVectorStore;

    //    @Autowired
    private ChatModel zhiPuAiChatModel;

    @Override
    public void load(String sourceFile) {
        List<Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2"))
        );

        // Add the documents to Milvus Vector Store
        milvusVectorStore.add(documents);
    }

    @Override
    public void search() {
        // Retrieve documents similar to a query
        SearchRequest searchRequest = SearchRequest.builder()
                .query("Spring")
                .topK(5)
                .build();
        List<Document> results = milvusVectorStore.similaritySearch(searchRequest);
    }


    /**
     * 检索向量库作为对话上下文
     * 用于接收用户问题的 query 占位符。
     * 一个 question_answer_context 占位符来接收检索到的上下文。
     */
    public void ragDemo() {
        PromptTemplate customPromptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template("""
                                 <query>
                        
                                 Context information is below.
                        
                        ---------------------
                        <question_answer_context>
                        ---------------------
                        
                        Given the context information and no prior knowledge, answer the query.
                        
                        Follow these rules:
                        
                        1. If the answer is not in the context, just say that you don't know.
                        2. Avoid statements like "Based on the context..." or "The provided information...".
                        """)
                .build();

        String question = "Where does the adventure of Anacletus and Birba take place?";

        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder(milvusVectorStore)
                .promptTemplate(customPromptTemplate)
                .build();

        String response = ChatClient.builder(zhiPuAiChatModel).build()
                .prompt(question)
                .advisors(qaAdvisor)
                .call()
                .content();
        System.out.println(response);
    }
}
