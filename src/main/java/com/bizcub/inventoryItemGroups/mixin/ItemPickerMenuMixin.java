package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Group;
import com.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;

@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
public abstract class ItemPickerMenuMixin {

    @Shadow @Final public NonNullList<@NotNull ItemStack> items;

    @Shadow public abstract void scrollTo(float f);

    @Inject(method = "getCarried", at = @At("HEAD"))
    private void toggleGroupVisibility(CallbackInfoReturnable<ItemStack> cir) {
        if (Main.tempListChanged) {
            Group group = Main.tempGroup;
            group.setVisibility(!group.isVisibility());

            if (group.isVisibility()) {
                ArrayList<ItemStack> itemsColl = group.getItems();
                Collections.reverse(itemsColl);
                itemsColl.forEach(itemStack -> items.add(Main.tempIndex + 1, itemStack));
            }
            else group.getItems().forEach(ignore -> items.remove(Main.tempIndex + 1));

            Main.tempListChanged = false;
            Main.tempInventoryItemStack = new ArrayList<>(items);
            scrollTo(Main.tempScrollOffs);
            Main.setIndexes();
        }
    }
}
