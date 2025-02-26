package com.example.searchingevents.controllers;

import com.example.searchingevents.bot.ParsingService;
import com.example.searchingevents.models.Event;
import com.example.searchingevents.models.EventDTO;
import com.example.searchingevents.models.SearchCriteria;
import com.example.searchingevents.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class SearchController {

    private final EventService eventService;
    private final ParsingService parsingService;

    @Autowired
    public SearchController(EventService eventService, ParsingService parsingService) {
        this.eventService = eventService;
        this.parsingService = parsingService;
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEvents(@RequestBody String query) {
        SearchCriteria criteria = parsingService.parseSearchCriteria(query);
        if (criteria == null) {
            return ResponseEntity.badRequest().build();
        }

        String eventTypeStr = criteria.getEventType() != null ? criteria.getEventType().name() : null;
        String cityStr = criteria.getCity() != null ? criteria.getCity().getValue() : null;

        List<Event> events = eventService.findEvents(
                eventTypeStr,
                cityStr,
                criteria.getMaxPrice(),
                criteria.getDateFrom(),
                criteria.getDateTo()
        );

        List<EventDTO> dtos = events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setChatId(event.getChatId());
        dto.setMessageId(event.getMessageId());
        dto.setRawText(event.getRawText());
        dto.setEventType(event.getEventType() != null ? event.getEventType().getDisplayValue() : "Невідомо");
        dto.setEventDate(event.getEventDate());
        dto.setPrice(event.getPrice() != null ? String.valueOf(event.getPrice()) : "Невідомо");
        dto.setCity(event.getCity() != null ? event.getCity() : "Невідомо");
        dto.setUrl(event.getUrl());
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }
}
