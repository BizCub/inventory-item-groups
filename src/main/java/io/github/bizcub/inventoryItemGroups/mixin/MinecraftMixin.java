package io.github.bizcub.inventoryItemGroups.mixin;

import io.github.bizcub.inventoryItemGroups.Main;
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

    @Unique Screen newScreen;

    @Inject(method = "tick", at = @At("TAIL"))
    private void iig$clearGroups(CallbackInfo ci) {
        //~ if >=26.2 '.screen' -> '.gui.screen()'
        Screen currentScreen = Minecraft.getInstance().gui.screen();
        if (currentScreen != null) {
            if (newScreen != null && !currentScreen.equals(newScreen) && !(currentScreen instanceof CreativeModeInventoryScreen)) {
                Main.groups.clear();
            }
            newScreen = currentScreen;
        } else {
            Main.groups.clear();
        }
    }
}
