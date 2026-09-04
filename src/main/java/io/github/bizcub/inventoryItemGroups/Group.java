package io.github.bizcub.inventoryItemGroups;

import io.github.bizcub.inventoryItemGroups.config.Config;
import io.github.bizcub.inventoryItemGroups.config.Sort;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
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
        if (Config.get().sort() == Sort.ALPHABETICALLY) {
            itemStacks.sort(Comparator.comparing(stack -> stack.getItem().toString()));
        }
        return itemStacks;
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
        return this.itemStacks.stream()
                .map(IndexedItemStack::getItemStack)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<IndexedItemStack> getItemsWithIndexes() {
        return this.itemStacks;
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
