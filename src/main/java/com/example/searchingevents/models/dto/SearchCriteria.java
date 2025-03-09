package com.example.searchingevents.models.dto;

import com.example.searchingevents.models.enums.City;
import com.example.searchingevents.models.enums.EventType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchCriteria {
    private EventType eventType;
    private City city;
    private int maxPrice;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
}
