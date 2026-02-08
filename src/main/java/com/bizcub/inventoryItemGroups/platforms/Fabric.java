//? fabric {
package com.bizcub.inventoryItemGroups.platforms;

import com.bizcub.inventoryItemGroups.Main;
import com.bizcub.inventoryItemGroups.config.Configs;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ModInitializer;

public class Fabric implements ModInitializer {

    @Override
    public void onInitialize() {

    }

    public static class ModMenu implements ModMenuApi {

        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return screen -> Configs.getConfigBuilderWithDemo().setParentScreen(screen).build();
        }
    }
}//?}
