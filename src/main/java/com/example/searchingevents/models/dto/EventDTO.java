package com.example.searchingevents.models.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDTO {
    private Long id;
    private Long chatId;
    private Integer messageId;
    private String rawText;
    private String eventType;
    private LocalDate eventDate;

    private List<TicketOptionDTO> ticketOptions;

    private String city;
    private String url;
    private LocalDateTime createdAt;
}

