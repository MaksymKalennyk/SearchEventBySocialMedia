package com.example.searchingevents.models;

import com.example.searchingevents.models.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private Integer messageId;

    @Lob
    private String rawText;

    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private LocalDateTime eventDateTime;

    private String city;

    private String url;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketOption> ticketOptions = new ArrayList<>();

    private LocalDateTime createdAt;

}
