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
public class Configs extends GlobalData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public General general = new General();

    public static void init() {
        AutoConfig.register(Configs.class, GsonConfigSerializer::new);
        configSaver();
    }

    public static void configSaver() {
        AutoConfig.getConfigHolder(Configs.class).registerSaveListener((manager, data) -> {
            Main.updateGroups();
            return InteractionResult.SUCCESS;
        });
    }

    public static Configs getInstance() {
        return AutoConfig.getConfigHolder(Configs.class).getConfig();
    }

    @Config(name = "general")
    public static class General implements ConfigData {
        public List<ItemGroup> itemGroups = new ArrayList<>();

        public General() {
        }
    }

    public static class ItemGroup {
        public String tab;
        public List<String> items = new ArrayList<>();
    }
}
