//? forge {
/*package com.bizcub.inventoryItemGroups.platform;

import com.bizcub.inventoryItemGroups.Main;
import com.bizcub.inventoryItemGroups.config.Compat;
import com.bizcub.inventoryItemGroups.config.Configs;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(Main.MOD_ID)
public class Forge {

    public Forge() {
        if (Compat.isClothConfigLoaded()) Configs.load();

        //? is_cloth_config_available {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> Configs.getConfigScreen(screen)));//?}
    }
}*///?}
