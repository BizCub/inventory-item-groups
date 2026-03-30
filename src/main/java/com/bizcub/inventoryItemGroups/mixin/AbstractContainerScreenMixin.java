package com.bizcub.inventoryItemGroups.mixin;

import com.bizcub.inventoryItemGroups.Group;
import com.bizcub.inventoryItemGroups.Main;
import com.bizcub.inventoryItemGroups.config.Compat;
import com.bizcub.inventoryItemGroups.config.Configs;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//~ if >=1.21.6 'RenderType' -> 'RenderPipelines'
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

    @Unique private static boolean iig$onScreen(int index) {
        return index <= 44;
    }

    @Shadow protected abstract List<Component> getTooltipFromContainerItem(ItemStack itemStack);

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
    private void iig$renderSprite(GuiGraphicsExtractor graphics, String location, int x, int y, int size) {
        //? >=1.21.6 {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, iig$getSprite(location), x, y, size, size);
        //?} >=1.21.2 {
        /*graphics.blitSprite(RenderType::guiTextured, iig$getSprite(location), x, y, size, size);
        *///?} >=1.21 {
        /*RenderSystem.disableDepthTest();
        graphics.blitSprite(iig$getSprite(location), x, y, size, size);
        RenderSystem.enableDepthTest();
        *///?} else {
        /*RenderSystem.disableDepthTest();
        graphics.blit(iig$getSprite(location), x, y, 0, 0, size, size, size, size);
        RenderSystem.enableDepthTest();*///?}
    }

    //~ if >=26.1 'renderSlot' -> 'extractSlot'
    @Redirect(method = "extractSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;getItem()Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
    private ItemStack renderItems(Slot slot) {
        int index = iig$calculateIndex(slot);
        Group group = Main.findGroupByIndex(index);

        return (Compat.isClothConfigLoaded() && Configs.getConfig().showGroupItems
                && group != null && group.getIconIndex() == index && iig$onScreen(slot.index))
                ? group.getItems().get(seconds % group.getItems().size())
                : slot.getItem();
    }

    //~ if >=26.1 'renderSlot' -> 'extractSlot'
    @Inject(method = "extractSlot", at = @At("HEAD"))
    private void renderSlotSprites(GuiGraphicsExtractor graphics, Slot slot, /*? >=1.21.11 {*/ int mouseX, int mouseY, /*?}*/ CallbackInfo ci) {
        ArrayList<Group> groupsOnSelectedTab = Main.groupsOnSelectedTab(Main.selectedTab);
        int index = iig$calculateIndex(slot);
        for (Group group : groupsOnSelectedTab) {
            if (iig$onScreen(slot.index) && group.isVisibility()) {
                if (group.getIconIndex() == index)
                    iig$renderSprite(graphics, "icon_slot", slot.x-1, slot.y-1, 18);

                for (ItemStack itemStack : group.getItems())
                    for (HashMap<ItemStack, Integer> itemStacksMap : group.getItemsWithIndexes())
                        if (itemStacksMap.containsKey(itemStack) && itemStacksMap.containsValue(index))
                            iig$renderSprite(graphics, "item_slot", slot.x-1, slot.y-1, 18);
            }
        }
    }

    //~ if >=26.1 'renderSlot' -> 'extractSlot'
    @Inject(method = "extractSlot", at = @At("TAIL"))
    private void renderVisibilitySprites(GuiGraphicsExtractor graphics, Slot slot, /*? >=1.21.11 {*/ int mouseX, int mouseY, /*?}*/ CallbackInfo ci) {
        ArrayList<Group> groupsOnSelectedTab = Main.groupsOnSelectedTab(Main.selectedTab);
        int index = iig$calculateIndex(slot);
        for (Group group : groupsOnSelectedTab) {
            if (iig$onScreen(slot.index) && group.getIconIndex() == index) {
                if (group.isVisibility())
                    iig$renderSprite(graphics, "minus", slot.x, slot.y, 16);
                else
                    iig$renderSprite(graphics, "plus", slot.x, slot.y, 16);
            }
        }
    }

    //~ if >=26.1 'renderTooltip' -> 'extractTooltip'
    @Redirect(method = "extractTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;getTooltipFromContainerItem(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
    private List<Component> renderGroupName(AbstractContainerScreen instance, ItemStack arg) {
        int index = iig$calculateIndex(hoveredSlot);
        Group group = Main.findGroupByIndex(index);

        return (group != null && index == group.getIconIndex() && iig$onScreen(hoveredSlot.index))
                ? List.of(group.getName())
                : this.getTooltipFromContainerItem(arg);
    }

    //~ if >=26.1 'renderTooltip' -> 'extractTooltip'
    @Inject(method = "extractTooltip", at = @At("HEAD"))
    private void getTicks(CallbackInfo ci) {
        tick++;
        if (tick == Minecraft.getInstance().options.framerateLimit().get()) {
            tick = 0;
            seconds++;
        }
    }
}
