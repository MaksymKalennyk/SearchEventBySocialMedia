package com.example.searchingevents.repos;

import com.example.searchingevents.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    void deleteByEventDateTimeBefore(LocalDateTime today);
}
