package vn.edu.fpt.model.dto;

import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.model.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventRevenueDTO {
    private BigDecimal totalRevenue;
    private MonthDTO month;
    private int day;

    public EventRevenueDTO() {
    }

    public EventRevenueDTO(BigDecimal totalRevenue, MonthDTO month) {
        this.totalRevenue = totalRevenue;
        this.month = month;
    }

    public EventRevenueDTO(BigDecimal totalRevenue, int day) {
        this.totalRevenue = totalRevenue;
        this.day = day;
    }

    public EventRevenueDTO(BigDecimal totalRevenue, MonthDTO month, int day) {
        this.totalRevenue = totalRevenue;
        this.month = month;
        this.day = day;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public MonthDTO getMonth() {
        return month;
    }

    public void setMonth(MonthDTO month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "RevenueDTO{" +
                "totalRevenue=" + totalRevenue +
                ", month=" + month.getString() +
                ", day=" + day +
                '}';
    }
}
