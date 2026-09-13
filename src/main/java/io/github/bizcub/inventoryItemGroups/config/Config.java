package io.github.bizcub.inventoryItemGroups.config;

import io.github.bizcub.inventoryItemGroups.Main;
import io.github.bizcub.simpleConfigLib.autoconfig.ConfigProvider;

import java.util.List;

public interface Config {
    static Config get() {
        return ConfigProvider.get(Config.class);
    }
    static void set(Config instance) {
        ConfigProvider.set(Config.class, instance);
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
