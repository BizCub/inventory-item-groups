package com.bizcub.inventoryItemGroups.platforms;


import com.bizcub.inventoryItemGroups.config.Configs;
/*? >=1.21.11 && !forge*/ import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.gui.screens.Screen;

public class PlatformInit {

    //? !(>=1.21.4 && forge) {
    public static Screen getScreen(Screen parent) {
        /*? >=1.21.11*/ return AutoConfigClient.getConfigScreen(Configs.class, parent).get();
        /*? <=1.21.10*/ /*return AutoConfig.getConfigScreen(Configs.class, parent).get();*/
    }//?}
}
