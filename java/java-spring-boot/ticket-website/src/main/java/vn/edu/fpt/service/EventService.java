package vn.edu.fpt.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.edu.fpt.exception.EventNotFoundException;
import vn.edu.fpt.model.dto.CommentDTO;
import vn.edu.fpt.model.dto.EventDTO;
import vn.edu.fpt.model.dto.StaffEventAttendeeDTO;
import vn.edu.fpt.model.entity.Category;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.model.entity.Venue;
import vn.edu.fpt.repository.CommentRepo;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.util.PageSetting;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EventService {
    private final EventRepo eventRepo;
    private final CategoryService categoryService;
    private final CommentRepo commentRepo;
    private static final Logger log = LoggerFactory.getLogger(EventService.class);



    public EventService(EventRepo eventRepo, CategoryService categoryService, CommentRepo commentRepo) {
        this.eventRepo = eventRepo;
        this.categoryService = categoryService;
        this.commentRepo = commentRepo;
    }

    @Transactional
    public Page<Event> getEventByPage(int pageNumber) {
        Pageable pageable =
                PageRequest.of(pageNumber, PageSetting.SIZE_OF_EACH_PAGE);
        return eventRepo.findAll(pageable);
    }

    @Transactional
    public Page<Event> getEventByPage(int pageNumber, int staffId) {
        Pageable pageable = PageRequest.of(pageNumber, PageSetting.SIZE_OF_EACH_PAGE);
        return eventRepo.getEventsByStaff(staffId, pageable);
    }

    @Transactional
    public Page<Event> getEventByPage(int pageNumber, int staffId, String status) {
        Pageable pageable = PageRequest.of(pageNumber, PageSetting.SIZE_OF_EACH_PAGE);

        if (status == null || status.isBlank()) {
            return eventRepo.getEventsByStaff(staffId, pageable);
        }

        return eventRepo.getEventsByStaffAndStatus(staffId, status, pageable);
    }

    //List Feature Event
    @Transactional
    public List<EventDTO> getListFeatureEvents(LocalDateTime now, String status, Pageable pageable) {
        return eventRepo.getListFeatureEvents(now, status, pageable);
    }

    @Transactional
    public List<EventDTO> getListUpcomingEvents(LocalDateTime now, String status, Pageable pageable) {
        return eventRepo.getListUpcomingEvents(now, status, pageable);
    }

    @Transactional
    public List<EventDTO> getListCompletedEvents(String status, Pageable pageable) {
        List<EventDTO> events = eventRepo.getListCompletedEvents(status, pageable);
        events.forEach(e -> {
            e.setRatinAvg(commentRepo.getAverageRating(e.getId()));
        });
        return events;
    }

    @Transactional
    public List<EventDTO> getListTrendingEvents(LocalDateTime now, String status, Pageable pageable) {
        return eventRepo.getListTrendingEvents(now, status, pageable);
    }

    public Map<Category, List<EventDTO>> getMapEventByCategory() {
        Map<Category, List<EventDTO>> mapEvent = new HashMap<>();
        List<Category> listCategories = categoryService.getAllCategory();
        for (Category category : listCategories) {
            mapEvent.put(
                    category,
                    eventRepo.getListEventByCategory(
                            LocalDateTime.now(),
                            "Published",
                            category.getName(),
                            PageRequest.of(0, 4))
            );
        }
        return mapEvent;
    }

    @Transactional
    public Page<Event> getPageEventLifeCycle(int pageNumber, int pageSize, int staffId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        // 🔥 FIX: dùng event theo staff thay vì findAll
        Page<Event> pageEvent = eventRepo.getEventsByStaff(staffId, pageable);
        for (Event event : pageEvent.getContent()) {
            Long total = eventRepo.getTotalTicketType(event.getId());
            if (total == null || total == 0) {
                event.setTicketSoldPercent(0);
            } else {
                long sold = 0;
                try {
                    Long soldObj = eventRepo.getTotalTicketSold(event.getId());
                    sold = (soldObj != null) ? soldObj : 0;
                } catch (Exception e) {
                    System.out.println("Sold null assign = 0");
                }
                event.setTicketSoldPercent(1.0 * sold / total * 100);
            }

            long revenue = 0;
            try {
                Long revenueObj = eventRepo.getTotalRevenue(event.getId(), "Paid");
                revenue = (revenueObj != null) ? revenueObj : 0;
            } catch (Exception e) {
                System.out.println("Do not have any sold ticket!");
            }
            event.setRevenue(BigDecimal.valueOf(revenue));
            event.setStartDateString(DateTimeFormatter
                    .ofPattern("EEE, MMM dd", Locale.ENGLISH)
                    .format(event.getStartDateTime()));
            event.setStartTimeString(DateTimeFormatter
                    .ofPattern("HH:mm", Locale.ENGLISH)
                    .format(event.getStartDateTime()));
        }
        return pageEvent;
    }

    @Transactional
    public Page<Event> getPageEventLifeCycle(int pageNumber, int pageSize, int staffId, String status) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Event> pageEvent = eventRepo.getEventsByStaffAndStatus(staffId, status, pageable);

        for (Event event : pageEvent.getContent()) {
            Long total = eventRepo.getTotalTicketType(event.getId());

            if (total == null || total == 0) {
                event.setTicketSoldPercent(0);
            } else {
                long sold = 0;
                try {
                    Long soldObj = eventRepo.getTotalTicketSold(event.getId());
                    sold = (soldObj != null) ? soldObj : 0;
                } catch (Exception e) {}

                event.setTicketSoldPercent(1.0 * sold / total * 100);
            }

            long revenue = 0;
            try {
                Long revenueObj = eventRepo.getTotalRevenue(event.getId(), "Paid");
                revenue = (revenueObj != null) ? revenueObj : 0;
            } catch (Exception e) {}

            event.setRevenue(BigDecimal.valueOf(revenue));

            event.setStartDateString(DateTimeFormatter
                    .ofPattern("EEE, MMM dd", Locale.ENGLISH)
                    .format(event.getStartDateTime()));

            event.setStartTimeString(DateTimeFormatter
                    .ofPattern("HH:mm", Locale.ENGLISH)
                    .format(event.getStartDateTime()));
        }

        return pageEvent;
    }


//    public Event getPublishedEvent(Integer eventId) {
//        return eventRepo.findByIdAndEventStatus(eventId, "Published").
//                orElseThrow(()-> new EventNotFoundException("Event not found or not published"));
//    }

    public Optional<Event> getPublishedEvent(Integer eventId) {
        return eventRepo.findByIdAndEventStatus(eventId, "Published");
    }

    // Get list comments
    public List<CommentDTO> getListCommentsByEventId(int eventId) {
        return eventRepo.getListCommentsByEventId(eventId);
    }

    // Them method de nguoi dung co the xem duoc event completed
    public Optional<Event> getCompletedEvent(Integer eventId) {
        return eventRepo.findByIdAndEventStatus(eventId, "Completed");
    }

    // Them method de nguoi dung co the xem duoc event completed
    public Optional<Event> getLiveEvent(Integer eventId) {
        return eventRepo.findByIdAndEventStatus(eventId, "Live");
    }

    public Optional<Event> getEventById(int id) {
        return eventRepo.findById(id);
    }

    public Double getViewCountPercent(int id) {
        return eventRepo.getViewCountPercentByEventId(id);
    }

    private Page<EventDTO> queryByTab(
            String tab,
            String keyword,
            List<Integer> category,
            String city,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Integer min,
            Integer max,
            String priceMode,
            Pageable pageable
    ) {

        if ("featured".equalsIgnoreCase(tab)) {
            return eventRepo.getFeaturedEventsWithFilter(
                    keyword, category, city,
                    startDateTime, endDateTime,
                    min, max, priceMode,
                    LocalDateTime.now(),
                    pageable
            );
        }

        if ("trending".equalsIgnoreCase(tab)) {
            return eventRepo.getTrendingEventsWithFilter(
                    keyword, category, city,
                    startDateTime, endDateTime,
                    min, max, priceMode,
                    LocalDateTime.now(),
                    pageable
            );
        }

        if ("upcoming".equalsIgnoreCase(tab)) {
            return eventRepo.getUpcomingEventsWithFilter(
                    keyword, category, city,
                    startDateTime, endDateTime,
                    min, max, priceMode,
                    LocalDateTime.now(),
                    pageable
            );
        }

        if ("completed".equalsIgnoreCase(tab)) {
            return eventRepo.getCompletedEventsWithFilter(
                    keyword, category, city,
                    startDateTime, endDateTime,
                    min, max, priceMode,
                    pageable
            );
        }

        if ("live".equalsIgnoreCase(tab)) {
            return eventRepo.getLiveEventsWithFilter(
                    keyword, category, city,
                    startDateTime, endDateTime,
                    min, max, priceMode,
                    pageable
            );
        }

        return eventRepo.searchAllEvents(
                keyword, category, city,
                startDateTime, endDateTime,
                min, max, priceMode,
                pageable
        );
    }

    @Transactional
    public Page<EventDTO> searchWithFilter(
            String keyword,
            String tab,
            String city,
            List<Integer> category,
            LocalDate startDate,
            LocalDate endDate,
            String minPrice,
            String maxPrice,
            String priceMode,
            String page
    ) {

        // 1. NORMALIZE & CLEAN DATA
        keyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        if (city != null && city.isBlank()) {
            city = null;
        }

        // 2. PARSE SAFE
        Integer min = null;
        Integer max = null;
        int pageIndex = 0;

        try {
            if (minPrice != null) {
                min = Integer.parseInt(minPrice);
                if (min < 0) min = 0;
            }
        } catch (Exception ignored) {
            min = null;
        }

        try {
            if (maxPrice != null) {
                max = Integer.parseInt(maxPrice);
                if (max < 0) max = null;
            }
        } catch (Exception ignored) {
            max = null;
        }

        try {
            pageIndex = Integer.parseInt(page);
            if (pageIndex < 0) pageIndex = 0;
        } catch (Exception ignored) {}

        if (!List.of("all", "free", "range").contains(priceMode)) {
            priceMode = "all";
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            LocalDate tmp = startDate;
            startDate = endDate;
            endDate = tmp;
        }

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59) : null;

        Pageable pageable = PageRequest.of(pageIndex, PageSetting.SIZE_OF_EACH_SEARCH);
        Page<EventDTO> result = queryByTab(
                tab, keyword, category, city,
                startDateTime, endDateTime,
                min, max, priceMode,
                pageable
        );

        if (pageIndex >= result.getTotalPages() && result.getTotalPages() > 0) {

            pageIndex = result.getTotalPages() - 1;

            pageable = PageRequest.of(pageIndex, PageSetting.SIZE_OF_EACH_SEARCH);

            // Query lại đúng page cuối
            result = queryByTab(
                    tab, keyword, category, city,
                    startDateTime, endDateTime,
                    min, max, priceMode,
                    pageable
            );
        }
        return result;
    }

    @Transactional
    public StaffEventAttendeeDTO getEventSummary(Integer eventId) {
        return eventRepo.getEventSummary(eventId);
    }

    @Transactional
    public void updateEventToLive() {
        eventRepo.updateLiveEvents();
        log.info("Update event to live");
    }

    @Transactional
    public void updateEndEvent() {
        eventRepo.updateEndEvent();
        log.info("Update Event To Completed");
    }

    @Transactional
    public void saveEvent(Event event) {
        eventRepo.save(event);
    }

    @Transactional
    public int updateVenue(Integer eventId, Venue venue) {
        Event event = eventRepo.getReferenceById(eventId);

        event.setVenue(venue);

        eventRepo.save(event);
        return venue.getId();
    }

    @Transactional
    public void incrementView(Integer id) {
        eventRepo.incrementView(id);
    }

    //Make by An
    public List<Event> getAll() {
        return eventRepo.findAll();
    }

    public List<Event> getAllByEveneStaff(int staffId) {
        return eventRepo.getAllEventByEventStaff(staffId);
    }
}
