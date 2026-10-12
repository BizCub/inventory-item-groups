package io.github.bizcub.inventoryItemGroups.forge;

import io.github.bizcub.inventoryItemGroups.Main;
import io.github.bizcub.inventoryItemGroups.config.ConfigHelper;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MOD_ID)
public class Forge {

    public Forge(FMLJavaModLoadingContext context) {
        FMLCommonSetupEvent.getBus(context.getModBusGroup()).addListener(Forge::onCommonSetup);

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> ConfigHelper.getScreen(screen)));
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(Main::init);
    }
}
