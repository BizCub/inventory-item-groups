package io.github.bizcub.inventoryItemGroups.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.bizcub.inventoryItemGroups.Group;
import io.github.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {

    @Shadow private static CreativeModeTab selectedTab;

    @WrapOperation(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;getDisplayItems()Ljava/util/Collection;"))
    private Collection<ItemStack> iig$groupsImplementation(CreativeModeTab selectedTab, Operation<Collection<ItemStack>> original) {
        Main.selectedTab = selectedTab;
        Main.createGroups();

        ArrayList<Group> groupsOnSelectedTab = Main.groupsOnSelectedTab(selectedTab);
        ArrayList<ItemStack> newStack = new ArrayList<>(original.call(selectedTab));
        ArrayList<ItemStack> removeItems = new ArrayList<>();

        for (Group group : groupsOnSelectedTab) {
            removeItems.addAll(group.getItems());
            removeItems.remove(group.getIcon());
        }

        newStack.removeAll(removeItems);

        Main.tempItemStacks = newStack;
        Main.setIndexes();
        return newStack;
    }

    @WrapWithCondition(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V"))
    private boolean iig$toggleInsteadOfCarry(CreativeModeInventoryScreen.ItemPickerMenu instance, ItemStack itemStack, @Local(argsOnly = true) Slot slot) {
        int index = Main.calculateIndex(instance.slots, slot.index);
        Group group = Main.findGroupByIndex(index);
        if (group != null && selectedTab.equals(group.getTab()) && group.getIconIndex() == index) {
            instance.setCarried(ItemStack.EMPTY);
            Main.pendingGroup = group;
            return false;
        }
        return true;
    }

    @Inject(method = "selectTab", at = @At("HEAD"))
    private void iig$updateSelectedTab(CreativeModeTab tab, CallbackInfo ci) {
        Main.selectedTab = tab;
    }
}
