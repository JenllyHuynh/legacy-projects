package vn.edu.fpt.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.dto.AiRecommendationResponse;
import vn.edu.fpt.model.dto.ChatMessage;
import vn.edu.fpt.service.AiRecommendationService;
import vn.edu.fpt.service.CustomerService;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/ai-chat")
public class AiChatController {

    private final AiRecommendationService aiService;
    private final CustomerService customerService;

    public AiChatController(AiRecommendationService aiService, CustomerService customerService) {
        this.aiService = aiService;
        this.customerService = customerService;
    }

    private String getEmailFromPrincipal(Object principal) {
        if (principal == null) return null;
        if (principal instanceof UserDetails) return ((UserDetails) principal).getUsername();
        if (principal instanceof OAuth2User) return ((OAuth2User) principal).getAttribute("email");
        return null;
    }

    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<AiRecommendationResponse> sendMessage(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal Object principal) {

        String message = payload.get("message");
        Integer customerId = null;

        String email = getEmailFromPrincipal(principal);
        if (email != null) {
            Optional<?> customerOpt = customerService.findByEmail(email);
            if (customerOpt.isPresent()) {
                // Giả sử customer có method getId()
                try {
                    customerId = (Integer) customerOpt.get().getClass().getMethod("getId").invoke(customerOpt.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        AiRecommendationResponse response = aiService.processUserMessage(message, customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChatHistory(
            @AuthenticationPrincipal Object principal) {

        Map<String, Object> response = new HashMap<>();

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(
                "assistant",
                "Xin chào! 👋 Tôi là trợ lý AI của <strong>EventGo</strong>.<br>Bạn đang tìm kiếm sự kiện nào hôm nay?",
                LocalDateTime.now()
        ));

        response.put("messages", messages);
        return ResponseEntity.ok(response);
    }
}
