package com.bizcub.inventoryItemGroups.platforms;

import com.bizcub.inventoryItemGroups.Main;
import net.fabricmc.api.ModInitializer;

public class Fabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Main.createItemsMapping();
    }
}
