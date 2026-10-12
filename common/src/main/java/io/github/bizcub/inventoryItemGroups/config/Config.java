package io.github.bizcub.inventoryItemGroups.config;

import io.github.bizcub.inventoryItemGroups.Main;

import java.util.List;

public interface Config {
    static Config get() {
        return Holder.INSTANCE;
    }

    static void set(final Config config) {
        if (config != null) {
            Holder.INSTANCE = config;
        }
    }

    class Holder {
        private static Config INSTANCE = new Config() { };
    }

    default Sort sort() {
        return Sort.DEFAULT;
    }

    default boolean showItemsInGroup() {
        return false;
    }

    default List<ItemGroup> groups() {
        return Main.getDefaultGroups();
    }
}
