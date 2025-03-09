package com.example.searchingevents.services;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.models.TicketOption;
import com.example.searchingevents.models.enums.EventType;
import com.example.searchingevents.repos.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event saveEvent(Event event) {
        event.setCreatedAt(LocalDateTime.now());
        return eventRepository.save(event);
    }

    public Optional<Event> findEventById(Long id) {
        return eventRepository.findById(id);
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

    public int getAveragePrice(Event event) {
        if (event.getTicketOptions() == null || event.getTicketOptions().isEmpty()) {
            return 0;
        }
        int sum = 0, count = 0;
        for (TicketOption opt : event.getTicketOptions()) {
            if (opt.getPrice() != null) {
                sum += opt.getPrice();
                count++;
            }
        }
        if (count == 0) return 0;
        return sum / count;
    }

    public List<Event> findEventsByUserInterest(String city, EventType type, int minBudget, int maxBudget) {
       List<Event> all = eventRepository.findAll();

        return all.stream()
                .filter(e -> e.getCity() != null && e.getCity().equalsIgnoreCase(city))
                .filter(e -> e.getEventType() == type)
                .filter(e -> {
                    if (e.getTicketOptions() == null || e.getTicketOptions().isEmpty()) return false;
                    return e.getTicketOptions().stream().anyMatch(opt -> {
                        Integer p = opt.getPrice();
                        if (p == null) return false;
                        return p >= minBudget && p <= maxBudget;
                    });
                })
                .toList();
    }
}

