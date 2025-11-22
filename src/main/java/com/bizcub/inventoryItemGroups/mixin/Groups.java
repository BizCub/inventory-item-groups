package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.GuiGraphics;
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
public class Groups {

    @Unique private static Slot inventoryItemGroups$slotClicked;

    @Shadow private float scrollOffs;

    @Inject(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
    private void slotClicked(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
        inventoryItemGroups$slotClicked = slot;

        Main.tempInventoryItems.clear();
        for (ItemStack itemStack : Main.tempInventoryItemStack) {
            Main.tempInventoryItems.add(itemStack.getItem().toString());
        }

        int index = Main.slotIndexCalculation(Main.tempInventoryItems.size(), scrollOffs, slot.index);
        String item = slot.getItem().getItem().toString();

        for (ArrayList<String> list : Main.itemGroups) {
            if (list.getFirst().equals(item) && Main.tempInventoryItems.indexOf(item) == index) {
                Main.listChanged(item, index, scrollOffs);
            }
        }
    }

    @Redirect(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
    private void hh(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack) {
        String item = itemStack.getItem().toString();
        int index = Main.slotIndexCalculation(Main.tempInventoryItemStack.size(), Main.tempScrollOffs, inventoryItemGroups$slotClicked.index);

        if (Main.itemIconIndexes.containsValue(index)) {
            instance.setCarried(ItemStack.EMPTY);
            Main.listChanged(item, index, scrollOffs);
        } else {
            instance.setCarried(itemStack);
        }
    }

    @Inject(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 2))
    private void ff(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
        inventoryItemGroups$slotClicked = slot;
    }

    @Redirect(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 2))
    private void gg(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack) {
        String item = itemStack.getItem().toString();
        int index = Main.slotIndexCalculation(Main.tempInventoryItemStack.size(), Main.tempScrollOffs, inventoryItemGroups$slotClicked.index);

        if (Main.itemIconIndexes.containsValue(index)) {
            instance.setCarried(ItemStack.EMPTY);
            Main.listChanged(item, index, scrollOffs);
        } else {
            instance.setCarried(itemStack);
        }
    }

    @Redirect(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;getDisplayItems()Ljava/util/Collection;"))
    private Collection<ItemStack> groupsImplementation(CreativeModeTab instance) {
        List<ItemStack> newStack = new ArrayList<>(instance.getDisplayItems());
        List<String> removeItemNames = new ArrayList<>();

        Main.visibilityGroup();

        for (ArrayList<String> list : Main.itemGroups) {
            removeItemNames.addAll(list);
            removeItemNames.remove(list.getFirst());
        }

        for (int i = 0; i < newStack.size(); i++) {
            String name = newStack.get(i).getItem().toString();

            for (String item : removeItemNames) {
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
    private void gg(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        Main.tempScrollOffs = scrollOffs;
    }
}
