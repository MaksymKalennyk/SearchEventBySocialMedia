package com.example.searchingevents.models.enums;

import java.util.Arrays;
import java.util.List;

public enum City {
    KYIV("Київ", Arrays.asList("київ", "києві", "києва", "києвом", "kiev", "kyiv")),
    LVIV("Львів", Arrays.asList("львів", "львова", "львові", "львовом")),
    ODESA("Одеса", Arrays.asList("одеса", "одесі", "одесою", "odesa")),
    KAMIANETS("Камʼянець-Подільський", Arrays.asList("кам'янець-подільський", "кам’янець", "kamianets", "кам’янець-подільський")),;

    private final String value;
    private final List<String> keywords;

    City(String value, List<String> keywords) {
        this.value = value;
        this.keywords = keywords;
    }

    public String getValue() {
        return value;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public static City detect(String text) {
        String lower = text.toLowerCase();
        for (City c : values()) {
            for (String kw : c.keywords) {
                if (lower.contains(kw)) {
                    return c;
                }
            }
        }
        return null;
    }
}
