package com.example.searchingevents.bot;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.models.enums.City;
import com.example.searchingevents.models.enums.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ParsingService {

    private static final Logger logger = LoggerFactory.getLogger(ParsingService.class);

    /**
     * Парсимо текст і, якщо знайдено релевантні дані (концерт/театр...),
     * повертаємо Event (без збереження).
     * Якщо текст не містить івенту, повертаємо null.
     */
    public Event parseMessage(String text) {
        if (text == null) return null;

        String cleaned = removeEmojis(text);
        String lower = cleaned.toLowerCase();

        // Визначаємо тип івенту
        EventType detectedType = EventType.detect(lower);
        if (detectedType == null) {
            return null;
        }

        // Визначаємо місто
        City detectedCity = City.detect(lower);

        // Парсимо дату, ціну і т.д.
        LocalDate date = parseDate(lower);
        Integer price = parsePrice(lower);

        // Формуємо Event
        Event event = new Event();
        event.setEventType(detectedType);       // enum
        // Якщо місто знайдено, збережемо його «людську» назву (наприклад, "Львів")
        if (detectedCity != null) {
            event.setCity(detectedCity.getValue());
        } else {
            event.setCity(null); // або "Невідомо"
        }
        event.setEventDate(date);
        event.setPrice(price);

        return event;
    }


    private LocalDate parseDate(String lower) {
        // Мапа назви місяця -> номер
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

        // RegEx: "14 лютого", "1 січня", ...
        String regex = "\\b(\\d{1,2})\\s+(січня|лютого|березня|квітня|травня|червня|липня|серпня|вересня|жовтня|листопада|грудня)\\b";
        Matcher matcher = Pattern.compile(regex).matcher(lower);
        if (matcher.find()) {
            String dayStr = matcher.group(1);
            String monthStr = matcher.group(2);
            int day = Integer.parseInt(dayStr);
            int month = monthMap.getOrDefault(monthStr, 0);

            int year = Year.now().getValue(); // Припускаємо поточний рік
            try {
                return LocalDate.of(year, month, day);
            } catch (DateTimeException e) {
                // Якщо date некоректна, вертаємо null
                return null;
            }
        }
        return null; // Якщо не знайшли нічого
    }

    /**
     * Шукаємо перше число, що передує або йде після (грн|₴|гривень).
     * Напр.: "200 грн", "300₴", "150 гривень".
     */
    private Integer parsePrice(String lower) {
        String regex = "(\\d+)\\s?(грн|₴|гривень)";
        Matcher matcher = Pattern.compile(regex).matcher(lower);
        if (matcher.find()) {
            String priceStr = matcher.group(1);
            try {
                return Integer.valueOf(priceStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Видалити емоджі (та інші 4-байтові символи) з тексту.
     */
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
}