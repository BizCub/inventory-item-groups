package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Group;
import com.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
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
public abstract class CreativeModeInventoryScreenMixin {

    @Shadow private float scrollOffs;

    @Unique private static Slot inventoryItemGroups$clickedSlot;

    @Unique private int inventoryItemGroups$indexCalculation(int inventorySize, int slotIndex) {
        float rows = (float) inventorySize / 9;
        rows = (float) Math.ceil(rows);
        int index = 0;
        if (inventorySize > 45) {
            index = Math.round((rows - 5) * scrollOffs);
            if (index > 0)
                index = index * 9;
        }
        return index + slotIndex;
    }

    @Unique private void inventoryItemGroups$mouseButtonsFix(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack) {
        int index = inventoryItemGroups$indexCalculation(Main.tempInventoryItemStack.size(), inventoryItemGroups$clickedSlot.index);
        for (Group group : Main.groups) {
            if (group.getIconIndex() == index) {
                instance.setCarried(ItemStack.EMPTY);
                Main.itemsChanged(index);
                break;
            } else instance.setCarried(itemStack);
        }
    }

    @Inject(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
    private void slotClicked(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
        inventoryItemGroups$clickedSlot = slot;
    }

    @Redirect(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
    private void mouseButtonsFix(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack) {
        inventoryItemGroups$mouseButtonsFix(instance, itemStack);
    }

    @Inject(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 2))
    private void getSlot(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
        inventoryItemGroups$clickedSlot = slot;
    }

    @Redirect(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 2))
    private void mouseMiddleButtonFix(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack) {
        inventoryItemGroups$mouseButtonsFix(instance, itemStack);
    }

    @Redirect(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;getDisplayItems()Ljava/util/Collection;"))
    private Collection<ItemStack> groupsImplementation(CreativeModeTab instance) {
        List<ItemStack> newStack = new ArrayList<>(instance.getDisplayItems());
        List<String> removeItems = new ArrayList<>();

        Main.hideGroups();

        for (Group group : Main.groups) {
            removeItems.addAll(group.getItems());
            removeItems.remove(group.getIcon());
        }

        for (int i = 0; i < newStack.size(); i++) {
            String name = newStack.get(i).getItem().toString();

            for (String item : removeItems) {
                if (name.equals(item)) {
                    newStack.remove(i);
                    i--;
                }
            }
        }

        Main.tempInventoryItemStack = newStack;
        return newStack;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void getScrollOffs(CallbackInfo ci) {
        Main.tempScrollOffs = scrollOffs;
    }
}
