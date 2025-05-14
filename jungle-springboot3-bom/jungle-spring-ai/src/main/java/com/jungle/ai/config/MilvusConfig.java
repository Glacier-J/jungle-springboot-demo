package com.jungle.ai.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.context.annotation.Bean;

//@Configuration
public class MilvusConfig {

    @Bean
    public VectorStore vectorStore(MilvusServiceClient milvusClient,  EmbeddingModel zhiPuAiEmbeddingModel) {
        return MilvusVectorStore.builder(milvusClient, zhiPuAiEmbeddingModel)
                .collectionName("test_vector_store")
                .databaseName("default")
                .indexType(IndexType.IVF_FLAT)
                .metricType(MetricType.COSINE)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .initializeSchema(true)
                .build();
    }

    @Bean
    public MilvusServiceClient milvusClient() {
        return new MilvusServiceClient(ConnectParam.newBuilder()
                .withAuthorization("minioadmin", "minioadmin")
                .withUri("milvusContainer.getEndpoint()")
                .build());
    }
}
