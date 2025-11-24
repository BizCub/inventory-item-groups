package com.bizcub.inventoryItemGroups;

import java.util.ArrayList;
import java.util.HashMap;

public class Group {
    private String tab;
    private HashMap<String, Integer> items = new HashMap<>();
    private boolean visibility;

    public Group(String tab, ArrayList<String> items) {
        this.tab = tab;
        this.visibility = false;

        for (String str : items) {
            this.items.put(str, 0);
        }
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public ArrayList<String> getItems() {
        return new ArrayList<>(items.keySet());
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
}
