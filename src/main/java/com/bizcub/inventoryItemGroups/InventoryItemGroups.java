package com.bizcub.inventoryItemGroups;

import com.bizcub.inventoryItemGroups.config.Compat;
import com.bizcub.inventoryItemGroups.config.ModConfig;

public class InventoryItemGroups {
    public static final String modId = /*$ mod_id {*/"inventory_item_groups"/*$}*/;
    /*? fabric*/ public static final String clothConfigId = "cloth-config";
    /*? forge || neoforge*/ /*public static final String clothConfigId = "cloth_config";*/

    public static void init() {
        if (Compat.isModLoaded(clothConfigId)) {
            ModConfig.init();
            Main.createMapping();
            Main.updateGroups();
        }
    }
}
