package com.jdktomcat.showcase.ai.smart.customer.service.prototype.controller;

import com.jdktomcat.showcase.ai.smart.customer.service.prototype.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody Map<String, String> payload){
        String user = payload.getOrDefault("user", "anonymous");
        String message = payload.get("message");
        String orderId = payload.get("orderId");
        if(message == null) return ResponseEntity.badRequest().body("message required");
        return ResponseEntity.ok(chatService.handle(user, message, orderId));
    }
}
