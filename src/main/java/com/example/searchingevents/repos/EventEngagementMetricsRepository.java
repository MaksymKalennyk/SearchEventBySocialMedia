package com.example.searchingevents.repos;

import com.example.searchingevents.models.EventEngagementMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventEngagementMetricsRepository extends JpaRepository<EventEngagementMetrics, Long> {
       Optional<EventEngagementMetrics> findByEventId(Long eventId);
}
