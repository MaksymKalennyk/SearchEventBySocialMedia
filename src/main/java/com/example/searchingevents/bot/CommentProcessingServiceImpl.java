package com.example.searchingevents.bot;

import com.example.searchingevents.bot.dto.SentimentResult;
import com.example.searchingevents.models.Event;
import com.example.searchingevents.models.EventEngagementMetrics;
import com.example.searchingevents.services.EventEngagementMetricsService;
import com.example.searchingevents.services.EventService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentProcessingServiceImpl implements CommentProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(CommentProcessingServiceImpl.class);

    private final EventService eventService;
    private final EventEngagementMetricsService eventEngagementMetricsService;
    private final CommentSentimentAnalyzer commentSentimentAnalyzer;

    @Override
    public void processComment(Message message) {
        if (message.getReplyToMessage() == null) {
            logger.debug("Повідомлення не є відповіддю. Пропущено.");
            return;
        }

        Long chatId = message.getChatId();
        Integer repliedMsgId = message.getReplyToMessage().getMessageId();
        String commentText = message.getText();

        logger.info("Обробка коментаря: chatId={}, replyToMessageId={}, текст='{}'",
                chatId, repliedMsgId, commentText);

        Optional<Event> eventOpt = eventService.findByMessageId(repliedMsgId);
        if (eventOpt.isEmpty()) {
            logger.warn("Не знайдено івенту для messageId={}", repliedMsgId);
            return;
        }

        Event event = eventOpt.get();
        EventEngagementMetrics metrics = eventEngagementMetricsService.findOrCreateByEvent(event);
        metrics.setCommentCount(metrics.getCommentCount() + 1);

        SentimentResult result = commentSentimentAnalyzer.analyze(commentText);
        logger.info("Результат сентимент-аналізу: {}", result.getSentiment());

        switch (result.getSentiment()) {
            case POSITIVE -> metrics.setPositiveCommentCount(metrics.getPositiveCommentCount() + 1);
            case NEGATIVE -> metrics.setNegativeCommentCount(metrics.getNegativeCommentCount() + 1);
            case NEUTRAL  -> metrics.setNeutralCommentCount(metrics.getNeutralCommentCount() + 1);
        }

        metrics.setLastUpdatedAt(LocalDateTime.now());
        eventEngagementMetricsService.save(metrics);
        logger.info("Метрики івенту ID={} оновлено: {}", event.getId(), metrics);
    }
}