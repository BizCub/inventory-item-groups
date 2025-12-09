package com.bizcub.inventoryItemGroups;

import com.bizcub.inventoryItemGroups.config.Compat;
import com.bizcub.inventoryItemGroups.config.ModConfig;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class Main {
    public static final String modId = /*$ mod_id {*/"inventory_item_groups"/*$}*/;

    public static HashMap<String, Item> itemsMapping = new HashMap<>();
    public static HashMap<CreativeModeTab, Collection<ItemStack>> itemsInTabsMapping = new HashMap<>();
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

    public static void createItemsInTabsMapping() {
        if (itemsInTabsMapping.isEmpty()) {
            CreativeModeTabs.allTabs().forEach(creativeModeTab -> {
                itemsInTabsMapping.put(creativeModeTab, creativeModeTab.getDisplayItems());
            });
        }
    }

    public static void updateGroups() {
        groups.clear();
        if (Compat.isModLoaded(Compat.clothConfigId) && !ModConfig.getInstance().general.itemGroups.isEmpty()) {
            ModConfig.getInstance().general.itemGroups.forEach(g -> {
//                if (!g.items.isEmpty())
//                    groups.add(new Group(g.tab, new ArrayList<>(g.items)));
            });
        }
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
        groups.clear();

        String buildingName = "Building Blocks";
        String coloredName = "Colored Blocks";

        ArrayList<ArrayList<String>> building = new ArrayList<>();
        ArrayList<ArrayList<String>> colored = new ArrayList<>();
        for (int i = 0; i < 19; i++) building.add(new ArrayList<>());
        for (int i = 0; i < 12; i++) colored.add(new ArrayList<>());

        itemsInTabsMapping.get(tabsMapping.get(buildingName)).forEach(itemStack -> {
            String item = itemStack.getItem().toString();
            if (item.contains("log") || item.contains("stem") || item.contains("bamboo_block")) {
                if (!item.contains("stripped")) building.getFirst().add(item);
                else building.get(1).add(item);
            }
            if (item.contains("wood") || item.contains("hyphae")) {
                if (!item.contains("stripped")) building.get(2).add(item);
                else building.get(3).add(item);
            }
            if (item.contains("stair")) building.get(4).add(item);
            if (item.contains("slab")) building.get(5).add(item);
            if (item.contains("planks") || (item.contains("mosaic"))) building.get(6).add(item);
            if (item.contains("fence_gate")) building.get(7).add(item);
            if (item.contains("fence")) building.get(8).add(item);
            if (item.contains("trapdoor")) building.get(9).add(item);
            if (item.contains("door")) building.get(10).add(item);
            if (item.contains("pressure_plate")) building.get(11).add(item);
            if (item.contains("button")) building.get(12).add(item);
            if (item.contains("bar")) building.get(13).add(item);
            if (item.contains("chain")) building.get(14).add(item);
            if (item.contains("copper")) building.get(15).add(item);
            if (item.contains("wall")) building.get(16).add(item);
            if (item.contains("bricks") || item.contains("chiseled") || item.contains("tiles") || item.contains("polished")) building.get(17).add(item);
            if (item.contains("sandstone")) building.get(18).add(item);
        });
        itemsInTabsMapping.get(tabsMapping.get(coloredName)).forEach(itemStack -> {
            String item = itemStack.getItem().toString();
            if (item.contains("wool")) colored.getFirst().add(item);
            if (item.contains("carpet")) colored.get(1).add(item);
            if (item.contains("glazed_terracotta")) colored.get(2).add(item);
            if (item.contains("terracotta")) colored.get(3).add(item);
            if (item.contains("concrete_powder")) colored.get(4).add(item);
            if (item.contains("concrete")) colored.get(5).add(item);
            if (item.contains("glass_pane")) colored.get(6).add(item);
            if (item.contains("glass")) colored.get(7).add(item);
            if (item.contains("shulker_box")) colored.get(8).add(item);
            if (item.contains("candle")) colored.get(9).add(item);
            if (item.contains("banner")) colored.get(10).add(item);
            if (item.contains("bed")) colored.get(11).add(item);
        });

        building.forEach(items -> groups.add(new Group(buildingName, items)));
        colored.forEach(items -> groups.add(new Group(coloredName, items)));
    }
}
