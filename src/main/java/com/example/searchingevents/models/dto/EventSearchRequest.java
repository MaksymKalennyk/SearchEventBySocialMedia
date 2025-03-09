package com.example.searchingevents.models.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventSearchRequest {
    private String city;
    private Integer maxPrice;
    private String eventType;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
}
