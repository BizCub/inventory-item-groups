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
        if (!groups.isEmpty()) return;

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
        ArrayList<String> pressure_plate = new ArrayList<>();
        ArrayList<String> button = new ArrayList<>();
        ArrayList<String> brick = new ArrayList<>();
        ArrayList<String> wall = new ArrayList<>();
        ArrayList<String> bar = new ArrayList<>();
        ArrayList<String> copper = new ArrayList<>();
        ArrayList<String> chain = new ArrayList<>();
        ArrayList<String> sandstone = new ArrayList<>();

        itemsInTabsMapping.get(tabsMapping.get("Building Blocks")).forEach(itemStack -> {
            String string = itemStack.getItem().toString();

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
            if (string.contains("planks") || (string.contains("mosaic") && !string.contains("mosaic_")))
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
            if (string.contains("pressure_plate"))
                pressure_plate.add(string);
            if (string.contains("button"))
                button.add(string);
            if ((string.contains("bricks") || string.contains("chiseled") || string.contains("tiles") || string.contains("polished")))
                brick.add(string);
            if (string.contains("wall"))
                wall.add(string);
            if (string.contains("bar"))
                bar.add(string);
            if (string.contains("copper"))
                copper.add(string);
            if (string.contains("chain"))
                chain.add(string);
            if (string.contains("sandstone"))
                sandstone.add(string);
        });

        groups.add(new Group("Building Blocks", log));
        groups.add(new Group("Building Blocks", stripped_log));
        groups.add(new Group("Building Blocks", wood));
        groups.add(new Group("Building Blocks", stripped_wood));
        groups.add(new Group("Building Blocks", plank));
        groups.add(new Group("Building Blocks", stair));
        groups.add(new Group("Building Blocks", slab));
        groups.add(new Group("Building Blocks", fence));
        groups.add(new Group("Building Blocks", fence_gate));
        groups.add(new Group("Building Blocks", door));
        groups.add(new Group("Building Blocks", trapdoor));
        groups.add(new Group("Building Blocks", pressure_plate));
        groups.add(new Group("Building Blocks", button));
        groups.add(new Group("Building Blocks", wall));
        groups.add(new Group("Building Blocks", bar));
        groups.add(new Group("Building Blocks", chain));
        groups.add(new Group("Building Blocks", copper));
        groups.add(new Group("Building Blocks", brick));
        groups.add(new Group("Building Blocks", sandstone));
    }
}
