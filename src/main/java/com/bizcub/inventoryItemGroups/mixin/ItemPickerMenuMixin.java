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

@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
public abstract class ItemPickerMenuMixin {

    @Shadow @Final public NonNullList<@NotNull ItemStack> items;

    @Shadow public abstract void scrollTo(float f);

    @Inject(method = "getCarried", at = @At("HEAD"))
    private void toggleGroupVisibility(CallbackInfoReturnable<ItemStack> cir) {
        if (Main.tempListChanged) {
//            String groupName;
//            boolean visible;
//
//            for (ArrayList<String> list : Main.itemGroups) {
//                if (list.contains(Main.tempGroupName)) {
//                    groupName = list.getFirst();
//                    visible = !Main.groupVisibility.get(groupName);
//                    Main.groupVisibility.put(groupName, visible);
//
//                    if (visible)
//                        list.reversed().forEach(str -> items.add(Main.tempIndex + 1, new ItemStack(Main.itemsMapping.get(str))));
//                    else
//                        list.forEach(ignored -> items.remove(Main.tempIndex + 1));
//                }
//            }

            for (Group group : Main.groups) {
                if (group.getItems().contains(Main.tempGroupName)) {
                    group.setVisibility(!group.isVisibility());

                    if (group.isVisibility())
                        group.getItems().reversed().forEach(str -> items.add(Main.tempIndex + 1, new ItemStack(Main.itemsMapping.get(str))));
                    else
                        group.getItems().forEach(ignored -> items.remove(Main.tempIndex + 1));
                }
            }

            Main.tempListChanged = false;
            scrollTo(Main.tempScrollOffs);
        }
        Main.tempInventoryItemStack = items;
    }
}
