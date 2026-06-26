package vn.edu.fpt.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.model.dto.EventDTO;
import vn.edu.fpt.service.CategoryService;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.service.VenueService;
import vn.edu.fpt.util.PageSetting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    private final EventService eventService;
    private final CategoryService categoryService;
    private final VenueService venueService;

    public SearchController(EventService eventService, CategoryService categoryService, VenueService venueService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.venueService = venueService;
    }

    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam(required = false) String keyword,
                         @RequestParam(required = false) String tab,
                         @RequestParam(required = false) String city,
                         @RequestParam(required = false) List<String> category,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                         @RequestParam(required = false) String minPrice,
                         @RequestParam(required = false) String maxPrice,
                         @RequestParam(defaultValue = "all") String priceMode,
                         @RequestParam(defaultValue = "0") String page) {

        List<Integer> categoryIds = null;

        if (category != null && !category.isEmpty()) {
            categoryIds = new ArrayList<>();

            for (String c : category) {
                if (c == null || c.isBlank()) continue;

                try {
                    int id = Integer.parseInt(c);
                    if (id > 0) {
                        categoryIds.add(id);
                    }
                } catch (NumberFormatException e) {
                }
            }

            if (categoryIds.isEmpty()) {
                categoryIds = null;
            }
        }

        Integer minPriceInt = null;
        Integer maxPriceInt = null;
        try { if (minPrice != null && !minPrice.isBlank()) minPriceInt = Integer.parseInt(minPrice); } catch (Exception ignored) {}
        try { if (maxPrice != null && !maxPrice.isBlank()) maxPriceInt = Integer.parseInt(maxPrice); } catch (Exception ignored) {}

        Page<EventDTO> listEvents = eventService.searchWithFilter(
                keyword, tab, city, categoryIds,
                startDate, endDate,
                minPrice, maxPrice,
                priceMode, page
        );

        model.addAttribute("keyword", keyword);
        model.addAttribute("tab", tab);
        model.addAttribute("selectedVenue", city);
        model.addAttribute("selectedCategories", categoryIds);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("minPrice", minPriceInt);
        model.addAttribute("maxPrice", maxPriceInt);
        model.addAttribute("priceMode", priceMode);

        model.addAttribute("venues", venueService.getFilterVenues());
        model.addAttribute("categories", categoryService.getAllCategory());

        model.addAttribute("listEvents", listEvents);
        model.addAttribute("totalPages", listEvents.getTotalPages());
        model.addAttribute("currentPage", listEvents.getNumber());
        model.addAttribute("totalItems", listEvents.getTotalElements());
        model.addAttribute("size", listEvents.getNumberOfElements());

        return "home/filter";
    }
}
