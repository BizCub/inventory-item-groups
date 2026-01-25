package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Group;
import com.bizcub.inventoryItemGroups.Main;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
/*? >=1.21.2*/ import net.minecraft.client.renderer.RenderPipelines;
/*? >=1.21.11*/ import net.minecraft.resources.Identifier;
/*? <=1.21.10*/ //import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
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

    @Shadow @Final protected T menu;

    @Unique private static boolean inventoryItemGroups$onScreen(Slot slot) {
        return slot.index <= 44;
    }

    @Unique
    /*? >=1.21.11*/ private static Identifier getSprite(String location) {
    /*? <=1.21.10*/ //private static ResourceLocation getSprite(String location) {
        String id = "inventory-item-groups";
        String path = "container/" + location;
        /*? >=1.21.11*/ return Identifier.fromNamespaceAndPath(id, path);
        /*? <=1.21.10*/ //return ResourceLocation.fromNamespaceAndPath(id, path);
    }

    @Unique
    private int inventoryItemGroups$calculateIndex(Slot slot) {
        int index = 0;
        if (menu.slots.size() > 1 && menu.slots.getFirst() != menu.slots.get(1))
            index = Main.tempInventoryItemStack.indexOf(menu.slots.get(1).getItem()) - 1;
        return index + slot.index;
    }

    @Unique
    private void renderSprite(GuiGraphics guiGraphics, String location, int x, int y, int width, int height) {
        guiGraphics.blitSprite(getSprite(location), x, y, width, height);
    }

    @Inject(method = "renderSlot", at = @At("HEAD"))
    /*? >=1.21.11*/ private void renderSlotSprites(GuiGraphics guiGraphics, Slot slot, int i, int j, CallbackInfo ci) {
    /*? <=1.21.10*/ //private void renderSlotSprites(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        ArrayList<Group> groupsOnSelectedTab = Main.groupsOnSelectedTab(Main.tempSelectedTab);
        int index = inventoryItemGroups$calculateIndex(slot);
        for (Group group : groupsOnSelectedTab) {
            if (inventoryItemGroups$onScreen(slot) && group.isVisibility()) {
                if (group.getIconIndex() == index)
                    renderSprite(guiGraphics, "icon_slot", slot.x-1, slot.y-1, 18, 18);

                for (String str : group.getItems())
                    if (group.getItemsWithIndexes().get(str) == index)
                        renderSprite(guiGraphics, "item_slot", slot.x-1, slot.y-1, 18, 18);
            }
        }
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    /*? >=1.21.11*/ private void renderVisibilitySprites(GuiGraphics guiGraphics, Slot slot, int i, int j, CallbackInfo ci) {
    /*? <=1.21.10*/ //private void renderVisibilitySprites(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        ArrayList<Group> groupsOnSelectedTab = Main.groupsOnSelectedTab(Main.tempSelectedTab);
        int index = inventoryItemGroups$calculateIndex(slot);
        for (Group group : groupsOnSelectedTab) {
            if (inventoryItemGroups$onScreen(slot) && group.getIconIndex() == index) {
                if (group.isVisibility())
                    renderSprite(guiGraphics, "minus", slot.x, slot.y, 16, 16);
                else
                    renderSprite(guiGraphics, "plus", slot.x, slot.y, 16, 16);
            }
        }
    }
}
