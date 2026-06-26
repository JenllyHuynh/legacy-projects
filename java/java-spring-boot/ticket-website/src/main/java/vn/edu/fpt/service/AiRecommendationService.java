package vn.edu.fpt.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.dto.AiRecommendationRequest;
import vn.edu.fpt.model.dto.AiRecommendationResponse;
import vn.edu.fpt.model.dto.EventDTO;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.model.entity.Order;
import vn.edu.fpt.model.entity.OrderItem;
import vn.edu.fpt.repository.CustomerRepo;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.repository.OrderRepo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AiRecommendationService {

    private final CustomerRepo customerRepo;
    private final EventRepo eventRepo;
    private final OrderRepo orderRepo;
    private final GeminiService geminiService;

    public AiRecommendationService(CustomerRepo customerRepo, EventRepo eventRepo,
                                   OrderRepo orderRepo, GeminiService geminiService) {
        this.customerRepo = customerRepo;
        this.eventRepo = eventRepo;
        this.orderRepo = orderRepo;
        this.geminiService = geminiService;
    }

    public AiRecommendationResponse processUserMessage(String message, Integer customerId) {
        System.out.println("=== [AI] processUserMessage | customerId=" + customerId + " | msg=" + message);

        Optional<Customer> customerOpt = customerId != null
                ? customerRepo.findById(customerId) : Optional.empty();

        AiRecommendationRequest request = new AiRecommendationRequest();
        request.setMessage(message);
        request.setCustomerId(customerId);

        if (customerOpt.isPresent()) {
            request.setNewCustomer(false);

            // Ưu tiên đơn đã thanh toán, fallback tất cả đơn
            List<Order> orders = orderRepo.findByCustomerIdAndOrderStatus(customerId, "Paid");
            if (orders == null || orders.isEmpty()) {
                orders = orderRepo.findByCustomerId(customerId);
            }
            System.out.println("=== [AI] orders=" + (orders != null ? orders.size() : 0));

            request.setEventHistory(orderRepo.findEventHistoryByCustomerId(customerId));
            request.setPreferredCategories(analyzePreferredCategories(orders));
            request.setPreferredVenues(analyzePreferredVenues(orders));

            // Context chi tiết: từng lần mua (event + địa điểm + loại vé + giá)
            List<AiRecommendationRequest.PurchaseContext> purchaseHistory = buildPurchaseHistory(orders);
            request.setPurchaseHistory(purchaseHistory);

            // Mức chi tiêu trung bình & cao nhất
            if (orders != null && !orders.isEmpty()) {
                BigDecimal total = BigDecimal.ZERO;
                BigDecimal max = BigDecimal.ZERO;
                int count = 0;
                for (Order o : orders) {
                    if (o.getTotalAmount() != null) {
                        total = total.add(o.getTotalAmount());
                        if (o.getTotalAmount().compareTo(max) > 0) max = o.getTotalAmount();
                        count++;
                    }
                }
                if (count > 0) {
                    request.setAvgSpentPerEvent(total.divide(BigDecimal.valueOf(count), 0, RoundingMode.HALF_UP));
                    request.setMaxSpentPerEvent(max);
                }
            }

            System.out.println("=== [AI] categories=" + request.getPreferredCategories());
            System.out.println("=== [AI] avgSpent=" + request.getAvgSpentPerEvent()
                    + " | maxSpent=" + request.getMaxSpentPerEvent());

        } else {
            request.setNewCustomer(true);
            request.setEventHistory(Collections.emptyList());
            request.setPreferredCategories(Collections.emptyList());
            request.setPreferredVenues(Collections.emptyList());
            request.setPurchaseHistory(Collections.emptyList());
            System.out.println("=== [AI] Guest user");
        }

        return geminiService.generateResponse(request);
    }

    /**
     * Build danh sách lịch sử mua vé chi tiết để AI có context thật
     */
    private List<AiRecommendationRequest.PurchaseContext> buildPurchaseHistory(List<Order> orders) {
        List<AiRecommendationRequest.PurchaseContext> result = new ArrayList<>();
        if (orders == null) return result;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                try {
                    Event event = item.getTicketType().getEvent();
                    if (event == null) continue;

                    result.add(new AiRecommendationRequest.PurchaseContext(
                            event.getTitle(),
                            event.getCategory() != null ? event.getCategory().getName() : "Khác",
                            event.getVenue() != null ? event.getVenue().getName() : "Online",
                            item.getTicketType().getName(),
                            item.getUnitPrice(),
                            order.getOrderDate() != null ? order.getOrderDate().format(fmt) : ""
                    ));
                } catch (Exception e) {
                    System.err.println("=== [AI] buildPurchaseHistory error: " + e.getMessage());
                }
            }
        }

        // Giới hạn 10 lần gần nhất để prompt không quá dài
        if (result.size() > 10) result = result.subList(result.size() - 10, result.size());
        return result;
    }

    private List<String> analyzePreferredCategories(List<Order> orders) {
        if (orders == null || orders.isEmpty()) return new ArrayList<>();
        Map<String, Long> count = new HashMap<>();
        for (Order order : orders)
            for (OrderItem item : order.getOrderItems()) {
                Event e = item.getTicketType().getEvent();
                if (e != null && e.getCategory() != null && e.getCategory().getName() != null)
                    count.merge(e.getCategory().getName(), 1L, Long::sum);
            }
        List<Map.Entry<String, Long>> list = new ArrayList<>(count.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(3, list.size()); i++) result.add(list.get(i).getKey());
        return result;
    }

    private List<String> analyzePreferredVenues(List<Order> orders) {
        if (orders == null || orders.isEmpty()) return new ArrayList<>();
        Map<String, Long> count = new HashMap<>();
        for (Order order : orders)
            for (OrderItem item : order.getOrderItems()) {
                Event e = item.getTicketType().getEvent();
                if (e != null && e.getVenue() != null && e.getVenue().getName() != null)
                    count.merge(e.getVenue().getName(), 1L, Long::sum);
            }
        List<Map.Entry<String, Long>> list = new ArrayList<>(count.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(3, list.size()); i++) result.add(list.get(i).getKey());
        return result;
    }

    public List<Event> getSuggestedEvents(AiRecommendationRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String status = "Published";
        List<EventDTO> dtos = new ArrayList<>();

        try {
            if (!request.isNewCustomer()
                    && request.getPreferredCategories() != null
                    && !request.getPreferredCategories().isEmpty()) {
                String topCategory = request.getPreferredCategories().get(0);
                System.out.println("=== [AI] Query by category: " + topCategory);
                dtos = eventRepo.getListEventByCategory(now, status, topCategory, PageRequest.of(0, 5));
                if (dtos.size() < 3) {
                    // Bổ sung trending nếu không đủ
                    dtos = eventRepo.getListTrendingEvents(now, status, PageRequest.of(0, 5));
                }
            } else {
                System.out.println("=== [AI] Query trending events");
                dtos = eventRepo.getListTrendingEvents(now, status, PageRequest.of(0, 5));
            }
            System.out.println("=== [AI] DTOs=" + dtos.size());
        } catch (Exception e) {
            System.err.println("=== [AI] getSuggestedEvents error: " + e.getMessage());
            return new ArrayList<>();
        }

        List<Event> events = new ArrayList<>();
        for (EventDTO dto : dtos) {
            if (dto != null && dto.getId() != null) {
                try {
                    eventRepo.findById(dto.getId()).ifPresent(events::add);
                } catch (Exception ex) {
                    System.err.println("=== [AI] findById error: " + dto.getId());
                }
            }
        }
        System.out.println("=== [AI] Events ready: " + events.size());
        return events;
    }

    public String generateMatchReason(Event event, AiRecommendationRequest request) {
        if (event == null) return "";
        if (request.isNewCustomer()) return "🔥 Sự kiện hot nhất hiện nay";

        List<String> reasons = new ArrayList<>();

        if (event.getCategory() != null && request.getPreferredCategories() != null
                && request.getPreferredCategories().contains(event.getCategory().getName()))
            reasons.add("phù hợp thể loại bạn thích");

        if (event.getVenue() != null && request.getPreferredVenues() != null
                && request.getPreferredVenues().contains(event.getVenue().getName()))
            reasons.add("tại địa điểm quen thuộc");

        if (request.getEventHistory() != null
                && request.getEventHistory().contains(event.getId().longValue()))
            reasons.add("bạn đã tham gia trước đây");

        // So giá vé rẻ nhất với mức chi tiêu trung bình
        if (request.getAvgSpentPerEvent() != null && event.getTicketTypes() != null) {
            event.getTicketTypes().stream()
                    .map(tt -> tt.getPrice())
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .ifPresent(minPrice -> {
                        if (minPrice.compareTo(request.getAvgSpentPerEvent()) <= 0)
                            reasons.add("giá phù hợp ngân sách của bạn");
                    });
        }

        return reasons.isEmpty() ? "✨ Đề xuất dành riêng cho bạn" : "✨ " + String.join(", ", reasons);
    }
}