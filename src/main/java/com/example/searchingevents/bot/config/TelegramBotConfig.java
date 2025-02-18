package com.example.searchingevents.bot.config;

import com.example.searchingevents.bot.EventBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotConfig.class);

    @Bean
    public TelegramBotsApi telegramBotsApi(EventBot eventBot) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(eventBot);
            logger.info("Бот успішно зареєстрований у TelegramBotsApi");
            return telegramBotsApi;
        } catch (TelegramApiException e) {
            logger.error("Помилка при реєстрації бота", e);
            throw new RuntimeException("Не вдалося зареєструвати бота", e);
        }
    }
}