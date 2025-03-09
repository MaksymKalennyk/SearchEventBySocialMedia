package com.example.searchingevents.services;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.repos.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * Фільтруємо події за:
     * 1) eventTypeStr (null = не фільтруємо)
     * 2) city (null = не фільтруємо)
     * 3) maxPrice (перевіряємо мінімальну ціну серед ticketOptions)
     * 4) діапазон дат
     */
    public List<Event> findEvents(String eventTypeStr, String city, int maxPrice, LocalDateTime fromDate, LocalDateTime toDate) {
        List<Event> all = eventRepository.findAll();

        return all.stream()
                .filter(e -> {
                    if (eventTypeStr == null) return true;
                    if (e.getEventType() == null) return true;
                    return e.getEventType().name().equalsIgnoreCase(eventTypeStr);
                })
                .filter(e -> {
                    if (city == null) return true;
                    if (e.getCity() == null) return true;
                    return e.getCity().equalsIgnoreCase(city);
                })
                .filter(e -> {
                    if (maxPrice <= 0) return true;
                    int minPriceOfEvent = e.getTicketOptions().stream()
                            .mapToInt(opt -> opt.getPrice() != null ? opt.getPrice() : 0)
                            .min()
                            .orElse(0);
                    return minPriceOfEvent <= maxPrice;
                })
                .filter(e -> {
                    if (fromDate == null || toDate == null) return true;
                    if (e.getEventDateTime() == null) return true;
                    return !e.getEventDateTime().isBefore(fromDate) && !e.getEventDateTime().isAfter(toDate);
                })
                .collect(Collectors.toList());
    }
}

