package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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
        if (Minecraft.getInstance().screen != null) {
            oldScreen = Minecraft.getInstance().screen;
            if (!oldScreen.equals(newScreen)) {
                Main.groups.clear();
            }
            newScreen = Minecraft.getInstance().screen;
        } else {
            Main.groups.clear();
        }
    }
}
