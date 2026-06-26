package vn.edu.fpt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.FileIO.FileIO;
import vn.edu.fpt.client.AddressClient;
import vn.edu.fpt.model.dto.EventStaffAssignmentDTO;
import vn.edu.fpt.model.dto.NotificationGroupDTO;
import vn.edu.fpt.model.dto.ValidationResult;
import vn.edu.fpt.model.entity.DiscountCode;
import vn.edu.fpt.model.entity.Venue;
import vn.edu.fpt.model.entity.*;
import vn.edu.fpt.repository.StaffRepo;
import vn.edu.fpt.repository.VenueRepo;
import vn.edu.fpt.service.*;
import vn.edu.fpt.util.PageSetting;
import vn.edu.fpt.service.CategoryService;
import vn.edu.fpt.service.DiscountCodeService;
import vn.edu.fpt.service.VenueService;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.service.TicketTypeService;
import vn.edu.fpt.validation.EventInfoValidation;
import vn.edu.fpt.validation.UrlValition;
import vn.edu.fpt.service.NotificationService;
import vn.edu.fpt.repository.TicketRepo;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/staff")
public class StaffController {
    @Value("${upload.root}")
    private String uploadRoot;
    @Value("${revenue.api.key}")
    private String REVENUE_KEY;
    @Autowired
    private PasswordEncoder passwordEncoder;
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
    private final TicketRepo ticketRepo;
    private final StaffNotificationService staffNotificationService;
    private final EventStaffAssignmentService eventStaffAssignmentService;
    private final RoleService roleService;
    private final StaffService staffService;
    private final PaymentService paymentService;
    private final StaffRepo staffRepo;
    private final OrderService orderService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private CustomerService customerService;

    public StaffController(EventService eventService, TicketTypeService ticketTypeService,
                           CategoryService categoryService, VenueRepo venueRepo, OrganizerService organizerService,
                           VenueService venueService, AddressClient addressClient, FileIO fileIO,
                           EventInfoValidation eventInfoValidation, NotificationService notificationService,
                           TicketRepo ticketRepo, StaffNotificationService staffNotificationService,
                           EventStaffAssignmentService eventStaffAssignmentService, RoleService roleService,
                           StaffService staffService, PaymentService paymentService, StaffRepo staffRepo, OrderService orderService) {
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
        this.ticketRepo = ticketRepo;
        this.staffNotificationService = staffNotificationService;
        this.eventStaffAssignmentService = eventStaffAssignmentService;
        this.roleService = roleService;
        this.staffService = staffService;
        this.paymentService = paymentService;
        this.staffRepo = staffRepo;
        this.orderService = orderService;
    }

    @GetMapping
    public String staff() {
        System.out.println("staff");
        return "redirect:/staff/event";
    }

    @GetMapping("/event")
    public String eventList(
            @RequestParam(value = "page", required = false, defaultValue = "1") String page,
            @RequestParam(value = "status", required = false) String status,
            Model model,
            @AuthenticationPrincipal Object principal
    ) {
        String email = getEmailFromPrincipal(principal);
        Optional<Staff> staffOptional = staffRepo.findByEmail(email);
        Staff staff = new Staff();
        if (staffOptional.isEmpty()) {
            System.out.println("KHONG TIM THAY STAFF!");
            return "error/404";
        }
        staff = staffOptional.get();
        int pageInt = 1;
        try {
            pageInt = Integer.parseInt(page);
        } catch (Exception e) {
            System.out.println("==============");
            System.out.println("Page invalid!");
            System.out.println("==============");
        }
        Set<String> validStatuses = Set.of(
                "Draft", "PendingApproval", "Published", "Completed",
                "Cancelled", "Rejected", "Approved", "Live"
        );

        if (status != null && !validStatuses.contains(status)) {
            status = null; // fallback về All
        }
        Page<Event> pageInfo = eventService.getEventByPage(0, staff.getId(), status);

        // 🔥 FIX: nếu không có data thì set pageInt = 1
        if (pageInfo.getTotalPages() == 0) {
            pageInt = 1;
        } else {
            if (pageInt > pageInfo.getTotalPages()) {
                pageInt = pageInfo.getTotalPages();
            } else if (pageInt < 1) {
                pageInt = 1;
            }
        }

        Page<Event> pageEvents = eventService.getPageEventLifeCycle(pageInt - 1, PageSetting.SIZE_OF_EACH_PAGE, staff.getId(), status);
        Map<String, String> statusClassMap = Map.of(
                "Published", "bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400",
                "Completed", "bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400",
                "Cancelled", "bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400",
                "Rejected", "bg-slate-200 text-slate-700 dark:bg-slate-700 dark:text-slate-300",
                "Approved", "bg-teal-100 text-teal-700 dark:bg-teal-900/30 dark:text-teal-400",
                "PendingApproval", "bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400",
                "Draft", "bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400 border border-slate-200 dark:border-slate-700",
                "Live", "bg-red-600 text-white animate-pulse shadow-lg shadow-red-500/40"
        );
        model.addAttribute("statusClassMap", statusClassMap);
        model.addAttribute("pageEvents", pageEvents);
        model.addAttribute("currentStatus", status); // 🔥 dùng để highlight UI
        return "staff/event/list";
    }

    @Transactional
    @PostMapping("/event/{id}/unpublish")
    public String unPublishEvent(@PathVariable("id") String id) {
        int idInt;
        try {
            idInt = Integer.parseInt(id);
        } catch (Exception e) {
            System.out.println("Error id!");
            return "error/404";
        }
        Optional<Event> eventOpt = eventService.getEventById(idInt);
        if (eventOpt.isEmpty()) {
            return "error/404";
        }
        Event event = eventOpt.get();
        event.setEventStatus("PendingApproval");
        event.setUpdatedAt(LocalDateTime.now());
        System.out.println(event.getEventStatus());
        return "redirect:/staff/event/{id}";
    }


    @Transactional
    @PostMapping("/event/{id}/publish")
    public String publishEvent(@PathVariable("id") String id) {
        int idInt;
        try {
            idInt = Integer.parseInt(id);
        } catch (Exception e) {
            System.out.println("Error id!");
            return "error/404";
        }
        Optional<Event> eventOpt = eventService.getEventById(idInt);
        if (eventOpt.isEmpty()) {
            return "error/404";
        }
        Event event = eventOpt.get();
        event.setEventStatus("Published");
        event.setPublishedAt(LocalDateTime.now());
        System.out.println(event.getEventStatus());
        return "redirect:/staff/event/{id}";
    }


    @GetMapping("/event/{id}")
    public String eventDetail(@PathVariable("id") String id, Model model) {
        int idInt = 1;
        try {
            idInt = Integer.parseInt(id);
        } catch (Exception e) {
            System.out.println("Error id!");
            return "error/404";
        }
        Optional<Event> eventOpt = eventService.getEventById(idInt);
        if (eventOpt.isEmpty()) {
            return "error/404";
        }
        Event event = eventOpt.get();
        event.setStartDateString(DateTimeFormatter
                .ofPattern("EEE, MMM dd", Locale.ENGLISH)
                .format(event.getStartDateTime()));
        event.setStartTimeString(DateTimeFormatter
                .ofPattern("HH:mm", Locale.ENGLISH)
                .format(event.getStartDateTime()));
        Double viewCountPercent = eventService.getViewCountPercent(event.getId());
        if (viewCountPercent == null) {
            viewCountPercent = 0.0;
        }
        event.setViewCountPercent(viewCountPercent);
        model.addAttribute("event", event);
        TicketType ticket = new TicketType();
        ticket.setEvent(event);
        //lấy danh sách khách hàng đã đăng ký sự kiện
        model.addAttribute("registrations", customerService.getRegistedCustomer(idInt));
        //lấy tổng doanh thu nhận được từ sự kiện
        BigDecimal totalAmount = paymentService.getTotlAmount(idInt);
        if (totalAmount == null) {
            totalAmount = BigDecimal.valueOf(0);
        }
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("revenue", paymentService.getTotlAmount(idInt));
        model.addAttribute("ticket", ticket);
        model.addAttribute("checkedInCount", ticketRepo.countCheckedInByEvent(idInt));
        model.addAttribute("totalRegistered", ticketRepo.countRegisteredByEvent(idInt));

        return "staff/event/detail";
    }

    @GetMapping("demo/{id}")
    public String previewEvent(@PathVariable("id") String id, Model model) {
        int idInt = 0;
        try {
            idInt = Integer.parseInt(id);
        } catch (Exception e) {
            System.out.println("Error id!");
            return "error/404";
        }
        Optional<Event> eventOptional = eventService.getEventById(idInt);
        if (eventOptional.isEmpty()) {
            return "error/404";
        }
        Event event = eventOptional.get();
        List<TicketType> ticketTypes = ticketTypeService.getActiveTicketTypes(idInt);
        LocalDateTime stopSellTime = event.getStartDateTime().minusHours(12);
        boolean canBuy = event.getEventStatus().equals("Published") &&
                event.getCurrentAttendees() < event.getMaxAttendees() && LocalDateTime.now().isBefore(stopSellTime);
        model.addAttribute("ticketTypes", ticketTypes);
        model.addAttribute("event", eventOptional.get());
        model.addAttribute("organizer", event);
        model.addAttribute("venue", event.getVenue());
        model.addAttribute("stopSellTime", stopSellTime);
        model.addAttribute("canBuy", canBuy);
        return "home/eventDetailDemo";
    }

    @PostMapping("/event/{id}/edit/cancel")
    public String cancelEditing(
            @PathVariable("id") String id,
            HttpSession session,
            @RequestParam("newLinkBanner") String newLinkBanner,
            @RequestParam("newLinkThumbnail") String newLinkThumbnail
    ) {
        int idInt = 0;
        if (UrlValition.getIntegerId(id) != null) {
            idInt = Integer.parseInt(id);
        }
        Optional<Event> eventOptional = eventService.getEventById(idInt);
        if (eventOptional.isEmpty()) {
            return "error/404";
        }
        Event event = eventOptional.get();
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
        return "redirect:/staff/event/" + event.getId();
    }

    @Transactional
    @PostMapping("/event/{id}/edit")
    public String editEvent(
            @PathVariable("id") String id,
            @RequestParam("title") String title,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("shortDescript") String shortDescript,
            @RequestParam("fullDescript") String fullDescript,
            @RequestParam("type") String type,
            @RequestParam("status") String status,
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
            HttpSession session
    ) {
        // Validation result
        ValidationResult result = new ValidationResult();
        // Messege
        Boolean hasMessage = false;
        String errorTitle = "";
        String message = "";
        // Check event validation
        int idInt = 0;
        if (UrlValition.getIntegerId(id) != null) {
            idInt = Integer.parseInt(id);
        }
        Optional<Event> eventOptional = eventService.getEventById(idInt);
        if (eventOptional.isEmpty()) {
            return "error/404";
        }
        Event event = eventOptional.get();

        List<String> eventStatusCanEdit = Arrays.asList(
                "Rejected",
                "PendingApproval",
                "Draft"
        );
        if (!eventStatusCanEdit.contains(event.getEventStatus())) {
            result.setErrorTitle("This event is starting. You can not edit!");
            result.setMessage("Sự kiện đang diễn ra, bạn có thể coi, không thể edit!");
            return handleCannotEdit(result, redirectAttributes, event);
        }

        // BANNER
        String banner = (String) session.getAttribute("newLinkBanner");
        if (banner != null && !banner.isBlank() && Files.exists(Path.of(uploadRoot + banner))) {
            System.out.println("Xóa banner cũ:");
            System.out.println(event.getBannerImageUrl());
            fileIO.deleteFile("/upload/event/banner/" + event.getBannerImageUrl());
            event.setBannerImageUrl((String) session.getAttribute("fileNameBanner"));
            fileIO.moveFile(banner, "/upload/event/banner/" + (String) session.getAttribute("fileNameBanner"));
            session.removeAttribute("newLinkBanner");
            session.removeAttribute("hasNewBanner");
            session.removeAttribute("fileNameBanner");
        } else {
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
        } else {
            System.out.println("Không có thumbnail mới để di chuyển!");

            System.out.println("DOES NOT HAVE NEW THUMBNAIL");
        }
        System.out.println("===============================================================");

        // Check validation

        // Check title:
        if (eventInfoValidation.checkTitle(title, result)) {
            event.setTitle(title.trim());
        } else {
            return handleValidationError(result, redirectAttributes, event);
        }

        // Check category:
        if (eventInfoValidation.checkCategoryId(categoryId, result)) {
            event.setCategory(categoryService.getCategoryById(Integer.parseInt(categoryId)).get());
        } else {
            return handleValidationError(result, redirectAttributes, event);
        }

        // Check short description
        if (eventInfoValidation.checkShortDescript(shortDescript, result)) {
            event.setShortDescription(shortDescript.trim());
        } else {
            return handleValidationError(result, redirectAttributes, event);
        }

        // Check full descript
        if (eventInfoValidation.checkFullDescript(fullDescript, result)) {
            event.setDescription(fullDescript.trim());
        } else {
            return handleValidationError(result, redirectAttributes, event);
        }

        // Check event type
        if (eventInfoValidation.checkType(type, result)) {
            event.setEventType(type);
        } else {
            return handleValidationError(result, redirectAttributes, event);
        }

        // Check event status
        if (eventInfoValidation.checkStatus(status, result)) {
            event.setEventStatus(status);
        } else {
            return handleValidationError(result, redirectAttributes, event);
        }

        // Check event's currency
        if (eventInfoValidation.checkCurrency(currency, result)) {
            event.setCurrency(currency);
        } else {
            return handleValidationError(result, redirectAttributes, event);
        }

        // Check Organizer
        if (eventInfoValidation.checkOrganizer(organizerId, result)) {
            event.setOrganizer(organizerService.getOrganizerById(Integer.parseInt(organizerId)).get());
        } else {
            return handleValidationError(result, redirectAttributes, event);
        }

        // Check Venue
        if (eventInfoValidation.checkVenueId(venueId, result)) {
            event.setVenue(venueService.getVenueById(Integer.parseInt(venueId)).get());
        } else {
            return handleValidationError(result, redirectAttributes, event);
        }

        // Check date:
        if (eventInfoValidation.checkDateTime(startDate, startTime, result)) {
            // do nothing
        } else {
            return handleValidationError(result, redirectAttributes, event);
        }

        if (eventInfoValidation.checkDateTime(endDate, endTime, result)) {
            // do nothing
        } else {
            return handleValidationError(result, redirectAttributes, event);
        }

        if (eventInfoValidation.checkDateTime(registrationDate, registrationTime, result)) {
            //
        } else {
            return handleValidationError(result, redirectAttributes, event);
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
        } else {
            return handleValidationError(result, redirectAttributes, event);
        }
        event.setUpdatedAt(LocalDateTime.now());
        return "redirect:/staff/event/" + event.getId();
    }

    @GetMapping("/event/{id}/edit")
    public String editEvent(
            @PathVariable("id") String id,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // Check event validation
        int idInt = 0;
        if (UrlValition.getIntegerId(id) != null) {
            idInt = Integer.parseInt(id);
        }
        Optional<Event> eventOptional = eventService.getEventById(idInt);
        if (eventOptional.isEmpty()) {
            return "error/404";
        }
        Event event = eventOptional.get();
        List<String> eventStatusCanEdit = Arrays.asList(
                "Rejected",
                "PendingApproval",
                "Draft"
        );
        if (!eventStatusCanEdit.contains(event.getEventStatus())) {
            ValidationResult result = new ValidationResult();
            result.setErrorTitle("This event is starting. You can not edit!");
            result.setMessage("Sự kiện đang diễn ra, bạn có thể coi, không thể edit!");
            return handleCannotEdit(result, redirectAttributes, event);
        }
//        List<TicketType> listTicketTypes = ticketTypeService.getAllTicketType();
//        model.addAttribute("listTicketType", listTicketTypes);

        List<Category> listCategories = categoryService.getAllCategory();
        List<Venue> listVenue = venueRepo.findAll();
        List<Organizer> listOrganizers = organizerService.getAllOrganizer();
        model.addAttribute("listOrganizers", listOrganizers);
        model.addAttribute("listVenue", listVenue);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("event", event);

        model.addAttribute("organizer", new Organizer());
        model.addAttribute("venue", new Venue());

        // Set session of link banner and thumbnail
        String newLinkBanner = (String) session.getAttribute("newLinkBanner");
        String newLinkThumbnail = (String) session.getAttribute("newLinkThumbnail");
        Boolean hasNewBanner = (Boolean) session.getAttribute("hasNewBanner");
        Boolean hasNewThumbnail = (Boolean) session.getAttribute("hasNewThumbnail");
        model.addAttribute("newLinkBanner", newLinkBanner);
        model.addAttribute("newLinkThumbnail", newLinkThumbnail);
        model.addAttribute("hasNewBanner", hasNewBanner);
        model.addAttribute("hasNewThumbnail", hasNewThumbnail);
        return "staff/event/edit";
    }

    @PostMapping("/event/{id}/edit/images")
    public String editEventImages(
            @PathVariable("id") String id,
            @RequestParam(value = "banner", required = false) MultipartFile banner,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "uploadBanner", required = false, defaultValue = "false") boolean uploadBanner,
            @RequestParam(value = "uploadThumbnail", required = false, defaultValue = "false") boolean uploadThumbnail,
            HttpSession session
    ) {
        // Check event is valid:
        int idInt = 0;
        if (UrlValition.getIntegerId(id) != null) {
            idInt = Integer.parseInt(id);
        }
        Optional<Event> eventOptional = eventService.getEventById(idInt);
        if (eventOptional.isEmpty()) {
            return "error/404";
        }
        Event event = eventOptional.get();

        // Check edit is accept?
        List<String> eventStatusCanEdit = Arrays.asList(
                "Rejected",
                "PendingApproval",
                "Draft"
        );
        if (!eventStatusCanEdit.contains(event.getEventStatus())) {
            ValidationResult result = new ValidationResult();
            result.setErrorTitle("This event is starting. You can not edit!");
            result.setMessage("Sự kiện đang diễn ra, bạn có thể coi, không thể edit!");
            return handleCannotEdit(result, redirectAttributes, event);
        }
        // Create messege
        boolean hasMessage = false;
        String errorTitle = "";
        String message = "";
        System.out.println("***************************************");

        // Create link of banner and thumbnail:
        String newLinkBanner = "";
        String newLinkThumbnail = "";
        System.out.println("uploadBanner: " + uploadBanner);
        System.out.println("uploadThumbnail: " + uploadThumbnail);
        // Check if already have banner in session:
        // Chỗ này yêu cầu rất chặt là check kỹ xem trong thư mục banner or thumbnail đã có file chưa?
        // Chỉ được lưu tối đa 1 file trên session để tránh tràn bộ nhớ!
        // Check banner and thumbnail is already upload?


//        fileIO.deleteFile((String) session.getAttribute("newLinkBanner"));
//        fileIO.deleteFile((String) session.getAttribute("newLinkThumbnail"));
//        session.removeAttribute("newLinkBanner");
//        session.removeAttribute("newLinkThumbnail");
//        session.removeAttribute("hasNewBanner");
//        session.removeAttribute("hasNewThumbnail");
//        session.removeAttribute("fileNameBanner");
//        session.removeAttribute("fileNameThumbnail");

        // BANNER
        if (uploadBanner) {
            Boolean hasNewBanner = false;
            String newLinkBannerSession;
            try {
                hasNewBanner = (Boolean) session.getAttribute("hasNewBanner");
                newLinkBannerSession = (String) session.getAttribute("newLinkBanner");
                if (hasNewBanner == true && newLinkBannerSession != null) {
                    // Detele session
                    session.removeAttribute("hasNewBanner");
                    session.removeAttribute("newLinkBanner");
                    // Remove file
                    fileIO.deleteFile(newLinkBannerSession);
                }
            } catch (Exception e) {
                //
            }
            if (banner.isEmpty()) {
                hasMessage = true;
                errorTitle += "File not found!";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/" + event.getId() + "/edit";
            }

            if (banner.getSize() > 20 * 1024 * 1024) {
                hasMessage = true;
                errorTitle += "File Too Large";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/" + event.getId() + "/edit";
            }
            String contentType = banner.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                hasMessage = true;
                errorTitle += "File is invalid!";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/" + event.getId() + "/edit";
            }
            String filename = banner.getOriginalFilename();
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            List<String> allowedExt = List.of("jpg", "jpeg", "png", "webp");

            if (!allowedExt.contains(ext)) {
                hasMessage = true;
                errorTitle += "File is not image!";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/" + event.getId() + "/edit";
            }

            try (InputStream is = banner.getInputStream()) {
                byte[] header = new byte[8];
                int bytesRead = is.read(header);

                if (bytesRead < 4) {
                    hasMessage = true;
                    errorTitle += "File is too small!";
                    message += "File quá nhỏ, không phải ảnh hợp lệ!";
                    redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                    redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                    redirectAttributes.addFlashAttribute("message", message);
                    return "redirect:/staff/event/" + event.getId() + "/edit";
                }

                // JPG: FF D8 FF
                if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
                    // JPEG OK
                }
                // PNG: 89 50 4E 47
                else if (header[0] == (byte) 0x89 &&
                        header[1] == (byte) 0x50 &&
                        header[2] == (byte) 0x4E &&
                        header[3] == (byte) 0x47) {
                    // PNG OK
                } else {
                    hasMessage = true;
                    errorTitle += "File is not a real images!";
                    message += "File quá nhỏ, không phải ảnh hợp lệ!";
                    redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                    redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                    redirectAttributes.addFlashAttribute("message", message);
                    return "redirect:/staff/event/" + event.getId() + "/edit";
                }

            } catch (Exception e) {
                hasMessage = true;
                errorTitle += "File is not a real images!";
                message += "File quá nhỏ, không phải ảnh hợp lệ!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/" + event.getId() + "/edit";
            }

            try {
                // Save images to temporal folder:
//                    String fileName = banner.getOriginalFilename();
                String fileName = UUID.randomUUID() + ".webp";
                String bannerUrl = event.getId() + "_" + fileName;
                Path path = Paths.get(uploadRoot + "/upload/temporal/event/banner/" + event.getId() + "_" + fileName);
                Files.createDirectories(path.getParent());
                // Resize banner
                imageService.resizeAndConvertToWebP(banner.getInputStream(), path, 1920, 0.7f);
//                    Files.write(path, banner.getBytes());
                System.out.println("Lưu thành công!");
                System.out.println("File đã được lưu vào: " + path);
                // Set new link banner
                newLinkBanner += "/upload/temporal/event/banner/" + event.getId() + "_" + fileName;
                // Save new link to session:
                session.setAttribute("newLinkBanner", newLinkBanner);
                session.setAttribute("hasNewBanner", true);
                session.setAttribute("fileNameBanner", event.getId() + "_" + fileName);

                // Send redirect attribute:
//                    redirectAttributes.addFlashAttribute("hasNewBanner", true);
            } catch (IOException e) {
                hasMessage = true;
                errorTitle += "Có lỗi trong quá trình upload ảnh!";
                message += "Vui lòng thử lại sau ít phút!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
            }
        }
        // THUMBNAIL
        else if (uploadThumbnail) {
            Boolean hasNewThumbnail = false;
            String newLinkThumbnailSession;
            try {
                hasNewThumbnail = (Boolean) session.getAttribute("hasNewThumbnail");
                newLinkThumbnailSession = (String) session.getAttribute("newLinkThumbnail");
                if (hasNewThumbnail == true && newLinkThumbnailSession != null) {
                    // Detele session
                    session.removeAttribute("hasNewThumbnail");
                    session.removeAttribute("newLinkThumbnail");
                    // Remove file
                    fileIO.deleteFile(newLinkThumbnailSession);
                }
            } catch (Exception e) {
                //
            }
            if (thumbnail.isEmpty()) {
                hasMessage = true;
                errorTitle += "File not found!";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/" + event.getId() + "/edit";
            }

            if (thumbnail.getSize() > 20 * 1024 * 1024) {
                hasMessage = true;
                errorTitle += "File Too Large";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/" + event.getId() + "/edit";
            }
            String contentType = thumbnail.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                hasMessage = true;
                errorTitle += "File is invalid!";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/" + event.getId() + "/edit";
            }
            String filename = thumbnail.getOriginalFilename();
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            List<String> allowedExt = List.of("jpg", "jpeg", "png", "webp");

            if (!allowedExt.contains(ext)) {
                hasMessage = true;
                errorTitle += "File is not image!";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/" + event.getId() + "/edit";
            }

            try (InputStream is = thumbnail.getInputStream()) {
                byte[] header = new byte[8];
                int bytesRead = is.read(header);

                if (bytesRead < 4) {
                    hasMessage = true;
                    errorTitle += "File is too small!";
                    message += "File quá nhỏ, không phải ảnh hợp lệ!";
                    redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                    redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                    redirectAttributes.addFlashAttribute("message", message);
                    return "redirect:/staff/event/" + event.getId() + "/edit";
                }

                // JPG: FF D8 FF
                if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
                    // JPEG OK
                }
                // PNG: 89 50 4E 47
                else if (header[0] == (byte) 0x89 &&
                        header[1] == (byte) 0x50 &&
                        header[2] == (byte) 0x4E &&
                        header[3] == (byte) 0x47) {
                    // PNG OK
                } else {
                    hasMessage = true;
                    errorTitle += "File is not a real images!";
                    message += "File quá nhỏ, không phải ảnh hợp lệ!";
                    redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                    redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                    redirectAttributes.addFlashAttribute("message", message);
                    return "redirect:/staff/event/" + event.getId() + "/edit";
                }

            } catch (Exception e) {
                hasMessage = true;
                errorTitle += "File is not a real images!";
                message += "File quá nhỏ, không phải ảnh hợp lệ!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/" + event.getId() + "/edit";
            }

            try {
                // Save images to temporal folder:
//                    String fileName = thumbnail.getOriginalFilename();
                String fileName = UUID.randomUUID() + ".webp";
                String thumbnailUrl = event.getId() + "_" + fileName;
                Path path = Paths.get(uploadRoot + "/upload/temporal/event/thumbnail/" + event.getId() + "_" + fileName);
                Files.createDirectories(path.getParent());
                // Resize:
                imageService.resizeAndConvertToWebP(thumbnail.getInputStream(), path, 600, 0.5f);
//                    Files.write(path, thumbnail.getBytes());
                System.out.println("Lưu thành công!");
                System.out.println("File đã được lưu vào: " + path);
                // Set new link thumbnail
                newLinkThumbnail += "/upload/temporal/event/thumbnail/" + thumbnailUrl;
                // Save link to session:
                session.setAttribute("newLinkThumbnail", newLinkThumbnail);
                // Send redirect attribute:
//                    redirectAttributes.addFlashAttribute("hasNewThumbnail", true);
                session.setAttribute("hasNewThumbnail", true);
                session.setAttribute("fileNameThumbnail", event.getId() + "_" + fileName);

            } catch (IOException e) {
                hasMessage = true;
                errorTitle += "Có lỗi trong quá trình upload ảnh!";
                message += "Vui lòng thử lại sau ít phút!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
            }
        }
        return "redirect:/staff/event/" + event.getId() + "/edit";
    }


    @Transactional
    @PostMapping("/event/{id}/edit/banner")
    public String updateBanner(
            @PathVariable("id") String id,
            @RequestParam("banner") MultipartFile banner,
            RedirectAttributes redirectAttributes
    ) {
        int idInt = 0;
        if (UrlValition.getIntegerId(id) != null) {
            idInt = Integer.parseInt(id);
        }
        Optional<Event> eventOptional = eventService.getEventById(idInt);
        if (eventOptional.isEmpty()) {
            return "error/404";
        }
        Event event = eventOptional.get();

        // Check edit:
        List<String> eventStatusCanEdit = Arrays.asList(
                "Rejected",
                "PendingApproval",
                "Draft"
        );
        if (!eventStatusCanEdit.contains(event.getEventStatus())) {
            ValidationResult result = new ValidationResult();
            result.setErrorTitle("This event is starting. You can not edit!");
            result.setMessage("Sự kiện đang diễn ra, bạn có thể coi, không thể edit!");
            return handleCannotEdit(result, redirectAttributes, event);
        }

        // Message
        boolean hasMessage = false;
        String errorTitle = "";
        String message = "";
        System.out.println("***************************************");
        System.out.println(banner.getOriginalFilename());
        System.out.println(banner.getSize());
        System.out.println(banner.getContentType());
        if (banner.isEmpty()) {
            hasMessage = true;
            errorTitle += "File not found!";
            message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
            redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
            redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/staff/event/" + event.getId();
        }

        if (banner.getSize() > 20 * 1024 * 1024) {
            hasMessage = true;
            errorTitle += "File Too Large";
            message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
            redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
            redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/staff/event/" + event.getId();
        }
        String contentType = banner.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            hasMessage = true;
            errorTitle += "File is invalid!";
            message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
            redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
            redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/staff/event/" + event.getId();
        }
        String filename = banner.getOriginalFilename();
        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        List<String> allowedExt = List.of("jpg", "jpeg", "png", "webp");

        if (!allowedExt.contains(ext)) {
            hasMessage = true;
            errorTitle += "File is not image!";
            message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
            redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
            redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/staff/event/" + event.getId();
        }

        try (InputStream is = banner.getInputStream()) {
            byte[] header = new byte[8];
            int bytesRead = is.read(header);

            if (bytesRead < 4) {
                hasMessage = true;
                errorTitle += "File is too small!";
                message += "File quá nhỏ, không phải ảnh hợp lệ!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/" + event.getId();
            }

            // JPG: FF D8 FF
            if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
                // JPEG OK
            }
            // PNG: 89 50 4E 47
            else if (header[0] == (byte) 0x89 &&
                    header[1] == (byte) 0x50 &&
                    header[2] == (byte) 0x4E &&
                    header[3] == (byte) 0x47) {
                // PNG OK
            } else {
                hasMessage = true;
                errorTitle += "File is not a real images!";
                message += "File quá nhỏ, không phải ảnh hợp lệ!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/" + event.getId();
            }

        } catch (Exception e) {
            hasMessage = true;
            errorTitle += "File is not a real images!";
            message += "File quá nhỏ, không phải ảnh hợp lệ!";
            redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
            redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/staff/event/" + event.getId();
        }

        try {
//            String fileName = banner.getOriginalFilename();
            String fileName = UUID.randomUUID() + ".webp";
            String bannerUrl = event.getId() + "_" + fileName;
            Path path = Paths.get(uploadRoot + "/upload/event/banner/" + event.getId() + "_" + fileName);
            Files.createDirectories(path.getParent());
            // Resize img
            imageService.resizeAndConvertToWebP(banner.getInputStream(), path, 1920, 0.7f);
            //
//            Files.write(path, banner.getBytes());
            System.out.println("Lưu thành công!");
            System.out.println("File đã được lưu vào: " + path);
            // Xoa anh cu
            Files.deleteIfExists(Paths.get(uploadRoot + "/upload/event/banner/" + event.getBannerImageUrl()));
            // Update link anh moi
            event.setBannerImageUrl(bannerUrl);
        } catch (IOException e) {
            hasMessage = true;
            errorTitle += "Có lỗi trong quá trình upload ảnh!";
            message += "Vui lòng thử lại sau ít phút!";
            redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
            redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
            redirectAttributes.addFlashAttribute("message", message);
        }
        event.setUpdatedAt(LocalDateTime.now());
        return "redirect:/staff/event/" + event.getId();
    }

//    @GetMapping("/event/create")
//    public String createEvent(
//            Model model,
//            HttpSession session
//    ) {
//        // Get categories
//        List<Category> listCategories = categoryService.getAllCategory();
//        List<Venue> listVenue = venueRepo.findAll();
//        List<Organizer> listOrganizers = organizerService.getAllOrganizer();
//        model.addAttribute("listOrganizers", listOrganizers);
//        model.addAttribute("listVenue", listVenue);
//        model.addAttribute("listCategories", listCategories);
//
//        // Set session of link banner and thumbnail
//        String newLinkBanner = (String) session.getAttribute("newLinkBanner");
//        String newLinkThumbnail = (String) session.getAttribute("newLinkThumbnail");
//        Boolean hasNewBanner = (Boolean) session.getAttribute("hasNewBanner");
//        Boolean hasNewThumbnail = (Boolean) session.getAttribute("hasNewThumbnail");
//        model.addAttribute("newLinkBanner", newLinkBanner);
//        model.addAttribute("newLinkThumbnail", newLinkThumbnail);
//        model.addAttribute("hasNewBanner", hasNewBanner);
//        model.addAttribute("hasNewThumbnail", hasNewThumbnail);
//        model.addAttribute("event", new Event());
//        return "staff/event/create";
//    }

    @PostMapping("/event/create/images")
    public String createEventImages(
            @RequestParam(value = "banner", required = false) MultipartFile banner,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "uploadBanner", required = false, defaultValue = "false") boolean uploadBanner,
            @RequestParam(value = "uploadThumbnail", required = false, defaultValue = "false") boolean uploadThumbnail,
            HttpSession session
    ) {

        // Create messege
        boolean hasMessage = false;
        String errorTitle = "";
        String message = "";
        System.out.println("***************************************");

        // Create link of banner and thumbnail:
        String newLinkBanner = "";
        String newLinkThumbnail = "";
        System.out.println("uploadBanner: " + uploadBanner);
        System.out.println("uploadThumbnail: " + uploadThumbnail);

        // BANNER
        if (uploadBanner) {
            Boolean hasNewBanner = false;
            String newLinkBannerSession;
            try {
                hasNewBanner = (Boolean) session.getAttribute("hasNewBanner");
                newLinkBannerSession = (String) session.getAttribute("newLinkBanner");
                if (hasNewBanner == true && newLinkBannerSession != null) {
                    // Detele session
                    session.removeAttribute("hasNewBanner");
                    session.removeAttribute("newLinkBanner");
                    // Remove file
                    fileIO.deleteFile(newLinkBannerSession);
                }
            } catch (Exception e) {
                //
            }
            if (banner.isEmpty()) {
                hasMessage = true;
                errorTitle += "File not found!";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/create";
            }

            if (banner.getSize() > 20 * 1024 * 1024) {
                hasMessage = true;
                errorTitle += "File Too Large";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/create";
            }
            String contentType = banner.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                hasMessage = true;
                errorTitle += "File is invalid!";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/create";
            }

            String filename = banner.getOriginalFilename();

            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            List<String> allowedExt = List.of("jpg", "jpeg", "png", "webp");

            if (!allowedExt.contains(ext)) {
                hasMessage = true;
                errorTitle += "File is not image!";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/create";
            }

            try (InputStream is = banner.getInputStream()) {
                byte[] header = new byte[8];
                int bytesRead = is.read(header);

                if (bytesRead < 4) {
                    hasMessage = true;
                    errorTitle += "File is too small!";
                    message += "The file is too small to be a valid image!";
                    redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                    redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                    redirectAttributes.addFlashAttribute("message", message);
                    return "redirect:/staff/event/create";
                }

                // JPG: FF D8 FF
                if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
                    // JPEG OK
                }
                // PNG: 89 50 4E 47
                else if (header[0] == (byte) 0x89 &&
                        header[1] == (byte) 0x50 &&
                        header[2] == (byte) 0x4E &&
                        header[3] == (byte) 0x47) {
                    // PNG OK
                } else {
                    hasMessage = true;
                    errorTitle += "File is not a real images!";
                    message += "The file is too small to be a valid image!";
                    redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                    redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                    redirectAttributes.addFlashAttribute("message", message);
                    return "redirect:/staff/event/create";
                }

            } catch (Exception e) {
                hasMessage = true;
                errorTitle += "File is not a real images!";
                message += "The file is too small to be a valid image!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/create";
            }

            try {
                // Save images to temporal folder:
//                    String fileName = banner.getOriginalFilename();
                String fileName = UUID.randomUUID() + ".webp";
                String bannerUrl = "createEvent" + "_" + fileName;
                Path path = Paths.get(uploadRoot + "/upload/temporal/event/banner/" + "createEvent" + "_" + fileName);
                Files.createDirectories(path.getParent());
                // Resize banner
                imageService.resizeAndConvertToWebP(banner.getInputStream(), path, 1920, 0.7f);
//                    Files.write(path, banner.getBytes());
                System.out.println("Lưu thành công!");
                System.out.println("File đã được lưu vào: " + path);
                // Set new link banner
                newLinkBanner += "/upload/temporal/event/banner/" + "createEvent" + "_" + fileName;
                // Save new link to session:
                session.setAttribute("newLinkBanner", newLinkBanner);
                session.setAttribute("hasNewBanner", true);
                session.setAttribute("fileNameBanner", "createEvent" + "_" + fileName);

                // Send redirect attribute:
//                    redirectAttributes.addFlashAttribute("hasNewBanner", true);
            } catch (IOException e) {
                hasMessage = true;
                errorTitle += "Có lỗi trong quá trình upload ảnh!";
                message += "Vui lòng thử lại sau ít phút!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
            }
        }
        // THUMBNAIL
        else if (uploadThumbnail) {
            Boolean hasNewThumbnail = false;
            String newLinkThumbnailSession;
            try {
                hasNewThumbnail = (Boolean) session.getAttribute("hasNewThumbnail");
                newLinkThumbnailSession = (String) session.getAttribute("newLinkThumbnail");
                if (hasNewThumbnail == true && newLinkThumbnailSession != null) {
                    // Detele session
                    session.removeAttribute("hasNewThumbnail");
                    session.removeAttribute("newLinkThumbnail");
                    // Remove file
                    fileIO.deleteFile(newLinkThumbnailSession);
                }
            } catch (Exception e) {
                //
            }
            if (thumbnail.isEmpty()) {
                hasMessage = true;
                errorTitle += "File not found!";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/create";
            }

            if (thumbnail.getSize() > 20 * 1024 * 1024) {
                hasMessage = true;
                errorTitle += "File Too Large";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/create";

            }
            String contentType = thumbnail.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                hasMessage = true;
                errorTitle += "File is invalid!";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/create";
            }
            String filename = thumbnail.getOriginalFilename();
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            List<String> allowedExt = List.of("jpg", "jpeg", "png", "webp");

            if (!allowedExt.contains(ext)) {
                hasMessage = true;
                errorTitle += "File is not image!";
                message += "The selected image exceeds the maximum size limit of 5MB. Please choose a smaller file!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/create";
            }

            try (InputStream is = thumbnail.getInputStream()) {
                byte[] header = new byte[8];
                int bytesRead = is.read(header);

                if (bytesRead < 4) {
                    hasMessage = true;
                    errorTitle += "File is too small!";
                    message += "File quá nhỏ, không phải ảnh hợp lệ!";
                    redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                    redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                    redirectAttributes.addFlashAttribute("message", message);
                    return "redirect:/staff/event/create";
                }

                // JPG: FF D8 FF
                if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
                    // JPEG OK
                }
                // PNG: 89 50 4E 47
                else if (header[0] == (byte) 0x89 &&
                        header[1] == (byte) 0x50 &&
                        header[2] == (byte) 0x4E &&
                        header[3] == (byte) 0x47) {
                    // PNG OK
                } else {
                    hasMessage = true;
                    errorTitle += "File is not a real images!";
                    message += "File quá nhỏ, không phải ảnh hợp lệ!";
                    redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                    redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                    redirectAttributes.addFlashAttribute("message", message);
                    return "redirect:/staff/event/create";

                }

            } catch (Exception e) {
                hasMessage = true;
                errorTitle += "File is not a real images!";
                message += "File quá nhỏ, không phải ảnh hợp lệ!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/staff/event/create";

            }

            try {
                // Save images to temporal folder:
//                    String fileName = thumbnail.getOriginalFilename();
                String fileName = UUID.randomUUID() + ".webp";
                String thumbnailUrl = "createEvent" + "_" + fileName;
                Path path = Paths.get(uploadRoot + "/upload/temporal/event/thumbnail/" + "createEvent" + "_" + fileName);
                Files.createDirectories(path.getParent());
                // Resize:
                imageService.resizeAndConvertToWebP(thumbnail.getInputStream(), path, 600, 0.5f);
//                    Files.write(path, thumbnail.getBytes());
                System.out.println("Lưu thành công!");
                System.out.println("File đã được lưu vào: " + path);
                // Set new link thumbnail
                newLinkThumbnail += "/upload/temporal/event/thumbnail/" + thumbnailUrl;
                // Save link to session:
                session.setAttribute("newLinkThumbnail", newLinkThumbnail);
                // Send redirect attribute:
//                    redirectAttributes.addFlashAttribute("hasNewThumbnail", true);
                session.setAttribute("hasNewThumbnail", true);
                session.setAttribute("fileNameThumbnail", "createEvent" + "_" + fileName);

            } catch (IOException e) {
                hasMessage = true;
                errorTitle += "Có lỗi trong quá trình upload ảnh!";
                message += "Vui lòng thử lại sau ít phút!";
                redirectAttributes.addFlashAttribute("hasMessage", hasMessage);
                redirectAttributes.addFlashAttribute("errorTitle", errorTitle);
                redirectAttributes.addFlashAttribute("message", message);
            }
        }
        return "redirect:/staff/event/create";
    }

//    @Transactional
//    @PostMapping("/event/create")
//    public String createEvent(
//            @RequestParam("title") String title,
//            @RequestParam("categoryId") String categoryId,
//            @RequestParam("shortDescript") String shortDescript,
//            @RequestParam("fullDescript") String fullDescript,
//            @RequestParam("type") String type,
//            @RequestParam("currency") String currency,
//            @RequestParam("organizerId") String organizerId,
//            @RequestParam("startDate") String startDate,
//            @RequestParam("startTime") String startTime,
//            @RequestParam("endDate") String endDate,
//            @RequestParam("endTime") String endTime,
//            @RequestParam("registrationDate") String registrationDate,
//            @RequestParam("registrationTime") String registrationTime,
//            @RequestParam("venueId") String venueId,
//            @RequestParam("onlineMeetingLink") String onlineMeetingLink,
//            RedirectAttributes redirectAttributes,
//            HttpSession session,
//            @ModelAttribute("event") Event event
//    ) {
//        // Validation result
//        ValidationResult result = new ValidationResult();
//        // Messege
//        Boolean hasMessage = false;
//        String errorTitle = "";
//        String message = "";
//
//        // Create new Event
//        Event event = new Event();
//
//        // BANNER
//        String banner = (String) session.getAttribute("newLinkBanner");
//        if (banner != null && !banner.isBlank() && Files.exists(Path.of(uploadRoot + banner))) {
//            event.setBannerImageUrl((String) session.getAttribute("fileNameBanner"));
//            fileIO.moveFile(banner, "/upload/event/banner/" + (String) session.getAttribute("fileNameBanner"));
//            session.removeAttribute("newLinkBanner");
//            session.removeAttribute("hasNewBanner");
//            session.removeAttribute("fileNameBanner");
//        }
//        else {
//            System.out.println("Không có banner mới để di chuyển!");
//            System.out.println("DOES NOT HAVE NEW BANNER");
//        }
//        System.out.println("===============================================================");
//
//        // THUMBNAIL
//        String thumbnail = (String) session.getAttribute("newLinkThumbnail");
//        if (thumbnail != null && !thumbnail.isBlank() && Files.exists(Path.of(uploadRoot + thumbnail))) {
//            System.out.println("Xóa thumbnail cũ:");
//            System.out.println(event.getThumbnailUrl());
//            fileIO.deleteFile("/upload/event/thumbnail/" + event.getThumbnailUrl());
//            event.setThumbnailUrl((String) session.getAttribute("fileNameThumbnail"));
//            fileIO.moveFile(thumbnail, "/upload/event/thumbnail/" + (String) session.getAttribute("fileNameThumbnail"));
//            session.removeAttribute("newLinkThumbnail");
//            session.removeAttribute("hasNewThumbnail");
//            session.removeAttribute("fileNameThumbnail");
//        }
//        else {
//            System.out.println("Không có thumbnail mới để di chuyển!");
//
//            System.out.println("DOES NOT HAVE NEW THUMBNAIL");
//        }
//        System.out.println("===============================================================");
//
//        // Check validation
//
//        // Check title:
//        if (eventInfoValidation.checkTitle(title, result)) {
//            event.setTitle(title.trim());
//        }
//        else {
//            return handleCannotCreate(result, redirectAttributes, event);
//        }
//
//        // Check category:
//        if (eventInfoValidation.checkCategoryId(categoryId, result)) {
//            event.setCategory(categoryService.getCategoryById(Integer.parseInt(categoryId)).get());
//        }
//        else {
//            return handleCannotCreate(result, redirectAttributes, event);
//        }
//
//        // Check short description
//        if (eventInfoValidation.checkShortDescript(shortDescript, result)) {
//            event.setShortDescription(shortDescript.trim());
//        } else {
//            return handleCannotCreate(result, redirectAttributes, event);
//        }
//
//        // Check full descript
//        if (eventInfoValidation.checkFullDescript(fullDescript, result)) {
//            event.setDescription(fullDescript.trim());
//        }
//        else {
//            return handleCannotCreate(result, redirectAttributes, event);
//        }
//
//        // Check event type
//        if (eventInfoValidation.checkType(type, result)) {
//            event.setEventType(type);
//        } else {
//            return handleCannotCreate(result, redirectAttributes, event);
//        }
//
//        // Check event status

    /// /        if (eventInfoValidation.checkStatus(status, result)) {
    /// /            event.setEventStatus(status);
    /// /        }
    /// /        else {
    /// /            return handleCannotCreate(result, redirectAttributes, event);
    /// /        }
//        event.setEventStatus("Draft");
//        // Check event's currency
//        if (eventInfoValidation.checkCurrency(currency, result)) {
//            event.setCurrency(currency);
//        } else {
//            return handleCannotCreate(result, redirectAttributes, event);
//        }
//
//        // Check Organizer
//        if (eventInfoValidation.checkOrganizer(organizerId, result)) {
//            event.setOrganizer(organizerService.getOrganizerById(Integer.parseInt(organizerId)).get());
//        }
//        else {
//            return handleCannotCreate(result, redirectAttributes, event);
//        }
//
//        // Check Venue
//        if (eventInfoValidation.checkVenueId(venueId, result)) {
//            event.setVenue(venueService.getVenueById(Integer.parseInt(venueId)).get());
//        }
//        else {
//            return handleCannotCreate(result, redirectAttributes, event);
//        }
//
//        // Check date:
//        if (eventInfoValidation.checkDateTime(startDate, startTime, result)) {
//            // do nothing
//        } else {
//            return handleCannotCreate(result, redirectAttributes, event);
//        }
//
//        if (eventInfoValidation.checkDateTime(endDate, endTime, result)) {
//            // do nothing
//        } else {
//            return handleCannotCreate(result, redirectAttributes, event);
//        }
//
//        if (eventInfoValidation.checkDateTime(registrationDate, registrationTime, result)) {
//            //
//        } else {
//            return handleCannotCreate(result, redirectAttributes, event);
//        }
//
//        // Check validation of Start date End date and regis date:
//        LocalDateTime startDateTime = LocalDateTime.parse(String.format("%sT%s", startDate, startTime));
//        LocalDateTime endDateTime = LocalDateTime.parse(String.format("%sT%s", endDate, endTime));
//        LocalDateTime regisDeadline = LocalDateTime.parse(String.format("%sT%s", registrationDate, registrationTime));
//        if (eventInfoValidation.checkValidationAllDate(
//                startDateTime,
//                endDateTime,
//                regisDeadline,
//                result
//        )) {
//            event.setStartDateTime(startDateTime);
//            event.setEndDateTime(endDateTime);
//            event.setRegistrationDeadline(regisDeadline);
//        }
//        else {
//            return handleCannotCreate(result, redirectAttributes, event);
//        }
//        event.setMaxAttendees(venueService.getVenueById(Integer.parseInt(venueId)).get().getCapacity());
//        event.setCurrentAttendees(0);
//        event.setViewCount(0);
//        // isfree và isFeatured là hai cái cần quan tâm khi set Hoặc làm form.
//        event.setIsFeatured(false);
//        event.setIsFree(false);
//        event.setCreatedAt(LocalDateTime.now());
//        // Khi update thì cần ghi log update at.
//        eventService.saveEvent(event);
//        redirectAttributes.addFlashAttribute("event", event);
//        return "redirect:/staff/event/create/ticket";
//    }

//    @GetMapping("/event/create/ticket")
//    public String createTicket(
//            @ModelAttribute("event") Event event,
//            RedirectAttributes redirectAttributes,
//            Model model
//    ) {
//        if (event == null) {
//            return "error/404";
//        }
//        TicketType ticket = new TicketType();
//        ticket.setEvent(event);
//        model.addAttribute("ticket", ticket);
//        redirectAttributes.addFlashAttribute("event", event);
//        return "staff/event/createTicket";
//    }
//
//    @PostMapping("/event/create/ticket")
//    public String createTicketPost(
//            @ModelAttribute("event") Event event,
//            RedirectAttributes redirectAttributes,
//            @ModelAttribute("ticket") TicketType ticket,
//            Model model
//    ) {
//        System.out.println(ticket);
//        Integer eventId = ticket.getEvent().getId();
//        event = eventService.getEventById(eventId).get();
//        ticket.setEvent(event);
//        ticket.setSalesEndDate(event.getRegistrationDeadline());
//        event.getTicketTypes().add(ticket);
//        ticketTypeService.createTicketType(ticket);
//        System.out.println(ticket);
//        ticketTypeService.createTicketType(ticket);
//        redirectAttributes.addFlashAttribute("event", event);
//        return "redirect:/staff/event/create/ticket";
//    }

//    @PostMapping("/event/create/cancel")
//    public String cancelCreate(
//            HttpSession session,
//            @RequestParam("newLinkBanner") String newLinkBanner,
//            @RequestParam("newLinkThumbnail") String newLinkThumbnail
//    ) {
//        session.removeAttribute("newLinkBanner");
//        session.removeAttribute("newLinkThumbnail");
//        session.removeAttribute("hasNewBanner");
//        session.removeAttribute("hasNewThumbnail");
//        session.removeAttribute("fileNameBanner");
//        session.removeAttribute("fileNameThumbnail");
//        // Delete file images
//        if (!newLinkBanner.isEmpty()) {
//            fileIO.deleteFile(newLinkBanner);
//        }
//        if (!newLinkThumbnail.isEmpty()) {
//            fileIO.deleteFile(newLinkThumbnail);
//        }
//        return "redirect:/staff/event";
//    }
    @GetMapping("/discount")
    public String ManageDiscount(@RequestParam(value = "page", required = false) Integer page,
                                 @ModelAttribute(value = "keyword") String keyword,
                                 @ModelAttribute(value = "filter") String filter,
                                 @ModelAttribute(value = "discountType") String discountType,
                                 Model model,
                                 HttpServletRequest request,
                                 @AuthenticationPrincipal Object principal) {

        // Phân quyền xem discount
        String email = getEmailFromPrincipal(principal);
        Staff staff = staffService.getStaffByEmail(email);
        System.err.println(staff.getId());
        //

        String url = request.getRequestURI();
        int totalActive = 0;
        if (filter.isEmpty()) {
            filter = "all";
        }
        if (discountType.isEmpty()) {
            discountType = "all";
        }
        System.err.println("filter: " + filter);
        // Phân trang
        List<Integer> viewPage = new ArrayList<>();
        viewPage.add(2);
        viewPage.add(3);
        viewPage.add(4);
        int pageIndex = 0;
        //==================================
        Page<DiscountCode> discountPages = discountSevice.findDiscountsWithCriteria(discountType, filter, keyword.trim(), 0, staff.getId());
        if (page != null) {
            if (page > discountPages.getTotalPages()) page = discountPages.getTotalPages();
            pageIndex = page - 1;
            if (pageIndex < 0) {
                pageIndex = 0;
            }
            //==================================
            discountPages = discountSevice.findDiscountsWithCriteria(discountType, filter, keyword.trim(), pageIndex, staff.getId());
            System.err.println(pageIndex);
            if (discountPages.getTotalPages() > 5) {
                if (pageIndex < 3) {
                    model.addAttribute("after", true);
                } else if (pageIndex < discountPages.getTotalPages() - 2) {
                    viewPage.set(0, page - 1);
                    viewPage.set(1, page);
                    viewPage.set(2, page + 1);
                    model.addAttribute("after", true);
                    model.addAttribute("before", true);
                } else {
                    viewPage.set(0, discountPages.getTotalPages() - 3);
                    viewPage.set(1, discountPages.getTotalPages() - 2);
                    viewPage.set(2, discountPages.getTotalPages() - 1);
                    model.addAttribute("before", true);
                }
            } else {
                viewPage = IntStream.rangeClosed(2, discountPages.getTotalPages() - 1)
                        .boxed()
                        .toList();
            }
            model.addAttribute("currentPage", page);
            model.addAttribute("viewPages", viewPage);
        } else {
            viewPage = IntStream.rangeClosed(2, discountPages.getTotalPages() - 1)
                    .boxed()
                    .toList();
            model.addAttribute("currentPage", 1);
            model.addAttribute("viewPages", viewPage);
        }
        if (discountPages.getTotalPages() > 1) {
            model.addAttribute("lastPage", discountPages.getTotalPages());
        }

        model.addAttribute("hasNext", discountPages.hasNext());
        model.addAttribute("hasPrevious", discountPages.hasPrevious());

        // lấy order tính
        int totalRedemption = 0;
        int expiredCodes = 0;
        LocalDateTime now = LocalDateTime.now();
        List<DiscountCode> discountCodes = discountSevice.getAll();
        for (DiscountCode dc : discountCodes) {
            if (dc.getValidTo().isBefore(now)) {
                expiredCodes += 1;
            }
            if (dc.getIsActive()) {
                totalActive += 1;
            }
        }
        if (!keyword.isEmpty()) {
            url += "?keyword=" + keyword;
        }
        if (url.length() == request.getRequestURI().length()) {
            url += "?filter=" + filter;
        } else {
            url += "&filter=" + filter;
        }
        if (url.length() == request.getRequestURI().length()) {
            url += "?discountType=" + discountType;
        } else {
            url += "&discountType=" + discountType;
        }
        System.err.println(url);
        model.addAttribute("url", url);
        model.addAttribute("discountType", discountType);
        model.addAttribute("filter", filter);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalActive", totalActive);
        model.addAttribute("totalRedemptions", totalRedemption);
        model.addAttribute("expiredCodes", expiredCodes);
        model.addAttribute("dcList", discountPages);
        model.addAttribute("totalElements", discountPages.getTotalElements());
        model.addAttribute("totalElementsOfPage", discountPages.getNumberOfElements());
        return "staff/discount/list";
    }

    @GetMapping("discount/new")
    public String create(Model model,
                         @AuthenticationPrincipal Object principal) {
        //lấy staff
        String email = getEmailFromPrincipal(principal);
        Staff staff = staffService.getStaffByEmail(email);
        //lấy event theo eventStaff
        List<Event> events = eventService.getAllByEveneStaff(staff.getId());

        DiscountCode dc = new DiscountCode();
        model.addAttribute("events", events);
        model.addAttribute("discountCode", dc);
        return "staff/discount/create";
    }

    @GetMapping("discount/edit")
    public String edit(@RequestParam("id") int discountCodeId,
                       Model model,
                       @AuthenticationPrincipal Object principal) {
        //lấy staff
        String email = getEmailFromPrincipal(principal);
        Staff staff = staffService.getStaffByEmail(email);
        //lấy event theo eventStaff
        List<Event> events = eventService.getAllByEveneStaff(staff.getId());

        DiscountCode dc = discountSevice.getDiscountCodeById(discountCodeId);
        events.removeIf(e -> e.getId().equals(dc.getEvent().getId()));
        model.addAttribute("events", events);
        model.addAttribute("discountCode", dc);
        return "staff/discount/edit";
    }

    @PostMapping("discount/save")
    public String save(@ModelAttribute(name = "discountCode") DiscountCode discountCode,
                       @ModelAttribute(name = "event") Integer eventId) {
        if (discountCode.getId() == null) {
            discountSevice.newDiscount(discountCode, eventId);
            return "redirect:/staff/discount";
        }
        System.out.println(discountSevice.editDiscountCode(discountCode, eventId));
        return "redirect:/staff/discount";
    }

    @GetMapping("discount/delete")
    public String deleteDiscount(@RequestParam("id") int discountId,
                                 RedirectAttributes redirectAttributes) {
        if (orderService.isDiscountCodeUsed(discountId)) {
            discountSevice.deleteDiscountCode(discountId);
        } else {
            redirectAttributes.addFlashAttribute("excep", "1");
            redirectAttributes.addFlashAttribute("id", discountId);
            return "redirect:/staff/discount?delete=fail";
        }
        return "redirect:/staff/discount";
    }

    @PostMapping(value = "/discount/deactive")
    public String deactivateDiscount(@RequestParam("id") int id) {
        discountSevice.deactiveCode(id);
        return "redirect:/staff/venue";
    }


    @GetMapping("/venue")
    public String ManageVenue(Model model,
                              @RequestParam(value = "page", required = false) Integer page,
                              @ModelAttribute("keyword") String keyword,
                              @ModelAttribute("filter") String filter,
                              @AuthenticationPrincipal Object principal,
                              HttpServletRequest request) {
        // Phân quyền sửa venue
        String email = getEmailFromPrincipal(principal);
        Staff staff = staffService.getStaffByEmail(email);
        //
        int totalVenue = 0;
        int totalActive = 0;
        int totalCapacity = 0;
        if (filter.isEmpty()) {
            filter = "all";
        }
        String url = request.getRequestURI();

        // Phân trang
        List<Integer> viewPage = new ArrayList<>();
        Page<Venue> venuePages;
        viewPage.add(2);
        viewPage.add(3);
        viewPage.add(4);
        int pageIndex = 0;
        venuePages = venueService.findVenuesWithCriteria(filter, keyword.trim(), 0);

        if (page != null) {
            if (page > venuePages.getTotalPages()) page = venuePages.getTotalPages();
            pageIndex = page - 1;
            pageIndex = page - 1;
            if (pageIndex < 0) {
                pageIndex = 0;
            }
            venuePages = venueService.findVenuesWithCriteria(filter, keyword.trim(), pageIndex);
            System.err.println(pageIndex);
            if (venuePages.getTotalPages() > 5) {
                if (pageIndex < 3) {
                    model.addAttribute("after", true);
                } else if (pageIndex < venuePages.getTotalPages() - 2) {
                    viewPage.set(0, page - 1);
                    viewPage.set(1, page);
                    viewPage.set(2, page + 1);
                    model.addAttribute("after", true);
                    model.addAttribute("before", true);
                } else {
                    viewPage.set(0, venuePages.getTotalPages() - 3);
                    viewPage.set(1, venuePages.getTotalPages() - 2);
                    viewPage.set(2, venuePages.getTotalPages() - 1);
                    model.addAttribute("before", true);
                }
            } else {
                viewPage = IntStream.rangeClosed(2, venuePages.getTotalPages() - 1)
                        .boxed()
                        .toList();
            }
            model.addAttribute("currentPage", page);
            model.addAttribute("viewPages", viewPage);
        } else {
            viewPage = IntStream.rangeClosed(2, venuePages.getTotalPages() - 1)
                    .boxed()
                    .toList();
            model.addAttribute("currentPage", 1);
            model.addAttribute("viewPages", viewPage);
        }
        if (venuePages.getTotalPages() > 1) {
            model.addAttribute("lastPage", venuePages.getTotalPages());
        }

        model.addAttribute("hasNext", venuePages.hasNext());
        model.addAttribute("hasPrevious", venuePages.hasPrevious());
        List<Venue> venueList = venueService.getAll();
        totalVenue = venueList.size();
        for (Venue v : venueList) {
            if (v.getIsActive()) {
                totalActive += 1;
            }
            totalCapacity += v.getCapacity();
        }
        if (!keyword.isEmpty()) {
            url += "?keyword=" + keyword;
        }
        if (url.length() == request.getRequestURI().length()) {
            url += "?filter=" + filter;
        } else {
            url += "&filter=" + filter;
        }
        System.err.println(url);
        model.addAttribute("url", url);
        model.addAttribute("filter", filter);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalVenue", totalVenue);
        model.addAttribute("totalActive", totalActive);
        model.addAttribute("totalCapacity", totalCapacity);
        model.addAttribute("staffId", staff.getId());
        model.addAttribute("venueList", venuePages);
        model.addAttribute("totalElements", venuePages.getTotalElements());
        model.addAttribute("totalElementsOfPage", venuePages.getNumberOfElements());
        return "staff/venue/list";
    }


    @GetMapping("/venue/delete")
    public String deleteVenue(@RequestParam("id") int id,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal Object principal) {
        try {
            // Phân quyền sửa venue
            String email = getEmailFromPrincipal(principal);
            Staff staff = staffService.getStaffByEmail(email);
            System.err.println(staff.getId());
            // so sánh kiểm tra quyền tác giả
            Venue venue = venueService.findVenueById(id);
            if (!venue.getStaff().getId().equals(staff.getId())) {
                return "redirect:/staff/venue";
            }
            // thực hiện xóa
            this.venueService.deleteVenue(id);

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            redirectAttributes.addFlashAttribute("excep", "1");
            redirectAttributes.addFlashAttribute("id", id);
            return "redirect:/staff/venue?delete=fail";
        }

        return "redirect:/staff/venue";
    }

    @PostMapping(value = "/venue/deactive")
    public String deactivateVenue(@RequestParam("id") int id) {
        venueService.deactive(id);
        return "redirect:/staff/venue";
    }

    @GetMapping("/venue/new")
    public String createVenue(Model model) {
        Venue venue = new Venue();
        model.addAttribute("venue", venue);
        return "staff/venue/create";
    }

    @GetMapping("/venue/edit")
    public String createVenue(@RequestParam("id") int venueId,
                              @AuthenticationPrincipal Object principal,
                              Model model) {
        // Phân quyền sửa venue
        String email = getEmailFromPrincipal(principal);
        Staff staff = staffService.getStaffByEmail(email);
        System.err.println(staff.getId());
        //
        Venue venue = venueService.findVenueById(venueId);
        if (!Objects.equals(venue.getStaff().getId(), staff.getId())) {
            return "staff/venue/edit";
        }
        String pathVaribleProvince = venue.getCity().replace(" ", "_");
        String pathVaribleWard = venue.getWard().replace(" ", "_");
        System.out.println(pathVaribleProvince);
        model.addAttribute("pathVaribleProvince", pathVaribleProvince);
        model.addAttribute("pathVaribleWard", pathVaribleWard);
        model.addAttribute("venue", venue);
        return "staff/venue/edit";
    }

    @PostMapping("/venue/save")
    public String saveVenue(@ModelAttribute("venue") Venue venue,
                            @AuthenticationPrincipal Object principal) {
        Staff staff = new Staff();
        System.err.println(venue.getLat());
        try {
            String email = getEmailFromPrincipal(principal);
            System.out.println(email);
            staff = staffService.getStaffByEmail(email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/staff/venue?save=fail";
        }
        System.out.println(venue.getId());
        if (venue.getId() == null) {
            if (staff == null) {
                System.err.println("Không tìm thấy tài khoản staff");
                ;
                return "redirect:/staff/venue?save=fail";
            }
            venue.setStaff(staff);
            venue.setCreatedAt(LocalDateTime.now());
            venue.setCity(addressClient.getProvinceByProvinceCode(venue.getCity(), 1).getName());
            venue.setWard(addressClient.getWardByWardCode(venue.getWard()).getName());
            if (venueService.saveVenue(venue) == 1) {
                return "redirect:/staff/venue?save=success";
            } else {
                return "redirect:/staff/venue?save=fail";
            }

        } else {
            Pattern pattern = Pattern.compile("^[0-9]*$");
            Matcher matcher = pattern.matcher(venue.getCity());
            if (matcher.matches()) {
                venue.setCity(addressClient.getProvinceByProvinceCode(venue.getCity(), 1).getName());
            } else {
                venue.setCity(venue.getCity().replace("_", " "));
            }
            matcher = pattern.matcher(venue.getWard());
            if (matcher.matches()) {
                venue.setWard(addressClient.getWardByWardCode(venue.getWard()).getName());
            } else {
                venue.setWard(venue.getWard().replace("_", " "));
            }
            if (venueService.updateVenue(venue) == 1) {
                return "redirect:/staff/venue?update=success";
            } else {
                return "redirect:/staff/venue?update=fail";
            }
        }
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
        model.addAttribute("newEventStaff", newEventStaff);
        model.addAttribute("events", events);
        model.addAttribute("roles", roles);
        model.addAttribute("staffs", staffs);
        model.addAttribute("totalElements", eventStaffAssignmentPages.getTotalElements());
        model.addAttribute("totalElementsOfPage", eventStaffAssignmentPages.getNumberOfElements());
        return "staff/assignEventStaff/list";
    }
//    @PostMapping("/assignEventStaff/save")
//    public String saveEventStaff(@ModelAttribute("newEventStaff") EventStaffAssignmentDTO newEventStaff,
//                                 @AuthenticationPrincipal Object principal,
//                                 RedirectAttributes redirectAttributes) {
//        if (principal == null) {
//            return "redirect:/auth/login";
//        }
//
//        try {
//            String email = getEmailFromPrincipal(principal);
//            Staff staff = staffRepo.findByEmail(email)
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản: " + email));
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            String role = auth.getAuthorities().stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .findFirst()
//                    .orElse("");
//            System.err.println(role);
//            System.out.println(newEventStaff.toString());
//            if (role.equalsIgnoreCase("role_admin")) {
//                System.err.println("Đủ quyền");
//                eventStaffAssignmentService.save(newEventStaff, staff.getId());
//            } else {
//                System.err.println("Không đủ quyền");
//            }
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
//        }
//
//        return "redirect:/staff/assignEventStaff";
//    }
//    @GetMapping("/assignEventStaff/delete")
//    public String deletaEventStaff(@RequestParam("id") String id) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String role = auth.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .findFirst()
//                .orElse("");
//        if (role.equalsIgnoreCase("role_admin")) {
//            eventStaffAssignmentService.deleteEventStaff(id);
//        } else {
//            System.err.println("Bạn không có quyền xóa!");
//        }
//        return "redirect:/staff/assignEventStaff";
//    }


    @GetMapping("/revenue")
    public String ManageRevenue() {
        System.out.println("staff");
        return "staff/revenueDetail";
    }

    @GetMapping("/event/{id}/notification/send")
    public String showSendNotification(@PathVariable("id") String id, Model model) {

        if (UrlValition.getIntegerId(id) == null) return "error/404";
        int idInt = Integer.parseInt(id);

        Optional<Event> eventOpt = eventService.getEventById(idInt);
        if (eventOpt.isEmpty()) return "error/404";
        Event event = eventOpt.get();

        // Format ngày event
        String eventDateStr = "";
        if (event.getStartDateTime() != null) {
            DateTimeFormatter fmt = DateTimeFormatter
                    .ofPattern("EEE, MMM dd yyyy · HH:mm", Locale.ENGLISH);
            eventDateStr = fmt.format(event.getStartDateTime());
        }

        int attendeeCount = notificationService.countAttendeesByEvent(idInt);
        int staffCount = staffNotificationService.countStaffByEvent(idInt); // ← MỚI

        // dong nay de debug
        System.out.println("=== staffCount for event " + idInt + " = " + staffCount + " ===");


        model.addAttribute("event", event);
        model.addAttribute("eventDateStr", eventDateStr);
        model.addAttribute("attendeeCount", attendeeCount);
        model.addAttribute("staffCount", staffCount);                          // ← MỚI

        return "staff/event/sendnotification";
    }


// ════════════════════════════════════════════════════════════════════════
// POST — Xử lý gửi thông báo (UC-29 Normal Flow steps 7-10)
// URL: POST /staff/event/{id}/notification/send
// ════════════════════════════════════════════════════════════════════════

    @PostMapping("/event/{id}/notification/send")
    public String sendNotification(
            @PathVariable("id") String id,
            @RequestParam("title") String title,
            @RequestParam("message") String message,
            @RequestParam("recipientScope") String recipientScope,
            @RequestParam(value = "channels", required = false) List<String> channels,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // ── 1. Validate event ID ──────────────────────────────────────
        if (UrlValition.getIntegerId(id) == null) return "error/404";
        int idInt = Integer.parseInt(id);

        Optional<Event> eventOpt = eventService.getEventById(idInt);
        if (eventOpt.isEmpty()) return "error/404";

        // ── 2. Log debug recipientScope ───────────────────────────────
        System.out.println("=== recipientScope received: " + recipientScope + " ===");

        // ── 3. Validate content ───────────────────────────────────────
        if (title == null || title.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorTitle", "Validation Error");
            redirectAttributes.addFlashAttribute("errorMessage", "Notification title cannot be empty.");
            return "redirect:/staff/event/" + idInt + "/notification/send";
        }
        if (message == null || message.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorTitle", "Validation Error");
            redirectAttributes.addFlashAttribute("errorMessage", "Message content cannot be empty.");
            return "redirect:/staff/event/" + idInt + "/notification/send";
        }
        if (title.trim().length() > 200) {
            redirectAttributes.addFlashAttribute("errorTitle", "Validation Error");
            redirectAttributes.addFlashAttribute("errorMessage", "Title must not exceed 200 characters.");
            return "redirect:/staff/event/" + idInt + "/notification/send";
        }
        if (message.trim().length() > 1000) {
            redirectAttributes.addFlashAttribute("errorTitle", "Validation Error");
            redirectAttributes.addFlashAttribute("errorMessage", "Message must not exceed 1000 characters.");
            return "redirect:/staff/event/" + idInt + "/notification/send";
        }
        if (channels == null || channels.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorTitle", "Validation Error");
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one notification channel.");
            return "redirect:/staff/event/" + idInt + "/notification/send";
        }

        // ── 4. Xác định channels ──────────────────────────────────────
        boolean hasInApp = channels.contains("INAPP");
        boolean hasEmail = channels.contains("EMAIL");

        // ── 5. Xác định đối tượng nhận theo recipientScope ───────────
        boolean sendToCustomers = "ALL_CUSTOMERS".equals(recipientScope) || "BOTH".equals(recipientScope);
        boolean sendToStaff = "ALL_STAFF".equals(recipientScope) || "BOTH".equals(recipientScope);

        System.out.println("=== sendToCustomers: " + sendToCustomers + ", sendToStaff: " + sendToStaff + " ===");

        // ── 6. Kiểm tra có người nhận không ──────────────────────────
        int customerCount = sendToCustomers ? notificationService.countAttendeesByEvent(idInt) : 0;
        int staffCount = sendToStaff ? staffNotificationService.countStaffByEvent(idInt) : 0;

        if (customerCount == 0 && staffCount == 0) {
            redirectAttributes.addFlashAttribute("errorTitle", "No Recipients Available");
            redirectAttributes.addFlashAttribute("errorMessage", "No registered attendees or assigned staff found.");
            return "redirect:/staff/event/" + idInt + "/notification/send";
        }

        // ── 7. Gửi thông báo ─────────────────────────────────────────
        try {
            int sentCustomers = 0;
            int sentStaff = 0;

            Integer senderStaffId = null;
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    String email = auth.getName(); // email của staff đang login
                    Staff currentStaff = staffRepo.findByEmail(email).orElse(null);
                    if (currentStaff != null) {
                        senderStaffId = currentStaff.getId();
                        System.out.println("=== senderStaffId: " + senderStaffId + " (" + email + ") ===");
                    }
                }
            } catch (Exception e) {
                System.err.println("[NotificationSend] Cannot get sender: " + e.getMessage());
            }

            if (sendToCustomers && customerCount > 0) {
                sentCustomers = notificationService.sendToEventAttendees(
                        idInt,
                        title.trim(),
                        message.trim(),
                        "InApp",
                        hasInApp,
                        hasEmail,
                        senderStaffId);
            }

            if (sendToStaff && staffCount > 0) {
                sentStaff = staffNotificationService.sendToEventStaff(
                        idInt, senderStaffId, title.trim(), message.trim(), hasInApp, hasEmail);
            }

            // ── 8. Build success message ──────────────────────────────
            String channelLabel = hasInApp && hasEmail ? "In-App & Email"
                    : hasEmail ? "Email" : "In-App";

            StringBuilder successMsg = new StringBuilder();
            if (sentCustomers > 0) successMsg.append(sentCustomers).append(" attendee(s)");
            if (sentCustomers > 0 && sentStaff > 0) successMsg.append(" and ");
            if (sentStaff > 0) successMsg.append(sentStaff).append(" staff member(s)");
            successMsg.append(" notified via ").append(channelLabel).append(".");

            redirectAttributes.addFlashAttribute("successMessage", successMsg.toString());

        } catch (Exception e) {
            System.err.println("[NotificationSend] Error for event " + idInt + ": " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorTitle", "System Error");
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An error occurred while sending notifications. Please try again later.");
        }

        return "redirect:/staff/event/" + idInt + "/notification/send";
    }

    @GetMapping("/notification")
    public String notificationHistory(
            @RequestParam(value = "eventId", required = false) Integer selectedEventId,
            Model model) {

        // Lấy tất cả groups
        List<NotificationGroupDTO> groups = notificationService.getNotificationHistory();

        // Tổng số notification batch đã gửi
        int totalNotifications = groups.stream()
                .mapToInt(NotificationGroupDTO::getNotificationCount)
                .sum();

        // Danh sách events có notification (cho filter dropdown)
        List<Event> events = groups.stream()
                .map(g -> {
                    Event e = new Event();
                    e.setId(g.getEventId());
                    e.setTitle(g.getEventTitle());
                    return e;
                })
                .collect(Collectors.toList());

        model.addAttribute("notificationGroups", groups);
        model.addAttribute("totalNotifications", totalNotifications);
        model.addAttribute("totalEvents", groups.size());
        model.addAttribute("events", events);
        model.addAttribute("selectedEventId", selectedEventId);

        return "staff/notification/history";
    }

    @GetMapping("/my-notifications")
    public String myNotifications() {
        return "staff/notification/myNotifications";
    }


    private String handleValidationError(ValidationResult result,
                                         RedirectAttributes redirectAttributes,
                                         Event event) {
        redirectAttributes.addFlashAttribute("hasMessage", true);
        redirectAttributes.addFlashAttribute("errorTitle", result.getErrorTitle());
        redirectAttributes.addFlashAttribute("message", result.getMessage());
        System.out.println("Error: " + result.getErrorTitle());
        return "redirect:/staff/event/" + event.getId() + "/edit";
    }

    private String handleCannotEdit(ValidationResult result,
                                    RedirectAttributes redirectAttributes,
                                    Event event) {
        redirectAttributes.addFlashAttribute("hasMessage", true);
        redirectAttributes.addFlashAttribute("errorTitle", result.getErrorTitle());
        redirectAttributes.addFlashAttribute("message", result.getMessage());
        System.out.println("Error: " + result.getErrorTitle());
        return "redirect:/staff/event/" + event.getId();
    }


//    private String handleCannotCreate(ValidationResult result,
//                                    RedirectAttributes redirectAttributes,
//                                    Event event) {
//        redirectAttributes.addFlashAttribute("hasMessage", true);
//        redirectAttributes.addFlashAttribute("errorTitle", result.getErrorTitle());
//        redirectAttributes.addFlashAttribute("message", result.getMessage());
//        System.out.println("Error: " + result.getErrorTitle());
//        return "redirect:/staff/event/create";
//    }


    //REST API
    @GetMapping(value = "/api/discount")
    public ResponseEntity<Map<String, String>> getListDiscount() {
        Map<String, String> response = new HashMap<>();
        for (DiscountCode dc : discountSevice.getAll()) {
            response.put(dc.getCode(), dc.getCode());
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping("/event/venue/save")
    public String saveVenueInEditEvent(@ModelAttribute("venue") Venue venue,
                                       @RequestParam Integer eventId,
                                       @RequestParam(required = false) Boolean setForEvent) {
        if (venue.getId() == null) {
            venue.setCreatedAt(LocalDateTime.now());
            venue.setCity(addressClient.getProvinceByProvinceCode(venue.getCity(), 1).getName());
            venue.setWard(addressClient.getWardByWardCode(venue.getWard()).getName());
            venue.setActive(true);
            if (venueService.saveVenue(venue) == 1) {
                if (Boolean.TRUE.equals(setForEvent)) {
                    eventService.updateVenue(eventId, venue);
                }

                return "redirect:/staff/event/" + eventId + "/edit";
            } else {
                return "redirect:/staff/event/" + eventId + "/edit";
            }

        } else {
            Pattern pattern = Pattern.compile("^[0-9]*$");
            Matcher matcher = pattern.matcher(venue.getCity());
            if (matcher.matches()) {
                venue.setCity(addressClient.getProvinceByProvinceCode(venue.getCity(), 1).getName());
            } else {
                venue.setCity(venue.getCity().replace("_", " "));
            }
            matcher = pattern.matcher(venue.getWard());
            if (matcher.matches()) {
                venue.setWard(addressClient.getWardByWardCode(venue.getWard()).getName());
            } else {
                venue.setWard(venue.getWard().replace("_", " "));
            }
            if (venueService.updateVenue(venue) == 1) {
                return "redirect:/staff/event/" + eventId + "/edit";
            } else {
                return "redirect:/staff/event/" + eventId + "/edit";
            }
        }
    }

}
