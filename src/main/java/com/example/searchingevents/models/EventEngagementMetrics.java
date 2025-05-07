package com.example.searchingevents.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_engagement_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEngagementMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer commentCount = 0;
    private Integer positiveCommentCount = 0;
    private Integer negativeCommentCount = 0;
    private Integer neutralCommentCount = 0;

    private LocalDateTime lastUpdatedAt;

    @OneToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
