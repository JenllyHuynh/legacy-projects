package vn.edu.fpt.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.dto.ValidationResult;
import vn.edu.fpt.model.entity.Comment;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.service.CommentService;
import vn.edu.fpt.service.CustomerService;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.service.NotificationService;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class CommentController {

    private final EventService eventService;
    private final EventRepo eventRepo;
    private final CustomerService customerService;
    private final CommentService commentService;
    private final NotificationService notificationService;

    public CommentController(EventService eventService, EventRepo eventRepo,
                             CustomerService customerService, CommentService commentService, NotificationService notificationService) {
        this.eventService = eventService;
        this.eventRepo = eventRepo;
        this.customerService = customerService;
        this.commentService = commentService;
        this.notificationService = notificationService;
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

    @PostMapping("/event/{id}/comment")
    public String postComment(
            @PathVariable("id") String id,
            RedirectAttributes redirectAttributes,
            @ModelAttribute("comment")Comment comment
            ) {
        ValidationResult result = new ValidationResult();

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
        String email;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
        } else {
            email = auth.getName();
        }
        Customer customer = new Customer();
        Optional<Customer> customerOpt = customerService.findByEmail(email);
        customer = customerOpt.get();
        if (customerOpt.isEmpty()) {
            result.setErrorTitle("Customer does not exist!");
            result.setMessage("Customer does not exist!");
            return handleCannotPostComment(result, redirectAttributes, event);
        }
        int countOrderOfCus = eventRepo.countOrder(event.getId(), customerOpt.get().getId());
        if (countOrderOfCus == 0) {
            result.setErrorTitle("Customer does not buy any ticket!");
            result.setMessage("Customer does not buy any ticket!");
            return handleCannotPostComment(result, redirectAttributes, event);
        }
        else {
            comment.setEvent(event);
            comment.setCustomer(customerOpt.get());
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());
            System.out.println(comment);
            commentService.save(comment);

        }
        notificationService.send(
                customer.getId(),
                "Post Comment Successful",
                "You have successfully posted a comment for the event. Your rating is " + comment.getRating()
                        + " stars, and your review has been submitted successfully.\n",
                "InApp"
                );
        redirectAttributes.addFlashAttribute("posted", true);
        return "redirect:/event/" + event.getId();
    }

    @PostMapping("/event/{eventId}/comment/delete/{commentId}")
    public String deleteComment(
            @PathVariable("eventId") String eventId,
            @PathVariable("commentId") String commentId,
            RedirectAttributes redirectAttributes
    ) {
        // Check event
        int idEventInt;
        try {
            idEventInt = Integer.parseInt(eventId);
        } catch (Exception e) {
            System.out.println("Error Event id!");
            return "error/404";
        }
        Optional<Event> eventOpt = eventService.getEventById(idEventInt);
        if (eventOpt.isEmpty()) {
            return "error/404";
        }

        // Check comment
        int idInt;
        try {
            idInt = Integer.parseInt(commentId);
        } catch (Exception e) {
            return "error/404";
        }

        Optional<Comment> commentOptional = commentService.getById(idInt);
        if (commentOptional.isEmpty()) {
            return "error/404";
        }
        Comment comment = commentOptional.get();

        // Get user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = getEmailFromAuth(auth);
        Optional<Customer> customerOptional = customerService.findByEmail(email);

        if (customerOptional.isEmpty() || !comment.getCustomer().getEmail().equalsIgnoreCase(email)) {
            return "error/404";
        }
        Customer customer = customerOptional.get();
        commentService.delete(comment);
        System.out.println("Delete Comment Success!");
        redirectAttributes.addFlashAttribute("deleted", true);
        notificationService.send(
                customer.getId(),
                "Delete Comment Successful",
                "You have successfully deleted your comment. We truly value your feedback and would greatly " +
                        "appreciate it if you could leave a new review to help us improve and serve you better.\n",
                "InApp"
        );
        return "redirect:/event/" + eventId;
    }

    private String handleCannotPostComment(ValidationResult result,
                                    RedirectAttributes redirectAttributes,
                                    Event event) {
        redirectAttributes.addFlashAttribute("hasMessage", true);
        redirectAttributes.addFlashAttribute("errorTitle", result.getErrorTitle());
        redirectAttributes.addFlashAttribute("message", result.getMessage());
        System.out.println("Error: " + result.getErrorTitle());
        return "redirect:/event/" + event.getId();
    }
}
