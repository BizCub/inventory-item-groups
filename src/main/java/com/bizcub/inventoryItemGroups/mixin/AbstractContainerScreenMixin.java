package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Group;
import com.bizcub.inventoryItemGroups.Main;
import com.bizcub.inventoryItemGroups.config.Compat;
import com.bizcub.inventoryItemGroups.config.Configs;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> {

    @Unique private int tick = 0;
    @Unique private int seconds = 0;

    @Shadow @Final protected T menu;
    @Shadow protected Slot hoveredSlot;

    @Shadow protected abstract List<Component> getTooltipFromContainerItem(ItemStack arg);

    @Unique private static boolean iig$onScreen(int index) {
        return index <= 44;
    }

    @Unique
    private int iig$calculateIndex(Slot slot) {
        int result = 0;
        var slots = menu.slots;
        if (!slots.isEmpty() && slots.size() > 1)
            result = Main.tempItemStacks.indexOf(slots.get(1).getItem());
        if (!slots.get(0).getItem().equals(slots.get(1).getItem())) result--;
        return result + slot.index;
    }

    @Unique
    private Identifier iig$getSprite(String location) {
        String id = Main.MOD_ID;
        String path = "container/" + location;
        /*? >=1.21*/ return Identifier.fromNamespaceAndPath(id, path);
        /*? <=1.20.6*/ //return new Identifier(id, "textures/gui/sprites/" + path + ".png");
    }

    @Unique
    private void iig$renderSprite(GuiGraphics guiGraphics, String location, int x, int y, int size) {
        //? >=1.21.6 {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, iig$getSprite(location), x, y, size, size);
        //?} >=1.21.2 {
        /*guiGraphics.blitSprite(RenderType::guiTextured, iig$getSprite(location), x, y, size, size);
        *///?} >=1.21 {
        /*RenderSystem.disableDepthTest();
        guiGraphics.blitSprite(iig$getSprite(location), x, y, size, size);
        RenderSystem.enableDepthTest();
        *///?} else {
        /*RenderSystem.disableDepthTest();
        guiGraphics.blit(iig$getSprite(location), x, y, 0, 0, size, size, size, size);
        RenderSystem.enableDepthTest();*///?}
    }

    @Inject(method = "renderTooltip", at = @At("HEAD"))
    private void getTicks(CallbackInfo ci) {
        tick++;
        if (tick == Minecraft.getInstance().options.framerateLimit().get()) {
            tick = 0;
            seconds++;
        }
    }

    @Redirect(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;getItem()Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
    private ItemStack renderItems(Slot slot) {
        int index = iig$calculateIndex(slot);
        Group group = Main.findGroupByIndex(index);

        if (Compat.isClothConfigLoaded() && Configs.getConfig().showGroupItems
                && group != null && group.getIconIndex() == index && iig$onScreen(slot.index)) {
            return group.getItems().get(seconds % group.getItems().size());
        }
        return slot.getItem();
    }

    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void renderSlotSprites(GuiGraphics guiGraphics, Slot slot, /*? >=1.21.11 {*/ int i, int j, /*?}*/ CallbackInfo ci) {
        ArrayList<Group> groupsOnSelectedTab = Main.groupsOnSelectedTab(Main.selectedTab);
        int index = iig$calculateIndex(slot);
        for (Group group : groupsOnSelectedTab) {
            if (iig$onScreen(slot.index) && group.isVisibility()) {
                if (group.getIconIndex() == index)
                    iig$renderSprite(guiGraphics, "icon_slot", slot.x-1, slot.y-1, 18);

                for (ItemStack itemStack : group.getItems())
                    for (HashMap<ItemStack, Integer> itemStacksMap : group.getItemsWithIndexes())
                        if (itemStacksMap.containsKey(itemStack) && itemStacksMap.containsValue(index))
                            iig$renderSprite(guiGraphics, "item_slot", slot.x-1, slot.y-1, 18);
            }
        }
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void renderVisibilitySprites(GuiGraphics guiGraphics, Slot slot, /*? >=1.21.11 {*/ int i, int j, /*?}*/ CallbackInfo ci) {
        ArrayList<Group> groupsOnSelectedTab = Main.groupsOnSelectedTab(Main.selectedTab);
        int index = iig$calculateIndex(slot);
        for (Group group : groupsOnSelectedTab) {
            if (iig$onScreen(slot.index) && group.getIconIndex() == index) {
                if (group.isVisibility())
                    iig$renderSprite(guiGraphics, "minus", slot.x, slot.y, 16);
                else
                    iig$renderSprite(guiGraphics, "plus", slot.x, slot.y, 16);
            }
        }
    }

    @Redirect(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;getTooltipFromContainerItem(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
    private List<Component> renderGroupName(AbstractContainerScreen instance, ItemStack arg) {
        int slot = iig$calculateIndex(hoveredSlot);
        Group group = Main.findGroupByIndex(slot);

        if (group != null && slot == group.getIconIndex()) {
            return List.of(group.getName());
        } else {
            return this.getTooltipFromContainerItem(arg);
        }
    }
}
