package io.github.bizcub.inventoryItemGroups.fabric;

import io.github.bizcub.inventoryItemGroups.Main;
import io.github.bizcub.inventoryItemGroups.config.ConfigHelper;
import net.fabricmc.loader.api.FabricLoader;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;

public class Fabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Main.init(FabricLoader.getInstance().isModLoaded(ConfigHelper.SIMPLE_CONFIG_MOD_ID));
    }

    public static class ModMenu implements ModMenuApi {

        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return ConfigHelper::getScreen;
        }
    }
}
