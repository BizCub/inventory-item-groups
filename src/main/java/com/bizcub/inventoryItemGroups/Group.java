package com.bizcub.inventoryItemGroups;

import java.util.ArrayList;
import java.util.HashMap;

public class Group {
    private final String tab;
    private final HashMap<String, Integer> icon = new HashMap<>();
    private final HashMap<String, Integer> items = new HashMap<>();
    private boolean visibility;

    public Group(String tab, ArrayList<String> items) {
        items = removeDuplicates(items);

        this.tab = tab;
        this.visibility = false;
        this.icon.put(items.getFirst(), -1);

        for (String str : items)
            this.items.put(str, -1);
    }

    public ArrayList<String> removeDuplicates(ArrayList<String> list) {
        Main.groups.forEach(group -> list.removeAll(group.getItems()));
        return list;
    }

    public String getTab() {
        return tab;
    }

    public ArrayList<String> getItems() {
        return Main.sortList(new ArrayList<>(items.keySet()));
    }

    public HashMap<String, Integer> getItemsWithIndexes() {
        return items;
    }

    public void setItemWithIndex(String item, int index) {
        this.items.put(item, index);
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getIcon() {
        return icon.keySet().stream().toList().getFirst();
    }

    public int getIconIndex() {
        return icon.get(getIcon());
    }

    public void setIconIndex(int index) {
        this.icon.put(getIcon(), index);
    }
}
