package com.example.searchingevents.services;

import com.example.searchingevents.models.TicketOption;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SeleniumScraperService {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumScraperService.class);

    private final ObjectMapper mapper = new ObjectMapper();

    public List<TicketOption> scrapePrices(String url) {
        logger.info("Починаємо скрапінг за посиланням: {}", url);

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        List<TicketOption> result = new ArrayList<>();
        try {
            driver.get(url);
            logger.info("Сторінку завантажено: {}", driver.getTitle());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("kr-shopping-cart-widget")));

            WebElement cartWidget = driver.findElement(By.cssSelector("kr-shopping-cart-widget"));

            String rawIframeLink = cartWidget.getAttribute("iframelink");
            logger.info("iframelink (raw) = {}", rawIframeLink);

            if (rawIframeLink == null || rawIframeLink.isBlank()) {
                logger.warn("iframelink порожній, не можемо знайти URL схеми залу.");
                return result;
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

                logger.debug("Варіант: price={}, seats={} (popup='{}')", price, seats, popupAttr);
                result.add(opt);
            }

        } catch (Exception e) {
            logger.error("Помилка під час скрапінгу {}", url, e);
        } finally {
            driver.quit();
            logger.info("Веб-драйвер закрито");
        }

        logger.info("Скрапінг завершено. Знайдено {} варіантів квитків.", result.size());
        return result;
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
            } catch (NumberFormatException ignored) {}
        }
        return 0;
    }
}
