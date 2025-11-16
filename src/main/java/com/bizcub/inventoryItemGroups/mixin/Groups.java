package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(CreativeModeInventoryScreen.class)
public class Groups {

    @Shadow private float scrollOffs;
    @Unique List<ItemStack> inventoryItemGroups$newStack = new ArrayList<>();

    @Inject(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
    private void slotClicked(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
        if (Main.checkItemNames(Main.doorsGroup, slot.getItem().getItem().toString())) {
            Main.listChanged(slot.getItem().getItem().toString(), inventoryItemGroups$newStack.indexOf(slot.getItem()), scrollOffs);
            System.out.println("clicked");
        }
    }

    @Redirect(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;getDisplayItems()Ljava/util/Collection;"))
    private Collection<ItemStack> groupsImplementation(CreativeModeTab instance) {
        List<ItemStack> newStack = new ArrayList<>(instance.getDisplayItems());
        List<String> addItemNames = Main.doorsGroup;

        for (int i = 0; i < newStack.size(); i++) {
            String name = newStack.get(i).getItem().toString();

            for (String item : addItemNames) {
                if (Main.checkItemNames(name, item)) {
                    newStack.add(i+1, new ItemStack(Items.NETHERITE_INGOT));
                    i++;
                }
            }
        }

        return newStack;
    }
}
