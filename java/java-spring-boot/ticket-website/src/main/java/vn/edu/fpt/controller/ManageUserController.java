package vn.edu.fpt.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.model.entity.Staff;
import vn.edu.fpt.service.CustomerService;
import vn.edu.fpt.service.StaffService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class ManageUserController {
    private final CustomerService customerService;
    private final StaffService staffService;

    public ManageUserController(CustomerService customerService, StaffService staffService) {
        this.customerService = customerService;
        this.staffService = staffService;
    }

    @GetMapping("/customer")
    public String list(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {

        // ===== Logged admin =====
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        int pageSize = 5;

        // ===== First query =====
        Page<Customer> customerPage =
                customerService.getPageCustomerByStatus(
                        page,
                        pageSize,
                        isActive
                );

        /*
         * ===============================
         * FIX: PAGE OUT OF RANGE
         * ===============================
         */
        if (page >= customerPage.getTotalPages() && customerPage.getTotalPages() > 0) {

            int lastPage = customerPage.getTotalPages() - 1;

            return "redirect:/admin/manage-customer?page="
                    + lastPage
                    + (isActive != null ? "&isActive=" + isActive : "");
        }

        // ===== Model =====
        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("currentPage", customerPage.getNumber());
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("isActive", isActive);

        return "admin/manage-user/customer";
    }

    //    @PostMapping("/admin/customer/{id}/status")
//    public String changeCustomerStatus(
//            @PathVariable Integer id,
//            @RequestParam("active") boolean active,
//            RedirectAttributes redirectAttributes
//    ) {
//
//        // update status
//        customerService.changeStatus(id, active);
//
//        // flash message (optional - rất nên có)
//        redirectAttributes.addFlashAttribute(
//                "successMessage",
//                active ? "Customer activated successfully"
//                        : "Customer deactivated successfully"
//        );
//
//        // redirect về list
//        return "redirect:/admin/manage-customer";
//    }
    @PostMapping("/customer/{id}/status")
    public String changeCustomerStatus(
            @PathVariable String id,
            @RequestParam("active") boolean active,
            RedirectAttributes redirectAttributes
    ) {
        System.out.println("ACTIVE");
        Integer customerId;

        // ===== Validate ID =====
        try {
            customerId = Integer.valueOf(id);
        } catch (NumberFormatException ex) {
            return handleStatusMessage(
                    "Invalid Customer",
                    "Customer ID is not valid.",
                    redirectAttributes
            );
        }

        // ===== Update status =====
        try {
            customerService.changeStatus(customerId, active);
        } catch (Exception ex) {
            return handleStatusMessage(
                    "Update Failed",
                    "Cannot update customer status.",
                    redirectAttributes
            );
        }

        // ===== Success =====
        return handleStatusMessage(
                active ? "Customer Activated" : "Customer Deactivated",
                active
                        ? "The customer has been activated successfully."
                        : "The customer has been deactivated successfully.",
                redirectAttributes
        );
    }

    private String handleStatusMessage(
            String title,
            String message,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("hasMessage", true);
        redirectAttributes.addFlashAttribute("errorTitle", title);
        redirectAttributes.addFlashAttribute("message", message);

        System.out.println("Message: " + title);

        return "redirect:/admin/customer";
    }

    private String handleStatusMessageStaff(
            String title,
            String message,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("hasMessage", true);
        redirectAttributes.addFlashAttribute("errorTitle", title);
        redirectAttributes.addFlashAttribute("message", message);

        System.out.println("Message: " + title);

        return "redirect:/admin/staff";
    }

    @GetMapping("/staff")
    public String listStaff(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {

        int pageSize = 5;

        Page<Staff> staffPage =
                staffService.getPageStaffByStatus(
                        page,
                        pageSize,
                        isActive
                );

        // ===== PAGE OUT OF RANGE FIX =====
        if (page >= staffPage.getTotalPages()
                && staffPage.getTotalPages() > 0) {

            int lastPage = staffPage.getTotalPages() - 1;

            return "redirect:/admin/staff?page="
                    + lastPage
                    + (isActive != null ? "&isActive=" + isActive : "");
        }

        model.addAttribute("staffs", staffPage.getContent());
        model.addAttribute("currentPage", staffPage.getNumber());
        model.addAttribute("totalPages", staffPage.getTotalPages());
        model.addAttribute("isActive", isActive);

        return "admin/manage-user/staff";
    }

    @PostMapping("/staff/{id}/status")
    public String changeStaffStatus(
            @PathVariable String id,
            @RequestParam("active") boolean active,
            RedirectAttributes redirectAttributes
    ) {

        Integer staffId;

        try {
            staffId = Integer.valueOf(id);
        } catch (NumberFormatException ex) {
            return handleStatusMessageStaff(
                    "Invalid Staff",
                    "Staff ID is not valid.",
                    redirectAttributes
            );
        }

        try {
            staffService.changeStatus(staffId, active);
        } catch (Exception ex) {
            return handleStatusMessageStaff(
                    "Update Failed",
                    "Cannot update staff status.",
                    redirectAttributes
            );
        }

        return handleStatusMessageStaff(
                active ? "Staff Activated" : "Staff Deactivated",
                active
                        ? "The staff has been activated successfully."
                        : "The staff has been deactivated successfully.",
                redirectAttributes
        );
    }

}