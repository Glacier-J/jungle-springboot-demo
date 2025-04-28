package com.jungle.langchain.store;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.util.List;

class PersistentChatMemoryStore implements ChatMemoryStore {

    /*
    默认情况下，ChatMemory 实现将 ChatMessage 存储在内存中
    自定义 ChatMemoryStore 以将 ChatMessage 存储在您选择的任何持久性存储中
     */
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // TODO: Implement getting all messages from the persistent store by memory ID.
        // ChatMessageDeserializer.messageFromJson(String) and
        // ChatMessageDeserializer.messagesFromJson(String) helper methods can be used to
        // easily deserialize chat messages from JSON.
        return null;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        // TODO: Implement updating all messages in the persistent store by memory ID.
        // ChatMessageSerializer.messageToJson(ChatMessage) and
        // ChatMessageSerializer.messagesToJson(List<ChatMessage>) helper methods can be used to
        // easily serialize chat messages into JSON.
    }

    @Override
    public void deleteMessages(Object memoryId) {
        // TODO: Implement deleting all messages in the persistent store by memory ID.
    }

//    ChatMemory chatMemory = MessageWindowChatMemory.builder()
//            .id("12345")
//            .maxMessages(10)
//            .chatMemoryStore(new PersistentChatMemoryStore())
//            .build();

}

