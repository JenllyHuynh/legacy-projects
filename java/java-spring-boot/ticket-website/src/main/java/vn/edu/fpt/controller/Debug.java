package vn.edu.fpt.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Debug {
    @GetMapping("/debug-session")
    public Map<String, Object> debugSession(HttpSession session) {
        // Controller này dùng để gởi session
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> names = session.getAttributeNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            map.put(name, session.getAttribute(name));
        }
        return map;
    }
}
