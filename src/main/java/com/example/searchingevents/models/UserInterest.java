package com.example.searchingevents.models;

import com.example.searchingevents.models.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_interests")
public class UserInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private String city;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private Integer averagePrice;

    private Integer frequency;
}