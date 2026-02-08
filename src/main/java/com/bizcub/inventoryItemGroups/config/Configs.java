package com.bizcub.inventoryItemGroups.config;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import java.util.List;

public class Configs {

    public static ConfigBuilder getConfigBuilderWithDemo() {
        ConfigBuilder builder = ConfigBuilder.create().setTitle(getTranslate("title"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory main = builder.getOrCreateCategory(getTranslate("category.main"));

        main.addEntry(entryBuilder.startKeyCodeField(Component.literal("Cool Key"), InputConstants.UNKNOWN).setDefaultValue(InputConstants.UNKNOWN).build());

        ConfigCategory groups = builder.getOrCreateCategory(getTranslate("category.groups"));
        if (Minecraft.getInstance().player == null) {
            SubCategoryBuilder ids = entryBuilder.startSubCategory(getTranslate("category.groups.ids")).setExpanded(false);
            for (CreativeModeTab creativeModeTab : CreativeModeTabs.allTabs()) {
                String tabId = convertToId(creativeModeTab.getDisplayName().getContents().toString());
                if (!tabId.equals("hotbar") && !tabId.equals("search") && !tabId.equals("op") && !tabId.equals("inventory")) {
                    ids.add(entryBuilder.startTextDescription(
                            Component.translatable("text.inventory_item_groups.category.groups.ids.list",
                                    Component.literal(creativeModeTab.getDisplayName().getString()),
                                    Component.literal(tabId).withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.copy"))).withClickEvent(new ClickEvent.CopyToClipboard(tabId))).withStyle(style -> style.withColor(ChatFormatting.WHITE))
                            ).withStyle(style -> style.withColor(ChatFormatting.GRAY))
                    ).build());
                }
            }
            groups.addEntry(ids.build());
        } else {
            groups.addEntry(entryBuilder.startTextDescription(getTranslate("category.groups.not_in_world")).build());
        }

//        main.addEntry(new NestedListListEntry<SubCategoryBuilder, MultiElementListEntry<SubCategoryBuilder>>(
//                Component.literal("Nice"),
//        ));

        return builder;
    }

    public static class AutoMessages {
        static final String tabName = "";
        static final List<String> defaultMessages = List.of();
        static final int defaultDelay = 1000;
    }

    private static Component getTranslate(String text) {
        return Component.translatable("text.inventory_item_groups" + text);
    }

    private static String convertToId(String tabId) {
        int firstIndex = tabId.indexOf("'");
        if (tabId.startsWith("key=", firstIndex - 4)) {
            tabId = tabId.substring(firstIndex + 1);
            tabId = tabId.substring(0, tabId.indexOf("'"));
            tabId = tabId.substring(tabId.lastIndexOf(".") + 1);
        }
        return tabId;
    }
}
