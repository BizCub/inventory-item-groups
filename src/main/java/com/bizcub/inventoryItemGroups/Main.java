package com.bizcub.inventoryItemGroups;

import com.bizcub.inventoryItemGroups.config.Compat;
import com.bizcub.inventoryItemGroups.config.Configs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import java.util.*;

public class Main {
    public static final String MOD_ID = /*$ mod_id*/ "inventory_item_groups";

    public static ArrayList<Group> groups = new ArrayList<>();
    public static CreativeModeTab selectedTab;
    public static GroupVisibilityToggle itemsChanged = new GroupVisibilityToggle();
    public static ArrayList<ItemStack> tempItemStacks = new ArrayList<>();

    public static ArrayList<RawGroup> rawDefaultGroups = new ArrayList<>();

    public static Group findGroupByIndex(int index) {
        for (Group group : groups)
            for (HashMap<ItemStack, Integer> itemStacksMap : group.getItemsWithIndexes())
                if (itemStacksMap.containsValue(index) || group.getIconIndex() == index)
                    return group;
        return null;
    }

    public static ArrayList<Group> groupsOnSelectedTab(CreativeModeTab selectedTab) {
        ArrayList<Group> groupsOnSelectedTab = new ArrayList<>();
        groups.forEach(group -> {
            if (selectedTab.equals(group.getTab()))
                groupsOnSelectedTab.add(group);
        });
        return groupsOnSelectedTab;
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

    public static void createGroups() {
        rawDefaultGroups.clear();
        groups.clear();

        if (Compat.isClothConfigLoaded() && Configs.load().addGroupsOverOld
                || !Compat.isClothConfigLoaded()) Main.createDefaultGroups();

        if (Compat.isClothConfigLoaded()) {
            for (Configs.ItemGroup group : Configs.load().groups) {
                List<String> tempListOfItems = group.itemNames.stream().map(Object::toString).toList();
                if (convertComponentToId(selectedTab.getDisplayName().getContents().toString()).equals(group.tabName))
                    addItems(group.groupName, false, tempListOfItems);
            }
        }

        rawDefaultGroups.forEach(rawGroup -> groups.add(new Group(getGroupTranslate(rawGroup), selectedTab, rawGroup.items)));
        validateGroups();
    }

    public static Component getGroupTranslate(RawGroup rawGroup) {
        if (rawGroup.name == null) rawGroup.name = "name";

        if ((Compat.isClothConfigLoaded() && Configs.load().translateGroups) || rawGroup.hasTranslation) {
            return Component.translatable("groupName.inventory_item_groups." + rawGroup.name);
        } else {
            return Component.literal(rawGroup.name);
        }
    }

    public static void createDefaultGroups() {
        String selectedTabId = convertComponentToId(selectedTab.getDisplayName().getContents().toString());
        if (selectedTabId.equals("buildingBlocks")) {
            addItems("logs", true, List.of("log", "stem", "bamboo_block"), List.of("stripped"));
            addItems("woods", true, List.of("wood", "hyphae"), List.of("stripped"));
            addItems("stripped_logs", true, List.of("log", "stem", "bamboo_block"));
            addItems("stripped_woods", true, List.of("wood", "hyphae"));
            addItems("stairs", true, List.of("stair"));
            addItems("slabs", true, List.of("slab"));
            addItems("planks", true, List.of("planks", "mosaic"));
            addItems("fence_gates", true, List.of("fence_gate"));
            addItems("fences", true, List.of("fence"));
            addItems("trapdoors", true, List.of("trapdoor"));
            addItems("doors", true, List.of("door"));
            addItems("pressure_plates", true, List.of("pressure_plate"));
            addItems("buttons", true, List.of("button"));
            addItems("bars", true, List.of("bar"));
            addItems("chains", true, List.of("chain"));
            addItems("copper", true, List.of("copper"));
            addItems("walls", true, List.of("wall"));
            addItems("decorative_stone", true, List.of("bricks", "chiseled", "tiles", "polished"));
            addItems("sandstone", true, List.of("sandstone"));
        }
        if (selectedTabId.equals("coloredBlocks")) {
            addItems("wool", true, List.of("wool"));
            addItems("carpets", true, List.of("carpet"));
            addItems("glazed_terracotta", true, List.of("glazed_terracotta"));
            addItems("terracotta", true, List.of("terracotta"));
            addItems("concrete_powder", true, List.of("concrete_powder"));
            addItems("concrete", true, List.of("concrete"));
            addItems("glass_panes", true, List.of("glass_pane"));
            addItems("glass", true, List.of("glass"));
            addItems("shulker_boxes", true, List.of("shulker_box"));
            addItems("candles", true, List.of("candle"));
            addItems("banners", true, List.of("banner"));
            addItems("beds", true, List.of("bed"));
        }
        if (selectedTabId.equals("natural")) {
            addItems("ores", true, List.of("_ore", "debris", "raw_"));
            addItems("mushrooms", true, List.of("mushroom", "fungus"));
            addItems("saplings", true, List.of("sapling", "propagule"));
            addItems("ground_cover", true, List.of("fern", "_grass", "bush", "_sprouts", "hanging_moss", "_vines"), List.of("_bush"));
            addItems("seeds", true, List.of("seeds", "_pod"));
            addItems("flowers", true, List.of("dandelion", "poppy", "orchid", "allium", "tulip", "daisy", "cornflower", "torchflower", "azure_bluet", "valley", "cactus_flower", "eyeblossom", "rose", "petals", "wildflower", "crimson_roots", "warped_roots", "sunflower", "peony", "lilac", "pitcher_plant"));
            addItems("leaves", true, List.of("leaves"));
            addItems("coral_blocks", true, List.of("coral_block"));
            addItems("coral_decorations", true, List.of("coral"));
            addItems("stone", true, List.of(":stone", "diorite", "andesite", "granite", "tuff", "basalt", "blackstone", "deepslate"));
            addItems("logs", true, List.of("log", "stem"));
        }
        if (selectedTabId.equals("functional")) {
            addItems("lanterns", true, List.of("lantern"), List.of("sea"));
            addItems("chains", true, List.of("chain"));
            addItems("bulbs", true, List.of("bulb"));
            addItems("anvils", true, List.of("anvil"));
            addItems("lightning_rods", true, List.of("lightning_rod"));
            addItems("shelves", true, List.of("_shelf"));
            addItems("hanging_signs", true, List.of("hanging_sign"));
            addItems("signs", true, List.of("sign"));
            addItems("chests", true, List.of("chest"));
            addItems("shulker_boxes", true, List.of("shulker_box"));
            addItems("beds", true, List.of("_bed"));
            addItems("candles", true, List.of("candle"));
            addItems("banners", true, List.of("banner"));
            addItems("skulls", true, List.of("head", "skull"));
            addItems("golem_statues", true, List.of("golem_statue"));
            addItems("infested_stone", true, List.of("infested"));
            addItems("paintings", true, List.of("painting"));
        }
        if (selectedTabId.equals("redstone")) {
            addItems("bulbs", true, List.of("bulb"));
            addItems("pressure_plates", true, List.of("pressure_plate"));
            addItems("transport", true, List.of("minecart", "boat", "_raft"));
            addItems("chests", true, List.of("chest"));
            addItems("rails", true, List.of("rail"));
        }
        if (selectedTabId.equals("tools")) {
            addItems("shovels", true, List.of("shovel"));
            addItems("pickaxes", true, List.of("pickaxe"));
            addItems("axes", true, List.of("axe"));
            addItems("hoes", true, List.of("hoe"));
            addItems("bundles", true, List.of("bundle"));
            addItems("harnesses", true, List.of("harness"));
            addItems("chest_boats", true, List.of("chest_boat", "chest_raft"));
            addItems("boats", true, List.of("boat", "_raft"));
            addItems("rails", true, List.of("rail"));
            addItems("minecarts", true, List.of("minecart"));
            addItems("discs", true, List.of("disc"));
            addItems("goat_horns", true, List.of("goat_horn"));
        }
        if (selectedTabId.equals("combat")) {
            addItems("swords", true, List.of("sword"));
            addItems("spears", true, List.of("spear"));
            addItems("axes", true, List.of("axe"));
            addItems("helmets", true, List.of("helmet"));
            addItems("chestplates", true, List.of("chestplate"));
            addItems("leggings", true, List.of("leggings"));
            addItems("boots", true, List.of("boots"));
            addItems("horse_armor", true, List.of("horse_armor"));
            addItems("nautilus_armor", true, List.of("nautilus_armor"));
            addItems("eggs", true, List.of("egg"));
            addItems("tipped_arrows", true, List.of("tipped_arrow"));
            addItems("firework_rockets", true, List.of("firework_rocket"));
        }
        if (selectedTabId.equals("foodAndDrink")) {
            addItems("suspicious_stews", true, List.of("suspicious_stew"));
            addItems("ominous_bottles", true, List.of("ominous_bottle"));
            addItems("splash_potions", true, List.of("splash_potion"));
            addItems("lingering_potions", true, List.of("lingering_potion"));
            addItems("potions", true, List.of("potion"));
            addItems("cooked_food", true, List.of("cooked"));
            addItems("raw_food", true, List.of("beef", "porkchop", "mutton", "chicken", "rabbit", ":cod", "salmon"), List.of("rabbit_"));
        }
        if (selectedTabId.equals("ingredients")) {
            addItems("dyes", true, List.of("dye"));
            addItems("banner_patterns", true, List.of("banner_pattern"));
            addItems("pottery_sherds", true, List.of("pottery_sherd"));
            addItems("smithing_templates", true, List.of("smithing_template"));
            addItems("enchanted_books", true, List.of("enchanted_book"));
        }
    }

    private static void addItems(String groupName, boolean hasTranslation, List<String> containedItems) {
        addItems(groupName, hasTranslation, containedItems, List.of());
    }

    private static void addItems(String groupName, boolean hasTranslation, List<String> containedItems, List<String> nonContainedItems) {
        rawDefaultGroups.add(new RawGroup());
        RawGroup rawGroup = rawDefaultGroups.get(rawDefaultGroups.size()-1);
        if (nonContainedItems.isEmpty()) nonContainedItems = List.of("1111111");

        for (ItemStack itemStack : selectedTab.getDisplayItems()) {
            String itemName = itemStack.getItem().toString();
            boolean flag = false;

            for (String containedItem : containedItems) {
                for (String nonContainedItem : nonContainedItems) {
                    if (itemName.contains(containedItem) && !itemName.contains(nonContainedItem)) {
                        rawGroup.items.add(itemStack);
                        rawGroup.name = groupName;
                        rawGroup.hasTranslation = hasTranslation;
                        flag = true;
                        break;
                    }
                }
                if (flag) break;
            }
        }
    }

    private static void validateGroups() {
        groups.removeIf(group -> group.getItems().isEmpty());
        groups.removeIf(group -> group.getItems().size() < 3);
    }

    public static String convertComponentToId(String tabId) {
        int firstIndex = tabId.indexOf("'");
        if (tabId.startsWith("key=", firstIndex - 4)) {
            tabId = tabId.substring(firstIndex + 1);
            tabId = tabId.substring(0, tabId.indexOf("'"));
            tabId = tabId.substring(tabId.lastIndexOf(".") + 1);
        }
        return tabId;
    }
}
