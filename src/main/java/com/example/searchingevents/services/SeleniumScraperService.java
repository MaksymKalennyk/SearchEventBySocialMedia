package com.example.searchingevents.services;

import com.example.searchingevents.models.TicketOption;
import com.example.searchingevents.bot.dto.ScrapeResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SeleniumScraperService {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumScraperService.class);

    private final ObjectMapper mapper = new ObjectMapper();

    public ScrapeResult scrapeEvent(String url) {
        logger.info("Починаємо скрапінг за посиланням: {}", url);

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        ScrapeResult result = ScrapeResult.builder()
                .ticketOptions(new ArrayList<>())
                .build();

        try {
            driver.get(url);
            logger.info("Сторінку завантажено: {}", driver.getTitle());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("kr-shopping-cart-widget")));

            WebElement dateElement = driver.findElement(By.cssSelector(".disp_row.event_info .date"));
            String dateTimeText = dateElement.getText().trim();
            logger.info("Знайдено рядок дати/часу: {}", dateTimeText);

            LocalDateTime parsedDateTime = parseDateTime(dateTimeText);
            result.setDateTime(parsedDateTime);
            logger.info("Розпарсили дату та час: {}", parsedDateTime);

            WebElement cartWidget = driver.findElement(By.cssSelector("kr-shopping-cart-widget"));
            String rawIframeLink = cartWidget.getAttribute("iframelink");
            logger.info("iframelink (raw) = {}", rawIframeLink);

            if (rawIframeLink == null || rawIframeLink.isBlank()) {
                logger.warn("iframelink порожній, не можемо знайти URL схеми залу.");
                return result; // повернемо, але list<TicketOption> буде пустим
            }

            JsonNode node = mapper.readTree(rawIframeLink);
            if (!node.has("value")) {
                logger.warn("У JSON немає поля 'value'. JSON={}", node);
                return result;
            }
            String hallUrl = node.get("value").asText().trim();
            logger.info("Реальний URL HallViewWidget = {}", hallUrl);

            driver.get(hallUrl);

            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".legend .legend-item")));

            List<WebElement> legendItems = driver.findElements(By.cssSelector(".legend .legend-item"));
            logger.info("Знайдено legend-item елементів: {}", legendItems.size());

            for (WebElement item : legendItems) {
                String priceAttr = item.getAttribute("price");
                if ("all".equalsIgnoreCase(priceAttr)) {
                    continue;
                }
                int price = parsePrice(priceAttr);

                String popupAttr = item.getAttribute("popup");
                int seats = parseSeats(popupAttr);

                TicketOption opt = TicketOption.builder()
                        .price(price)
                        .availableSeats(seats)
                        .build();

                logger.debug("Квиткова опція: price={}, seats={} (popup='{}')", price, seats, popupAttr);
                result.getTicketOptions().add(opt);
            }

        } catch (Exception e) {
            logger.error("Помилка під час скрапінгу {}", url, e);
        } finally {
            driver.quit();
            logger.info("Веб-драйвер закрито");
        }

        logger.info("Скрапінг завершено. Дата/час={}, варіантів квитків={}.",
                result.getDateTime(),
                result.getTicketOptions().size());
        return result;
    }

    /**
     * Парсимо рядок "12 березня 2025, 18:00" у LocalDateTime.
     * Припускаємо, що формат завжди: "день місяцьРодовий рік, HH:MM"
     *
     * @param dateTimeText "12 березня 2025, 18:00"
     * @return LocalDateTime
     */
    private LocalDateTime parseDateTime(String dateTimeText) {
        // Приклад: "12 березня 2025, 18:00"
        // Спочатку розділимо за комою
        String[] parts = dateTimeText.split(",");
        if (parts.length < 2) {
            logger.warn("Несподіваний формат: '{}'", dateTimeText);
            return null;
        }

        String datePart = parts[0].trim();
        String timePart = parts[1].trim();

        String[] dateTokens = datePart.split("\\s+"); // [ "12", "березня", "2025" ]
        if (dateTokens.length < 3) return null;
        int day = Integer.parseInt(dateTokens[0]);
        String monthStr = dateTokens[1].toLowerCase(); // "березня"
        int year = Integer.parseInt(dateTokens[2]);

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

        int month = monthMap.getOrDefault(monthStr, 1);

        String[] hours = timePart.split(":");
        if (hours.length < 2) return null;
        int hour = Integer.parseInt(hours[0]);
        int minute = Integer.parseInt(hours[1]);

        return LocalDateTime.of(year, month, day, hour, minute);
    }

    private int parsePrice(String priceAttr) {
        if (priceAttr == null) return 0;
        try {
            return Integer.parseInt(priceAttr);
        } catch (NumberFormatException e) {
            logger.warn("Не вдалося перетворити priceAttr='{}' на число. Повертаємо 0.", priceAttr);
            return 0;
        }
    }

    private int parseSeats(String popupAttr) {
        if (popupAttr == null) return 0;
        Matcher m = Pattern.compile("(\\d+)").matcher(popupAttr);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }
}