package com.example.searchingevents.bot;

import com.example.searchingevents.bot.dto.SentimentResult;

public interface CommentSentimentAnalyzer {
    SentimentResult analyze(String text);
}