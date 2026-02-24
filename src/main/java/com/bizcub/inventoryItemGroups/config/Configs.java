package com.bizcub.inventoryItemGroups.config;

import com.bizcub.inventoryItemGroups.Main;
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

public class Configs {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", Main.MOD_ID + ".json");

    public boolean addGroupsOverOld;
    public boolean translateGroups;
    public Sort sort = Sort.ALPHABETICALLY;
    public List<ItemGroup> groups = new ArrayList<>();

    public static Screen getConfigScreen(Screen parent) {
        Configs config = load();

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
                .setDefaultValue(true)
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
                    Configs.ItemGroup currentElem = elem != null ? elem : new Configs.ItemGroup("", "", new ArrayList<>(), new ArrayList<>());
                    if (currentElem.groupName == null) currentElem.groupName = "name";
                    return new MultiElementListEntry<>(
                            getTranslate("category.groups.group.name"),
                            currentElem,
                            List.of(
                                    //entryBuilder.startDropdownMenu(
                                    //                Component.literal("Name"),
                                    //                DropdownMenuBuilder.TopCellElementBuilder.of(
                                    //                        currentElem.name,
                                    //                        str -> str,
                                    //                        s -> Component.literal(s)
                                    //                ),
                                    //                DropdownMenuBuilder.CellCreatorBuilder.of(s -> Component.literal(s))
                                    //        ).setSelections(NAME_OPTIONS)
                                    //        .setDefaultValue("Default")
                                    //        .setSaveConsumer(value -> currentElem.name = value)
                                    //        .setSuggestionMode(false)
                                    //        .build(),
                                    entryBuilder.startStrField(getTranslate("category.groups.group.group_name"), currentElem.groupName)
                                            .setDefaultValue("")
                                            .setSaveConsumer(value -> currentElem.groupName = value)
                                            .build(),
                                    entryBuilder.startStrField(getTranslate("category.groups.group.tab_name"), currentElem.tabName)
                                            .setDefaultValue("")
                                            .setSaveConsumer(value -> currentElem.tabName = value)
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
        for (CreativeModeTab creativeModeTab : CreativeModeTabs.allTabs()) {
            String tabId = Main.convertComponentToId(creativeModeTab.getDisplayName().getContents().toString());
            if (!tabId.equals("hotbar") && !tabId.equals("search") && !tabId.equals("op") && !tabId.equals("inventory")) {
                ids.add(entryBuilder.startTextDescription(
                        getTranslate("category.groups.ids.entry",
                                Component.literal(creativeModeTab.getDisplayName().getString()),
                                Component.literal(tabId).withStyle(style -> style.withHoverEvent(getHoverEvent(Component.translatable("chat.copy"))).withClickEvent(getClickEvent(tabId))).withStyle(style -> style.withColor(ChatFormatting.WHITE))
                        ).withStyle(style -> style.withColor(ChatFormatting.GRAY))
                ).build());
            }
        }
        groups.addEntry(ids.build());

        ConfigCategory main = builder.getOrCreateCategory(getTranslate("category.main"));
        main.addEntry(entryBuilder.startEnumSelector(getTranslate("category.main.sort"), Sort.class, config.sort)
                .setDefaultValue(Sort.DEFAULT)
                .setEnumNameProvider(e -> getTranslate(((Configs.Sort) e).getKey()))
                .setSaveConsumer(value -> config.sort = value)
                .build()
        );

        return builder.build();
    }

    public static class ItemGroup {
        public String groupName;
        public String tabName;
        public List<Object> equivalentItems;
        public List<Object> containedItems;

        public ItemGroup(String groupName, String tabName, List<Object> containedItems, List<Object> equivalentItems) {
            this.groupName = groupName;
            this.tabName = tabName;
            this.equivalentItems = equivalentItems;
            this.containedItems = containedItems;
        }
    }

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

    public static Configs load() {
        if (Files.exists(CONFIG_PATH)) {
            try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
                return GSON.fromJson(reader, Configs.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Configs();
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HoverEvent getHoverEvent(Component component) {
        /*? >=1.21.2*/ return new HoverEvent.ShowText(
        /*? <=1.21.1*/ //return new HoverEvent(HoverEvent.Action.SHOW_TEXT,
            component
        );
    }

    private static ClickEvent getClickEvent(String tabId) {
        /*? >=1.21.2*/ return new ClickEvent.CopyToClipboard(
        /*? <=1.21.1*/ //return new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                tabId
        );
    }

    private static Component getTranslate(String text) {
        return Component.translatable(getTranslateKey(text));
    }

    private static MutableComponent getTranslate(String text, Object... objects) {
        return Component.translatable(getTranslateKey(text), objects);
    }

    private static String getTranslateKey(String text) {
        return "text.inventory_item_groups." + text;
    }
}
