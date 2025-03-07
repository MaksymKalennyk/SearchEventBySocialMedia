package com.example.searchingevents.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ticket_options")
public class TicketOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer price;
    private Integer availableSeats;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
