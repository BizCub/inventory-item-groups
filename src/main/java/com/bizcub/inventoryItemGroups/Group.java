package com.bizcub.inventoryItemGroups;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class Group {
    private final String tab;
    private final HashMap<ItemStack, Integer> icon = new HashMap<>();
    private final ArrayList<HashMap<ItemStack, Integer>> itemStacks = new ArrayList<>();
    private boolean visibility;

    public Group(String tab, ArrayList<ItemStack> itemStacks) {
        this.tab = tab;
        this.visibility = false;
        itemStacks = removeDuplicates(itemStacks);

        if (!itemStacks.isEmpty()) {
            this.icon.put(itemStacks.getFirst(), -1);

            for (ItemStack itemStack : itemStacks) {
                this.itemStacks.add(new HashMap<>());
                this.itemStacks.getLast().put(itemStack, -1);
            }
        }
    }

    public ArrayList<ItemStack> removeDuplicates(ArrayList<ItemStack> list) {
        ArrayList<Group> groupsOnSelectedTab = Main.groupsOnSelectedTab(Main.tabsMapping.get(tab));
        groupsOnSelectedTab.forEach(group -> list.removeAll(group.getItems()));
        return list;
    }

    public String getTab() {
        return tab;
    }

    public ArrayList<ItemStack> getItems() {
        ArrayList<ItemStack> list = new ArrayList<>();
        this.itemStacks.forEach(itemStacksMap -> list.add(itemStacksMap.keySet().iterator().next()));
        return list;
    }

    public ArrayList<HashMap<ItemStack, Integer>> getItemsWithIndexes() {
        return itemStacks;
    }

    public void setItemWithIndex(ItemStack item, int index) {
        this.itemStacks.forEach(itemStacksMap -> {
            if (itemStacksMap.containsKey(item)) {
                itemStacksMap.put(item, index);
            }
        });
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public ItemStack getIcon() {
        return icon.keySet().stream().toList().getFirst();
    }

    public int getIconIndex() {
        return icon.get(getIcon());
    }

    public void setIconIndex(int index) {
        this.icon.put(getIcon(), index);
    }
}
