package com.example.searchingevents.services;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.repos.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCleanupService {

    private final EventRepository eventRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deletePastEvents() {
        LocalDateTime today = LocalDateTime.now();
        List<Event> oldEvents = eventRepository.findByEventDateTimeBefore(today);

        for (Event event : oldEvents) {
            event.getTicketOptions().clear();
            eventRepository.delete(event);
        }
    }
}
