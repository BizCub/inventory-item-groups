package com.bizcub.inventoryItemGroups.config;

import com.bizcub.inventoryItemGroups.Main;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer.GlobalData;
import net.minecraft.world.InteractionResult;

import java.util.ArrayList;
import java.util.List;

@Config(name = Main.modId)
public class ModConfig extends GlobalData {
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public General general = new General();

    public static void init() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        configSaver();
    }

    public static void configSaver() {
        AutoConfig.getConfigHolder(ModConfig.class).registerSaveListener((manager, data) -> {
            Main.updateGroups();
            return InteractionResult.SUCCESS;
        });
    }

    public static ModConfig getInstance() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Config(name = "general")
    public static class General implements ConfigData {
        public List<ItemGroups> itemGroups = new ArrayList<>();

        public General() {
        }
    }

    public static class ItemGroups {
        public String tab;
        public List<String> items = new ArrayList<>();
    }
}
