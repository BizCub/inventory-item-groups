package io.github.bizcub.inventoryItemGroups.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.bizcub.inventoryItemGroups.Group;
import io.github.bizcub.inventoryItemGroups.IndexedItemStack;
import io.github.bizcub.inventoryItemGroups.Main;
import io.github.bizcub.inventoryItemGroups.config.Config;
import io.github.bizcub.inventoryItemGroups.config.ConfigHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> {

    @Shadow @Final protected T menu;
    @Shadow protected Slot hoveredSlot;

    @Unique private static boolean iig$onScreen(int index) {
        return index <= 44;
    }

    @Unique
    private Identifier iig$getSprite(String location) {
        String id = Main.MOD_ID;
        String path = "container/" + location;
        return Identifier.fromNamespaceAndPath(id, path);
    }

    @Unique
    private void iig$renderSprite(GuiGraphicsExtractor graphics, String location, int x, int y, int size) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, iig$getSprite(location), x, y, size, size);
    }

    @WrapOperation(method = "extractSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;getItem()Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
    private ItemStack iig$renderItems(Slot slot, Operation<ItemStack> original) {
        int index = Main.calculateIndex(menu.slots, slot.index);
        Group group = Main.findGroupByIndex(index);

        ItemStack real = original.call(slot);
        return (ConfigHelper.isConfigLoaded() && Config.get().showItemsInGroup()
                && group != null && Main.selectedTab.equals(group.getTab())
                && group.getIconIndex() == index && iig$onScreen(slot.index)
                && ItemStack.isSameItem(real, group.getIcon()))
                ? group.getItems().get((int) (Util.getMillis() / 1500) % group.getItems().size())
                : real;
    }

    @Inject(method = "extractSlot", at = @At("HEAD"))
    private void iig$renderSlotSprites(CallbackInfo ci, @Local(argsOnly = true) GuiGraphicsExtractor graphics, @Local(argsOnly = true) Slot slot) {
        ArrayList<Group> groupsOnSelectedTab = Main.groupsOnSelectedTab(Main.selectedTab);
        int index = Main.calculateIndex(menu.slots, slot.index);
        for (Group group : groupsOnSelectedTab) {
            if (iig$onScreen(slot.index) && group.isVisibility()) {
                if (group.getIconIndex() == index)
                    iig$renderSprite(graphics, "icon_slot", slot.x-1, slot.y-1, 18);

                for (IndexedItemStack entry : group.getItemsWithIndexes())
                    if (entry.getIndex() == index)
                        iig$renderSprite(graphics, "item_slot", slot.x-1, slot.y-1, 18);
            }
        }
    }

    @Inject(method = "extractSlot", at = @At("TAIL"))
    private void iig$renderVisibilitySprites(CallbackInfo ci, @Local(argsOnly = true) GuiGraphicsExtractor graphics, @Local(argsOnly = true) Slot slot) {
        ArrayList<Group> groupsOnSelectedTab = Main.groupsOnSelectedTab(Main.selectedTab);
        int index = Main.calculateIndex(menu.slots, slot.index);
        for (Group group : groupsOnSelectedTab) {
            if (iig$onScreen(slot.index) && group.getIconIndex() == index) {
                iig$renderSprite(graphics, group.isVisibility() ? "minus" : "plus", slot.x, slot.y, 16);
            }
        }
    }

    @WrapOperation(method = "extractTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;getTooltipFromContainerItem(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
    private List<Component> iig$renderGroupName(AbstractContainerScreen instance, ItemStack itemStack, Operation<List<Component>> original) {
        int index = Main.calculateIndex(menu.slots, hoveredSlot.index);
        Group group = Main.findGroupByIndex(index);

        return (group != null && Main.selectedTab.equals(group.getTab()) && index == group.getIconIndex() && iig$onScreen(hoveredSlot.index))
                ? List.of(group.getName())
                : original.call(instance, itemStack);
    }
}
