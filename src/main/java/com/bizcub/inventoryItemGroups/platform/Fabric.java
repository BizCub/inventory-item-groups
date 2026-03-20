//? fabric {
package com.bizcub.inventoryItemGroups.platform;

import com.bizcub.inventoryItemGroups.config.Compat;
import com.bizcub.inventoryItemGroups.config.Configs;
import net.fabricmc.api.ModInitializer;

public class Fabric implements ModInitializer {

    @Override
    public void onInitialize() {
        if (Compat.isClothConfigLoaded()) Configs.load();
    }

//    public static class ModMenu implements ModMenuApi {
//
//        @Override
//        public ConfigScreenFactory<?> getModConfigScreenFactory() {
//            return Configs::getConfigScreen;
//        }
//    }
}//?}
