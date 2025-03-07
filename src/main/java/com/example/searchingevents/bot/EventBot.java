package com.example.searchingevents.bot;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.services.EventService;
import com.example.searchingevents.services.SeleniumScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(EventBot.class);

    private final String botToken = System.getProperty("bot.token", "default-token");
    private final String botUsername = System.getProperty("bot.username", "default-username");

    private final EventService eventService;
    private final ParsingService parsingService;

    // Додаємо SeleniumScraperService
    private final SeleniumScraperService seleniumScraperService;

    @Autowired
    public EventBot(EventService eventService,
                    ParsingService parsingService,
                    SeleniumScraperService seleniumScraperService) {
        super(new DefaultBotOptions());
        this.eventService = eventService;
        this.parsingService = parsingService;
        this.seleniumScraperService = seleniumScraperService;
        logger.info("EventBot створено з EventService, ParsingService, та SeleniumScraperService");
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update == null) {
            logger.warn("Отримано null update");
            return;
        }

        logger.info("Отримано новий update: {}", update);

        if (update.hasMessage() && update.getMessage().hasText()) {
            boolean isGroup = update.getMessage().isGroupMessage()
                    || update.getMessage().isSuperGroupMessage();
            boolean isChannel = update.getMessage().isChannelMessage();

            logger.info("Повідомлення з каналу: {}, з групи: {}", isChannel, isGroup);

            if (isChannel || isGroup) {
                String text = update.getMessage().getText();
                Long chatId = update.getMessage().getChatId();
                Integer messageId = update.getMessage().getMessageId();

                String cleanedText = parsingService.removeEmojis(text);

                List<Event> eventsToSave = new ArrayList<>();

                List<MessageEntity> entities = update.getMessage().getEntities();
                if (entities != null) {
                    int countLinks = 0;
                    for (MessageEntity entity : entities) {
                        if ("text_link".equals(entity.getType())) {
                            countLinks++;
                            String textLink = entity.getText();
                            String linkUrl  = entity.getUrl();

                            Event ev = parsingService.parseFromTextLink(
                                    textLink,
                                    linkUrl,
                                    cleanedText
                            );
                            ev.setChatId(chatId);
                            ev.setMessageId(messageId);
                            ev.setRawText(cleanedText);

                            if (ev.getUrl() != null && !ev.getUrl().isEmpty()) {
                                logger.info("Викликаємо SeleniumScraperService для URL={}", ev.getUrl());
                                var ticketOptions = seleniumScraperService.scrapePrices(ev.getUrl());
                                ticketOptions.forEach(opt -> opt.setEvent(ev));
                                ev.getTicketOptions().addAll(ticketOptions);
                            }

                            eventsToSave.add(ev);
                        }
                    }
                    if (countLinks == 0) {
                        List<Event> fallbackList = parsingService.parseMultipleEvents(cleanedText);
                        for (Event ev : fallbackList) {
                            ev.setChatId(chatId);
                            ev.setMessageId(messageId);
                            ev.setRawText(cleanedText);

                            if (ev.getUrl() != null && !ev.getUrl().isEmpty()) {
                                logger.info("Викликаємо SeleniumScraperService для URL={}", ev.getUrl());
                                var ticketOptions = seleniumScraperService.scrapePrices(ev.getUrl());
                                ticketOptions.forEach(opt -> opt.setEvent(ev));
                                ev.getTicketOptions().addAll(ticketOptions);
                            }
                            eventsToSave.add(ev);
                        }
                    }
                } else {
                    List<Event> fallbackList = parsingService.parseMultipleEvents(cleanedText);
                    for (Event ev : fallbackList) {
                        ev.setChatId(chatId);
                        ev.setMessageId(messageId);
                        ev.setRawText(cleanedText);

                        if (ev.getUrl() != null && !ev.getUrl().isEmpty()) {
                            logger.info("Викликаємо SeleniumScraperService для URL={}", ev.getUrl());
                            var ticketOptions = seleniumScraperService.scrapePrices(ev.getUrl());
                            ticketOptions.forEach(opt -> opt.setEvent(ev));
                            ev.getTicketOptions().addAll(ticketOptions);
                        }
                        eventsToSave.add(ev);
                    }
                }

                if (!eventsToSave.isEmpty()) {
                    for (Event ev : eventsToSave) {
                        logger.info("Збереження розпізнаного івенту: {}", ev);
                        eventService.saveEvent(ev);
                    }
                } else {
                    logger.warn("Не вдалося розпізнати жоден івент у повідомленні: {}", text);
                }
            }
        } else {
            logger.debug("Update не містить текстового повідомлення.");
        }
    }
}