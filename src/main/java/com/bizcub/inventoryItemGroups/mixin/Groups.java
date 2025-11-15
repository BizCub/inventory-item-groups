package com.bizcub.inventoryItemGroups.mixin;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(CreativeModeInventoryScreen.class)
public class Groups {

    @Redirect(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;getDisplayItems()Ljava/util/Collection;"))
    private static Collection<ItemStack> gg(CreativeModeTab instance) {
        List<ItemStack> newStack = new ArrayList<>(instance.getDisplayItems());
        String addItemName = "cherry_door";
        String removeItemName = "oak_door";

        for (int i = 0; i < newStack.size(); i++) {
            String name = newStack.get(i).getItem().toString();

            // add item
            if (name.equals(addItemName) || name.equals("minecraft:" + addItemName)) {
                newStack.add(i+1, new ItemStack(Items.NETHERITE_INGOT));
                i++;
            }

            // remove item
            if (name.equals(removeItemName) || name.equals("minecraft:" + removeItemName)) {
                newStack.remove(i);
                i--;
            }
        }

        return newStack;
    }
}
