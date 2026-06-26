package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.dto.ChartDTO;
import vn.edu.fpt.model.dto.DashboardDTO;
import vn.edu.fpt.service.DashboardService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class DashboardController {

    private DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

//    @GetMapping("/dashboard")
//    public DashboardDTO getDashboard() {
//        return dashboardService.getDashboard();
//    }

    @GetMapping("/dashboard")
    public ChartDTO getDashboard(
            @RequestParam String from,
            @RequestParam String to
    ) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        return dashboardService.buildDashboard(fromDate, toDate);
    }

//    @GetMapping("/top-events")
//    public ChartDTO getTopEvents(
//            @RequestParam(defaultValue = "5") int limit
//    ) {
//        return dashboardService.getTopEvents(limit);
//    }
//
//    @GetMapping("/customer-growth")
//    public ChartDTO getCustomerGrowth() {
//        return dashboardService.getCustomerGrowth();
//    }
//
//    @GetMapping("/event-categories")
//    public ChartDTO getCategoryReport() {
//        return dashboardService.getCategoryReport();
//    }
//
//    @GetMapping("/revenue")
//    public ChartDTO getRevenue(
//            @RequestParam String from,
//            @RequestParam String to
//    ) {
//        LocalDate fromDate = LocalDate.parse(from);
//        LocalDate toDate = LocalDate.parse(to);
//
//        return dashboardService.getRevenue(fromDate, toDate);
//    }




}
