package io.github.bizcub.inventoryItemGroups.config;

import io.github.bizcub.simpleConfigLib.autoconfig.annotation.ListConfig;
import io.github.bizcub.simpleConfigLib.autoconfig.annotation.Tooltip;

import java.util.ArrayList;
import java.util.List;

public class ItemGroup {
    public ItemGroup() {}

    public ItemGroup(String groupName, String tabId, List<String> containedItems, List<String> nonContainedItems) {
        this.groupName = groupName;
        this.tabId = tabId;
        this.containedItems = new ArrayList<>(containedItems);
        this.nonContainedItems = new ArrayList<>(nonContainedItems);
    }

    public String groupName = "";

    public String tabId = "";

    @Tooltip
    @ListConfig(addToFront = true)
    public List<String> equivalentItems = new ArrayList<>();

    @Tooltip
    @ListConfig(addToFront = true)
    public List<String> containedItems = new ArrayList<>();

    @Tooltip
    @ListConfig(addToFront = true)
    public List<String> nonContainedItems = new ArrayList<>();
}
