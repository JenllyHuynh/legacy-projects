package vn.edu.fpt.util.event;


public class PaymentCompletedEvent {
    private final Integer customerId;
    private final Integer orderId;
    private final Integer paymentId;
    private final boolean success;

    public PaymentCompletedEvent(Integer customerId, Integer orderId, Integer paymentId, boolean success) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.success = success;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public boolean isSuccess() {
        return success;
    }
}
