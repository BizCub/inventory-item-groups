package io.github.bizcub.inventoryItemGroups;

import io.github.bizcub.inventoryItemGroups.config.Config;
import io.github.bizcub.inventoryItemGroups.config.ConfigHelper;
import io.github.bizcub.inventoryItemGroups.config.Sort;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Group {
    private boolean visibility;
    private final CreativeModeTab tab;
    private IndexedItemStack icon;
    private final Component name;
    private final ArrayList<IndexedItemStack> itemStacks = new ArrayList<>();

    public Group(Component name, CreativeModeTab tab, ArrayList<ItemStack> itemStacks) {
        this.name = name;
        this.tab = tab;
        this.visibility = false;
        itemStacks = removeDuplicates(itemStacks);
        itemStacks = sort(itemStacks);

        if (!itemStacks.isEmpty()) {
            this.icon = new IndexedItemStack(itemStacks.get(0), -1);

            for (ItemStack itemStack : itemStacks) {
                this.itemStacks.add(new IndexedItemStack(itemStack, -1));
            }
        }
    }

    public ArrayList<ItemStack> sort(ArrayList<ItemStack> itemStacks) {
        LinkedHashMap<ItemStack, String> map = new LinkedHashMap<>();
        for (ItemStack itemStack : itemStacks) map.put(itemStack, itemStack.getItem().toString());

        if (ConfigHelper.isConfigLoaded() && Config.get().sort() == Sort.ALPHABETICALLY) {
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
        this.itemStacks.forEach(entry -> list.add(entry.getItemStack()));
        return list;
    }

    public ArrayList<IndexedItemStack> getItemsWithIndexes() {
        return itemStacks;
    }

    public void setItemWithIndex(ItemStack item, int index) {
        this.itemStacks.forEach(entry -> {
            if (entry.getItemStack().equals(item)) {
                entry.setIndex(index);
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
        return icon.getItemStack();
    }

    public int getIconIndex() {
        return icon.getIndex();
    }

    public void setIconIndex(int index) {
        this.icon.setIndex(index);
    }
}
