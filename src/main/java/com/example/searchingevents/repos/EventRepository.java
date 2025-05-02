package com.example.searchingevents.repos;

import com.example.searchingevents.models.Event;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Modifying
    @Transactional
    List<Event> findByEventDateTimeBefore(LocalDateTime today);
}
