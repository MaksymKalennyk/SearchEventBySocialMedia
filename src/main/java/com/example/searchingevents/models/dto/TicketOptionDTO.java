package com.example.searchingevents.models.dto;

import lombok.Data;

@Data
public class TicketOptionDTO {
    private Long id;
    private Integer price;
    private Integer availableSeats;
}
