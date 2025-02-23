package com.example.searchingevents.bot;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.models.SearchCriteria;
import com.example.searchingevents.models.enums.City;
import com.example.searchingevents.models.enums.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ParsingService {

    private static final Logger logger = LoggerFactory.getLogger(ParsingService.class);

    public Event parseFromTextLink(String textLink, String linkUrl, String fullMessage) {
        String cityPart = textLink;
        String datePart = null;

        int commaIndex = textLink.indexOf(',');
        if (commaIndex != -1) {
            cityPart = textLink.substring(0, commaIndex).trim();
            datePart = textLink.substring(commaIndex + 1).trim();
        }

        EventType detectedType = detectTypeFromText(fullMessage.toLowerCase());

        City detectedCity = City.detect(cityPart.toLowerCase());

        LocalDate parsedDate = parseDate(Objects.requireNonNullElse(datePart, textLink).toLowerCase());

        Event ev = new Event();
        ev.setEventType(detectedType);

        if (detectedCity != null) {
            ev.setCity(detectedCity.getValue());
        } else {
            ev.setCity(cityPart);
        }

        ev.setEventDate(parsedDate);
        ev.setUrl(linkUrl);

        Integer price = parsePrice(fullMessage.toLowerCase());
        ev.setPrice(price);

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
            String datePart = matcher.group(2).trim();
            String urlPart  = matcher.group(3).trim();

            City detectedCity = City.detect(cityPart.toLowerCase());
            LocalDate parsedDate = parseDate(datePart.toLowerCase());

            Event ev = new Event();
            ev.setEventType(detectedType);
            ev.setUrl(urlPart);

            if (detectedCity != null) {
                ev.setCity(detectedCity.getValue());
            } else {
                ev.setCity(cityPart);
            }
            ev.setEventDate(parsedDate);

            Integer price = parsePrice(cleaned.toLowerCase());
            ev.setPrice(price);

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
        LocalDate date = parseDate(text.toLowerCase());
        Integer price = parsePrice(text.toLowerCase());

        Event e = new Event();
        e.setEventType(detectedType);
        if (detectedCity != null) {
            e.setCity(detectedCity.getValue());
        }
        e.setEventDate(date);
        e.setPrice(price);
        return e;
    }

    private EventType detectTypeFromText(String lower) {
        return EventType.detect(lower);
    }

    private LocalDate parseDate(String lower) {
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("січня", 1);
        monthMap.put("лютого", 2);
        monthMap.put("березня", 3);
        monthMap.put("квітня", 4);
        monthMap.put("травня", 5);
        monthMap.put("червня", 6);
        monthMap.put("липня", 7);
        monthMap.put("серпня", 8);
        monthMap.put("вересня", 9);
        monthMap.put("жовтня", 10);
        monthMap.put("листопада", 11);
        monthMap.put("грудня", 12);

        String regex = "\\b(\\d{1,2})\\s+(січня|лютого|березня|квітня|травня|червня|липня|серпня|вересня|жовтня|листопада|грудня)\\b";
        Matcher m = Pattern.compile(regex).matcher(lower);
        if (m.find()) {
            String dayStr = m.group(1);
            String monthStr = m.group(2);
            int day = Integer.parseInt(dayStr);
            int month = monthMap.getOrDefault(monthStr, 0);

            int year = Year.now().getValue();
            try {
                return java.time.LocalDate.of(year, month, day);
            } catch (DateTimeException e) {
                return null;
            }
        }
        return null;
    }

    private Integer parsePrice(String lower) {
        String regex = "(\\d+)\\s?(грн|₴|гривень)";
        Matcher matcher = Pattern.compile(regex).matcher(lower);
        if (matcher.find()) {
            try {
                return Integer.valueOf(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
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

        // Очищаємо текст від емоджі та переводимо в нижній регістр
        String lower = removeEmojis(text).toLowerCase();
        SearchCriteria criteria = new SearchCriteria();

        // --- Парсинг діапазону дат ---
        // Шукаємо шаблон: "з <дата> до <дата>" (наприклад, "з 1 березня до 30 березня")
        Pattern dateRangePattern = Pattern.compile("з\\s+(\\d{1,2}\\s+\\p{L}+)\\s+до\\s+(\\d{1,2}\\s+\\p{L}+)");
        Matcher dateRangeMatcher = dateRangePattern.matcher(lower);
        if (dateRangeMatcher.find()) {
            String fromDateStr = dateRangeMatcher.group(1).trim();
            String toDateStr = dateRangeMatcher.group(2).trim();
            LocalDate dateFrom = parseDate(fromDateStr);
            LocalDate dateTo = parseDate(toDateStr);
            criteria.setDateFrom(dateFrom);
            criteria.setDateTo(dateTo);
        } else {
            criteria.setDateFrom(null);
            criteria.setDateTo(null);
        }

        // --- Парсинг бюджету ---
        // Шукаємо "до <число> (гривень|грн|₴)"
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

        // --- Парсинг міста ---
        // Використовуємо регекс, що дозволяє знайти як "київ", так і "києві" (аналогічно для інших міст)
        Pattern cityPattern = Pattern.compile("в\\s+((?:київ|києві)|(?:львів|львові)|(?:одеса|одесі))");
        Matcher cityMatcher = cityPattern.matcher(lower);
        if (cityMatcher.find()) {
            String cityFound = cityMatcher.group(1);
            City detectedCity = City.detect(cityFound);
            if (detectedCity != null) {
                criteria.setCity(detectedCity);
            } else {
                criteria.setCity(null);
            }
        } else {
            criteria.setCity(null);
        }

        // --- Парсинг типу івенту ---
        // Якщо запит містить і "театр"/"вистава", і "концерт" – не фільтруємо за типом (залишаємо null)
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