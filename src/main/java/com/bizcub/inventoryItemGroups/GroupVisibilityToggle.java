package com.bizcub.inventoryItemGroups;

public class GroupVisibilityToggle {
    private boolean isListChanged;
    private Group group;
    private int itemIndex;
    private float scrollOffs;

    public void toggle(int itemIndex, float scrollOffs) {
        this.isListChanged = true;
        this.group = Main.findGroupByIndex(itemIndex);
        this.itemIndex = itemIndex;
        this.scrollOffs = scrollOffs;
    }

    public void off() {
        this.isListChanged = false;
    }

    public boolean isListChanged() {
        return isListChanged;
    }

    public Group getGroup() {
        return group;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public float getScrollOffs() {
        return scrollOffs;
    }
}
