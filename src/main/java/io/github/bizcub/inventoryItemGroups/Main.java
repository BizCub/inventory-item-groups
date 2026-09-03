package io.github.bizcub.inventoryItemGroups;

import io.github.bizcub.inventoryItemGroups.config.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;

import java.util.*;

public class Main {
    public static final String MOD_ID = /*$ mod_id*/ "inventory_item_groups";

    public static ArrayList<Group> groups = new ArrayList<>();
    public static ArrayList<RawGroup> rawDefaultGroups = new ArrayList<>();
    public static CreativeModeTab selectedTab;
    public static Group pendingGroup = null;
    public static ArrayList<ItemStack> tempItemStacks = new ArrayList<>();

    public static void init() {
        if (ConfigHelper.isSimpleConfigLoaded()) {
            Config.set(SimpleConfig.getInstance().get());
        } else if (ConfigHelper.isClothConfigLoaded()) {
            ClothConfig.load();
            Config.set(ClothConfig.getConfig());
        }
    }

    public static String getTabId(CreativeModeTab tab) {
        Identifier key = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
        return key != null ? key.toString() : tab.getDisplayName().getString();
    }

    public static List<Component> getTabIds() {
        List<Component> list = new ArrayList<>();

        for (CreativeModeTab creativeModeTab : CreativeModeTabs.allTabs()) {
            String tabId = Main.getTabId(creativeModeTab);
            if (!tabId.equals("minecraft:hotbar") && !tabId.equals("minecraft:search") && !tabId.equals("minecraft:op_blocks") && !tabId.equals("minecraft:inventory")) {
                list.add(
                        Component.translatable(
                                        "text.inventory_item_groups.option.id_of_menu_tabs.element",
                                        Component.literal(creativeModeTab.getDisplayName().getString()),
                                        Component.literal(tabId).withStyle(style -> style
                                                .withHoverEvent(getHoverEvent(Component.translatable("chat.copy")))
                                                .withClickEvent(getClickEvent(tabId))
                                                .withColor(ChatFormatting.WHITE))
                                )
                                .withStyle(ChatFormatting.GRAY)
                );
            }
        }

        return list;
    }

    public static int calculateIndex(List<Slot> slots, int slotIndex) {
        if (slots.isEmpty()) return -1;

        int result;
        int secondItemIndex = slots.size() >= 2 ? tempItemStacks.indexOf(slots.get(1).getItem()) : -1;

        if (secondItemIndex >= 0) {
            result = secondItemIndex;
            if (!slots.get(0).getItem().equals(slots.get(1).getItem())) result--;
        } else {
            result = tempItemStacks.indexOf(slots.get(0).getItem());
        }

        if (result < 0) result = 0;
        return result + slotIndex;
    }

    public static ArrayList<Group> groupsOnSelectedTab(CreativeModeTab selectedTab) {
        ArrayList<Group> groupsOnSelectedTab = new ArrayList<>();
        groups.forEach(group -> {
            if (selectedTab.equals(group.getTab()))
                groupsOnSelectedTab.add(group);
        });
        return groupsOnSelectedTab;
    }

    public static Group findGroupByIndex(int index) {
        for (Group group : groups)
            for (HashMap<ItemStack, Integer> itemStacksMap : group.getItemsWithIndexes())
                if (itemStacksMap.containsValue(index) || group.getIconIndex() == index)
                    return group;
        return null;
    }

    public static void setIndexes() {
        ArrayList<Group> groupsOnSelectedTab = groupsOnSelectedTab(selectedTab);
        ArrayList<ItemStack> newStack = new ArrayList<>(tempItemStacks);

        for (Group group : groupsOnSelectedTab) {
            boolean setIcon = false;
            for (ItemStack itemStack : group.getItems()) {
                int firstIndex = newStack.indexOf(itemStack);
                int lastIndex = newStack.lastIndexOf(itemStack);
                if (newStack.contains(itemStack)) {
                    if (group.isVisibility()) {
                        if (firstIndex != lastIndex && !setIcon) {
                            group.setIconIndex(firstIndex);
                            group.setItemWithIndex(itemStack, lastIndex);
                            setIcon = true;
                        } else
                            group.setItemWithIndex(itemStack, firstIndex);
                    } else {
                        group.setIconIndex(firstIndex);
                        group.getItems().forEach(item1 ->
                            group.setItemWithIndex(item1, -1));
                    }
                }
            }
        }
    }

    private static void addConfigItems(String groupName, List<String> containedItems, List<String> nonContainedItems, List<String> equivalentItems) {
        addItems(groupName, containedItems, nonContainedItems, equivalentItems);
    }

    private static void addItems(String groupName, List<String> containedItems, List<String> nonContainedItems, List<String> equivalentItems) {
        rawDefaultGroups.add(new RawGroup());
        RawGroup rawGroup = rawDefaultGroups.get(rawDefaultGroups.size() - 1);
        if (nonContainedItems.isEmpty()) nonContainedItems = List.of("1111111");

        for (ItemStack itemStack : selectedTab.getDisplayItems()) {
            String itemName = itemStack.getItem().toString();
            boolean flag = false;

            for (String containedItem : containedItems) {
                for (String nonContainedItem : nonContainedItems) {
                    if (itemName.contains(containedItem) && !itemName.contains(nonContainedItem)) {
                        addRawGroup(rawGroup, groupName, itemStack);
                        flag = true;
                        break;
                    }
                }
                if (flag) break;
            }

            for (String equivalentItem : equivalentItems) {
                if (equivalentItem.equals(itemName)) {
                    addRawGroup(rawGroup, groupName, itemStack);
                    break;
                }
            }
        }
    }

    private static void addRawGroup(RawGroup rawGroup, String groupName, ItemStack itemStack) {
        rawGroup.items.add(itemStack);
        rawGroup.name = groupName;
    }

    public static void createGroups() {
        rawDefaultGroups.clear();
        groups.clear();

        for (ItemGroup group : Config.get().groups()) {
            List<String> tempListOfItems = group.containedItems.stream().map(Object::toString).toList();
            List<String> tempListOfNonItems = group.nonContainedItems.stream().map(Object::toString).toList();
            List<String> tempListOfItemIds = group.equivalentItems.stream().map(Object::toString).toList();
            if (getTabId(selectedTab).equals(group.tabId))
                addConfigItems(group.groupName, tempListOfItems, tempListOfNonItems, tempListOfItemIds);
        }

        rawDefaultGroups.forEach(rawGroup -> groups.add(new Group(getGroupTranslate(rawGroup), selectedTab, rawGroup.items)));
        validateGroups();
    }

    public static List<ItemGroup> getDefaultGroups() {
        List<ItemGroup> defaults = new ArrayList<>();

        String tabId = "minecraft:building_blocks";
        addDefaultGroup(defaults, tabId, "logs", List.of("log", "stem", "bamboo_block"), List.of("stripped"));
        addDefaultGroup(defaults, tabId, "woods", List.of("wood", "hyphae"), List.of("stripped"));
        addDefaultGroup(defaults, tabId, "stripped_logs", List.of("log", "stem", "bamboo_block"), List.of());
        addDefaultGroup(defaults, tabId, "stripped_woods", List.of("wood", "hyphae"), List.of());
        addDefaultGroup(defaults, tabId, "stairs", List.of("stair"), List.of());
        addDefaultGroup(defaults, tabId, "slabs", List.of("slab"), List.of());
        addDefaultGroup(defaults, tabId, "planks", List.of("planks", "mosaic"), List.of());
        addDefaultGroup(defaults, tabId, "fence_gates", List.of("fence_gate"), List.of());
        addDefaultGroup(defaults, tabId, "fences", List.of("fence"), List.of());
        addDefaultGroup(defaults, tabId, "trapdoors", List.of("trapdoor"), List.of());
        addDefaultGroup(defaults, tabId, "doors", List.of("door"), List.of());
        addDefaultGroup(defaults, tabId, "pressure_plates", List.of("pressure_plate"), List.of());
        addDefaultGroup(defaults, tabId, "buttons", List.of("button"), List.of());
        addDefaultGroup(defaults, tabId, "bars", List.of("bar"), List.of("cinnabar"));
        addDefaultGroup(defaults, tabId, "chains", List.of("chain"), List.of());
        addDefaultGroup(defaults, tabId, "copper", List.of("copper"), List.of());
        addDefaultGroup(defaults, tabId, "walls", List.of("wall"), List.of());
        addDefaultGroup(defaults, tabId, "decorative_stone", List.of("bricks", "chiseled", "tiles", "polished"), List.of());
        addDefaultGroup(defaults, tabId, "sandstone", List.of("sandstone"), List.of());

        tabId = "minecraft:colored_blocks";
        addDefaultGroup(defaults, tabId, "wool", List.of("wool"), List.of());
        addDefaultGroup(defaults, tabId, "carpets", List.of("carpet"), List.of());
        addDefaultGroup(defaults, tabId, "glazed_terracotta", List.of("glazed_terracotta"), List.of());
        addDefaultGroup(defaults, tabId, "terracotta", List.of("terracotta"), List.of());
        addDefaultGroup(defaults, tabId, "concrete_powder", List.of("concrete_powder"), List.of());
        addDefaultGroup(defaults, tabId, "concrete", List.of("concrete"), List.of());
        addDefaultGroup(defaults, tabId, "glass_panes", List.of("glass_pane"), List.of());
        addDefaultGroup(defaults, tabId, "glass", List.of("glass"), List.of());
        addDefaultGroup(defaults, tabId, "shulker_boxes", List.of("shulker_box"), List.of());
        addDefaultGroup(defaults, tabId, "candles", List.of("candle"), List.of());
        addDefaultGroup(defaults, tabId, "banners", List.of("banner"), List.of());
        addDefaultGroup(defaults, tabId, "beds", List.of("bed"), List.of());

        tabId = "minecraft:natural_blocks";
        addDefaultGroup(defaults, tabId, "ores", List.of("_ore", "debris", "raw_"), List.of());
        addDefaultGroup(defaults, tabId, "mushrooms", List.of("mushroom", "fungus"), List.of());
        addDefaultGroup(defaults, tabId, "saplings", List.of("sapling", "propagule"), List.of());
        addDefaultGroup(defaults, tabId, "ground_cover", List.of("fern", "_grass", "bush", "_sprouts", "hanging_moss", "_vines"), List.of("_bush"));
        addDefaultGroup(defaults, tabId, "seeds", List.of("seeds", "_pod"), List.of());
        addDefaultGroup(defaults, tabId, "flowers", List.of("dandelion", "poppy", "orchid", "allium", "tulip", "daisy", "cornflower", "torchflower", "azure_bluet", "valley", "cactus_flower", "eyeblossom", "rose", "petals", "wildflower", "crimson_roots", "warped_roots", "sunflower", "peony", "lilac", "pitcher_plant"), List.of());
        addDefaultGroup(defaults, tabId, "leaves", List.of("leaves"), List.of());
        addDefaultGroup(defaults, tabId, "coral_blocks", List.of("coral_block"), List.of());
        addDefaultGroup(defaults, tabId, "coral_decorations", List.of("coral"), List.of());
        addDefaultGroup(defaults, tabId, "stone", List.of(":stone", "diorite", "andesite", "granite", "tuff", "basalt", "blackstone", "deepslate"), List.of());
        addDefaultGroup(defaults, tabId, "logs", List.of("log", "stem"), List.of());

        tabId = "minecraft:functional_blocks";
        addDefaultGroup(defaults, tabId, "lanterns", List.of("lantern"), List.of("sea"));
        addDefaultGroup(defaults, tabId, "chains", List.of("chain"), List.of());
        addDefaultGroup(defaults, tabId, "bulbs", List.of("bulb"), List.of());
        addDefaultGroup(defaults, tabId, "anvils", List.of("anvil"), List.of());
        addDefaultGroup(defaults, tabId, "lightning_rods", List.of("lightning_rod"), List.of());
        addDefaultGroup(defaults, tabId, "shelves", List.of("_shelf"), List.of());
        addDefaultGroup(defaults, tabId, "hanging_signs", List.of("hanging_sign"), List.of());
        addDefaultGroup(defaults, tabId, "signs", List.of("sign"), List.of());
        addDefaultGroup(defaults, tabId, "chests", List.of("chest"), List.of());
        addDefaultGroup(defaults, tabId, "shulker_boxes", List.of("shulker_box"), List.of());
        addDefaultGroup(defaults, tabId, "beds", List.of("_bed"), List.of());
        addDefaultGroup(defaults, tabId, "candles", List.of("candle"), List.of());
        addDefaultGroup(defaults, tabId, "banners", List.of("banner"), List.of());
        addDefaultGroup(defaults, tabId, "skulls", List.of("head", "skull"), List.of());
        addDefaultGroup(defaults, tabId, "golem_statues", List.of("golem_statue"), List.of());
        addDefaultGroup(defaults, tabId, "infested_stone", List.of("infested"), List.of());
        addDefaultGroup(defaults, tabId, "paintings", List.of("painting"), List.of());

        tabId = "minecraft:redstone_blocks";
        addDefaultGroup(defaults, tabId, "bulbs", List.of("bulb"), List.of());
        addDefaultGroup(defaults, tabId, "pressure_plates", List.of("pressure_plate"), List.of());
        addDefaultGroup(defaults, tabId, "transport", List.of("minecart", "boat", "_raft"), List.of());
        addDefaultGroup(defaults, tabId, "chests", List.of("chest"), List.of());
        addDefaultGroup(defaults, tabId, "rails", List.of("rail"), List.of());

        tabId = "minecraft:tools_and_utilities";
        addDefaultGroup(defaults, tabId, "shovels", List.of("shovel"), List.of());
        addDefaultGroup(defaults, tabId, "pickaxes", List.of("pickaxe"), List.of());
        addDefaultGroup(defaults, tabId, "axes", List.of("axe"), List.of());
        addDefaultGroup(defaults, tabId, "hoes", List.of("hoe"), List.of());
        addDefaultGroup(defaults, tabId, "bundles", List.of("bundle"), List.of());
        addDefaultGroup(defaults, tabId, "firework_rockets", List.of("firework_rocket"), List.of());
        addDefaultGroup(defaults, tabId, "harnesses", List.of("harness"), List.of());
        addDefaultGroup(defaults, tabId, "chest_boats", List.of("chest_boat", "chest_raft"), List.of());
        addDefaultGroup(defaults, tabId, "boats", List.of("boat", "_raft"), List.of());
        addDefaultGroup(defaults, tabId, "rails", List.of("rail"), List.of());
        addDefaultGroup(defaults, tabId, "minecarts", List.of("minecart"), List.of());
        addDefaultGroup(defaults, tabId, "discs", List.of("disc"), List.of());
        addDefaultGroup(defaults, tabId, "goat_horns", List.of("goat_horn"), List.of());
        addDefaultGroup(defaults, tabId, "creature_buckets", List.of("cod_bucket", "salmon_bucket", "tropical_fish_bucket", "pufferfish_bucket", "axolotl_bucket", "tadpole_bucket", "sulfur_cube_bucket"), List.of());

        tabId = "minecraft:combat";
        addDefaultGroup(defaults, tabId, "swords", List.of("sword"), List.of());
        addDefaultGroup(defaults, tabId, "spears", List.of("spear"), List.of());
        addDefaultGroup(defaults, tabId, "axes", List.of("axe"), List.of());
        addDefaultGroup(defaults, tabId, "helmets", List.of("helmet"), List.of());
        addDefaultGroup(defaults, tabId, "chestplates", List.of("chestplate"), List.of());
        addDefaultGroup(defaults, tabId, "leggings", List.of("leggings"), List.of());
        addDefaultGroup(defaults, tabId, "boots", List.of("boots"), List.of());
        addDefaultGroup(defaults, tabId, "horse_armor", List.of("horse_armor"), List.of());
        addDefaultGroup(defaults, tabId, "nautilus_armor", List.of("nautilus_armor"), List.of());
        addDefaultGroup(defaults, tabId, "eggs", List.of("egg"), List.of());
        addDefaultGroup(defaults, tabId, "tipped_arrows", List.of("tipped_arrow"), List.of());
        addDefaultGroup(defaults, tabId, "firework_rockets", List.of("firework_rocket"), List.of());

        tabId = "minecraft:food_and_drinks";
        addDefaultGroup(defaults, tabId, "suspicious_stews", List.of("suspicious_stew"), List.of());
        addDefaultGroup(defaults, tabId, "ominous_bottles", List.of("ominous_bottle"), List.of());
        addDefaultGroup(defaults, tabId, "splash_potions", List.of("splash_potion"), List.of());
        addDefaultGroup(defaults, tabId, "lingering_potions", List.of("lingering_potion"), List.of());
        addDefaultGroup(defaults, tabId, "potions", List.of("potion"), List.of());
        addDefaultGroup(defaults, tabId, "cooked_food", List.of("cooked"), List.of());
        addDefaultGroup(defaults, tabId, "raw_food", List.of("beef", "porkchop", "mutton", "chicken", "rabbit", ":cod", "salmon"), List.of("rabbit_"));

        tabId = "minecraft:ingredients";
        addDefaultGroup(defaults, tabId, "dyes", List.of("dye"), List.of());
        addDefaultGroup(defaults, tabId, "banner_patterns", List.of("banner_pattern"), List.of());
        addDefaultGroup(defaults, tabId, "pottery_sherds", List.of("pottery_sherd"), List.of());
        addDefaultGroup(defaults, tabId, "smithing_templates", List.of("smithing_template"), List.of());
        addDefaultGroup(defaults, tabId, "enchanted_books", List.of("enchanted_book"), List.of());

        return defaults;
    }

    private static void addDefaultGroup(List<ItemGroup> list, String tabId, String groupName, List<String> containedItems, List<String> nonContainedItems) {
        ItemGroup group = new ItemGroup();
        group.tabId = tabId;
        group.groupName = groupName;
        group.containedItems = new ArrayList<>(containedItems);
        group.nonContainedItems = new ArrayList<>(nonContainedItems);
        list.add(group);
    }

    private static void validateGroups() {
        groups.removeIf(group -> group.getItems().isEmpty());
        groups.removeIf(group -> group.getItems().size() < 3);
    }

    public static Component getGroupTranslate(RawGroup rawGroup) {
        if (rawGroup.name == null) rawGroup.name = "name";

        String key = "group_name.inventory_item_groups." + rawGroup.name;
        return Language.getInstance().has(key)
                ? Component.translatable(key)
                : Component.literal(rawGroup.name);
    }

    private static HoverEvent getHoverEvent(Component component) {
        //~ if >=1.21.5 'HoverEvent(HoverEvent.Action.SHOW_TEXT,' -> 'HoverEvent.ShowText('
        return new HoverEvent.ShowText(component);
    }

    private static ClickEvent getClickEvent(String tabId) {
        //~ if >=1.21.5 'ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,' -> 'ClickEvent.CopyToClipboard('
        return new ClickEvent.CopyToClipboard(tabId);
    }
}
