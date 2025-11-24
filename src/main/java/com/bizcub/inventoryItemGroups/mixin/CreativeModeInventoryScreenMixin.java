package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Group;
import com.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
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

    @Unique private static Slot inventoryItemGroups$clickedSlot;

    @Shadow private float scrollOffs;

    @Shadow
    public abstract List<Component> getTooltipFromContainerItem(ItemStack itemStack);

    @Inject(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
    private void slotClicked(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
        inventoryItemGroups$clickedSlot = slot;

        Main.tempInventoryItems.clear();
        for (ItemStack itemStack : Main.tempInventoryItemStack)
            Main.tempInventoryItems.add(itemStack.getItem().toString());

        int index = Main.indexCalculation(Main.tempInventoryItems.size(), scrollOffs, slot.index);
        String item = slot.getItem().getItem().toString();

//        for (ArrayList<String> list : Main.itemGroups)
//            if (list.getFirst().equals(item) && Main.tempInventoryItems.indexOf(item) == index)
//                Main.itemsChanged(item, index, scrollOffs);

        for (Group group : Main.groups)
            if (group.getItems().getFirst().equals(item) && Main.tempInventoryItems.indexOf(item) == index)
                Main.itemsChanged(item, index, scrollOffs);
    }

    @Inject(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 2))
    private void getSlot(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
        inventoryItemGroups$clickedSlot = slot;
    }

    @Unique private void inventoryItemGroups$mouseButtonsFix(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack) {
        String item = itemStack.getItem().toString();
        int index = Main.indexCalculation(Main.tempInventoryItemStack.size(), Main.tempScrollOffs, inventoryItemGroups$clickedSlot.index);

        for (Group group : Main.groups) {
            if (group.getItemsWithIndexes().containsValue(index)) {
                instance.setCarried(ItemStack.EMPTY);
                Main.itemsChanged(item, index, scrollOffs);
            } else
                instance.setCarried(itemStack);
        }

//        if (Main.iconIndexes.containsValue(index)) {
//            instance.setCarried(ItemStack.EMPTY);
//            Main.itemsChanged(item, index, scrollOffs);
//        } else
//            instance.setCarried(itemStack);
    }

    @Redirect(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
    private void mouseButtonsFix(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack) {
        inventoryItemGroups$mouseButtonsFix(instance, itemStack);
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

//        for (ArrayList<String> list : Main.itemGroups) {
//            removeItems.addAll(list);
//            removeItems.remove(list.getFirst());
//        }

        for (Group group : Main.groups) {
            removeItems.addAll(group.getItems());
            removeItems.remove(group.getItems().getFirst());
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
    private void getScrollOffs(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        Main.tempScrollOffs = scrollOffs;
    }
}
