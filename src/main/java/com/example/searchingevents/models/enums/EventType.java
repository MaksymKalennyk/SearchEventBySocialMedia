package com.example.searchingevents.models.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum EventType {
    CONCERT("Концерт", Arrays.asList("концерт", "концерті", "concert")),
    THEATRE("Театр", Arrays.asList("театр", "театру", "вистава", "виставі", "play", "theatre")),
    EXHIBITION("Виставка", Arrays.asList("виставка", "exhibition", "виставці"));

    private final String displayValue;
    private final List<String> keywords;

    EventType(String displayValue, List<String> keywords) {
        this.displayValue = displayValue;
        this.keywords = keywords;
    }

    public static EventType detect(String lowerText) {
        for (EventType type : values()) {
            for (String kw : type.getKeywords()) {
                if (lowerText.contains(kw)) {
                    return type;
                }
            }
        }
        return null;
    }
}
