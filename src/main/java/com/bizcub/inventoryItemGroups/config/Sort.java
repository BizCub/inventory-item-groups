package com.bizcub.inventoryItemGroups.config;

public enum Sort {
    DEFAULT("category.main.sort.default"),
    ALPHABETICALLY("category.main.sort.alphabetically");

    private final String translationKey;

    Sort(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getKey() {
        return this.translationKey;
    }
}
