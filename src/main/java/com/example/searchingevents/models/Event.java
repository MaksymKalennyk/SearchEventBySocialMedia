package com.example.searchingevents.models;

import com.example.searchingevents.models.enums.EventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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
    private LocalDate eventDate;
    private Integer price;

    private String city;

    private String url;

    private LocalDateTime createdAt;

}
