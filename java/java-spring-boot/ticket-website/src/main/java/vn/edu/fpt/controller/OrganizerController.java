package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.dto.OrganizerDTO;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.model.entity.Organizer;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.service.OrganizerService;

@Controller
@RequestMapping("/staff/event")
public class OrganizerController {

    private OrganizerService organizerService;

    private EventService eventService;

    public OrganizerController(OrganizerService organizerService, EventService eventService) {
        this.organizerService = organizerService;
        this.eventService = eventService;
    }


    @PostMapping("/organizer/create")
    public String createOrganizer(@Valid @ModelAttribute("organizer") OrganizerDTO organizerDTO,
                                  BindingResult result,
                                  @RequestParam Integer eventId, Model model) {

        if (result.hasErrors()) {
            Event event = eventService.getEventById(eventId).orElseThrow(()-> new RuntimeException("Event Not Found!"));
            model.addAttribute("event", event);
            return "redirect:/staff/event/" + eventId + "/edit";

        }
        organizerService.createOrganizer(organizerDTO);

        return "redirect:/staff/event/" + eventId + "/edit";
    }

    @PostMapping("/create/organizer/create")
    public String createEventOrganizer(@Valid @ModelAttribute("organizer") OrganizerDTO organizerDTO,
                                  BindingResult result, Model model) {

        if (result.hasErrors()) {
//            Event event = eventService.getEventById(eventId).orElseThrow(()-> new RuntimeException("Event Not Found!"));
//            model.addAttribute("event", event);
            return "redirect:/staff/event//create";

        }
        organizerService.createOrganizer(organizerDTO);

        return "redirect:/staff/event/create";
    }




}
