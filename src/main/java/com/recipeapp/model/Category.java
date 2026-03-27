package com.recipeapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {

    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    DESSERT("Dessert"),
    SNACK("Snack"),
    SOUP("Soup"),
    SALAD("Salad"),
    PASTA("Pasta"),
    BAKING("Baking"),
    OTHER("Other");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Category fromLabel(String value) {
        for (Category c : values()) {
            if (c.label.equalsIgnoreCase(value) || c.name().equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + value);
    }
}
