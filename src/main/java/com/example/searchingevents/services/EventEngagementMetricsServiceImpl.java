package com.example.searchingevents.services;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.models.EventEngagementMetrics;
import com.example.searchingevents.repos.EventEngagementMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventEngagementMetricsServiceImpl implements EventEngagementMetricsService {

    private final EventEngagementMetricsRepository metricsRepository;

    @Override
    public EventEngagementMetrics findOrCreateByEvent(Event event) {
        return metricsRepository.findByEventId(event.getId())
                .orElseGet(() -> {
                    EventEngagementMetrics newMetrics = EventEngagementMetrics.builder()
                            .event(event)
                            .commentCount(0)
                            .positiveCommentCount(0)
                            .negativeCommentCount(0)
                            .neutralCommentCount(0)
                            .lastUpdatedAt(LocalDateTime.now())
                            .build();
                    return metricsRepository.save(newMetrics);
                });
    }


    @Override
    public void save(EventEngagementMetrics metrics) {
        metricsRepository.save(metrics);
    }
}
