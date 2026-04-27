package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Unique Screen oldScreen;
    @Unique Screen newScreen;

    @Inject(method = "tick", at = @At("TAIL"))
    private void clearGroups(CallbackInfo ci) {
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen != null) {
            oldScreen = currentScreen;
            if (newScreen != null && !oldScreen.equals(newScreen)) {
                if (!(currentScreen instanceof CreativeModeInventoryScreen)) {
                    Main.groups.clear();
                }
            }
            newScreen = currentScreen;
        } else {
            Main.groups.clear();
        }
    }
}
