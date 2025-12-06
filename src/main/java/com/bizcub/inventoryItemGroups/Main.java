package com.bizcub.inventoryItemGroups;

import com.bizcub.inventoryItemGroups.config.Compat;
import com.bizcub.inventoryItemGroups.config.ModConfig;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Arrays;

public class Main {
    public static final String modId = /*$ mod_id {*/"inventory_item_groups"/*$}*/;

    public static ArrayList<ArrayList<String>> itemGroups = new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList("minecraft:cherry_door", "minecraft:spruce_door", "minecraft:birch_door", "minecraft:oak_door")),
            new ArrayList<>(Arrays.asList("minecraft:cherry_trapdoor", "minecraft:spruce_trapdoor")),
            new ArrayList<>(Arrays.asList("minecraft:diamond_axe", "minecraft:iron_axe"))
    ));
    public static HashMap<String, Item> itemsMapping = new HashMap<>();
    public static HashMap<String, CreativeModeTab> tabsMapping = new HashMap<>();
    public static ArrayList<Group> groups = new ArrayList<>();

    public static boolean tempListChanged;
    public static ArrayList<ItemStack> tempInventoryItemStack = new ArrayList<>();
    public static CreativeModeTab tempSelectedTab;
    public static Group tempGroup;
    public static int tempIndex;
    public static float tempScrollOffs;

    public static void init() {
        if (Compat.isModLoaded(Compat.clothConfigId)) {
            ModConfig.init();
            createMapping();
        }
        updateGroups();
    }

    public static void createMapping() {
        for (int i = 1; true; i++) {
            Item item = Item.byId(i);
            itemsMapping.put(item.toString(), item);
            if (item == Items.AIR) break;
        }

        CreativeModeTabs.allTabs().forEach(tab ->
                tabsMapping.put(tab.getDisplayName().getString(), tab));
    }

    public static void updateGroups() {
        groups.clear();
        if (Compat.isModLoaded(Compat.clothConfigId) && !ModConfig.getInstance().general.itemGroups.isEmpty()) {
            ModConfig.getInstance().general.itemGroups.forEach(g -> {
                if (!g.items.isEmpty())
                    groups.add(new Group(g.tab, new ArrayList<>(g.items)));
            });
        } else
            createDefaultGroups();
    }

    public static void hideGroups() {
        groups.forEach(group -> {
            group.setVisibility(false);
            group.getItems().forEach(item ->
                group.setItemWithIndex(item, -1));
        });
    }

    public static void itemsChanged(int index) {
        tempListChanged = true;
        tempIndex = index;
        groups.forEach(group -> {
            if (group.getIconIndex() == index)
                tempGroup = group;
        });
    }

    public static Group findGroupByIndex(int index) {
        for (Group group : groups)
            if (group.getItemsWithIndexes().containsValue(index) || group.getIconIndex() == index)
                return group;
        return null;
    }

    public static ArrayList<Group> groupsOnSelectedTab(CreativeModeTab selectedTab) {
        ArrayList<Group> groupsOnSelectedTab = new ArrayList<>();
        groups.forEach(group -> {
            if (selectedTab == tabsMapping.get(group.getTab()))
                groupsOnSelectedTab.add(group);
        });
        return groupsOnSelectedTab;
    }

    public static void setIndexes() {
        ArrayList<Group> groupsOnSelectedTab = groupsOnSelectedTab(tempSelectedTab);
        ArrayList<String> newStack = new ArrayList<>();

        tempInventoryItemStack.forEach(itemStack ->
            newStack.add(itemStack.getItem().toString()));

        for (Group group : groupsOnSelectedTab) {
            boolean setIcon = false;
            for (String item : group.getItems()) {
                int firstIndex = newStack.indexOf(item);
                int lastIndex = newStack.lastIndexOf(item);
                if (newStack.contains(item)) {
                    if (group.isVisibility()) {
                        if (firstIndex != lastIndex && !setIcon) {
                            group.setIconIndex(firstIndex);
                            group.setItemWithIndex(item, lastIndex);
                            setIcon = true;
                        } else
                            group.setItemWithIndex(item, firstIndex);
                    } else {
                        group.setIconIndex(firstIndex);
                        group.getItems().forEach(item1 ->
                            group.setItemWithIndex(item1, -1));
                    }
                }
            }
        }
    }

    public static ArrayList<String> sortList(ArrayList<String> list) {
        Collections.sort(list);
        return list;
    }

    public static void createDefaultGroups() {
        ArrayList<String> log = new ArrayList<>();
        ArrayList<String> stripped_log = new ArrayList<>();
        ArrayList<String> wood = new ArrayList<>();
        ArrayList<String> stripped_wood = new ArrayList<>();
        ArrayList<String> plank = new ArrayList<>();
        ArrayList<String> stair = new ArrayList<>();
        ArrayList<String> slab = new ArrayList<>();
        ArrayList<String> fence = new ArrayList<>();
        ArrayList<String> fence_gate = new ArrayList<>();
        ArrayList<String> door = new ArrayList<>();
        ArrayList<String> trapdoor = new ArrayList<>();

        itemsMapping.forEach((string, item) -> {
            if ((string.contains("log") || string.contains("stem") || string.contains("bamboo_block"))) {
                if (!string.contains("stripped"))
                    log.add(string);
                else
                    stripped_log.add(string);
            }
            if (string.contains("_wood") || string.contains("hyphae")) {
                if (!string.contains("stripped"))
                    wood.add(string);
                else
                    stripped_wood.add(string);
            }
            if (string.contains("planks"))
                plank.add(string);
            if (string.contains("stair"))
                stair.add(string);
            if (string.contains("slab"))
                slab.add(string);
            if (string.contains("fence"))
                if (!string.contains("gate"))
                    fence.add(string);
                else
                    fence_gate.add(string);
            if (string.contains("door"))
                if (!string.contains("trapdoor"))
                    door.add(string);
                else
                    trapdoor.add(string);
        });

        groups.add(new Group("Building Blocks", sortList(log)));
        groups.add(new Group("Building Blocks", sortList(stripped_log)));
        groups.add(new Group("Building Blocks", sortList(wood)));
        groups.add(new Group("Building Blocks", sortList(stripped_wood)));
        groups.add(new Group("Building Blocks", sortList(plank)));
        groups.add(new Group("Building Blocks", sortList(stair)));
        groups.add(new Group("Building Blocks", sortList(slab)));
        groups.add(new Group("Building Blocks", sortList(fence)));
        groups.add(new Group("Building Blocks", sortList(fence_gate)));
        groups.add(new Group("Building Blocks", sortList(door)));
        groups.add(new Group("Building Blocks", sortList(trapdoor)));
    }
}
