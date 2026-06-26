package vn.edu.fpt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.dto.*;
import vn.edu.fpt.model.entity.*;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.model.entity.TicketType;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.service.*;
import vn.edu.fpt.service.session.SessionService;
import vn.edu.fpt.util.PageSetting;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping
public class HomeController {
    private final EventService eventService;
    private final CategoryService categoryService;
    private final TicketTypeService ticketTypeService;
    private final CustomerService customerService;
    private final EventRepo eventRepo;
    private final CommentService commentService;
    private final ProfileController profileController;
    private final SessionService sessionService;
    private final TicketService ticketService;
    private final OrderItemService orderItemService;
    private ValidationResult result = new ValidationResult();


    public HomeController(EventService eventService, CategoryService categoryService,
                          TicketTypeService ticketTypeService, CustomerService customerService, EventRepo eventRepo,
                          CommentService commentService, ProfileController profileController, SessionService sessionService,
                          TicketService ticketService, OrderItemService orderItemService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.ticketTypeService = ticketTypeService;
        this.customerService = customerService;
        this.eventRepo = eventRepo;
        this.commentService = commentService;
        this.profileController = profileController;
        this.sessionService = sessionService;
        this.ticketService = ticketService;
        this.orderItemService = orderItemService;
    }

    @GetMapping({"/", "/home", "/event"})
    public String home(Model model) {
        // Categories List
        List<Category> listCategory = categoryService.getAllCategory();
        // Each event list has 4 items
        Pageable pageable = PageRequest.of(0, PageSetting.SIZE_OF_EACH_CART);
        // Feature event
        List<EventDTO> listFeatureEvents = eventService.getListFeatureEvents(
                LocalDateTime.now(), "Published", pageable
        );
        // Upcoming event
        List<EventDTO> listUpcomingEvents = eventService.getListUpcomingEvents(
                LocalDateTime.now(), "Published", pageable
        );
        // Trending event
        List<EventDTO> listTrendingEvents = eventService.getListTrendingEvents(
                LocalDateTime.now(), "Published", pageable
        );
        // Completed event
        List<EventDTO> listCompletedEvents = eventService.getListCompletedEvents(
                "Completed", pageable
        );

        // Live event
        List<EventDTO> listLiveEvents = eventService.getListCompletedEvents(
                "live", pageable
        );

        // List event by category
        Map<Category, List<EventDTO>> mapEventByCategories = eventService.getMapEventByCategory();
        model.addAttribute("mapEventByCategories", mapEventByCategories);
        model.addAttribute("listCategory", listCategory);
        model.addAttribute("listTrendingEvents", listTrendingEvents);
        model.addAttribute("listUpcomingEvents", listUpcomingEvents);
        model.addAttribute("listFeatureEvents", listFeatureEvents);
        model.addAttribute("listLiveEvents", listLiveEvents);
        model.addAttribute("listCompletedEvents", listCompletedEvents);
        return "home/homePage";
    }

    @Transactional
    @GetMapping("/event/{id}")
    public String eventDetail(@PathVariable("id") Integer id,
                              HttpSession session, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Set<Integer> viewedEvents =
                (Set<Integer>) session.getAttribute("VIEWED_EVENTS");

        if (viewedEvents == null) {
            viewedEvents = new HashSet<>();
        }

        if (!viewedEvents.contains(id)) {
            eventService.incrementView(id);
            viewedEvents.add(id);
            session.setAttribute("VIEWED_EVENTS", viewedEvents);
        }
        Event event = new Event();
        Optional<Event> eventOptional = eventService.getPublishedEvent(id);
        if (eventOptional.isPresent()) {
            event = eventOptional.get();
        }
        else {
            eventOptional = eventService.getCompletedEvent(id);
        }

        if (eventOptional.isPresent()) {
            event = eventOptional.get();
        } else {
            eventOptional = eventService.getLiveEvent(id);
        }

        if (eventOptional.isPresent()) {
            event = eventOptional.get();
        }

        LocalDateTime stopSellTime = event.getStartDateTime().minusHours(12);

        boolean canBuy = event.getEventStatus().equals("Published") &&
                event.getCurrentAttendees() < event.getMaxAttendees();
        List<TicketType> ticketTypes = ticketTypeService.getActiveTicketTypes(id);
        List<CommentDTO> listComments = eventService.getListCommentsByEventId(id);
        for (TicketType tt : ticketTypes) {
            int conlai = ticketService.countNumberOfTicketAvailable(tt.getId());
            if (conlai <= 0) {
                tt.setSoldOut(true);
            } else tt.setSoldOut(false);
            tt.setAvailableLeft(conlai);
        }
        model.addAttribute("currentUserEmail", getEmailFromAuth(auth));
//        Integer availableTicketNumber = ticketTypeService.getAvailableTicketsCount(id);
        model.addAttribute(PageSetting.EVENT_NAME_ATTRIBUTES, event);
        model.addAttribute(PageSetting.TICKET_TYPE_LIST, ticketTypes);
        model.addAttribute("organizer", event.getOrganizer());
        model.addAttribute("venue", event.getVenue());
        model.addAttribute("stopSellTime", stopSellTime);
        model.addAttribute("canBuy", canBuy);
        model.addAttribute("listComments", listComments);

        String email = getEmailFromAuth(auth);
        // Rating avg
        Double ratingAvg = commentService.getAverageRating(id);
        if (ratingAvg == null) ratingAvg = 0.0;

        if (auth != null && auth.isAuthenticated()
                && !auth.getName().equals("anonymousUser")) {

            Optional<Customer> customerOptional = customerService.findByEmail(email);

            if (customerOptional.isPresent()) {
                List<CheckoutItem> eventCheckout = sessionService.getCheckoutItemsByEvent(session, id);

                Double total = sessionService.totalByEvent(eventCheckout);

                model.addAttribute("totalInSession", total);
                model.addAttribute("checkoutItems", eventCheckout);
                int countOrderOfCus = eventRepo.countOrder(event.getId(), customerOptional.get().getId());
                model.addAttribute("customer", customerOptional.get());
                model.addAttribute("countOrderOfCus", countOrderOfCus);
                model.addAttribute("hasCommented", commentService.hasCommented(customerOptional.get().getId(), event.getId()));


            }
        } else {
            model.addAttribute("totalInSession", 0);
            model.addAttribute("checkoutItems", "");
            model.addAttribute("customer", new Customer());
            model.addAttribute("countOrderOfCus", 0);
            model.addAttribute("hasCommented", false);
        }
        Comment comment = new Comment();
        comment.setEvent(new Event());
        comment.setCustomer(new Customer());
        model.addAttribute("comment", comment);
        // Rating
        model.addAttribute("ratingAvg", ratingAvg);
        // Percent of start
        model.addAttribute("five", commentService.getAvgOfStart(id, 5));
        model.addAttribute("four", commentService.getAvgOfStart(id, 4));
        model.addAttribute("three", commentService.getAvgOfStart(id, 3));
        model.addAttribute("two", commentService.getAvgOfStart(id, 2));
        model.addAttribute("one", commentService.getAvgOfStart(id, 1));
        return "home/eventDetail";
    }

    @PostMapping("/events/{id}/view")
    @ResponseBody
    public void increaseView(@PathVariable Integer id) {
        eventService.incrementView(id);
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


    @PostMapping("/checkout/add")
//    @ResponseBody
    public String addToCheckout(@RequestParam Integer ticketTypeId,
                                @RequestParam String ticketName,
                                @RequestParam Double price,
                                @RequestParam Integer quantity,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        // Get user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = getEmailFromAuth(auth);
        if (email == null) return "redirect:/auth/login";
        Optional<Customer> customerOptional = customerService.findByEmail(email);
        Customer customer = customerOptional.get();


        TicketType ticketType = new TicketType();
        Optional<TicketType> ticketTypeOpt = ticketTypeService.getTicketTypeOptById(ticketTypeId);
        if (ticketTypeOpt.isPresent()) {
            ticketType = ticketTypeOpt.get();
        }
        Integer maxPerUser = ticketType.getMaxTicketsPerUser();

        if (maxPerUser != null && maxPerUser > 0) {

            // ===== số lượng đã mua =====
            Integer purchasedQuantity =
                    orderItemService.countPurchasedTicket(
                            customer.getId(),
                            ticketType.getId()
                    );

            // ===== tổng hiện tại =====
            int totalQuantity =
                    purchasedQuantity + quantity;

            // ===== vượt giới hạn =====
            if (totalQuantity > maxPerUser) {

                result.setErrorTitle("Ticket quantity exceeded!");
                result.setMessage(
                        "You have already purchased "
                                + purchasedQuantity
                                + " ticket(s). Maximum allowed for \""
                                + ticketType.getName()
                                + "\" is "
                                + maxPerUser + "."
                );


                // rollback đúng item vừa thêm
//                cartService.decreaseItem(cart.getId(), addedItem.getId());
                Event event = ticketType.getEvent();

                return handleCannotCheckOut(result, redirectAttributes, event);
            }
        }

        String result = sessionService.saveSession(session, ticketTypeId, quantity);
        if (!result.equals("ok")) {
            redirectAttributes.addFlashAttribute("errorMessage", "The number of tickets of this type has reached its maximum capacity. The remaining quantity is " + result);
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Add item to buy successfully!");
        }
        // quay lại trang trước
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;

//        TicketTypeDTO ticketType = ticketTypeService.getTicketTypeById(ticketTypeId);
//        Integer eventId = ticketType.getEventId();
//        Integer maxTicket = ticketType.getMaxTicketsPerUser();
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String email = auth.getName();
//        Optional<Customer> customerOptional = customerService.findByEmail(email);
//        if (customerOptional.isPresent()) {
//            List<CheckoutItem> checkout =
//                    (List<CheckoutItem>) session.getAttribute("checkout");
//
//            if(checkout == null){
//                checkout = new ArrayList<>();
//            }
//
//            // kiểm tra nếu ticket đã tồn tại thì cộng số lượng
//            Optional<CheckoutItem> existing = checkout.stream()
//                    .filter(i -> i.getTicketTypeId().equals(ticketTypeId))
//                    .findFirst();
//
//            if(existing.isPresent()) {
//                CheckoutItem item = existing.get();
//                if (item.getQuantity() + quantity > maxTicket) {
//                    return "error";
//                }
//                item.setQuantity(item.getQuantity() + quantity);
//            } else {
//                if (quantity > maxTicket) {
//                    return "error 2";
//                }
//
//                CheckoutItem item = new CheckoutItem();
//                item.setTicketTypeId(ticketTypeId);
//                item.setTicketName(ticketName);
//                item.setPrice(price);
//                item.setQuantity(quantity);
//                item.setEventId(eventId);
//                checkout.add(item);
//            }
//
//            session.setAttribute("checkout", checkout);
//
//            return "ok";
//
//        } else {
//            return "error 3";
//        }


    }

    @GetMapping("/test-session")
    @ResponseBody
    public String checkSession(HttpSession session) {

        Enumeration<String> attrs = session.getAttributeNames();

        while (attrs.hasMoreElements()) {
            String name = attrs.nextElement();
            Object value = session.getAttribute(name);

            System.out.println(name + " = " + value);
        }

        return "Check console";
    }

    private String handleCannotCheckOut(ValidationResult result,
                                        RedirectAttributes redirectAttributes,
                                        Event event) {
        redirectAttributes.addFlashAttribute("hasMessage", true);
        redirectAttributes.addFlashAttribute("errorTitle", result.getErrorTitle());
        redirectAttributes.addFlashAttribute("message", result.getMessage());
        System.out.println("Error: " + result.getErrorTitle());
        return "redirect:/event/" + event.getId();
    }
}
