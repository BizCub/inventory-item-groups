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

    static boolean isLoaded() {
        return Holder.INSTANCE != Holder.DEFAULT;
    }

    class Holder {
        private static final Config DEFAULT = new Config() { };
        private static Config INSTANCE = DEFAULT;
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
