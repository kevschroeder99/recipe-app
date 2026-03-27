package com.recipeapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Difficulty {

    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    private final String label;

    Difficulty(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Difficulty fromLabel(String value) {
        if (value == null) return null;
        for (Difficulty d : values()) {
            if (d.label.equalsIgnoreCase(value) || d.name().equalsIgnoreCase(value)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Unknown difficulty: " + value);
    }
}
