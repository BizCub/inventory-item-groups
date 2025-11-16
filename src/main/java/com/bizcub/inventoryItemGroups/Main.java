package com.bizcub.inventoryItemGroups;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<String> doorsGroup = new ArrayList<>(List.of("cherry_door", "spruce_door"));

    public static boolean tempListChanged;
    public static String tempGroupName;
    public static int tempIndex;
    public static float tempScrollOffs;

    public static boolean checkItemNames(List<String> items, String item) {
        return items.contains(item) || items.contains(item.replace("minecraft:", ""));
    }

    public static boolean checkItemNames(String item1, String item2) {
        return item1.equals(item2) || item1.equals("minecraft:" + item2);
    }

    public static void listChanged(String groupName, int index, float scrollOffs) {
        tempListChanged = true;
        tempGroupName = groupName;
        tempIndex = index;
        tempScrollOffs = scrollOffs;
    }
}
