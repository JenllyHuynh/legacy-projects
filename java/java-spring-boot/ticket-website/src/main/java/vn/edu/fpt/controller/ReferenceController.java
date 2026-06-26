package vn.edu.fpt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReferenceController {
    @GetMapping("/help")
    public String help() {
        return "reference/help";
    }
}
