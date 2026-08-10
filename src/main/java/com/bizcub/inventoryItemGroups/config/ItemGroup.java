package com.bizcub.inventoryItemGroups.config;

import io.github.bizcub.simpleConfigLib.autoconfig.annotation.ListConfig;
import io.github.bizcub.simpleConfigLib.autoconfig.annotation.Tooltip;

import java.util.ArrayList;
import java.util.List;

public class ItemGroup {
    public String groupName = "";

    public String tabName = "";

    @Tooltip
    @ListConfig(addToFront = true)
    public List<String> equivalentItems = new ArrayList<>();

    @Tooltip
    @ListConfig(addToFront = true)
    public List<String> containedItems = new ArrayList<>();
}
