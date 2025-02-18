package com.example.searchingevents.services;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.repos.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }


    public Event saveEvent(Event event) {
        event.setCreatedAt(LocalDateTime.now());
        return eventRepository.save(event);
    }

    public List<Event> findEvents(String eventTypeStr, String city, int maxPrice, LocalDate fromDate, LocalDate toDate) {
        List<Event> all = eventRepository.findAll();

        return all.stream()
                .filter(e -> {
                    if (e.getEventType() == null) return false;
                    return e.getEventType().name().equalsIgnoreCase(eventTypeStr);
                })
                .filter(e -> e.getCity() != null
                        && e.getCity().equalsIgnoreCase(city))
                .filter(e -> e.getPrice() != null
                        && e.getPrice() <= maxPrice)
                .filter(e -> {
                    if (e.getEventDate() == null) return false;
                    return !e.getEventDate().isBefore(fromDate)
                            && !e.getEventDate().isAfter(toDate);
                })
                .collect(Collectors.toList());
    }

}
