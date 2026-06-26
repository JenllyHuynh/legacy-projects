package vn.edu.fpt.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.service.PaymentService;

@RestController
@RequestMapping("/api/revenue")
public class RevenueApiController {

    @Value("${revenue.api.key}")
    private String REVENUE_KEY;

    private final PaymentService paymentService;

    public RevenueApiController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{eventId}/m/totalRevenue")
    public ResponseEntity<?> getTotalRevenueByMonth(@PathVariable("eventId") String eventId) {
            System.err.println("Đã gọi revenue api m");
            return ResponseEntity.ok(paymentService.getRevenueByMonth(Integer.parseInt(eventId)));
    }

    @GetMapping("/{eventId}/d/totalRevenue")
    public ResponseEntity<?> getTotalRevenueByDate(@PathVariable("eventId") String eventId) {
        System.err.println("Đã gọi revenue api d");
        return ResponseEntity.ok(paymentService.getRevenueByDate(Integer.parseInt(eventId)));
    }

}
