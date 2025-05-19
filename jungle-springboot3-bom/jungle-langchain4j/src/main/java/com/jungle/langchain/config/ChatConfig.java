package com.jungle.langchain.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.jungle.langchain.constant.ChatConstant.BASE_URL;
import static com.jungle.langchain.constant.ChatConstant.OPENAI_API_KEY;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static java.time.Duration.ofSeconds;

@Slf4j
@Configuration
public class ChatConfig {

    /**
     * 配置 OpenAi ChatModel
     *
     * @return
     */
    @Bean
    public ChatModel openAiChatModel() {
        log.info(">>>>> 配置 openAiChatModel");
        return OpenAiChatModel.builder()
                .baseUrl(BASE_URL)
                .modelName(GPT_4_O_MINI)
                .apiKey(OPENAI_API_KEY/*System.getenv("OPENAI_API_KEY")*/)
                .temperature(0.3)
                .timeout(ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    /**
     * 配置 OpenAi streaming ChatModel, 流式传输
     *
     * @return
     */
    @Bean
    public StreamingChatModel openAiStreamingChatModel() {
        log.info(">>>>> 配置 openAiStreamingChatModel");
        return OpenAiStreamingChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .baseUrl(BASE_URL)
                .modelName(GPT_4_O_MINI)
                .logRequests(true)
                .logResponses(true)
                .temperature(0.3)
                .timeout(ofSeconds(60))
                .build();
    }

}
