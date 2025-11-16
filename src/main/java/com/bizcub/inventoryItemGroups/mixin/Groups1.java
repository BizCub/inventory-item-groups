package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
public abstract class Groups1 {

    @Shadow @Final public NonNullList<@NotNull ItemStack> items;

    @Shadow public abstract void scrollTo(float f);

    @Inject(method = "scrollTo", at = @At("HEAD"))
    private void gg(float f, CallbackInfo ci) {
        System.out.println("scrolled");
    }

    @Inject(method = "getCarried", at = @At("HEAD"))
    private void gg(CallbackInfoReturnable<ItemStack> cir) {
        if (Main.tempListChanged) {
            items.add(1, new ItemStack(Items.SADDLE));
            Main.tempListChanged = false;
            scrollTo(Main.tempScrollOffs);
            System.out.println("GGGGGGGGGGGGGGGJDJHKFI");
        }
    }
}
