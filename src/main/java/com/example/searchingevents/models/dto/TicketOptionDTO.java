package com.example.searchingevents.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketOptionDTO {
    private Long id;
    private Integer price;
    private Integer availableSeats;
}
