package com.example.searchingevents.services;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.models.TicketOption;
import com.example.searchingevents.models.dto.EventTopDTO;
import com.example.searchingevents.models.dto.TicketOptionDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EventMapper {

    public EventTopDTO toDTO(Event event) {
        return new EventTopDTO(
                event.getId(),
                event.getRawText(),
                event.getEventType().name(),
                event.getEventDateTime() != null ? event.getEventDateTime().toString() : null,
                event.getCity(),
                event.getUrl(),
                event.getTicketOptions().stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }

    public TicketOptionDTO toDTO(TicketOption option) {
        return new TicketOptionDTO(option.getId(),option.getPrice(), option.getAvailableSeats());
    }
}

