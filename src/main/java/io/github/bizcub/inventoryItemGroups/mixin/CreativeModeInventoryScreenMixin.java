package io.github.bizcub.inventoryItemGroups.mixin;

import io.github.bizcub.inventoryItemGroups.Group;
import io.github.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.ContainerInput;
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

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {

    @Shadow private static CreativeModeTab selectedTab;

    @Redirect(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;getDisplayItems()Ljava/util/Collection;"))
    private Collection<ItemStack> iig$groupsImplementation(CreativeModeTab selectedTab) {
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
    private void iig$mouseButtonsFix(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack, Slot slot) {
        int index = Main.calculateIndex(instance.slots, slot.index);
        Group group = Main.findGroupByIndex(index);

        if (group != null && selectedTab.equals(group.getTab()) && group.getIconIndex() == index) {
            instance.setCarried(ItemStack.EMPTY);
            Main.pendingGroup = group;
        } else
            instance.setCarried(itemStack);
    }

    @Redirect(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
    private void iig$mouseButtonsFix(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack, Slot slot, int slotId, int buttonNum, ContainerInput containerInput) {
        iig$mouseButtonsFix(instance, itemStack, slot);
    }

    @Redirect(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 2))
    private void iig$mouseMiddleButtonFix(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack, Slot slot, int slotId, int buttonNum, ContainerInput containerInput) {
        iig$mouseButtonsFix(instance, itemStack, slot);
    }

    @Inject(method = "selectTab", at = @At("HEAD"))
    private void iig$updateSelectedTab(CreativeModeTab tab, CallbackInfo ci) {
        Main.selectedTab = tab;
    }
}
