package com.example.searchingevents.bot.dto;

import com.example.searchingevents.models.TicketOption;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public  class ScrapeResult {
    private LocalDateTime dateTime;
    private List<TicketOption> ticketOptions;
}
