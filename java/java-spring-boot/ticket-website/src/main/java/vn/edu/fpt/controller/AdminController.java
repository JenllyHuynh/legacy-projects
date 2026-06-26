package vn.edu.fpt.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.dto.EventStaffAssignmentDTO;
import vn.edu.fpt.model.entity.*;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.service.EventStaffAssignmentService;
import vn.edu.fpt.service.RoleService;
import vn.edu.fpt.service.StaffService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import vn.edu.fpt.model.dto.CategoryDTO;
import vn.edu.fpt.model.dto.CategoryResponseDTO;
import vn.edu.fpt.service.CategoryService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.model.dto.DashboardDTO;
import vn.edu.fpt.model.dto.RevenueDTO;
import vn.edu.fpt.service.DashboardService;
import vn.edu.fpt.service.OrderService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.IntStream;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EventStaffAssignmentService eventStaffAssignmentService;
    private final RoleService roleService;
    private final EventService eventService;
    private final StaffService staffService;
    private final CategoryService categoryService;

    public AdminController(EventStaffAssignmentService eventStaffAssignmentService,
                           RoleService roleService,
                           EventService eventService,
                           StaffService staffService,
                           CategoryService categoryService) {
        this.eventStaffAssignmentService = eventStaffAssignmentService;
        this.eventService = eventService;
        this.roleService = roleService;
        this.staffService = staffService;
        this.categoryService = categoryService;
    }

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private OrderService orderService;


    @GetMapping
    public String adminDashboard() {
        return "redirect:/admin/system-revenue";
    }

    @GetMapping("/system-revenue")
    public String adminSystemRevenue(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                     Model model) {

        // default = 7 ngày gần nhất
//        LocalDate to = LocalDate.now();
//        LocalDate from = to.minusDays(365);
        if (from == null || to == null) {
            to = LocalDate.now();
            from = to.minusDays(7);
        }
        LocalDate prevFromDate = from.minusDays(365);
        LocalDate prevToDate = to.minusDays(365);
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);
        LocalDateTime prevFrom = prevFromDate.atStartOfDay();
        LocalDateTime prevTo = prevToDate.atTime(23, 59, 59);

        DashboardDTO data = dashboardService.getDashboard(from, to);
        List<RevenueDTO> transactions = orderService.getRevenueData();
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("preFrom", prevFrom);
        model.addAttribute("prevTo", prevTo);

        model.addAttribute("data", data);
        model.addAttribute("transactions", transactions);

        model.addAttribute("fromDate", from);
        model.addAttribute("toDate", to);
        return "admin/system-revenue";
    }

    @GetMapping("/transactions")
    public String viewAllTransactions(Model model) {

        List<RevenueDTO> transactions = orderService.getRevenueData();

        model.addAttribute("transactions", transactions);

        return "admin/transactions"; // file HTML mới
    }

    @GetMapping("/export-excel")
    public void exportExcel(HttpServletResponse response) throws IOException {

        // 1. Setup response
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=revenue.xlsx");

        // 2. Workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Revenue");

        // 3. Header
        String[] columns = {
                "OrderID", "CustomerID", "CustomerName",
                "EventID", "EventName", "Amount",
                "Status", "PaidAt", "PaymentStatus"
        };

        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columns.length; i++) {
            headerRow.createCell(i).setCellValue(columns[i]);
        }
        System.out.println("aksdfg");
        // 4. Data
        List<RevenueDTO> data = orderService.getRevenueData();
        System.out.println("aksdfg1");
        int rowIdx = 1;

        for (RevenueDTO dto : data) {

            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(dto.getOrderId());
            row.createCell(1).setCellValue(dto.getCustomerId());
            row.createCell(2).setCellValue(dto.getCustomerName());

            row.createCell(3).setCellValue(dto.getEventId());
            row.createCell(4).setCellValue(dto.getEventName());

            row.createCell(5).setCellValue(dto.getAmount());
            row.createCell(6).setCellValue(dto.getStatus());
            if (dto.getPaidAt() == null) {
                row.createCell(7).setCellValue("No Data!");
            } else {
                row.createCell(7).setCellValue(dto.getPaidAt().toString());
            }
            row.createCell(8).setCellValue(
                    dto.getPaymentStatus() != null ? dto.getPaymentStatus() : "N/A"
            );
        }
        System.out.println("aksdfg2");
        // 5. Auto size
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 6. Write file
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    @GetMapping("/role-management")
    public String roleMngView() {
        return "admin/manage-role/role";
    }

    @GetMapping("/customer-management")
    public String customerMngView() {
        return "admin/manage-user/customer";
    }

    /**
     * Lấy email từ principal (hỗ trợ cả UserDetails và OAuth2User)
     */
    private String getEmailFromPrincipal(Object principal) {
        if (principal == null) {
            System.out.println("Principal is null!");
            return null;
        }

        System.out.println("Principal class: " + principal.getClass().getName());

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            System.out.println("Email from UserDetails: " + email);
            return email;
        } else if (principal instanceof OAuth2User) {
            String email = ((OAuth2User) principal).getAttribute("email");
            System.out.println("Email from OAuth2User: " + email);
            return email;
        }

        System.out.println("Unknown principal type: " + principal.getClass());
        return null;
    }

    @GetMapping("/assignEventStaff")
    public String viewEventStaff(Model model,
                                 HttpServletRequest request,
                                 @ModelAttribute("keyword") String keyword,
                                 @ModelAttribute("filter") String filter,
                                 @RequestParam(name = "page", required = false) Integer page) {
        String url = request.getRequestURI();
        if (filter.isEmpty()) {
            filter = "all";
        }

        // Phân trang
        List<Integer> viewPage = new ArrayList<>();
        Page<EventStaffAssignment> eventStaffAssignmentPages;
        viewPage.add(2);
        viewPage.add(3);
        viewPage.add(4);
        int pageIndex = 0;
        eventStaffAssignmentPages = eventStaffAssignmentService.findEventStaffsWithCriteria(filter, keyword.trim(), 0);

        if (page != null) {
            if (page > eventStaffAssignmentPages.getTotalPages()) page = eventStaffAssignmentPages.getTotalPages();
            pageIndex = page - 1;
            pageIndex = page - 1;
            if (pageIndex < 0) {
                pageIndex = 0;
            }
            eventStaffAssignmentPages = eventStaffAssignmentService.findEventStaffsWithCriteria(filter, keyword.trim(), pageIndex);
            System.err.println(pageIndex);
            if (eventStaffAssignmentPages.getTotalPages() > 5) {
                if (pageIndex < 3) {
                    model.addAttribute("after", true);
                } else if (pageIndex < eventStaffAssignmentPages.getTotalPages() - 2) {
                    viewPage.set(0, page - 1);
                    viewPage.set(1, page);
                    viewPage.set(2, page + 1);
                    model.addAttribute("after", true);
                    model.addAttribute("before", true);
                } else {
                    viewPage.set(0, eventStaffAssignmentPages.getTotalPages() - 3);
                    viewPage.set(1, eventStaffAssignmentPages.getTotalPages() - 2);
                    viewPage.set(2, eventStaffAssignmentPages.getTotalPages() - 1);
                    model.addAttribute("before", true);
                }
            } else {
                viewPage = IntStream.rangeClosed(2, eventStaffAssignmentPages.getTotalPages() - 1)
                        .boxed()
                        .toList();
            }
            model.addAttribute("currentPage", page);
            model.addAttribute("viewPages", viewPage);
        } else {
            viewPage = IntStream.rangeClosed(2, eventStaffAssignmentPages.getTotalPages() - 1)
                    .boxed()
                    .toList();
            model.addAttribute("currentPage", 1);
            model.addAttribute("viewPages", viewPage);
        }
        if (eventStaffAssignmentPages.getTotalPages() > 1) {
            model.addAttribute("lastPage", eventStaffAssignmentPages.getTotalPages());
        }

        model.addAttribute("hasNext", eventStaffAssignmentPages.hasNext());
        model.addAttribute("hasPrevious", eventStaffAssignmentPages.hasPrevious());

        if (!keyword.isEmpty()) {
            url += "?keyword=" + keyword;
        }
        if (url.length() == request.getRequestURI().length()) {
            url += "?filter=" + filter;
        } else {
            url += "&filter=" + filter;
        }


        EventStaffAssignmentDTO newEventStaff = new EventStaffAssignmentDTO();
        List<Event> events = eventService.getAll();
        List<Role> roles = roleService.getAll();
        List<Staff> staffs = staffService.getAllStaff();
        model.addAttribute("url", url);
        model.addAttribute("keyword", keyword);
        model.addAttribute("filter", filter);
        model.addAttribute("eventStaffAssignments", eventStaffAssignmentPages);
        model.addAttribute("totalElements", eventStaffAssignmentPages.getTotalElements());
        model.addAttribute("totalElementsOfPage", eventStaffAssignmentPages.getNumberOfElements());
        model.addAttribute("newEventStaff", newEventStaff);
        model.addAttribute("events", events);
        model.addAttribute("roles", roles);
        model.addAttribute("staffs", staffs);
        return "admin/assignEventStaff/list";
    }

    @PostMapping("/assignEventStaff/save")
    public String saveEventStaff(@ModelAttribute("newEventStaff") EventStaffAssignmentDTO newEventStaff,
                                 @ModelAttribute("action") String action,
                                 @AuthenticationPrincipal Object principal,
                                 RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        try {
            String email = getEmailFromPrincipal(principal);
            Staff staff = staffService.getStaffByEmail(email);
            if (staff == null) {
                return "redirect:/admin/assignEventStaff";
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String role = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("");
            System.err.println(role);
            System.out.println(newEventStaff.toString());
            if (role.equalsIgnoreCase("role_admin")) {
                System.err.println("Đủ quyền");
                eventStaffAssignmentService.save(newEventStaff, staff.getId());
            } else {
                System.err.println("Không đủ quyền");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/assignEventStaff";
    }

    @PostMapping(value = "/assignEventStaff/deactive")
    public String deactivateEventStaff(@RequestParam("id") String id) {
        eventStaffAssignmentService.deactiveEventStaff(id);
        return "redirect:/admin/assignEventStaff";
    }

    @GetMapping("/assignEventStaff/delete")
    public String deletaEventStaff(@RequestParam("id") String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");
        if (role.equalsIgnoreCase("role_admin")) {
            eventStaffAssignmentService.deleteEventStaff(id);
        } else {
            System.err.println("Bạn không có quyền xóa!");
        }
        return "redirect:/admin/assignEventStaff";
    }

    @GetMapping("/staff-management")
    public String staffMngView() {
        return "redirect:/admin/staff";
    }

    @GetMapping("/assign-management")
    public String AssignmentStaff() {
        return "admin/manage-user/event-assignments";
    }

    // ── Category Management ──────────────────────────────────────────

    @GetMapping("/category-management")
    public String categoryMngView(Model model) {
        List<CategoryResponseDTO> categories = categoryService.getAllCategoryResponse();
        long unusedCount = categories.stream()
                .filter(c -> c.getEventCount() == 0)
                .count();

        model.addAttribute("categories", categories);
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("unusedCount", unusedCount);
        model.addAttribute("categoryDTO", new CategoryDTO());
        return "admin/category";
    }

    @PostMapping("/category-management/create")
    public String createCategory(
            @ModelAttribute("categoryDTO") CategoryDTO dto,
            RedirectAttributes redirectAttributes) {
        String error = categoryService.createCategory(dto);
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Category created successfully");
        }
        return "redirect:/admin/category-management";
    }

    @PostMapping("/category-management/update/{id}")
    public String updateCategory(
            @PathVariable Integer id,
            @ModelAttribute("categoryDTO") CategoryDTO dto,
            RedirectAttributes redirectAttributes) {
        String error = categoryService.updateCategory(id, dto);
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully");
        }
        return "redirect:/admin/category-management";
    }

    @PostMapping("/category-management/delete/{id}")
    public String deleteCategory(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        String error = categoryService.deleteCategory(id);
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully");
        }
        return "redirect:/admin/category-management";
    }
}