package io.github.bizcub.inventoryItemGroups.config;

import io.github.bizcub.inventoryItemGroups.Main;
import io.github.bizcub.simpleConfigLib.autoconfig.ConfigHolder;
import io.github.bizcub.simpleConfigLib.autoconfig.annotation.*;
import net.minecraft.network.chat.Component;

import java.util.List;

@AutoConfig(name = Main.MOD_ID, translate = true, snakeCaseKeys = true)
public class SimpleConfig implements Config {
    public static ConfigHolder<SimpleConfig> getInstance() {
        return ConfigHolder.register(SimpleConfig.class);
    }

    @EnumConfig(translate = true)
    public Sort sort = Config.super.sort();

    @Tooltip
    public boolean showItemsInGroup = Config.super.showItemsInGroup();

    @ListConfig(addToFront = true, translateElements = true)
    public List<ItemGroup> groups = Config.super.groups();

    @ListConfig(editable = false)
    public List<Component> idOfMenuTabs = Main.getTabIds();

    @Override
    public Sort sort() {
        return this.sort;
    }

    @Override
    public boolean showItemsInGroup() {
        return this.showItemsInGroup;
    }

    @Override
    public List<ItemGroup> groups() {
        return this.groups;
    }
}
