package com.example.searchingevents.models;

import com.example.searchingevents.models.enums.City;
import com.example.searchingevents.models.enums.EventType;

import java.time.LocalDate;

public class SearchCriteria {
    private EventType eventType;
    private City city;
    private int maxPrice;
    private LocalDate dateFrom;
    private LocalDate dateTo;


    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }
}
