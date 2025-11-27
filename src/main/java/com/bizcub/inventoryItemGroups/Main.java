package com.bizcub.inventoryItemGroups;

import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static ArrayList<ArrayList<String>> itemGroups = new ArrayList<>(List.of(
            new ArrayList<>(List.of("minecraft:cherry_door", "minecraft:spruce_door", "minecraft:birch_door", "minecraft:oak_door")),
            new ArrayList<>(List.of("minecraft:cherry_trapdoor", "minecraft:spruce_trapdoor"))));
    public static HashMap<String, Item> itemsMapping = new HashMap<>();
    public static HashMap<String, CreativeModeTab> tabsMapping = new HashMap<>();
    public static ArrayList<Group> groups = new ArrayList<>();

    public static boolean tempListChanged;
    public static List<ItemStack> tempInventoryItemStack = new ArrayList<>();
    public static Group tempGroup;
    public static int tempIndex;
    public static float tempScrollOffs;

    public static void createMapping() {
        for (int i = 1; true; i++) {
            Item item = Item.byId(i);
            itemsMapping.put(item.toString(), item);
            if (item == Items.AIR) break;
        }

        for (CreativeModeTab tab : CreativeModeTabs.allTabs())
            tabsMapping.put(tab.getDisplayName().getString(), tab);

        groups.add(new Group("Building Blocks", itemGroups.getFirst()));
        groups.add(new Group("Building Blocks", itemGroups.get(1)));
    }

    public static void hideGroups() {
        for (Group group : groups) {
            group.setVisibility(false);
            for (String str : group.getItems())
                group.setItemWithIndex(str, -1);
        }
    }

    public static void itemsChanged(int index) {
        tempListChanged = true;
        tempIndex = index;
        for (Group group : groups)
            if (group.getIconIndex() == index)
                tempGroup = group;
    }

    public static void setIndexes() {
        ArrayList<String> newStack = new ArrayList<>();
        for (ItemStack itemStack : Main.tempInventoryItemStack)
            newStack.add(itemStack.getItem().toString());

        for (int i = 0; i < Main.tempInventoryItemStack.size()-1; i++) {
            String item = Main.tempInventoryItemStack.get(i).getItem().toString();

            for (Group group : Main.groups) {
                for (String ignore : group.getItems()) {
                    if (group.getItems().contains(item)) {
                        if (group.isVisibility()) {
                            if (newStack.indexOf(item) != newStack.lastIndexOf(item) && newStack.indexOf(item) == i)
                                group.setIconIndex(i);
                            group.setItemWithIndex(item, i);
                        } else {
                            group.setIconIndex(i);
                            group.setItemWithIndex(item, -1);
                        }
                    }
                }
            }
        }
    }
}
