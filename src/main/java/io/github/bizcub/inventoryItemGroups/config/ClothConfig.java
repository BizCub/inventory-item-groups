package io.github.bizcub.inventoryItemGroups.config;

import io.github.bizcub.inventoryItemGroups.Main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClothConfig implements Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", Main.MOD_ID + ".json");

    public boolean addGroupsOverOld;
    public boolean translateGroups;
    public boolean showGroupItems;
    public Sort sort = Sort.DEFAULT;
    public List<ItemGroup> groups = new ArrayList<>();
    public static ClothConfig config;

    public static Screen getConfigScreen(Screen parent) {
        load();
        Config.set(config);

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(getTranslate("title"))
                .setSavingRunnable(config::save);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory groups = builder.getOrCreateCategory(getTranslate("category.groups"));
        groups.addEntry(entryBuilder.startBooleanToggle(getTranslate("category.groups.addGroupsOverOld"), config.addGroupsOverOld)
                .setDefaultValue(true)
                .setTooltip(getTranslate("category.groups.addGroupsOverOld.tooltip"))
                .setSaveConsumer(value -> config.addGroupsOverOld = value)
                .build()
        );
        groups.addEntry(entryBuilder.startBooleanToggle(getTranslate("category.groups.translateGroups"), config.translateGroups)
                .setDefaultValue(false)
                .setTooltip(getTranslate("category.groups.translateGroups.tooltip"))
                .setSaveConsumer(value -> config.translateGroups = value)
                .build()
        );
        groups.addEntry(new NestedListListEntry<ItemGroup, MultiElementListEntry<ItemGroup>>(
                getTranslate("category.groups.group"),
                config.groups,
                false,
                Optional::empty,
                newList -> config.groups = newList,
                List::of,
                entryBuilder.getResetButtonKey(),
                true,
                true,
                (elem, nestedListListEntry) -> {
                    ItemGroup currentElem = elem != null ? elem : new ItemGroup();
                    if (currentElem.groupName == null) currentElem.groupName = "name";
                    return new MultiElementListEntry<>(
                            getTranslate("category.groups.group.name"),
                            currentElem,
                            List.of(
                                    entryBuilder.startStrField(getTranslate("category.groups.group.group_name"), currentElem.groupName)
                                            .setDefaultValue("")
                                            .setSaveConsumer(value -> currentElem.groupName = value)
                                            .build(),
                                    entryBuilder.startStrField(getTranslate("category.groups.group.tab_name"), currentElem.tabId)
                                            .setDefaultValue("")
                                            .setSaveConsumer(value -> currentElem.tabId = value)
                                            .build(),
                                    entryBuilder.startStrList(getTranslate("category.groups.group.equivalentItems"), currentElem.equivalentItems.stream().map(Object::toString).toList())
                                            .setDefaultValue(List.of())
                                            .setTooltip(getTranslate("category.groups.group.equivalentItems.tooltip"))
                                            .setSaveConsumer(objects -> {
                                                currentElem.equivalentItems.clear();
                                                currentElem.equivalentItems.addAll(objects);
                                            })
                                            .build(),
                                    entryBuilder.startStrList(getTranslate("category.groups.group.containedItems"), currentElem.containedItems.stream().map(Object::toString).toList())
                                            .setDefaultValue(List.of())
                                            .setTooltip(getTranslate("category.groups.group.containedItems.tooltip"))
                                            .setSaveConsumer(objects -> {
                                                currentElem.containedItems.clear();
                                                currentElem.containedItems.addAll(objects);
                                            })
                                            .build()
                            ),
                            true
                    );
                }
        ));
        SubCategoryBuilder ids = entryBuilder.startSubCategory(getTranslate("category.groups.ids")).setExpanded(false);
        Main.getTabIds().forEach(id -> ids.add(entryBuilder.startTextDescription(id).build()));
        groups.addEntry(ids.build());

        ConfigCategory main = builder.getOrCreateCategory(getTranslate("category.main"));
        main.addEntry(entryBuilder.startEnumSelector(getTranslate("category.main.sort"), Sort.class, config.sort)
                .setDefaultValue(Sort.DEFAULT)
                .setEnumNameProvider(e -> getTranslate(((Sort) e).getKey()))
                .setSaveConsumer(value -> config.sort = value)
                .build()
        );
        main.addEntry(entryBuilder.startBooleanToggle(getTranslate("category.main.show_group_items"), config.showGroupItems)
                .setDefaultValue(false)
                .setTooltip(getTranslate("category.main.show_group_items.tooltip"))
                .setSaveConsumer(value -> config.showGroupItems = value)
                .build()
        );

        return builder.build();
    }

    public static ClothConfig getConfig() {
        return config;
    }

    public static void load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
                config = GSON.fromJson(reader, ClothConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            config = new ClothConfig();
            config.addGroupsOverOld = true;
        }
    }

    public void save() {
        Config.set(config);
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Component getTranslate(String text) {
        return Component.translatable("text.inventory_item_groups." + text);
    }

    @Override
    public boolean addGroupsOverOld() {
        return this.addGroupsOverOld;
    }

    @Override
    public boolean translateGroups() {
        return this.translateGroups;
    }

    @Override
    public List<ItemGroup> groups() {
        return this.groups;
    }

    @Override
    public Sort sort() {
        return this.sort;
    }

    @Override
    public boolean showItemsInGroup() {
        return this.showGroupItems;
    }
}
