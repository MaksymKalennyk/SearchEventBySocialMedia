package com.example.searchingevents.bot;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.models.dto.SearchCriteria;
import com.example.searchingevents.models.enums.City;
import com.example.searchingevents.models.enums.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ParsingService {

    private static final Logger logger = LoggerFactory.getLogger(ParsingService.class);

    public Event parseFromTextLink(String textLink, String linkUrl, String fullMessage) {
        String cityPart = textLink;

        int commaIndex = textLink.indexOf(',');
        if (commaIndex != -1) {
            cityPart = textLink.substring(0, commaIndex).trim();
        }

        EventType detectedType = detectTypeFromText(fullMessage.toLowerCase());

        City detectedCity = City.detect(cityPart.toLowerCase());

        Event ev = new Event();
        ev.setEventType(detectedType);
        if (detectedCity != null) {
            ev.setCity(detectedCity.getValue());
        } else {
            ev.setCity(cityPart);
        }
        ev.setUrl(linkUrl);

        return ev;
    }

    public List<Event> parseMultipleEvents(String text) {
        if (text == null) {
            return Collections.emptyList();
        }

        String cleaned = removeEmojis(text);
        EventType detectedType = detectTypeFromText(cleaned.toLowerCase());

        String regex = "([^,]+),\\s*([^()]+)\\((https?://[^)]+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cleaned);

        List<Event> events = new ArrayList<>();
        while (matcher.find()) {
            String cityPart = matcher.group(1).trim();
            String urlPart  = matcher.group(3).trim();

            City detectedCity = City.detect(cityPart.toLowerCase());

            Event ev = new Event();
            ev.setEventType(detectedType);
            ev.setUrl(urlPart);

            if (detectedCity != null) {
                ev.setCity(detectedCity.getValue());
            } else {
                ev.setCity(cityPart);
            }

            events.add(ev);
        }

        if (events.isEmpty()) {
            Event single = parseSingleEvent(cleaned);
            if (single != null) {
                events.add(single);
            }
        }

        return events;
    }

    public Event parseSingleEvent(String text) {
        EventType detectedType = detectTypeFromText(text.toLowerCase());
        if (detectedType == null) {
            return null;
        }
        City detectedCity = City.detect(text.toLowerCase());

        Event e = new Event();
        e.setEventType(detectedType);
        if (detectedCity != null) {
            e.setCity(detectedCity.getValue());
        }

        return e;
    }

    private EventType detectTypeFromText(String lower) {
        return EventType.detect(lower);
    }

    public String removeEmojis(String text) {
        if (text == null) return null;
        StringBuilder sb = new StringBuilder();
        text.codePoints().forEach(cp -> {
            if (Character.isBmpCodePoint(cp)) {
                sb.appendCodePoint(cp);
            }
        });
        return sb.toString();
    }

    public SearchCriteria parseSearchCriteria(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        String lower = removeEmojis(text).toLowerCase();
        SearchCriteria criteria = new SearchCriteria();

         Pattern budgetPattern = Pattern.compile("до\\s+(\\d+)\\s?(грн|₴|гривень)");
        Matcher budgetMatcher = budgetPattern.matcher(lower);
        if (budgetMatcher.find()) {
            try {
                int budget = Integer.parseInt(budgetMatcher.group(1));
                criteria.setMaxPrice(budget);
            } catch (NumberFormatException e) {
                criteria.setMaxPrice(0);
            }
        } else {
            criteria.setMaxPrice(0);
        }

        Pattern cityPattern = Pattern.compile("в\\s+((?:київ|києві)|(?:львів|львові)|(?:одеса|одесі))");
        Matcher cityMatcher = cityPattern.matcher(lower);
        if (cityMatcher.find()) {
            String cityFound = cityMatcher.group(1);
            City detectedCity = City.detect(cityFound);
            criteria.setCity(detectedCity);
        } else {
            criteria.setCity(null);
        }

        boolean hasTheatre = lower.contains("театр") || lower.contains("вистава");
        boolean hasConcert = lower.contains("концерт");
        if (hasTheatre && hasConcert) {
            criteria.setEventType(null);
        } else if (hasConcert) {
            criteria.setEventType(EventType.CONCERT);
        } else if (hasTheatre) {
            criteria.setEventType(EventType.THEATRE);
        } else {
            criteria.setEventType(null);
        }

        logger.info("Parsed search criteria: eventType={}, city={}, maxPrice={}, dateFrom={}, dateTo={}",
                criteria.getEventType(),
                criteria.getCity() != null ? criteria.getCity().getValue() : null,
                criteria.getMaxPrice(), criteria.getDateFrom(), criteria.getDateTo());
        return criteria;
    }

}