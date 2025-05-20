package com.jungle.langchain.handler;


import com.jungle.langchain.service.assistant.ChatBot;
import com.jungle.langchain.service.assistant.GreetingExpert;


public class MilesOfSmiles {

    private final GreetingExpert greetingExpert;
    private final ChatBot chatBot;

    public MilesOfSmiles(GreetingExpert greetingExpert, ChatBot chatBot) {
        this.greetingExpert = greetingExpert;
        this.chatBot = chatBot;
    }

    public String handle(String userMessage) {
        //如何是问候语，给一个默认回复
        if (greetingExpert.isGreeting(userMessage)) {
            return "Greetings from Miles of Smiles! How can I make your day better?";
        } else {
            return chatBot.reply(userMessage);
        }
    }
}