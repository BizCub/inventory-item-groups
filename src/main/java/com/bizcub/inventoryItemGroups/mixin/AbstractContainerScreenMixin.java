package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin<T extends AbstractContainerMenu> {

    @Unique private static final Identifier PLUS_SPRITE = Identifier.fromNamespaceAndPath("inventory-item-groups", "container/plus");
    @Unique private static final Identifier MINUS_SPRITE = Identifier.fromNamespaceAndPath("inventory-item-groups", "container/minus");
    @Unique private static final Identifier ICON_SLOT_SPRITE = Identifier.fromNamespaceAndPath("inventory-item-groups", "container/icon_slot");
    @Unique private static final Identifier ITEM_SLOT_SPRITE = Identifier.fromNamespaceAndPath("inventory-item-groups", "container/item_slot");

    @Shadow @Final protected T menu;

    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void renderSlotSprites(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        NonNullList<@NotNull Slot> slots = menu.slots;

        int index = 0;
        if (slots.size() > 1 && slots.getFirst() != slots.get(1))
            index = Main.tempInventoryItemStack.indexOf(slots.get(1).getItem()) - 1;
        index += slot.index;

        boolean visible = false;
        if (Main.iconIndexes.containsValue(index) && slot.index <= 44) {
            for (String str : Main.iconIndexes.keySet())
                if (Main.iconIndexes.get(str) == index)
                    visible = Main.groupVisibility.get(str);

            if (visible)
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ITEM_SLOT_SPRITE, slot.x-1, slot.y-1, 18, 18);
        }
        if (Main.itemIndexes.containsValue(index) && slot.index <= 44)
            for (String str : Main.itemIndexes.keySet())
                if (Main.itemIndexes.get(str) == index)
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_SLOT_SPRITE, slot.x-1, slot.y-1, 18, 18);
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void renderVisibilitySprites(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        ArrayList<ItemStack> newStack = new ArrayList<>(Main.tempInventoryItemStack);
        Main.iconIndexes.clear();
        Main.itemIndexes.clear();

        for (int i = newStack.size()-1; i > 0; i--) {
            String item = newStack.get(i).getItem().toString();

            for (ArrayList<String> list : Main.itemGroups)
                if (list.getFirst().equals(item))
                    Main.iconIndexes.put(item, i);
        }

        for (int i = 0; i < newStack.size(); i++) {
            String item = newStack.get(i).getItem().toString();

            for (ArrayList<String> list : Main.itemGroups)
                if (Main.groupVisibility.get(list.getFirst()))
                    for (String str : list)
                        if (str.equals(item))
                            Main.itemIndexes.put(str, i);
        }

        NonNullList<@NotNull Slot> slots = menu.slots;
        int index = 0;
        if (slots.size() > 1 && slots.getFirst() != slots.get(1))
            index = Main.tempInventoryItemStack.indexOf(slots.get(1).getItem()) - 1;
        index += slot.index;
        boolean visible = false;
        if (Main.iconIndexes.containsValue(index) && slot.index <= 45) {
            for (String str : Main.iconIndexes.keySet())
                if (Main.iconIndexes.get(str) == index)
                    visible = Main.groupVisibility.get(str);

            if (visible)
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, MINUS_SPRITE, slot.x, slot.y, 16, 16);
            else
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PLUS_SPRITE, slot.x, slot.y, 16, 16);
        }
    }
}
