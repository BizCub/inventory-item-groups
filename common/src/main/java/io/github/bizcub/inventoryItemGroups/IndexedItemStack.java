package io.github.bizcub.inventoryItemGroups;

import net.minecraft.world.item.ItemStack;

public class IndexedItemStack {
    private final ItemStack itemStack;
    private int index;

    public IndexedItemStack(ItemStack itemStack, int index) {
        this.itemStack = itemStack;
        this.index = index;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
