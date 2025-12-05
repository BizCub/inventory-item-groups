package com.bizcub.inventoryItemGroups;

import com.bizcub.inventoryItemGroups.config.Compat;
import com.bizcub.inventoryItemGroups.config.ModConfig;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static final String modId = /*$ mod_id {*/"inventory_item_groups"/*$}*/;

    public static ArrayList<ArrayList<String>> itemGroups = new ArrayList<>(List.of(
            new ArrayList<>(List.of("minecraft:cherry_door", "minecraft:spruce_door", "minecraft:birch_door", "minecraft:oak_door")),
            new ArrayList<>(List.of("minecraft:cherry_trapdoor", "minecraft:spruce_trapdoor")),
            new ArrayList<>(List.of("minecraft:diamond_axe", "minecraft:iron_axe"))
    ));
    public static HashMap<String, Item> itemsMapping = new HashMap<>();
    public static HashMap<String, CreativeModeTab> tabsMapping = new HashMap<>();
    public static ArrayList<Group> groups = new ArrayList<>();

    public static boolean tempListChanged;
    public static List<ItemStack> tempInventoryItemStack = new ArrayList<>();
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

    public static List<Group> groupsOnSelectedTab(CreativeModeTab selectedTab) {
        List<Group> groupsOnSelectedTab = new ArrayList<>();
        for (Group group : groups)
            if (selectedTab == tabsMapping.get(group.getTab()))
                groupsOnSelectedTab.add(group);
        return groupsOnSelectedTab;
    }

    public static void setIndexes() {
        List<Group> groupsOnSelectedTab = groupsOnSelectedTab(tempSelectedTab);
        ArrayList<String> newStack = new ArrayList<>();
        for (ItemStack itemStack : tempInventoryItemStack)
            newStack.add(itemStack.getItem().toString());

        for (int i = 0; i < tempInventoryItemStack.size()-1; i++) {
            String item = tempInventoryItemStack.get(i).getItem().toString();

            for (Group group : groupsOnSelectedTab) {
                if (group.getItems().contains(item)) {
                    if (group.isVisibility()) {
                        if (newStack.indexOf(item) != newStack.lastIndexOf(item) && newStack.indexOf(item) == i)
                            group.setIconIndex(i);
                        else group.setItemWithIndex(item, i);
                    } else {
                        group.setIconIndex(i);
                        for (String items : group.getItems())
                            group.setItemWithIndex(items, -1);
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

        itemsMapping.forEach((string, item) -> {
            if (((string.contains("log") || string.contains("stem") || string.contains("bamboo_block")) && !string.contains("stripped")))
                log.add(string);
            if (((string.contains("log") || string.contains("stem") || string.contains("bamboo_block")) && string.contains("stripped")))
                stripped_log.add(string);
            if ((string.contains("_wood") || string.contains("hyphae")) && !string.contains("stripped"))
                wood.add(string);
            if ((string.contains("wood") || string.contains("hyphae")) && string.contains("stripped"))
                stripped_wood.add(string);
        });

        groups.add(new Group("Building Blocks", sortList(log)));
        groups.add(new Group("Building Blocks", sortList(stripped_log)));
        groups.add(new Group("Building Blocks", sortList(wood)));
        groups.add(new Group("Building Blocks", sortList(stripped_wood)));
    }
}
