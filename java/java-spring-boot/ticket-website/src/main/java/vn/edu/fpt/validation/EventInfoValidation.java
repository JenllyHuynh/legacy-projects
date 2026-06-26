package vn.edu.fpt.validation;

import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Component;
import vn.edu.fpt.model.dto.ValidationResult;
import vn.edu.fpt.model.entity.Category;
import vn.edu.fpt.model.entity.Organizer;
import vn.edu.fpt.model.entity.Venue;
import vn.edu.fpt.service.CategoryService;
import vn.edu.fpt.service.OrganizerService;
import vn.edu.fpt.service.TicketTypeService;
import vn.edu.fpt.service.VenueService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class EventInfoValidation {

    // Trich phu thuoc
    private final CategoryService categoryService;
    private final VenueService venueService;
    private final OrganizerService organizerService;
    private final TicketTypeService ticketTypeService;


    public EventInfoValidation(CategoryService categoryService, VenueService venueService, OrganizerService organizerService, TicketTypeService ticketTypeService) {
        this.categoryService = categoryService;
        this.venueService = venueService;
        this.organizerService = organizerService;
        this.ticketTypeService = ticketTypeService;
    }

    public boolean checkTitle(String title, ValidationResult result) {
        if (title == null || title.isBlank()) {
            result.setErrorTitle("Title is empty!");
            result.setMessage("Title is empty!");
            return false;
        }
        if (title.length() > 255) {
            result.setErrorTitle("Title is too long!");
            result.setMessage("Title is too long! Please try less than 255 characters!");
            return false;
        }
        return true;
    }

    public boolean checkCategoryId(String categoryId, ValidationResult result) {
        int id;
        try {
            id = Integer.parseInt(categoryId);
        } catch (Exception e) {
            result.setErrorTitle("Category is invalid!");
            result.setMessage("Category is invalid! Please try again!");
            return false;
        }

        Optional<Category> categoryOptional = categoryService.getCategoryById(id);
        if (!categoryOptional.isPresent()) {
            result.setErrorTitle("Category not found!");
            result.setMessage("Category does not exist!");
            return false;
        }
        return true;
    }

    public boolean checkShortDescript(String shortDescript, ValidationResult result) {
        if (shortDescript == null || shortDescript.isBlank()) {
            result.setErrorTitle("Short description is empty!");
            result.setMessage("Short description is empty!");
            return false;
        } else if (shortDescript.length() > 500) {
            result.setErrorTitle("> 500");
            result.setMessage("> 500");
            return false;
        }
        return true;
    }

    public boolean checkFullDescript(String fullDescript, ValidationResult result) {
        if (fullDescript == null || fullDescript.isBlank()) {
            result.setErrorTitle("Full descript is empty!");
            result.setMessage("Full descript is empty!");
            return false;
        }
        return true;
    }

    public boolean checkType(String type, ValidationResult result) {
        List<String> listType = new ArrayList<>(
                List.of("online", "offline")
        );
        if (!listType.contains(type.trim().toLowerCase())) {
            result.setErrorTitle("Invalid event type!");
            result.setMessage("Invalid event type!");
            return false;
        }
        return true;
    }

    public boolean checkStatus(String status, ValidationResult result) {
        List<String> listStatus = new ArrayList<>(
            List.of("cancelled", "rejected", "published", "approved", "draft")
        );
        if (!listStatus.contains(status.trim().toLowerCase())) {
            result.setErrorTitle("Event status is invalid!");
            result.setMessage("Event status is invalid!");
            return false;
        }
        return true;
    }

    public boolean checkCurrency(String currency, ValidationResult result) {
        List<String> listCurrency = new ArrayList<>(
                List.of("usd", "vnd")
        );
        if (!listCurrency.contains(currency.trim().toLowerCase())) {
            result.setErrorTitle("Invalid curency!");
            result.setMessage("Invalid curency!");
            return false;
        }
        return true;
    }

    public boolean checkVenueId(String venueId, ValidationResult result) {
        int id;
        try {
            id = Integer.parseInt(venueId);
        } catch (Exception e) {
            result.setErrorTitle("Invalid venue");
            result.setMessage("Invalid venue");
            return false;
        }
        Optional<Venue> venueOptional = venueService.getVenueById(id);
        if (!venueOptional.isPresent()) {
            result.setErrorTitle("Venue does not exist!");
            result.setMessage("Venue does not exist!");
            return false;
        }
        return true;
    }

    public boolean checkOrganizer(String organizerId, ValidationResult result) {
        int id;
        try {
            id = Integer.parseInt(organizerId);
        } catch (Exception e) {
            result.setErrorTitle("Invalid Organizer!");
            result.setMessage("Invalid Organizer!");
            return false;
        }
        Optional<Organizer> optionalOrganizer = organizerService.getOrganizerById(id);
        if (!optionalOrganizer.isPresent()) {
            result.setErrorTitle("Organizer does not exist!");
            result.setMessage("Organizer does not exist!");
            return false;
        }
        return true;
    }
    // Đã check datetime
    public boolean checkDateTime(String startDate, String startTime, ValidationResult result) {
        // Check valid DateTime:
        String date = String.format("%sT%s", startDate, startTime);
        LocalDateTime startDateTime;
        try {
            startDateTime = LocalDateTime.parse(date);
        } catch (Exception e) {
            result.setErrorTitle("Invalid Date Time!");
            result.setMessage("Invalid Date Time!");
            return false;
        }
        // Check datetime is in today or in the future
        if (startDateTime.isBefore(LocalDateTime.now())) {
            result.setErrorTitle("Invalid Date Time");
            result.setMessage("Please select a date and time in the future.");
            return false;
        }
        return true;
    }

    // Check ticket type:
    // public boolean checkTicketType()

    public boolean checkValidationAllDate(LocalDateTime start, LocalDateTime end,
                                          LocalDateTime regisded, ValidationResult result) {
        if (start.isAfter(end)) {
            result.setErrorTitle("Invalid date range");
            result.setMessage("The start date must be earlier than the end date. " +
                    "Please enter a valid date range.");
            return false;
        }

        if (end.isBefore(start)) {
            result.setErrorTitle("Invalid date range");
            result.setMessage("The end date must be later than the start date. " +
                    "Please enter a valid date range.");
            return false;
        }

        if (regisded.isAfter(end)) {
            result.setErrorTitle("Invalid ticket purchase time");
            result.setMessage("Tickets must be purchased before the event starts.");
            return false;
        }

        if (regisded.isAfter(start)) {
            result.setErrorTitle("Invalid ticket purchase time");
            result.setMessage("Tickets must be sold before the event starts.");
            return false;
        }

        if (start.equals(end)) {
            result.setErrorTitle("Invalid event duration");
            result.setMessage("The event start time and end time cannot be the same.");
            return false;
        }

        if (end.minusMinutes(20).isBefore(start)) {
            result.setErrorTitle("Invalid event duration");
            result.setMessage("The event duration must be at least 20 minutes.");
            return false;
        }
        return true;
    }



}
