package com.bizcub.inventoryItemGroups.config;

/*? fabric*/ import net.fabricmc.loader.api.FabricLoader;
/*? forge*/ /*import net.minecraftforge.fml.ModList;*/
/*? neoforge*/ /*import net.neoforged.fml.ModList;*/

public class Compat {
    public static boolean isModLoaded(String modId) {
        /*? fabric*/ return FabricLoader.getInstance().isModLoaded(modId);
        /*? forge || neoforge*/ /*return ModList.get().isLoaded(modId);*/
    }
}
