package com.jdktomcat.showcase.ai.smart.customer.controller;

import com.jdktomcat.showcase.ai.smart.customer.service.ChatMemoryService;
import com.jdktomcat.showcase.ai.smart.customer.service.OrderQueryTool;
import com.jdktomcat.showcase.ai.smart.customer.service.TechSupportTool;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatMemoryService memoryService;
    private final OrderQueryTool orderTool;
    private final TechSupportTool techTool;

    public ChatController(ChatMemoryService memoryService,
                          OrderQueryTool orderTool,
                          TechSupportTool techTool) {
        this.memoryService = memoryService;
        this.orderTool = orderTool;
        this.techTool = techTool;
    }

    @PostMapping("/ask")
    public Map<String, String> ask(@RequestBody Map<String, String> req) {
        String message = req.get("message");

        if (message.matches(".*(订单|物流|发货).*")) {
            String id = message.replaceAll("\\D+", "");
            return Map.of("reply", orderTool.getOrderStatus(id));
        } else if (message.matches(".*(支付|登录|技术|问题).*")) {
            return Map.of("reply", techTool.answer(message));
        } else {
            String reply = memoryService.chat(message);
            return Map.of("reply", reply);
        }
    }
}
