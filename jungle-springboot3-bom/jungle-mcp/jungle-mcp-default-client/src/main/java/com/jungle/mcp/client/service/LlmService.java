package com.jungle.mcp.client.service;

public class LlmService {

//    public LlmService(ChatModel chatModel, ToolCallbackProvider toolCallbackProvider) {
//        this.chatModel = chatModel;
//
//        this.planningChatClient = ChatClient.builder(chatModel)
//                .defaultSystem(PLANNING_SYSTEM_PROMPT)
//                .defaultAdvisors(new MessageChatMemoryAdvisor(planningMemory))
//                .defaultAdvisors(new SimpleLoggerAdvisor())
//                .defaultTools(ToolBuilder.getPlanningAgentToolCallbacks())
//                .defaultTools(toolCallbackProvider)
//                .build();
//
//        this.chatClient = ChatClient.builder(chatModel)
//                .defaultSystem(MANUS_SYSTEM_PROMPT)
//                .defaultAdvisors(new MessageChatMemoryAdvisor(memory))
//                .defaultAdvisors(new SimpleLoggerAdvisor())
//                .defaultTools(ToolBuilder.getManusAgentToolCalls())
//                .defaultTools(toolCallbackProvider)
//                .defaultOptions(OpenAiChatOptions.builder().internalToolExecutionEnabled(false).build())
//                .build();
//
//        this.finalizeChatClient = ChatClient.builder(chatModel)
//                .defaultSystem(FINALIZE_SYSTEM_PROMPT)
//                .defaultAdvisors(new MessageChatMemoryAdvisor(finalizeMemory))
//                .defaultAdvisors(new SimpleLoggerAdvisor())
//                .build();
//    }
}
