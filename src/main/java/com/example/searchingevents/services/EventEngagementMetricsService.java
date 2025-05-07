package com.example.searchingevents.services;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.models.EventEngagementMetrics;

public interface EventEngagementMetricsService {
    EventEngagementMetrics findOrCreateByEvent(Event event);
    void save(EventEngagementMetrics metrics);
}
