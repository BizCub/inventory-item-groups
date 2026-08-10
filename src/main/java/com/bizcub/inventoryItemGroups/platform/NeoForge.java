//? neoforge {
/*package com.bizcub.inventoryItemGroups.platform;

import com.bizcub.inventoryItemGroups.Main;
import com.bizcub.inventoryItemGroups.config.ConfigHelper;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Main.MOD_ID)
public class NeoForge {

    public NeoForge() {
        Main.init();

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () ->
            (container, parent) -> ConfigHelper.getScreen(parent));
    }
}*///?}
