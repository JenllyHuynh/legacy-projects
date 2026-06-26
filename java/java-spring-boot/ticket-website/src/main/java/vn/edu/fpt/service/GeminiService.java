package vn.edu.fpt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.edu.fpt.model.dto.AiRecommendationRequest;
import vn.edu.fpt.model.dto.AiRecommendationResponse;
import vn.edu.fpt.model.entity.Event;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GeminiService {

    // Đừng có chuyển cái này đi qua application.properties tại đang dùng model của con chat mới
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_API_KEY = "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";
    private static final String MODEL = "llama-3.3-70b-versatile";

    private final RestTemplate restTemplate;
    private final AiRecommendationService recommendationService;
    private final ObjectMapper objectMapper;

    public GeminiService(@Lazy AiRecommendationService recommendationService,
                         ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.recommendationService = recommendationService;
        this.objectMapper = objectMapper;
    }

    private String callGroqApi(String systemPrompt, String userPrompt) {
        try {
            System.out.println("=== [Groq] Calling API...");

            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", MODEL);
            body.put("temperature", 0.75);
            body.put("max_tokens", 700);

            ArrayNode messages = objectMapper.createArrayNode();

            ObjectNode sys = objectMapper.createObjectNode();
            sys.put("role", "system");
            sys.put("content", systemPrompt);
            messages.add(sys);

            ObjectNode user = objectMapper.createObjectNode();
            user.put("role", "user");
            user.put("content", userPrompt);
            messages.add(user);

            body.set("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(GROQ_API_KEY);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    GROQ_API_URL, new HttpEntity<>(body.toString(), headers), String.class);

            System.out.println("=== [Groq] Status: " + response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String text = root.path("choices").get(0)
                        .path("message").path("content").asText();
                System.out.println("=== [Groq] Reply length: " + text.length());
                return text;
            }

            return "Xin lỗi, em không thể xử lý yêu cầu lúc này. Vui lòng thử lại! 🤔";

        } catch (Exception e) {
            System.err.println("=== [Groq] Error: " + e.getMessage());
            return "Xin lỗi, em không thể kết nối AI lúc này. Thử lại sau nhé! 🙇";
        }
    }

    private String buildSystemPrompt() {
        return """
                Bạn là trợ lý AI tư vấn sự kiện của EventGo — nền tảng đặt vé sự kiện hàng đầu Việt Nam.
                
                Phong cách trả lời:
                - Dùng tiếng Việt, thân thiện, tự nhiên như người bạn tư vấn thật
                - Trả lời 3-5 câu, đúng trọng tâm
                - Dùng emoji phù hợp (1-3 emoji/tin nhắn)
                - KHÔNG giới thiệu bản thân lại nếu đã giới thiệu rồi
                - KHÔNG lặp lại câu giống nhau giữa các tin nhắn
                
                Quan trọng về cách đề cập sự kiện:
                - Bạn sẽ được cung cấp danh sách sự kiện thật từ hệ thống
                - Hãy nhắc TÊN CỤ THỂ của 1-3 sự kiện trong danh sách đó vào câu trả lời
                - Nêu thêm địa điểm, ngày hoặc giá vé nếu user hỏi về những thông tin đó
                - Cuối reply LUÔN kết thúc bằng: "Bạn có thể xem thêm ở phần 👇 Gợi ý cho bạn bên dưới nhé!"
                """;
    }

    /**
     * Build user prompt - nhét danh sách event thật vào để AI đề cập tên cụ thể
     */
    private String buildUserPrompt(AiRecommendationRequest request, List<Event> events) {
        StringBuilder sb = new StringBuilder();

        // --- Context khách hàng ---
        if (request.isNewCustomer()) {
            sb.append("Đây là khách mới chưa có lịch sử mua vé.\n\n");
        } else {
            sb.append("=== THÔNG TIN KHÁCH HÀNG ===\n");

            if (request.getPurchaseHistory() != null && !request.getPurchaseHistory().isEmpty()) {
                sb.append("Lịch sử tham gia:\n");
                for (AiRecommendationRequest.PurchaseContext ctx : request.getPurchaseHistory()) {
                    sb.append("  - ").append(ctx.getEventTitle())
                            .append(" | ").append(ctx.getCategory())
                            .append(" | ").append(ctx.getVenue())
                            .append(" | Vé: ").append(ctx.getTicketTypeName())
                            .append(" | Giá: ").append(formatPrice(ctx.getPricePaid()))
                            .append("\n");
                }
            }
            if (request.getPreferredCategories() != null && !request.getPreferredCategories().isEmpty())
                sb.append("Thể loại yêu thích: ").append(String.join(", ", request.getPreferredCategories())).append("\n");
            if (request.getPreferredVenues() != null && !request.getPreferredVenues().isEmpty())
                sb.append("Địa điểm hay lui tới: ").append(String.join(", ", request.getPreferredVenues())).append("\n");
            if (request.getAvgSpentPerEvent() != null)
                sb.append("Chi tiêu trung bình: ").append(formatPrice(request.getAvgSpentPerEvent())).append("\n");
            sb.append("\n");
        }

        // --- Danh sách sự kiện thật từ DB ---
        if (events != null && !events.isEmpty()) {
            sb.append("=== SỰ KIỆN ĐANG CÓ TRÊN EVENTGO (dùng để tư vấn) ===\n");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Event e : events) {
                sb.append("• ").append(e.getTitle());

                if (e.getCategory() != null)
                    sb.append(" | Thể loại: ").append(e.getCategory().getName());
                if (e.getVenue() != null)
                    sb.append(" | Địa điểm: ").append(e.getVenue().getName());
                if (e.getStartDateTime() != null)
                    sb.append(" | Ngày: ").append(e.getStartDateTime().format(fmt));

                // Giá vé thấp nhất
                if (e.getTicketTypes() != null && !e.getTicketTypes().isEmpty()) {
                    if (Boolean.TRUE.equals(e.getIsFree())) {
                        sb.append(" | Giá: Miễn phí");
                    } else {
                        e.getTicketTypes().stream()
                                .map(tt -> tt.getPrice())
                                .filter(Objects::nonNull)
                                .min(BigDecimal::compareTo)
                                .ifPresent(minPrice ->
                                        sb.append(" | Giá từ: ").append(formatPrice(minPrice)));
                    }
                }
                sb.append("\n");
            }
            sb.append("\n");
        }

        // --- Câu hỏi của user ---
        sb.append("=== CÂU HỎI ===\n").append(request.getMessage());

        return sb.toString();
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) return "N/A";
        return String.format("%,.0f VND", price);
    }

    public AiRecommendationResponse generateResponse(AiRecommendationRequest request) {
        System.out.println("=== [Groq] generateResponse | isNewCustomer=" + request.isNewCustomer());

        try {
            // 1. Lấy events từ DB TRƯỚC — để nhét vào prompt
            List<Event> suggestedEvents = recommendationService.getSuggestedEvents(request);
            System.out.println("=== [Groq] suggestedEvents=" + suggestedEvents.size());

            // 2. Gọi Groq với danh sách event thật trong prompt
            String aiReply = callGroqApi(buildSystemPrompt(), buildUserPrompt(request, suggestedEvents));

            AiRecommendationResponse response = new AiRecommendationResponse();
            response.setReply(aiReply);

            // 3. Map events sang DTO để hiển thị card
            List<AiRecommendationResponse.SuggestedEvent> suggestions = new ArrayList<>();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Event event : suggestedEvents) {
                AiRecommendationResponse.SuggestedEvent se = new AiRecommendationResponse.SuggestedEvent();
                se.setEventId(event.getId().longValue());
                se.setTitle(event.getTitle());
                se.setCategory(event.getCategory() != null ? event.getCategory().getName() : "Đa dạng");
                se.setVenue(event.getVenue() != null ? event.getVenue().getName() : "Nhiều địa điểm");
                se.setDate(event.getStartDateTime() != null ? event.getStartDateTime().format(fmt) : "");
                se.setMatchReason(recommendationService.generateMatchReason(event, request));

                // Giá vé thấp nhất
                if (event.getTicketTypes() != null && !event.getTicketTypes().isEmpty()) {
                    if (Boolean.TRUE.equals(event.getIsFree())) {
                        se.setPrice("Miễn phí");
                    } else {
                        event.getTicketTypes().stream()
                                .map(tt -> tt.getPrice())
                                .filter(Objects::nonNull)
                                .min(BigDecimal::compareTo)
                                .ifPresent(minPrice -> {
                                    String currency = event.getCurrency() != null ? event.getCurrency() : "VND";
                                    se.setPrice(String.format("%,.0f %s", minPrice, currency));
                                });
                    }
                }

                suggestions.add(se);
                System.out.println("=== [Groq] Event card: " + event.getTitle() + " | " + se.getPrice());
            }

            response.setSuggestedEvents(suggestions);
            response.setRecommendationType(request.isNewCustomer() ? "popular" : "personalized");

            System.out.println("=== [Groq] Done | reply=" + aiReply.length() + " chars | cards=" + suggestions.size());
            return response;

        } catch (Exception e) {
            System.err.println("=== [Groq] Fatal: " + e.getMessage());
            e.printStackTrace();

            AiRecommendationResponse fallback = new AiRecommendationResponse();
            fallback.setReply("Xin lỗi, em đang gặp sự cố. Vui lòng thử lại sau. 🙏");
            fallback.setSuggestedEvents(new ArrayList<>());
            fallback.setRecommendationType("error");
            return fallback;
        }
    }
}