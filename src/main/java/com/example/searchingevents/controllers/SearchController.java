package com.example.searchingevents.controllers;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.models.dto.EventDTO;
import com.example.searchingevents.models.dto.EventSearchRequest;
import com.example.searchingevents.models.dto.TicketOptionDTO;
import com.example.searchingevents.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class SearchController {

    private final EventService eventService;

    @Autowired
    public SearchController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEvents(@RequestBody EventSearchRequest request) {

        List<Event> events = eventService.findEvents(
                request.getEventType(),
                request.getCity(),
                request.getMaxPrice(),
                request.getDateFrom(),
                request.getDateTo()
        );

        List<EventDTO> dtos = events.stream()
                .map(e -> convertToDTO(e, request.getMaxPrice()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    private EventDTO convertToDTO(Event event, Integer maxPrice) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setChatId(event.getChatId());
        dto.setMessageId(event.getMessageId());
        dto.setRawText(event.getRawText());

        dto.setEventType(event.getEventType() != null
                ? event.getEventType().getDisplayValue()
                : "Невідомо");

        dto.setEventDateTime(event.getEventDateTime());
        dto.setCity(event.getCity() != null ? event.getCity() : "Невідомо");
        dto.setUrl(event.getUrl());
        dto.setCreatedAt(event.getCreatedAt());

        if (event.getTicketOptions() != null) {
            List<TicketOptionDTO> optionDTOs = event.getTicketOptions().stream()
                    .filter(opt -> maxPrice == null
                            || maxPrice <= 0
                            || (opt.getPrice() != null && opt.getPrice() <= maxPrice))
                    .map(opt -> {
                        TicketOptionDTO oDto = new TicketOptionDTO();
                        oDto.setId(opt.getId());
                        oDto.setPrice(opt.getPrice());
                        oDto.setAvailableSeats(opt.getAvailableSeats());
                        return oDto;
                    })
                    .collect(Collectors.toList());
            dto.setTicketOptions(optionDTOs);
        } else {
            dto.setTicketOptions(Collections.emptyList());
        }

        return dto;
    }
}