package io.github.bizcub.inventoryItemGroups.neoforge;

import io.github.bizcub.inventoryItemGroups.Main;
import io.github.bizcub.inventoryItemGroups.config.ConfigHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Main.MOD_ID)
public class NeoForge {

    public NeoForge() {
        Main.init(ModList.get().isLoaded(ConfigHelper.SIMPLE_CONFIG_MOD_ID));

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () ->
                (container, parent) -> ConfigHelper.getScreen(parent));
    }
}
