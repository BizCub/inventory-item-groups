package com.bizcub.inventoryItemGroups;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static ArrayList<ArrayList<String>> itemGroups = new ArrayList<>(List.of(
            new ArrayList<>(List.of("minecraft:cherry_door", "minecraft:spruce_door", "minecraft:birch_door", "minecraft:oak_door")),
            new ArrayList<>(List.of("minecraft:cherry_trapdoor", "minecraft:spruce_trapdoor"))));
    public static HashMap<String, Boolean> groupVisibility = new HashMap<>();
    public static HashMap<String, Item> itemsMapping = new HashMap<>();

    public static boolean tempListChanged;
    public static List<ItemStack> tempInventoryItemStack = new ArrayList<>();
    public static List<String> tempInventoryItems = new ArrayList<>();
    public static String tempGroupName;
    public static int tempIndex;
    public static float tempScrollOffs;

    public static void createItemsMapping() {
        int i = 1;
        while (true) {
            Item item = Item.byId(i);
            itemsMapping.put(item.toString(), item);
            i++;
            if (item == Items.AIR) break;
        }
    }

    public static void visibilityGroup() {
        for (ArrayList<String> list : itemGroups) {
            groupVisibility.put(list.getFirst(), false);
        }
    }

    public static void listChanged(String groupName, int index, float scrollOffs) {
        tempListChanged = true;
        tempGroupName = groupName;
        tempIndex = index;
        tempScrollOffs = scrollOffs;
    }

    public static int slotIndexCalculation(int inventorySize, float scrollOffs, int index) {
        float rows = (float) inventorySize / 9;
        rows = (float) Math.ceil(rows);

        int itemsCount = 0;
        if (inventorySize > 45) {
            itemsCount = Math.round((rows - 5) * scrollOffs);
            if (itemsCount > 0) {
                itemsCount = itemsCount * 9;
            }
        }
        itemsCount += index;

        return itemsCount;
    }
}
