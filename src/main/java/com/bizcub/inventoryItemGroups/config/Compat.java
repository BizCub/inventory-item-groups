package com.bizcub.inventoryItemGroups.config;

/*? fabric*/ import net.fabricmc.loader.api.FabricLoader;
/*? forge*/ //import net.minecraftforge.fml.ModList;
/*? neoforge*/ //import net.neoforged.fml.ModList;

public class Compat {
    public static final String CLOTH_CONFIG_ID =
            /*? fabric*/ "cloth-config";
            /*? forge || neoforge*/ //"cloth_config";

    public static boolean isModLoaded(String modId) {
        /*? fabric*/ return FabricLoader.getInstance().isModLoaded(modId);
        /*? forge || neoforge*/ //return ModList.get().isLoaded(modId);
    }

    public static boolean isClothConfigLoaded() {
        return isModLoaded(CLOTH_CONFIG_ID);
    }
}
