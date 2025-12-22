package com.bizcub.inventoryItemGroups.platforms;

import com.bizcub.inventoryItemGroups.Main;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;

public class Fabric implements ClientModInitializer, ModMenuApi {

    @Override
    public void onInitializeClient() {
        Main.init();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return PlatformInit::getScreen;
    }
}
