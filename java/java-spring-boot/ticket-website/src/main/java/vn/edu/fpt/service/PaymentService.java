package vn.edu.fpt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.dto.EventRevenueDTO;
import vn.edu.fpt.model.dto.MonthDTO;
import vn.edu.fpt.model.entity.*;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.repository.PaymentRepo;
import vn.edu.fpt.util.PageSetting;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepo paymentRepo;
    private final EventRepo eventRepo;

    public PaymentService(PaymentRepo paymentRepo,
                          EventRepo eventRepo) {
        this.paymentRepo = paymentRepo;
        this.eventRepo = eventRepo;
    }

//    public PaymentService() {
//    }

    public void save(Payment payment) {
        paymentRepo.save(payment);
    }

    public Optional<Payment> getPaymentById(int id) {
        return paymentRepo.findById(id);
    }

    public Optional<Payment> getPaymentByTransactionIdPayment(String transactionId) {
        return paymentRepo.findByTransactionId(transactionId);
    }

    public BigDecimal getTotlAmount(int eventId) {
        return paymentRepo.getTotalAmount(eventId);
    }

    public List<EventRevenueDTO> getRevenueByMonth(int eventId) {
        List<EventRevenueDTO> revenue = new ArrayList<>();
        Event event = eventRepo.getEventById(eventId);
        try {
            event.getCreatedAt().getMonth().getValue();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        for (int i = event.getCreatedAt().getMonth().getValue();
             i <= event.getRegistrationDeadline().getMonth().getValue();
             i++) {
            System.err.println(Month.of(i).name() + ": " + paymentRepo.getTotalAmountByMonth(eventId, i));
            revenue.add(new EventRevenueDTO(paymentRepo.getTotalAmountByMonth(eventId, i), new MonthDTO(i, Month.of(i).name())));
        }

        return revenue;
    }

    public List<EventRevenueDTO> getRevenueByDate(int eventId) {
        List<EventRevenueDTO> revenue = new ArrayList<>();
        Event event = eventRepo.getEventById(eventId);
        try {
            event.getCreatedAt().getMonth().getValue();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        for (int i = event.getCreatedAt().getMonth().getValue();
             i <= event.getRegistrationDeadline().getMonth().getValue();
             i++) {
            for (int j = event.getCreatedAt().getDayOfMonth();
                 j <= YearMonth.of(event.getCreatedAt().getYear(), event.getCreatedAt().getMonth()).lengthOfMonth();
                 j++) {
                if (paymentRepo.getTotalAmountByDate(eventId, j, i) != null) {
                    revenue.add(new EventRevenueDTO(paymentRepo.getTotalAmountByDate(eventId, j, i), new MonthDTO(i, Month.of(i).name()), j));
                }
            }
        }
        for (EventRevenueDTO e : revenue) {
            System.err.println(e.toString());
        }
        return revenue;
    }


    public Page<Payment> getAllPaymentHistory(int customerId, String status, int page) {
        Pageable pageable = PageRequest.of(
                page,
                PageSetting.SIZE_OF_EACH_ORDER_HISTORY,
                Sort.by("createdAt").descending()
        );
        return paymentRepo.getAllPaymentByCustomerId(customerId, status, pageable);
    }

    public double calculateDiscount(List<CartItem> items,
                                    DiscountCode discountCode) {

        if (discountCode == null) {
            return 0;
        }

        double eventSubtotal = 0;

        // 1️⃣ chỉ cộng tiền item thuộc event của discount
        for (CartItem item : items) {

            if (item.getTicketType()
                    .getEvent()
                    .getId()
                    .equals(discountCode.getEvent().getId())) {

                eventSubtotal +=
                        item.getTicketType().getPrice().doubleValue()
                                * item.getQuantity();
            }
        }

        // không có item hợp lệ
        if (eventSubtotal == 0) {
            return 0;
        }

        // 2️⃣ apply discount
        if ("Percentage".equals(discountCode.getDiscountType())) {

            return Math.max(
                    eventSubtotal *
                            discountCode.getDiscountValue().doubleValue() / 100,
                    0
            );

        } else { // Fixed amount

            return Math.min(
                    discountCode.getDiscountValue().doubleValue(),
                    eventSubtotal // tránh âm tiền
            );
        }
    }

    public double calculateDiscountOrderItems(List<OrderItem> items,
                                    DiscountCode discountCode) {

        if (discountCode == null) {
            return 0;
        }

        double eventSubtotal = 0;

        // 1️⃣ chỉ cộng tiền item thuộc event của discount
        for (OrderItem item : items) {

            if (item.getTicketType()
                    .getEvent()
                    .getId()
                    .equals(discountCode.getEvent().getId())) {

                eventSubtotal +=
                        item.getTicketType().getPrice().doubleValue()
                                * item.getQuantity();
            }
        }

        // không có item hợp lệ
        if (eventSubtotal == 0) {
            return 0;
        }

        // 2️⃣ apply discount
        if ("Percentage".equals(discountCode.getDiscountType())) {

            return Math.max(
                    eventSubtotal *
                            discountCode.getDiscountValue().doubleValue() / 100,
                    0
            );

        } else { // Fixed amount

            return Math.min(
                    discountCode.getDiscountValue().doubleValue(),
                    eventSubtotal // tránh âm tiền
            );
        }
    }
}
