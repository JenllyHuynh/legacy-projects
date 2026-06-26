package vn.edu.fpt.model.dto;

public class DashboardDTO {
    public double totalRevenue;
    public double lastYearRevenue;
    public double revenueGrowth;

    public long totalEvents;
    public long lastMonthEvents;
    public double eventGrowth;

    public long totalUsers;
    public long newUsers;
    public double userGrowth;

    public long ticketsThisMonth;
    public long lastMonthTickets;
    public double ticketGrowth;

    public DashboardDTO() {
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getLastYearRevenue() {
        return lastYearRevenue;
    }

    public void setLastYearRevenue(double lastYearRevenue) {
        this.lastYearRevenue = lastYearRevenue;
    }

    public double getRevenueGrowth() {
        return revenueGrowth;
    }

    public void setRevenueGrowth(double revenueGrowth) {
        this.revenueGrowth = revenueGrowth;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public long getLastMonthEvents() {
        return lastMonthEvents;
    }

    public void setLastMonthEvents(long lastMonthEvents) {
        this.lastMonthEvents = lastMonthEvents;
    }

    public double getEventGrowth() {
        return eventGrowth;
    }

    public void setEventGrowth(double eventGrowth) {
        this.eventGrowth = eventGrowth;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(long newUsers) {
        this.newUsers = newUsers;
    }

    public double getUserGrowth() {
        return userGrowth;
    }

    public void setUserGrowth(double userGrowth) {
        this.userGrowth = userGrowth;
    }

    public long getTicketsThisMonth() {
        return ticketsThisMonth;
    }

    public void setTicketsThisMonth(long ticketsThisMonth) {
        this.ticketsThisMonth = ticketsThisMonth;
    }

    public long getLastMonthTickets() {
        return lastMonthTickets;
    }

    public void setLastMonthTickets(long lastMonthTickets) {
        this.lastMonthTickets = lastMonthTickets;
    }

    public double getTicketGrowth() {
        return ticketGrowth;
    }

    public void setTicketGrowth(double ticketGrowth) {
        this.ticketGrowth = ticketGrowth;
    }


}
