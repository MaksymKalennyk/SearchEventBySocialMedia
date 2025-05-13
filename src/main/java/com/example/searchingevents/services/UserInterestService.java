package com.example.searchingevents.services;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.models.UserInterest;
import com.example.searchingevents.models.Users;
import com.example.searchingevents.models.enums.EventType;
import com.example.searchingevents.repos.UserInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInterestService {

    private final UserInterestRepository userInterestRepository;
    private final EventService eventService;

    public void trackInterest(Users user, String city, EventType type, int price) {
        UserInterest existing = userInterestRepository.findByUserAndCityAndEventType(user, city, type);
        if (existing != null) {
            int oldPrice = existing.getAveragePrice() != null ? existing.getAveragePrice() : price;
            int oldFreq = existing.getFrequency() != null ? existing.getFrequency() : 1;
            int newAvg = (int) Math.round((oldPrice * oldFreq + price) / (double)(oldFreq + 1));

            existing.setAveragePrice(newAvg);
            existing.setFrequency(oldFreq + 1);
            userInterestRepository.save(existing);
        } else {
            UserInterest interest = UserInterest.builder()
                    .user(user)
                    .city(city)
                    .eventType(type)
                    .averagePrice(price)
                    .frequency(1)
                    .build();
            userInterestRepository.save(interest);
        }
    }

    public List<Event> getRecommendedEvents(Users user, int limit) {
        List<UserInterest> interests = userInterestRepository.findAllByUserOrderByFrequencyDesc(user);

        if (interests == null || interests.isEmpty()) {
            return eventService.findTopEventsByEngagement(limit);
        }

        List<Event> result = new ArrayList<>();
        for (UserInterest i : interests) {
            if (result.size() >= limit) break;

            int avgPrice = i.getAveragePrice() != null ? i.getAveragePrice() : 0;
            int minBudget = (int)Math.round(avgPrice * 0.8);
            int maxBudget = (int)Math.round(avgPrice * 1.2);

           List<Event> events = eventService.findEventsByUserInterest(i.getCity(), i.getEventType(), minBudget, maxBudget);

            for (Event e : events) {
                if (!result.contains(e)) {
                    result.add(e);
                    if (result.size() >= limit) break;
                }
            }
        }
        return result;
    }
}
