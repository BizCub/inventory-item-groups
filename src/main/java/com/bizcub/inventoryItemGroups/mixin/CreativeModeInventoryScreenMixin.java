package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Group;
import com.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.screens.Screen;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin extends Screen {

    protected CreativeModeInventoryScreenMixin(Component component) {
        super(component);
    }

    @Shadow private float scrollOffs;
    @Shadow private static CreativeModeTab selectedTab;

    @Unique private static Slot iig$clickedSlot;

    @Redirect(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;getDisplayItems()Ljava/util/Collection;"))
    private Collection<ItemStack> groupsImplementation(CreativeModeTab selectedTab) {
        Main.selectedTab = selectedTab;
        Main.createGroups();

        ArrayList<Group> groupsOnSelectedTab = Main.groupsOnSelectedTab(selectedTab);
        ArrayList<ItemStack> newStack = new ArrayList<>(selectedTab.getDisplayItems());
        ArrayList<ItemStack> removeItems = new ArrayList<>();

        for (Group group : groupsOnSelectedTab) {
            removeItems.addAll(group.getItems());
            removeItems.remove(group.getIcon());
        }

        for (int i = 0; i < newStack.size(); i++) {
            ItemStack itemStack = newStack.get(i);

            for (ItemStack removableItemStacks : removeItems) {
                if (itemStack.equals(removableItemStacks)) {
                    newStack.remove(i);
                    i--;
                }
            }
        }

        Main.tempItemStacks = newStack;
        Main.setIndexes();
        return newStack;
    }

    @Unique
    private int iig$indexCalculation(int inventorySize, int slotIndex) {
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

    @Unique
    private void iig$mouseButtonsFix(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack) {
        int index = iig$indexCalculation(Main.tempItemStacks.size(), iig$clickedSlot.index);
        Group group = Main.findGroupByIndex(index);

        if (group != null && selectedTab.equals(group.getTab()) && group.getIconIndex() == index) {
            instance.setCarried(ItemStack.EMPTY);
            Main.itemsChanged.toggle(index, scrollOffs);
        } else instance.setCarried(itemStack);
    }

    @Inject(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
    private void slotClicked(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
        iig$clickedSlot = slot;
    }

    @Redirect(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
    private void mouseButtonsFix(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack) {
        iig$mouseButtonsFix(instance, itemStack);
    }

    @Inject(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 2))
    private void getSlot(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
        iig$clickedSlot = slot;
    }

    @Redirect(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 2))
    private void mouseMiddleButtonFix(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack) {
        iig$mouseButtonsFix(instance, itemStack);
    }

    @Inject(method = "mouseReleased", at = @At("RETURN"))
    private void removeTexturesDisplay(CallbackInfoReturnable<Boolean> cir) {
        if (selectedTab.getType() != CreativeModeTab.Type.CATEGORY) {
            Main.groups.clear();
        }
    }

    @Override
    public void onClose() {
        Main.groups.clear();
        super.onClose();
    }
}
