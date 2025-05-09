package com.jungle.mcp.client.controller;


import com.jungle.mcp.client.service.IChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/mcp")
public class ChatController {

    private final IChatService chatService;

    @PostMapping("/chat")
    public String chat(@RequestParam("message") String message) {
//        return chatService.chat(message);
        return chatService.chat1(message);
    }

}
