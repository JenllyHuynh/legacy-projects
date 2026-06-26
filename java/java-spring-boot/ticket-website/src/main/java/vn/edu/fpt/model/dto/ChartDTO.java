package vn.edu.fpt.model.dto;

import vn.edu.fpt.model.entity.Order;

import java.util.ArrayList;
import java.util.List;

public class ChartDTO {

    private DashboardDTO kpi;

//    Line Chart Revenue
    private List<String> labels = new ArrayList<>();
    private List<String> date = new ArrayList<>();
    private List<Double> revenue = new ArrayList<>();
//    Top event Chart
    private List<String> eventNames = new ArrayList<>();
    private List<Double> revenues = new ArrayList<>();


//    Growth User chart
    List<Long> newUsers = new ArrayList<>();
    List<Long> returnUsers = new ArrayList<>();

//    Category chart
    private List<String> nameCate = new ArrayList<>();
    private List<Long> countByCate = new ArrayList<>();

    private List<Order> transactions;

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Double> getRevenue() {
        return revenue;
    }

    public void setRevenue(List<Double> revenue) {
        this.revenue = revenue;
    }

    public List<String> getEventNames() {
        return eventNames;
    }

    public void setEventNames(List<String> eventNames) {
        this.eventNames = eventNames;
    }

    public List<Double> getRevenues() {
        return revenues;
    }

    public void setRevenues(List<Double> revenues) {
        this.revenues = revenues;
    }


    public List<Long> getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(List<Long> newUsers) {
        this.newUsers = newUsers;
    }

    public List<Long> getReturnUsers() {
        return returnUsers;
    }

    public void setReturnUsers(List<Long> returnUsers) {
        this.returnUsers = returnUsers;
    }

    public List<String> getNameCate() {
        return nameCate;
    }

    public void setNameCate(List<String> nameCate) {
        this.nameCate = nameCate;
    }

    public List<Long> getCountByCate() {
        return countByCate;
    }

    public void setCountByCate(List<Long> countByCate) {
        this.countByCate = countByCate;
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public DashboardDTO getKpi() {
        return kpi;
    }

    public void setKpi(DashboardDTO kpi) {
        this.kpi = kpi;
    }

    public List<Order> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Order> transactions) {
        this.transactions = transactions;
    }
}
