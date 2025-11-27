package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Group;
import com.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin<T extends AbstractContainerMenu> {

    @Shadow @Final protected T menu;

    @Unique private static final Identifier PLUS_SPRITE = Identifier.fromNamespaceAndPath("inventory-item-groups", "container/plus");
    @Unique private static final Identifier MINUS_SPRITE = Identifier.fromNamespaceAndPath("inventory-item-groups", "container/minus");
    @Unique private static final Identifier ICON_SLOT_SPRITE = Identifier.fromNamespaceAndPath("inventory-item-groups", "container/icon_slot");
    @Unique private static final Identifier ITEM_SLOT_SPRITE = Identifier.fromNamespaceAndPath("inventory-item-groups", "container/item_slot");

    @Unique private static boolean inventoryItemGroups$onScreen(Slot slot) {
        return slot.index <= 44;
    }

    @Unique private int inventoryItemGroups$calculateIndex(Slot slot) {
        int index = 0;
        if (menu.slots.size() > 1 && menu.slots.getFirst() != menu.slots.get(1))
            index = Main.tempInventoryItemStack.indexOf(menu.slots.get(1).getItem()) - 1;
        return index + slot.index;
    }

    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void renderSlotSprites(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        int index = inventoryItemGroups$calculateIndex(slot);
        for (Group group : Main.groups) {
            if (inventoryItemGroups$onScreen(slot) && group.isVisibility()) {
                if (group.getIconIndex() == index)
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_SLOT_SPRITE, slot.x-1, slot.y-1, 18, 18);

                for (String str : group.getItems())
                    if (group.getItemsWithIndexes().get(str) == index)
                        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ITEM_SLOT_SPRITE, slot.x-1, slot.y-1, 18, 18);
            }
        }
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void renderVisibilitySprites(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        int index = inventoryItemGroups$calculateIndex(slot);
        for (Group group : Main.groups) {
            if (inventoryItemGroups$onScreen(slot) && group.getIconIndex() == index) {
                if (group.isVisibility())
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, MINUS_SPRITE, slot.x, slot.y, 16, 16);
                else
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PLUS_SPRITE, slot.x, slot.y, 16, 16);
            }
        }
    }
}
