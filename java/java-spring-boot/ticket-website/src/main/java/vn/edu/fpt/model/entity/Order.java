package vn.edu.fpt.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderId", nullable = false)
    private Integer id;

    // Thêm mối quan hệ với Customer để làm A.i recommend
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "CustomerId", nullable = false)  // Khóa ngoại đến Customer
//    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CustomerId", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new LinkedHashSet<>();

    @Nationalized
    @ColumnDefault("'Pending'")
    @Column(name = "OrderStatus", length = 20)
    private String orderStatus;

    @ColumnDefault("getdate()")
    @Column(name = "OrderDate")
    private LocalDateTime orderDate;

    @Column(name = "TotalAmount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order")
    private Set<Payment> payments = new LinkedHashSet<>();

    @Size(max = 200)
    @Nationalized
    @Column(name = "country", length = 200)
    private String country;

    @Size(max = 200)
    @Nationalized
    @Column(name = "city", length = 200)
    private String city;

    @Size(max = 200)
    @Nationalized
    @Column(name = "ward", length = 200)
    private String ward;

    @Size(max = 225)
    @Nationalized
    @Column(name = "streetAddress", length = 225)
    private String streetAddress;

    @Size(max = 225)
    @Nationalized
    @Column(name = "OrderInfo", length = 225)
    private String OrderInfo;

    public String getOrderInfo() {
        return OrderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        OrderInfo = orderInfo;
    }

    public DiscountCode getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(DiscountCode discountCode) {
        this.discountCode = discountCode;
    }


    @ManyToOne
    @JoinColumn(name = "DiscountCodeId")
    private DiscountCode discountCode;

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Set<Payment> getPayments() {
        return payments;
    }

    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer +
                ", orderItems=" + orderItems +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", discountCode=" + discountCode +
                '}';
    }
}