package vn.edu.fpt.controller;


import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.dto.TicketTypeDTO;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.service.TicketTypeService;

import java.time.LocalDateTime;

/**
 * Controller quản lý Ticket Types cho Events
 * Base URL: /staff/event/{eventId}/ticket-types
 * Url mẫu: http://localhost:8080/staff/event/1/ticket-types
 *
 * NOTE: Entity dùng Instant (match với DATETIME2 trong SQL Server) -> để khớp với database
 *       DTO dùng LocalDateTime (dễ binding với HTML datetime-local input) theo thuận yêu cầu
 *       Conversion được xử lý tự động trong Service layer -> làm cái bước này cho chắc ăn
 */
@Controller
@RequestMapping("/staff/event/{eventId}/ticket-types")
public class TicketTypeController {

    private static final Logger logger = LoggerFactory.getLogger(TicketTypeController.class);

    private final TicketTypeService ticketTypeService;

    private final EventRepo eventRepo;

    public TicketTypeController(TicketTypeService ticketTypeService, EventRepo eventRepo) {
        this.ticketTypeService = ticketTypeService;
        this.eventRepo = eventRepo;
    }

    /**
     * View all ticket types của một event
     * URL: GET /staff/event/{eventId}/ticket-types
     */
    @GetMapping
    public String viewTicketTypes(@PathVariable Integer eventId, Model model) {
        logger.info("Viewing ticket types for eventId: {}", eventId);

        try {
            Event event = eventRepo.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

            model.addAttribute("eventId", eventId);
            model.addAttribute("eventTitle", event.getTitle());
            model.addAttribute("ticketTypes", ticketTypeService.getAllTicketTypesByEvent(eventId));
            return "staff/ticket-types/list";
        } catch (Exception e) {
            logger.error("Error viewing ticket types for eventId: {}", eventId, e);
            model.addAttribute("error", "Cannot load list ticket types: " + e.getMessage());
            model.addAttribute("eventId", eventId);
            model.addAttribute("eventTitle", "Unknown Event");
            model.addAttribute("ticketTypes", java.util.Collections.emptyList());
            return "staff/ticket-types/list";
        }
    }

    /**
     * Show create form
     * URL: GET /staff/event/{eventId}/ticket-types/create
     */
    @GetMapping("/create")
    public String showCreateForm(@PathVariable Integer eventId, Model model) {
        logger.info("Showing create form for eventId: {}", eventId);

        try {
            Event event = eventRepo.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

            TicketTypeDTO ticketTypeDTO = new TicketTypeDTO();
            ticketTypeDTO.setEventId(eventId);

            // Thêm thông tin event vào model để validate ở frontend
            model.addAttribute("ticketType", ticketTypeDTO);
            model.addAttribute("eventId", eventId);
            model.addAttribute("eventStartDateTime", event.getStartDateTime());
            model.addAttribute("eventEndDateTime", event.getEndDateTime());

            return "staff/ticket-types/create";
        } catch (Exception e) {
            logger.error("Error showing create form for eventId: {}", eventId, e);
            model.addAttribute("error", "Cannot load create form: " + e.getMessage());
            return "redirect:/staff/event";
        }
    }

    /**
     * Create new ticket type
     * URL: POST /staff/event/{eventId}/ticket-types
     */
    @PostMapping
    public String createTicketType(
            @PathVariable Integer eventId,
            @Valid @ModelAttribute("ticketType") TicketTypeDTO ticketTypeDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Creating ticket type for eventId: {}", eventId);

        // Lấy thông tin event để validate
        Event event;
        try {
            event = eventRepo.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Event not found: " + e.getMessage());
            return "redirect:/staff/event";
        }
        // FIX-7: Chỉ block event đã bị Cancelled — các status khác đều cho phép tạo vé
        String status = event.getEventStatus();
        if ("Cancelled".equals(status)) {
            model.addAttribute("error", "Cannot create ticket types for a cancelled event.");
            model.addAttribute("ticketType", ticketTypeDTO);
            model.addAttribute("eventId", eventId);
            model.addAttribute("eventStartDateTime", event.getStartDateTime());
            model.addAttribute("eventEndDateTime", event.getEndDateTime());
            return "staff/ticket-types/create";
        }

        // Validation errors
        validateTicketTypeDates(ticketTypeDTO, event, result);

        if (result.hasErrors()) {
            logger.warn("Validation errors: {}", result.getAllErrors());
            model.addAttribute("ticketType", ticketTypeDTO);
            model.addAttribute("eventId", eventId);
            model.addAttribute("eventStartDateTime", event.getStartDateTime());
            model.addAttribute("eventEndDateTime", event.getEndDateTime());
            return "staff/ticket-types/create";
        }

        try {
            ticketTypeDTO.setEventId(eventId);
            TicketTypeDTO created = ticketTypeService.createTicketType(ticketTypeDTO);

            logger.info("Successfully created ticket type with id: {}", created.getId());
            redirectAttributes.addFlashAttribute("success", "Create ticket type done!");

            return "redirect:/staff/event/" + eventId + "/ticket-types";

        } catch (Exception e) {
            logger.error("Error creating ticket type for eventId: {}", eventId, e);
            model.addAttribute("error", "Cannot create ticket type: " + e.getMessage());
            model.addAttribute("ticketType", ticketTypeDTO);
            model.addAttribute("eventId", eventId);
            model.addAttribute("eventStartDateTime", event.getStartDateTime());
            model.addAttribute("eventEndDateTime", event.getEndDateTime());
            return "staff/ticket-types/create";
        }
    }

    /**
     * Show edit form
     * URL: GET /staff/event/{eventId}/ticket-types/{id}/edit
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Integer eventId,
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Showing edit form for ticketTypeId: {}, eventId: {}", id, eventId);

        try {
            Event event = eventRepo.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

            TicketTypeDTO ticketTypeDTO = ticketTypeService.getTicketTypeById(id);

            if (!ticketTypeDTO.getEventId().equals(eventId)) {
                logger.warn("Ticket type {} not belonging event {}", id, eventId);
                redirectAttributes.addFlashAttribute("error", "Ticket type not belonging event");
                return "redirect:/staff/event/" + eventId + "/ticket-types";
            }

            model.addAttribute("ticketType", ticketTypeDTO);
            model.addAttribute("eventId", eventId);
            model.addAttribute("eventStartDateTime", event.getStartDateTime());
            model.addAttribute("eventEndDateTime", event.getEndDateTime());

            return "staff/ticket-types/edit";

        } catch (RuntimeException e) {
            logger.error("Error loading ticket type for edit, id: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Cannot find ticket type: " + e.getMessage());
            return "redirect:/staff/event/" + eventId + "/ticket-types";
        }
    }

    /**
     * Update ticket type
     * URL: POST /staff/event/{eventId}/ticket-types/{id}
     */
    @PostMapping("/{id}")
    public String updateTicketType(
            @PathVariable Integer eventId,
            @PathVariable Integer id,
            @Valid @ModelAttribute("ticketType") TicketTypeDTO ticketTypeDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Updating ticket type id: {}, eventId: {}", id, eventId);

        Event event;
        try {
            event = eventRepo.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Event not found: " + e.getMessage());
            return "redirect:/staff/event";
        }

        // FIX-7b: Chỉ block event đã bị Cancelled
        String statusUpdate = event.getEventStatus();
        if ("Cancelled".equals(statusUpdate)) {
            redirectAttributes.addFlashAttribute("error", "Cannot edit ticket types for a cancelled event.");
            return "redirect:/staff/event/" + eventId + "/ticket-types";
        }

        // Validate dates
        validateTicketTypeDates(ticketTypeDTO, event, result);

        if (result.hasErrors()) {
            logger.warn("Validation errors: {}", result.getAllErrors());
            model.addAttribute("ticketType", ticketTypeDTO);
            model.addAttribute("eventId", eventId);
            model.addAttribute("eventStartDateTime", event.getStartDateTime());
            model.addAttribute("eventEndDateTime", event.getEndDateTime());
            return "staff/ticket-types/edit";
        }

        try {
            ticketTypeDTO.setId(id);
            ticketTypeDTO.setEventId(eventId);

            TicketTypeDTO updated = ticketTypeService.updateTicketType(id, ticketTypeDTO);

            logger.info("Successfully updated ticket type id: {}", id);
            redirectAttributes.addFlashAttribute("success", "Update ticket type done!");

            return "redirect:/staff/event/" + eventId + "/ticket-types";

        } catch (Exception e) {
            logger.error("Error updating ticket type id: {}", id, e);
            model.addAttribute("error", "Cannot update ticket type: " + e.getMessage());
            model.addAttribute("ticketType", ticketTypeDTO);
            model.addAttribute("eventId", eventId);
            model.addAttribute("eventStartDateTime", event.getStartDateTime());
            model.addAttribute("eventEndDateTime", event.getEndDateTime());
            return "staff/ticket-types/edit";
        }
    }

    /**
     * Xóa mềm ticket type (set isActive = false)
     * URL: POST /staff/event/{eventId}/ticket-types/{id}/delete
     */
    @PostMapping("/{id}/delete")
    public String deleteTicketType(
            @PathVariable Integer eventId,
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        logger.info("Soft deleting ticket type id: {}, eventId: {}", id, eventId);

        try {
            ticketTypeService.deleteTicketType(id);
            logger.info("Successfully deactivated ticket type id: {}", id);
            redirectAttributes.addFlashAttribute("success", "Disabled ticket type!");
        } catch (Exception e) {
            logger.error("Error deactivating ticket type id: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Cannot disable ticket type: " + e.getMessage());
        }

        return "redirect:/staff/event/" + eventId + "/ticket-types";
    }

    /**
     * Xóa cứng ticket type (xóa khỏi database)
     * URL: POST /staff/event/{eventId}/ticket-types/{id}/hard-delete
     */
    @PostMapping("/{id}/hard-delete")
    public String hardDeleteTicketType(
            @PathVariable Integer eventId,
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        logger.info("Hard deleting ticket type id: {}, eventId: {}", id, eventId);

        try {
            ticketTypeService.hardDeleteTicketType(id);
            logger.info("Successfully deleted ticket type id: {}", id);
            redirectAttributes.addFlashAttribute("success", "Deleted ticket type done!");
        } catch (RuntimeException e) {
            // Service đã throw exception với message rõ ràng
            logger.error("Error deleting ticket type id: {}", id, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error deleting ticket type id: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Cannot delete ticket type: " + e.getMessage());
        }

        return "redirect:/staff/event/" + eventId + "/ticket-types";
    }

    /**
     * Kích hoạt lại ticket type (set isActive = true)
     * URL: POST /staff/event/{eventId}/ticket-types/{id}/activate
     */
    @PostMapping("/{id}/activate")
    public String activateTicketType(
            @PathVariable Integer eventId,
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        logger.info("Activating ticket type id: {}, eventId: {}", id, eventId);

        try {
            ticketTypeService.activateTicketType(id);
            logger.info("Successfully activated ticket type id: {}", id);
            redirectAttributes.addFlashAttribute("success", "Ticket type activated!");
        } catch (Exception e) {
            logger.error("Error activating ticket type id: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Cannot activate ticket type: " + e.getMessage());
        }
        return "redirect:/staff/event/" + eventId + "/ticket-types";
    }

    /**
     * Validate logic cho Sales Start/End Date
     *
     * FIX-6a: Bỏ param isEdit và block check ngày quá khứ
     *   Lý do: staff được phép set ngày quá khứ khi tạo vé cho event đã tạo từ trước
     *          Rule duy nhất cần thiết: salesEndDate phải TRƯỚC eventStart
     *
     * FIX-6b: Sửa off-by-one — salesEndDate == eventStart giờ bị chặn
     *   Lý do: nếu salesEndDate == eventStart thì đúng lúc event bắt đầu vẫn đang bán vé -> sai nghiệp vụ
     *   Cũ:  isAfter(eventStart)          -> cho phép bằng -> sai
     *   Mới: !isBefore(eventStart)        -> bằng cũng bị chặn -> đúng
     */
    private void validateTicketTypeDates(TicketTypeDTO dto, Event event, BindingResult result) {
        LocalDateTime eventStart = event.getStartDateTime();

        // 1. Sales End Date PHẢI TRƯỚC (không được bằng) Event Start DateTime
        if (dto.getSalesEndDate() != null && eventStart != null) {
            if (!dto.getSalesEndDate().isBefore(eventStart)) {
                result.rejectValue("salesEndDate", "error.salesEndDate",
                        String.format("Sales must end before event starts (Event starts at: %s)",
                                eventStart.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
            }
        }

        // 2. End Date phải sau Start Date
        if (dto.getSalesStartDate() != null && dto.getSalesEndDate() != null) {
            if (!dto.getSalesEndDate().isAfter(dto.getSalesStartDate())) {
                result.rejectValue("salesEndDate", "error.salesEndDate",
                        "Sales end date must be after sales start date");
            }
        }

        // 3. Khoảng cách tối thiểu 1 giờ
        if (dto.getSalesStartDate() != null && dto.getSalesEndDate() != null) {
            if (dto.getSalesEndDate().isBefore(dto.getSalesStartDate().plusHours(1))) {
                result.rejectValue("salesEndDate", "error.salesEndDate",
                        "Sales period must be at least 1 hour");
            }
        }
    }
}