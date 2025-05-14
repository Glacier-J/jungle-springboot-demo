package com.jungle.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class EmbeddingTest {

    @Resource(name = "zhiPuAiEmbeddingModel")
    private EmbeddingModel embeddingModel;

    @Test
    public void testEmbedding(){
        EmbeddingResponse embeddingResponse = embeddingModel.call(
                new EmbeddingRequest(List.of("Hello World", "World is big and salvation is near"),
                        ZhiPuAiEmbeddingOptions.builder()
                                .model("Different-Embedding-Model-Deployment-Name")
                                .build()));

        System.out.println(embeddingResponse);
    }
}
