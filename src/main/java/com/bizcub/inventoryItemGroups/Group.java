package com.bizcub.inventoryItemGroups;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class Group {
    private final String tab;
    private final HashMap<ItemStack, Integer> icon = new HashMap<>();
    private final HashMap<ItemStack, Integer> itemStacks = new HashMap<>();
    private boolean visibility;

    public Group(String tab, ArrayList<ItemStack> itemStacks) {
        this.tab = tab;
        this.visibility = false;
        itemStacks = removeDuplicates(itemStacks);

        if (!itemStacks.isEmpty()) {
            this.icon.put(itemStacks.get(0), -1);

            for (ItemStack itemStack : itemStacks)
                this.itemStacks.put(itemStack, -1);
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
        return new ArrayList<>(itemStacks.keySet());
    }

    public HashMap<ItemStack, Integer> getItemsWithIndexes() {
        return itemStacks;
    }

    public void setItemWithIndex(ItemStack item, int index) {
        this.itemStacks.put(item, index);
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public ItemStack getIcon() {
        return icon.keySet().stream().toList().get(0);
    }

    public int getIconIndex() {
        return icon.get(getIcon());
    }

    public void setIconIndex(int index) {
        this.icon.put(getIcon(), index);
    }
}
