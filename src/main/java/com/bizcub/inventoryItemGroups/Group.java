package com.bizcub.inventoryItemGroups;

import com.bizcub.inventoryItemGroups.config.Compat;
import com.bizcub.inventoryItemGroups.config.Configs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Group {
    private boolean visibility;
    private final CreativeModeTab tab;
    private final HashMap<ItemStack, Integer> icon = new HashMap<>();
    private final Component name;
    private final ArrayList<HashMap<ItemStack, Integer>> itemStacks = new ArrayList<>();

    public Group(Component name, CreativeModeTab tab, ArrayList<ItemStack> itemStacks) {
        this.name = name;
        this.tab = tab;
        this.visibility = false;
        itemStacks = removeDuplicates(itemStacks);
        itemStacks = sort(itemStacks);

        if (!itemStacks.isEmpty()) {
            this.icon.put(itemStacks.get(0), -1);

            for (ItemStack itemStack : itemStacks) {
                this.itemStacks.add(new HashMap<>());
                this.itemStacks.get(this.itemStacks.size()-1).put(itemStack, -1);
            }
        }
    }

    public ArrayList<ItemStack> sort(ArrayList<ItemStack> itemStacks) {
        LinkedHashMap<ItemStack, String> map = new LinkedHashMap<>();
        for (ItemStack itemStack : itemStacks) map.put(itemStack, itemStack.getItem().toString());

        if (Compat.isClothConfigLoaded() && Configs.load().sort == Configs.Sort.ALPHABETICALLY) {
            map = map.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }

        return new ArrayList<>(map.keySet());
    }

    public ArrayList<ItemStack> removeDuplicates(ArrayList<ItemStack> list) {
        ArrayList<Group> groupsOnSelectedTab = Main.groupsOnSelectedTab(tab);
        groupsOnSelectedTab.forEach(group -> list.removeAll(group.getItems()));
        return list;
    }

    public Component getName() {
        return name;
    }

    public CreativeModeTab getTab() {
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
        return icon.keySet().stream().toList().get(0);
    }

    public int getIconIndex() {
        return icon.get(getIcon());
    }

    public void setIconIndex(int index) {
        this.icon.put(getIcon(), index);
    }
}
