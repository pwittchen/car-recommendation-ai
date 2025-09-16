package com.github.pwittchen.backend.model;

public record CarPreferences(
        boolean isNew,
        int budget,
        String type,
        String fuel,
        String brand,
        String comment
) {
}
