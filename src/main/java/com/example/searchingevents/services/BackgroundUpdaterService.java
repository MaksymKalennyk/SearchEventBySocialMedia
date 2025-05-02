package com.example.searchingevents.services;

import com.example.searchingevents.models.Event;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BackgroundUpdaterService {

    private final EventService eventService;
    private final SeleniumScraperService seleniumScraperService;

    @Autowired
    public BackgroundUpdaterService(EventService eventService, SeleniumScraperService seleniumScraperService) {
        this.eventService = eventService;
        this.seleniumScraperService = seleniumScraperService;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void refreshEvents() {
        List<Event> allEvents = eventService.findAll();
        for (Event event : allEvents) {
            if (event.getUrl() == null || event.getUrl().isBlank()) continue;

            var scrape = seleniumScraperService.scrapeEvent(event.getUrl());

            event.setEventDateTime(scrape.getDateTime());
            event.setLastUpdatedAt(LocalDateTime.now());

            event.getTicketOptions().clear();
            for (var opt : scrape.getTicketOptions()) {
                opt.setEvent(event);
                event.getTicketOptions().add(opt);
            }

            eventService.saveEvent(event);
        }
    }
}
