package com.example.searchingevents.bot;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommentProcessingService {
    void processComment(Message message);
}
