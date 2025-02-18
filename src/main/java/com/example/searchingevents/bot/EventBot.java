package com.example.searchingevents.bot;

import com.example.searchingevents.models.Event;
import com.example.searchingevents.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EventBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(EventBot.class);

    private final String botToken = "7748130126:AAEybdxxqL1G2n0GdvKHYuHkl_hlYszjzro";
    private final String botUsername = "Searching-Events";

    private final EventService eventService;
    private final ParsingService parsingService;

    @Autowired
    public EventBot(EventService eventService, ParsingService parsingService) {
        super(new DefaultBotOptions());
        this.eventService = eventService;
        this.parsingService = parsingService;
        logger.info("EventBot створено з EventService та ParsingService");
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

                logger.info("Отримано повідомлення в чаті {} (ID: {}): {}", chatId, messageId, text);

                // Парсимо повідомлення
                Event parsedEvent = parsingService.parseMessage(text);
                if (parsedEvent != null) {
                    parsedEvent.setChatId(chatId);
                    parsedEvent.setMessageId(messageId);
                    // Зберігаємо сирий текст (без емоджі) або оригінальний - це на ваш розсуд:
                    // Якщо треба оригінал (щоб не втратити емоджі), краще налаштувати utf8mb4 у БД
                    parsedEvent.setRawText(parsingService.removeEmojis(text));

                    logger.info("Збереження розпізнаного івенту: {}", parsedEvent);

                    // Зберігаємо в БД
                    eventService.saveEvent(parsedEvent);
                } else {
                    logger.warn("Не вдалося розпізнати івент у повідомленні: {}", text);
                }
            }
        } else {
            logger.debug("Update не містить текстового повідомлення.");
        }
    }
}
