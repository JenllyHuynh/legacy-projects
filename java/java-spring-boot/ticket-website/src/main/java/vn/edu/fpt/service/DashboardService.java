package vn.edu.fpt.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.dto.ChartDTO;
import vn.edu.fpt.model.dto.DashboardDTO;
import vn.edu.fpt.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private OrderRepo orderRepo;

    private EventRepo eventRepo;

    private CustomerRepo customerRepo;

    private TicketRepo ticketRepo;

    private OrderItemRepo orderItemRepo;

    private OrderService orderService;

    public DashboardService(OrderRepo orderRepo, EventRepo eventRepo,
                            CustomerRepo customerRepo, TicketRepo ticketRepo, OrderItemRepo orderItemRepo,
                            OrderService orderService
    ) {
        this.orderRepo = orderRepo;
        this.eventRepo = eventRepo;
        this.customerRepo = customerRepo;
        this.ticketRepo = ticketRepo;
        this.orderItemRepo = orderItemRepo;
        this.orderService = orderService;
    }

//    public DashboardDTO getDashboard() {
//
//        DashboardDTO dto = new DashboardDTO();
//
//        dto.setTotalRevenue(orderRepo.getTotalRevenue());
//        LocalDate now = LocalDate.now();
//        int currentMonth = now.getMonthValue();
//        int currentYear = now.getYear();
//
//        // Tháng trước
//        int lastMonth = currentMonth - 1;
//        int yearOfLastMonth = currentYear;
//
//        if (lastMonth == 0) {
//            lastMonth = 12;
//            yearOfLastMonth = currentYear - 1;
//        }
//
//        // Query DB
//        double currentRevenue =
//                orderRepo.getRevenueByMonth(currentMonth, currentYear);
//
//        double lastRevenue =
//                orderRepo.getRevenueByMonth(lastMonth, yearOfLastMonth);
//
//        // Gán DTO
//        dto.setCurrentMonthRevenue(currentRevenue);
//        dto.setLastMonthRevenue(lastRevenue);
//
//        // Tính % tăng trưởng
//        if (lastRevenue == 0) {
//            dto.setGrowthRate(100);
//        } else {
//            dto.setGrowthRate(((currentRevenue - lastRevenue) / lastRevenue) * 100);
//        }
//
//        dto.setTotalEvents(eventRepo.countEvents());
//        dto.setTotalCustomers(customerRepo.countCustomers());
//        dto.setTicketsSold(ticketRepo.countTickets());
//
//        return dto;
//    }

    public DashboardDTO getDashboard(LocalDate from, LocalDate to) {
        DashboardDTO dto = new DashboardDTO();

        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);

        // ===== Tính khoảng thời gian trước =====
        long days = ChronoUnit.DAYS.between(from, to) + 1;

        LocalDate prevFromDate = from.minusDays(365);
        LocalDate prevToDate = to.minusDays(365);

        LocalDateTime prevFrom = prevFromDate.atStartOfDay();
        LocalDateTime prevTo = prevToDate.atTime(23, 59, 59);
        // ===== Revenue =====
        dto.totalRevenue = orderRepo.sumRevenueBetween(fromDate, toDate);
        dto.lastYearRevenue = orderRepo.sumRevenuePrevious(prevFrom, prevTo);
        dto.revenueGrowth = calculateGrowth(dto.totalRevenue, dto.lastYearRevenue);

        // ===== Events =====
        dto.totalEvents = eventRepo.countEventsBetween(fromDate, toDate);
        dto.lastMonthEvents = eventRepo.countEventsBetween(prevFrom, prevTo);
        dto.eventGrowth = calculateGrowth(dto.totalEvents, dto.lastMonthEvents);

        // ===== Users =====
        dto.totalUsers = customerRepo.totalUser();
        dto.newUsers = customerRepo.countNewThisMonth();
        dto.userGrowth = calculateGrowth(dto.newUsers, dto.totalUsers - dto.newUsers);

        // ===== Tickets =====
        dto.ticketsThisMonth = orderRepo.countTicketsBetween(fromDate, toDate);
        dto.lastMonthTickets = orderRepo.countTicketsBetween(prevFrom, prevTo);
        dto.ticketGrowth = calculateGrowth(dto.ticketsThisMonth, dto.lastMonthTickets);

        return dto;
    }

    private double calculateGrowth(double current, double previous) {
        if (previous == 0) return 100;
        return ((current - previous) / previous) * 100;
    }


    public ChartDTO getTopEvents(int limit, LocalDate from, LocalDate to) {

        Pageable pageable = PageRequest.of(0, limit);
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);

        List<Object[]> result = orderItemRepo.getTopEventsBetween(fromDate, toDate, pageable);

        List<String> names = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Object[] row : result) {
            names.add((String) row[0]); // event title
            values.add(((Number) row[1]).doubleValue()); // revenue
        }

        ChartDTO dto = new ChartDTO();
        dto.setEventNames(names);
        dto.setRevenues(values);

        return dto;
    }

    public ChartDTO getCustomerGrowth(LocalDate from, LocalDate to) {

        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);
        List<Object[]> result = orderRepo.getCustomerGrowth(fromDate, toDate);

        List<String> labels = new ArrayList<>();
        List<Long> newUsers = new ArrayList<>();
        List<Long> returningUsers = new ArrayList<>();

        int i = 1;

        for (Object[] row : result) {
            labels.add("W" + i++);
            newUsers.add(((Number) row[1]).longValue());
            returningUsers.add(((Number) row[2]).longValue());
        }

        ChartDTO dto = new ChartDTO();
        dto.setLabels(labels);
        dto.setNewUsers(newUsers);
        dto.setReturnUsers(returningUsers);

        return dto;
    }

    public ChartDTO getCategoryReport(LocalDate from, LocalDate to) {
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);
        List<Object[]> result = eventRepo.countEventsByCategoryBetween(fromDate, toDate);
        List<String> names = new ArrayList<>();
        List<Long> countByCate = new ArrayList<>();

        for (Object[] o : result) {
            names.add((String) o[0]);
            countByCate.add(((Number) o[1]).longValue());
        }
        ChartDTO dto = new ChartDTO();
        dto.setNameCate(names);
        dto.setCountByCate(countByCate);

        return dto;
    }


    public ChartDTO getRevenue(LocalDate from, LocalDate to) {

        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);

        List<Object[]> results = orderRepo.getRevenueByDate(fromDate, toDate);

        List<String> date = new ArrayList<>();
        List<Double> revenue = new ArrayList<>();
        for (Object[] o : results) {
            date.add((String) o[0].toString());
            Number value = (Number) o[1]; // nhận mọi kiểu số
            revenue.add(value.doubleValue());
        }

        ChartDTO dto = new ChartDTO();
        dto.setDate(date);
        dto.setRevenue(revenue);

        return dto;
    }

    public ChartDTO buildDashboard(LocalDate from, LocalDate to) {

        ChartDTO res = new ChartDTO();

        // ===== KPI =====
        res.setKpi(getDashboard(from, to));

        // ===== Charts =====
        ChartDTO revenueSystem = getRevenue(from, to);
//        res.setLabels(revenueSystem.getLabels());
        res.setDate(revenueSystem.getDate());
        res.setRevenue(revenueSystem.getRevenue());
        ChartDTO eventTop = getTopEvents(5, from , to);
        res.setEventNames(eventTop.getEventNames());
        res.setRevenues(eventTop.getRevenues());
        ChartDTO user = getCustomerGrowth(from, to);
        res.setLabels(user.getLabels());
        res.setNewUsers(user.getNewUsers());
        res.setReturnUsers(user.getReturnUsers());
        ChartDTO cate = getCategoryReport(from, to);
        res.setNameCate(cate.getNameCate());
        res.setCountByCate(cate.getCountByCate());

        // ===== Transactions =====
//        res.setTransactions(orderService.getRecentTransactions());

        return res;
    }

}
