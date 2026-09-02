package com.cimb.callmonitoring.model;

public enum SentimentFilter {
    BELOW_70,
    AT_OR_ABOVE_70;

    public static SentimentFilter from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return switch (value.toLowerCase()) {
            case "below70" -> BELOW_70;
            case "above70" -> AT_OR_ABOVE_70;
            default -> null;
        };
    }
}