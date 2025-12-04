package com.bizcub.inventoryItemGroups.platforms;

import com.bizcub.inventoryItemGroups.InventoryItemGroups;
import net.fabricmc.api.ModInitializer;

public class Fabric implements ModInitializer {

    @Override
    public void onInitialize() {
        InventoryItemGroups.init();
    }
}
