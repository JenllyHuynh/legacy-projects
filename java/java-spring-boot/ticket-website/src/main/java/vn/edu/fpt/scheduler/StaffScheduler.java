package vn.edu.fpt.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.edu.fpt.service.EventService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class StaffScheduler {
    private final EventService eventService;
    private static final Logger log = LoggerFactory.getLogger(StaffScheduler.class);


    // ════════════════════════════════════════════════════════════════════════
    // Chỗ này dùng để set db sau mỗi 60s theo thời gian thực
    // Set EvenStatus
    // ════════════════════════════════════════════════════════════════════════
    @Scheduled(fixedDelay = 60000)
    public void updateEventStatus() {
        eventService.updateEventToLive();
    }

    @Scheduled(fixedDelay = 60000)
    public void updateEndEventStatus() {
        eventService.updateEndEvent();
    }
}
