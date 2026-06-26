package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.dto.MyTicketDTO;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.repository.CustomerRepo;
import vn.edu.fpt.service.QrService;
import vn.edu.fpt.service.TicketService;

import java.util.List;


@Controller
public class MyTicketController {
//    Event Name
//  TicketType Name
//    Quantity
//    Date
//    Status
//    Action: Download QR,

    //    Di tu ticket -> ticketType -> event
//    Di tu ticket -> OrderItem -> Order -> thong so
    @Autowired
    private TicketService ticketService;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private QrService qrService;

    private String getEmailFromPrincipal(Object principal) {
        if (principal == null) return null;
        if (principal instanceof UserDetails) return ((UserDetails) principal).getUsername();
        if (principal instanceof OAuth2User) return ((OAuth2User) principal).getAttribute("email");
        return null;
    }

    @GetMapping("/myticket")
    public String myTicketView(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            @AuthenticationPrincipal Object principal) {

        String email = getEmailFromPrincipal(principal);
        if (email == null) return "redirect:/auth/login";

        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        List<MyTicketDTO> tickets =
                ticketService.getMyTickets(customer.getId());

        model.addAttribute("tickets", tickets);
        return "user/myTicket";
    }

    @GetMapping(value = "/api/tickets/{ticketId}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getTicketQR(@PathVariable Integer ticketId) throws Exception {

        byte[] qr = qrService.generateTicketQR(ticketId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("ticket-" + ticketId + ".png")
                        .build()
        );
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(qr, headers, HttpStatus.OK);
    }


//    Cai dat cho role Staff moi thiet lap duoc
//    @GetMapping("/api/checkin")
//    public String checkin(
//            @RequestParam String t,
//            @RequestParam String s
//    ) throws Exception {
//
//        String expectedSig = SignatureUtil.generateSignature(t);
//
//        if (!expectedSig.equals(s)) {
//            return "Invalid ticket";
//        }
//
//        Ticket ticket = ticketRepo.getReferenceById(Integer.parseInt(t));
//
//        if (ticket.getTicketStatus().equals("CheckedIn")) {
//            return "Ticket already used";
//        }
//
//        ticket.setTicketStatus("CheckedIn");
//        ticketRepo.save(ticket);
//
//        return "Check-in success";
//    }


}
