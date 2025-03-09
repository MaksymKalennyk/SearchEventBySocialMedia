package com.example.searchingevents.controllers;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.models.Users;
import com.example.searchingevents.models.enums.EventType;
import com.example.searchingevents.services.AuthenticationService;
import com.example.searchingevents.services.EventService;
import com.example.searchingevents.services.UserInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class RecommendationController {

    private final UserInterestService userInterestService;
    private final EventService eventService;
    private final AuthenticationService authService;

    @PostMapping("/events/track/{eventId}")
    public void trackInterest(@PathVariable Long eventId) {
        Users currentUser = authService.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("No authenticated user"));

        Event event = eventService.findEventById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));


        String city = event.getCity();
        EventType type = event.getEventType();
        int avgPrice = eventService.getAveragePrice(event);

        userInterestService.trackInterest(currentUser, city, type, avgPrice);
    }

    @GetMapping("/events/top")
    public List<Event> getTopRecommendations() {
        Users currentUser = authService.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("No authenticated user"));

        return userInterestService.getRecommendedEvents(currentUser, 3);
    }
}
