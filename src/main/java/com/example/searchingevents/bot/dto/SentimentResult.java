package com.example.searchingevents.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SentimentResult {
    private String originalText;
    private String translatedText;
    private Sentiment sentiment;

    public enum Sentiment {
        POSITIVE,
        NEGATIVE,
        NEUTRAL
    }
}
