package com.example.searchingevents.services;

import com.example.searchingevents.repos.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EventCleanupService {

    private final EventRepository eventRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deletePastEvents() {
        LocalDate today = LocalDate.now();
        eventRepository.deleteByEventDateBefore(today);
    }
}
