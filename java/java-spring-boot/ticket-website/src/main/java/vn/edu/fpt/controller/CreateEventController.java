package vn.edu.fpt.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.FileIO.FileIO;
import vn.edu.fpt.client.AddressClient;
import vn.edu.fpt.model.dto.ValidationResult;
import vn.edu.fpt.model.entity.*;
import vn.edu.fpt.repository.VenueRepo;
import vn.edu.fpt.service.*;
import vn.edu.fpt.validation.EventInfoValidation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/staff")
public class CreateEventController {

    @Value("${upload.root}")
    private String uploadRoot;
    @Autowired
    private DiscountCodeService discountSevice;
    private final VenueService venueService;
    private final AddressClient addressClient;
    private final EventService eventService;
    private final TicketTypeService ticketTypeService;
    private final CategoryService categoryService;
    private final VenueRepo venueRepo;
    private final FileIO fileIO;
    private final EventInfoValidation eventInfoValidation;
    private final OrganizerService organizerService;
    private final NotificationService notificationService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private EventStaffAssignmentService eventStaffAssignmentService;
    @Autowired
    private RoleService roleService;

    public CreateEventController(EventService eventService, TicketTypeService ticketTypeService,
                                 CategoryService categoryService, VenueRepo venueRepo, OrganizerService organizerService,
                                 VenueService venueService, AddressClient addressClient, FileIO fileIO,
                                 EventInfoValidation eventInfoValidation, NotificationService notificationService) {
        this.eventService = eventService;
        this.ticketTypeService = ticketTypeService;
        this.categoryService = categoryService;
        this.venueRepo = venueRepo;
        this.organizerService = organizerService;
        this.venueService = venueService;
        this.fileIO = fileIO;
        this.addressClient = addressClient;
        this.eventInfoValidation = eventInfoValidation;
        this.notificationService = notificationService;
    }

    @GetMapping("/event/create")
    public String createEvent(Model model, HttpSession session) {

        // 🔥 lấy draft từ session
        Event draft = (Event) session.getAttribute("eventDraft");

        if (draft != null) {
            model.addAttribute("event", draft);
        } else if (!model.containsAttribute("event")) {
            Event event = new Event();
            event.setTicketTypes(new HashSet<>());
            model.addAttribute("event", event);
        }

        // Get categories
        List<Category> listCategories = categoryService.getAllCategory();
        List<Venue> listVenue = venueRepo.findAll();
        List<Organizer> listOrganizers = organizerService.getAllOrganizer();

        model.addAttribute("listOrganizers", listOrganizers);
        model.addAttribute("listVenue", listVenue);
        model.addAttribute("listCategories", listCategories);

        model.addAttribute("organizer", new Organizer());
        model.addAttribute("venue", new Venue());

        // Banner + Thumbnail session
        model.addAttribute("newLinkBanner", session.getAttribute("newLinkBanner"));
        model.addAttribute("newLinkThumbnail", session.getAttribute("newLinkThumbnail"));
        model.addAttribute("hasNewBanner", session.getAttribute("hasNewBanner"));
        model.addAttribute("hasNewThumbnail", session.getAttribute("hasNewThumbnail"));

        return "staff/event/create";
    }

    @Transactional
    @PostMapping("/event/create")
    public String createEvent(
            @RequestParam("title") String title,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("shortDescript") String shortDescript,
            @RequestParam("fullDescript") String fullDescript,
            @RequestParam("type") String type,
            @RequestParam("currency") String currency,
            @RequestParam("organizerId") String organizerId,
            @RequestParam("startDate") String startDate,
            @RequestParam("startTime") String startTime,
            @RequestParam("endDate") String endDate,
            @RequestParam("endTime") String endTime,
            @RequestParam("registrationDate") String registrationDate,
            @RequestParam("registrationTime") String registrationTime,
            @RequestParam("venueId") String venueId,
            @RequestParam("onlineMeetingLink") String onlineMeetingLink,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            @ModelAttribute("event") Event event
    ) {
        // Validation result
        ValidationResult result = new ValidationResult();
        // Messege
        Boolean hasMessage = false;
        String errorTitle = "";
        String message = "";

        // Create new Event
        // Event event = new Event();

        // BANNER
        String banner = (String) session.getAttribute("newLinkBanner");
        if (banner != null && !banner.isBlank() && Files.exists(Path.of(uploadRoot + banner))) {
            event.setBannerImageUrl((String) session.getAttribute("fileNameBanner"));
            fileIO.moveFile(banner, "/upload/event/banner/" + (String) session.getAttribute("fileNameBanner"));
            session.removeAttribute("newLinkBanner");
            session.removeAttribute("hasNewBanner");
            session.removeAttribute("fileNameBanner");
        }
        else {
            System.out.println("Không có banner mới để di chuyển!");
            System.out.println("DOES NOT HAVE NEW BANNER");
        }
        System.out.println("===============================================================");

        // THUMBNAIL
        String thumbnail = (String) session.getAttribute("newLinkThumbnail");
        if (thumbnail != null && !thumbnail.isBlank() && Files.exists(Path.of(uploadRoot + thumbnail))) {
            System.out.println("Xóa thumbnail cũ:");
            System.out.println(event.getThumbnailUrl());
            fileIO.deleteFile("/upload/event/thumbnail/" + event.getThumbnailUrl());
            event.setThumbnailUrl((String) session.getAttribute("fileNameThumbnail"));
            fileIO.moveFile(thumbnail, "/upload/event/thumbnail/" + (String) session.getAttribute("fileNameThumbnail"));
            session.removeAttribute("newLinkThumbnail");
            session.removeAttribute("hasNewThumbnail");
            session.removeAttribute("fileNameThumbnail");
        }
        else {
            System.out.println("Không có thumbnail mới để di chuyển!");

            System.out.println("DOES NOT HAVE NEW THUMBNAIL");
        }
        System.out.println("===============================================================");

        // Check validation

        // Check title:
        if (eventInfoValidation.checkTitle(title, result)) {
            event.setTitle(title.trim());
        }
        else {
            return handleCannotCreate(result, redirectAttributes, event, session);
        }

        // Check category:
        if (eventInfoValidation.checkCategoryId(categoryId, result)) {
            event.setCategory(categoryService.getCategoryById(Integer.parseInt(categoryId)).get());
        }
        else {
            return handleCannotCreate(result, redirectAttributes, event, session);
        }

        // Check short description
        if (eventInfoValidation.checkShortDescript(shortDescript, result)) {
            event.setShortDescription(shortDescript.trim());
        } else {
            return handleCannotCreate(result, redirectAttributes, event, session);
        }

        // Check full descript
        if (eventInfoValidation.checkFullDescript(fullDescript, result)) {
            event.setDescription(fullDescript.trim());
        }
        else {
            return handleCannotCreate(result, redirectAttributes, event, session);
        }

        // Check event type
        if (eventInfoValidation.checkType(type, result)) {
            event.setEventType(type);
        } else {
            return handleCannotCreate(result, redirectAttributes, event, session);
        }

        // Check event status
//        if (eventInfoValidation.checkStatus(status, result)) {
//            event.setEventStatus(status);
//        }
//        else {
//            return handleCannotCreate(result, redirectAttributes, event, session);
//        }
        event.setEventStatus("Draft");
        // Check event's currency
        if (eventInfoValidation.checkCurrency(currency, result)) {
            event.setCurrency(currency);
        } else {
            return handleCannotCreate(result, redirectAttributes, event, session);
        }

        // Check Organizer
        if (eventInfoValidation.checkOrganizer(organizerId, result)) {
            event.setOrganizer(organizerService.getOrganizerById(Integer.parseInt(organizerId)).get());
        }
        else {
            return handleCannotCreate(result, redirectAttributes, event, session);
        }

        // Check Venue
        if (eventInfoValidation.checkVenueId(venueId, result)) {
            event.setVenue(venueService.getVenueById(Integer.parseInt(venueId)).get());
        }
        else {
            return handleCannotCreate(result, redirectAttributes, event, session);
        }

        // Check date:
        if (eventInfoValidation.checkDateTime(startDate, startTime, result)) {
            // do nothing
        } else {
            return handleCannotCreate(result, redirectAttributes, event, session);
        }

        if (eventInfoValidation.checkDateTime(endDate, endTime, result)) {
            // do nothing
        } else {
            return handleCannotCreate(result, redirectAttributes, event, session);
        }

        if (eventInfoValidation.checkDateTime(registrationDate, registrationTime, result)) {
            //
        } else {
            return handleCannotCreate(result, redirectAttributes, event, session);
        }

        // Check validation of Start date End date and regis date:
        LocalDateTime startDateTime = LocalDateTime.parse(String.format("%sT%s", startDate, startTime));
        LocalDateTime endDateTime = LocalDateTime.parse(String.format("%sT%s", endDate, endTime));
        LocalDateTime regisDeadline = LocalDateTime.parse(String.format("%sT%s", registrationDate, registrationTime));
        if (eventInfoValidation.checkValidationAllDate(
                startDateTime,
                endDateTime,
                regisDeadline,
                result
        )) {
            event.setStartDateTime(startDateTime);
            event.setEndDateTime(endDateTime);
            event.setRegistrationDeadline(regisDeadline);
        }
        else {
            return handleCannotCreate(result, redirectAttributes, event, session);
        }
        event.setMaxAttendees(venueService.getVenueById(Integer.parseInt(venueId)).get().getCapacity());
        event.setCurrentAttendees(0);
        event.setViewCount(0);
        // isfree và isFeatured là hai cái cần quan tâm khi set Hoặc làm form.
        event.setIsFeatured(false);
        event.setIsFree(false);
        event.setCreatedAt(LocalDateTime.now());
        // Khi update thì cần ghi log update at.
        eventService.saveEvent(event);

        // Assign event
        // User
        // Get user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = getEmailFromAuth(auth);
        if (email == null) return "redirect:/auth/login";
        Optional<Staff> staffOptional = staffService.getStaffById(email);
        if (staffOptional.isEmpty()) {
            return "redirect:/login";
        }
        Staff staff = staffOptional.get();

        EventStaffAssignment eventStaffAssignment = new EventStaffAssignment();

        Role role = roleService.getRoleById(2)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        EventStaffAssignmentId id = new EventStaffAssignmentId();
        // Set id
        id.setEventId(event.getId());
        id.setStaffId(staff.getId());
        id.setRoleId(role.getId());
        id.setAssignedBy(staff.getId());
        eventStaffAssignment.setId(id);

        // Set event
        eventStaffAssignment.setEvent(event);
        eventStaffAssignment.setStaff(staff);
        eventStaffAssignment.setRole(role);
        eventStaffAssignment.setAssignedBy(staff);
        eventStaffAssignment.setAssignedAt(LocalDateTime.now());
        eventStaffAssignment.setIsActive(true);
        eventStaffAssignmentService.save(eventStaffAssignment);

        redirectAttributes.addFlashAttribute("event", event);
        session.removeAttribute("eventDraft");
        return "redirect:/staff/event/create/ticket";
    }

    private String getEmailFromAuth(Authentication auth) {
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) return ((UserDetails) principal).getUsername();
        if (principal instanceof OAuth2User) return ((OAuth2User) principal).getAttribute("email");
        // fallback (thường là email với form login, nhưng với OAuth2 có thể là "sub" nên không tin 100%)
        String name = auth.getName();
        if (name == null || "anonymousUser".equals(name)) return null;
        return name;
    }

    @GetMapping("/event/create/ticket")
    public String createTicket(
            @ModelAttribute("event") Event event,
            Model model
    ) {
        if (event == null) {
            return "error/404";
        }
        TicketType ticket = new TicketType();
        ticket.setEvent(event);
        model.addAttribute("ticket", ticket);
        return "staff/event/createTicket";
    }

    @PostMapping("/event/create/ticket")
    public String createTicketPost(
            @ModelAttribute("event") Event event,
            RedirectAttributes redirectAttributes,
            @ModelAttribute("ticket") TicketType ticket,
            Model model,
            @RequestParam(value = "isEventDetail", required = false, defaultValue = "false") String isEventDetail
    ) {
        System.out.println(isEventDetail);
        Boolean isEventDetailBl;
        System.out.println(ticket);
        Integer eventId = ticket.getEvent().getId();
        event = eventService.getEventById(eventId).get();
        ticket.setEvent(event);
        ticket.setSalesEndDate(event.getRegistrationDeadline());
        event.getTicketTypes().add(ticket);
        ticketTypeService.createTicketType(ticket);
        System.out.println(ticket);
        redirectAttributes.addFlashAttribute("event", event);
        try {
            isEventDetailBl = Boolean.parseBoolean(isEventDetail);
            if (isEventDetailBl) {
                System.out.println(isEventDetailBl);
                return "redirect:/staff/event/" + event.getId();
            }
        } catch(Exception e) {
        }
            return "redirect:/staff/event/create/ticket";
    }

    @PostMapping("/event/create/cancel")
    public String cancelCreate(
            HttpSession session,
            @RequestParam("newLinkBanner") String newLinkBanner,
            @RequestParam("newLinkThumbnail") String newLinkThumbnail
    ) {
        session.removeAttribute("newLinkBanner");
        session.removeAttribute("newLinkThumbnail");
        session.removeAttribute("hasNewBanner");
        session.removeAttribute("hasNewThumbnail");
        session.removeAttribute("fileNameBanner");
        session.removeAttribute("fileNameThumbnail");
        // Delete file images
        if (!newLinkBanner.isEmpty()) {
            fileIO.deleteFile(newLinkBanner);
        }
        if (!newLinkThumbnail.isEmpty()) {
            fileIO.deleteFile(newLinkThumbnail);
        }
        return "redirect:/staff/event";
    }


    @PostMapping("/event/create/finish")
    public String finish(
            @ModelAttribute("event") Event event,
            SessionStatus status,
            Model model
    ) {
        model.addAttribute("event", event);
        return "staff/event/createSuccess";
    }

    private String handleCannotCreate(ValidationResult result,
                                      RedirectAttributes redirectAttributes,
                                      Event event, HttpSession session) {
        redirectAttributes.addFlashAttribute("hasMessage", true);
        redirectAttributes.addFlashAttribute("errorTitle", result.getErrorTitle());
        redirectAttributes.addFlashAttribute("message", result.getMessage());
        session.setAttribute("eventDraft", event);
        System.out.println("Error: " + result.getErrorTitle());
        return "redirect:/staff/event/create";
    }

    private String getEmailFromPrincipal(Object principal) {
        if (principal == null) return null;
        if (principal instanceof UserDetails) return ((UserDetails) principal).getUsername();
        if (principal instanceof OAuth2User) return ((OAuth2User) principal).getAttribute("email");
        return null;
    }

    @PostMapping("/event/create/venue/save")
    public String saveVenueInEditEvent(@ModelAttribute("venue") Venue venue,
                                       @RequestParam (required = false) Boolean setForEvent,
                                       @AuthenticationPrincipal Object principal
    ) {
        String email = getEmailFromPrincipal(principal);
        Staff staff = staffService.getStaffByEmail(email);
        if(venue.getId() == null) {
            venue.setCreatedAt(LocalDateTime.now());
            venue.setCity(addressClient.getProvinceByProvinceCode(venue.getCity(), 1).getName());
            venue.setWard(addressClient.getWardByWardCode(venue.getWard()).getName());
            venue.setActive(true);
            venue.setStaff(staff);
            if (venueService.saveVenue(venue) == 1) {
//                if (Boolean.TRUE.equals(setForEvent)) {
//                    eventService.updateVenue(eventId, venue);
//                }

                return "redirect:/staff/event/create";
            } else {
                return "redirect:/staff/event/create";
            }

        } else {
            Pattern pattern = Pattern.compile("^[0-9]*$");
            Matcher matcher = pattern.matcher(venue.getCity());
            if(matcher.matches()) {
                venue.setCity(addressClient.getProvinceByProvinceCode(venue.getCity(), 1).getName());
            } else {
                venue.setCity(venue.getCity().replace("_"," "));
            }
            matcher = pattern.matcher(venue.getWard());
            if(matcher.matches()) {
                venue.setWard(addressClient.getWardByWardCode(venue.getWard()).getName());
            } else {
                venue.setWard(venue.getWard().replace("_"," "));
            }
            if(venueService.updateVenue(venue) == 1) {
                return "redirect:/staff/event/create";
            } else {
                return "redirect:/staff/event/create";
            }
        }
    }

}
