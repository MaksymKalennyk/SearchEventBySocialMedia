package com.example.searchingevents.models.dto;

import java.util.List;

public record EventTopDTO(Long id,
                          String rawText,
                          String eventType,
                          String eventDateTime,
                          String city,
                          String url,
                          List<TicketOptionDTO> ticketOptions) {
}
