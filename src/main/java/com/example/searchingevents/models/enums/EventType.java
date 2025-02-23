package com.example.searchingevents.models.enums;

import java.util.Arrays;
import java.util.List;

public enum EventType {
    CONCERT(Arrays.asList("концерт", "концерті", "concert")),
    THEATRE(Arrays.asList("театр", "театру", "вистава", "виставі", "play", "theatre")),
    EXHIBITION(Arrays.asList("виставка", "exhibition", "виставці"));

    private final List<String> keywords;

    EventType(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public static EventType detect(String lowerText) {
        for (EventType type : values()) {
            for (String kw : type.keywords) {
                if (lowerText.contains(kw)) {
                    return type;
                }
            }
        }
        return null;
    }
}
