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
    public static HashMap<String, Integer> iconIndexes = new HashMap<>();
    public static HashMap<String, Integer> itemIndexes = new HashMap<>();
    public static HashMap<String, Boolean> groupVisibility = new HashMap<>();
    public static HashMap<String, Item> itemsMapping = new HashMap<>();

    public static List<ItemStack> tempInventoryItemStack = new ArrayList<>();
    public static List<String> tempInventoryItems = new ArrayList<>();
    public static boolean tempListChanged;
    public static String tempGroupName;
    public static int tempIndex;
    public static float tempScrollOffs;

    public static void createMapping() {
        for (int i = 1; true; i++) {
            Item item = Item.byId(i);
            itemsMapping.put(item.toString(), item);
            if (item == Items.AIR) break;
        }
    }

    public static void hideGroups() {
        for (ArrayList<String> list : itemGroups)
            groupVisibility.put(list.getFirst(), false);
    }

    public static void itemsChanged(String groupName, int index, float scrollOffs) {
        tempListChanged = true;
        tempGroupName = groupName;
        tempIndex = index;
        tempScrollOffs = scrollOffs;
    }

    public static int indexCalculation(int inventorySize, float scrollOffs, int slotIndex) {
        float rows = (float) inventorySize / 9;
        rows = (float) Math.ceil(rows);

        int index = 0;
        if (inventorySize > 45) {
            index = Math.round((rows - 5) * scrollOffs);
            if (index > 0) {
                index = index * 9;
            }
        }
        index += slotIndex;

        return index;
    }
}
