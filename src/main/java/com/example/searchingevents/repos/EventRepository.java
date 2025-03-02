package com.example.searchingevents.repos;

import com.example.searchingevents.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    void deleteByEventDateBefore(LocalDate today);
}
