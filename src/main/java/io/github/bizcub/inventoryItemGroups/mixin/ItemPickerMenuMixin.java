package io.github.bizcub.inventoryItemGroups.mixin;

import io.github.bizcub.inventoryItemGroups.Group;
import io.github.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;

@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
public abstract class ItemPickerMenuMixin {

    @Shadow @Final public NonNullList<@NotNull ItemStack> items;

    @Shadow public abstract void scrollTo(float scrollOffs);
    @Shadow protected abstract float getScrollForRowIndex(int rowIndex);

    @Inject(method = "getCarried", at = @At("HEAD"))
    private void iig$toggleGroupVisibility(CallbackInfoReturnable<ItemStack> cir) {
        Group group = Main.pendingGroup;
        if (group != null) {
            group.setVisibility(!group.isVisibility());

            int topRow = iig$currentTopRow();
            int insertIndex = group.getIconIndex() + 1;

            if (group.isVisibility()) {
                ArrayList<ItemStack> itemsColl = group.getItems();
                Collections.reverse(itemsColl);
                itemsColl.forEach(itemStack -> items.add(insertIndex, itemStack));
            }
            else
                group.getItems().forEach(ignore -> items.remove(insertIndex));

            Main.pendingGroup = null;
            scrollTo(getScrollForRowIndex(topRow));
            Main.tempItemStacks = new ArrayList<>(items);
            Main.setIndexes();
        }
    }

    @Unique
    private int iig$currentTopRow() {
        var slots = ((AbstractContainerMenu) (Object) this).slots;
        if (slots.isEmpty()) return 0;
        int topLeft = slots.size() >= 2 ? Main.tempItemStacks.indexOf(slots.get(1).getItem()) : -1;
        if (topLeft >= 0) {
            if (!slots.get(0).getItem().equals(slots.get(1).getItem())) topLeft--;
        } else {
            topLeft = Main.tempItemStacks.indexOf(slots.get(0).getItem());
        }
        return Math.max(topLeft, 0) / 9;
    }
}
