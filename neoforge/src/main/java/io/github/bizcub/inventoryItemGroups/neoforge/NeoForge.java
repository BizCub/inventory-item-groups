package io.github.bizcub.inventoryItemGroups.neoforge;

import io.github.bizcub.inventoryItemGroups.Main;
import io.github.bizcub.inventoryItemGroups.config.ConfigHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Main.MOD_ID)
public class NeoForge {

    public NeoForge(IEventBus modBus) {
        modBus.addListener(NeoForge::onCommonSetup);

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () ->
                (container, parent) -> ConfigHelper.getScreen(parent));
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(Main::init);
    }
}
