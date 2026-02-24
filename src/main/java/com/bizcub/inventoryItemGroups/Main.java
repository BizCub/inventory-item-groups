package com.bizcub.inventoryItemGroups;

import com.bizcub.inventoryItemGroups.config.Compat;
import com.bizcub.inventoryItemGroups.config.Configs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import java.util.*;

public class Main {
    public static final String MOD_ID = /*$ mod_id*/ "inventory_item_groups";

    public static ArrayList<Group> groups = new ArrayList<>();
    public static ArrayList<RawGroup> rawDefaultGroups = new ArrayList<>();
    public static CreativeModeTab selectedTab;
    public static GroupVisibilityToggle itemsChanged = new GroupVisibilityToggle();
    public static ArrayList<ItemStack> tempItemStacks = new ArrayList<>();

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

    private static void addDefaultItems(String groupName, List<String> containedItems) {
        addItems(groupName, containedItems, List.of(), List.of(), true);
    }

    private static void addDefaultItems(String groupName, List<String> containedItems, List<String> nonContainedItems) {
        addItems(groupName, containedItems, nonContainedItems, List.of(), true);
    }

    private static void addConfigItems(String groupName, List<String> containedItems, List<String> equivalentItems, boolean hasTranslation) {
        addItems(groupName, containedItems, List.of(), equivalentItems, hasTranslation);
    }

    private static void addItems(String groupName, List<String> containedItems, List<String> nonContainedItems, List<String> equivalentItems, boolean hasTranslation) {
        rawDefaultGroups.add(new RawGroup());
        RawGroup rawGroup = rawDefaultGroups.get(rawDefaultGroups.size()-1);
        if (nonContainedItems.isEmpty()) nonContainedItems = List.of("1111111");

        for (ItemStack itemStack : selectedTab.getDisplayItems()) {
            String itemName = itemStack.getItem().toString();
            String itemNameWithoutNamespace = itemName.contains(":") ? itemName.split(":")[1] : itemName;
            boolean flag = false;

            for (String containedItem : containedItems) {
                for (String nonContainedItem : nonContainedItems) {
                    if (itemName.contains(containedItem) && !itemName.contains(nonContainedItem)) {
                        addRawGroup(rawGroup, groupName, itemStack, hasTranslation);
                        flag = true;
                        break;
                    }
                }
                if (flag) break;
            }

            for (String equivalentItem : equivalentItems) {
                if (equivalentItem.equals(itemNameWithoutNamespace)) {
                    addRawGroup(rawGroup, groupName, itemStack, hasTranslation);
                    break;
                }
            }
        }
    }

    private static void addRawGroup(RawGroup rawGroup, String groupName, ItemStack itemStack, boolean hasTranslation) {
        rawGroup.items.add(itemStack);
        rawGroup.name = groupName;
        rawGroup.hasTranslation = hasTranslation;
    }

    public static void createGroups() {
        rawDefaultGroups.clear();
        groups.clear();

        if (Compat.isClothConfigLoaded() && Configs.getConfig().addGroupsOverOld
                || !Compat.isClothConfigLoaded()) createDefaultGroups();

        if (Compat.isClothConfigLoaded()) {
            for (Configs.ItemGroup group : Configs.getConfig().groups) {
                List<String> tempListOfItems = group.containedItems.stream().map(Object::toString).toList();
                List<String> tempListOfItemIds = group.equivalentItems.stream().map(Object::toString).toList();
                if (convertComponentToId(selectedTab.getDisplayName().getContents().toString()).equals(group.tabName))
                    addConfigItems(group.groupName, tempListOfItems, tempListOfItemIds, Configs.getConfig().translateGroups);
            }
        }

        rawDefaultGroups.forEach(rawGroup -> groups.add(new Group(getGroupTranslate(rawGroup), selectedTab, rawGroup.items)));
        validateGroups();
    }

    public static void createDefaultGroups() {
        String selectedTabId = convertComponentToId(selectedTab.getDisplayName().getContents().toString());
        if (selectedTabId.equals("buildingBlocks")) {
            addDefaultItems("logs", List.of("log", "stem", "bamboo_block"), List.of("stripped"));
            addDefaultItems("woods", List.of("wood", "hyphae"), List.of("stripped"));
            addDefaultItems("stripped_logs", List.of("log", "stem", "bamboo_block"));
            addDefaultItems("stripped_woods", List.of("wood", "hyphae"));
            addDefaultItems("stairs", List.of("stair"));
            addDefaultItems("slabs", List.of("slab"));
            addDefaultItems("planks", List.of("planks", "mosaic"));
            addDefaultItems("fence_gates", List.of("fence_gate"));
            addDefaultItems("fences", List.of("fence"));
            addDefaultItems("trapdoors", List.of("trapdoor"));
            addDefaultItems("doors", List.of("door"));
            addDefaultItems("pressure_plates", List.of("pressure_plate"));
            addDefaultItems("buttons", List.of("button"));
            addDefaultItems("bars", List.of("bar"));
            addDefaultItems("chains", List.of("chain"));
            addDefaultItems("copper", List.of("copper"));
            addDefaultItems("walls", List.of("wall"));
            addDefaultItems("decorative_stone", List.of("bricks", "chiseled", "tiles", "polished"));
            addDefaultItems("sandstone", List.of("sandstone"));
        }
        if (selectedTabId.equals("coloredBlocks")) {
            addDefaultItems("wool", List.of("wool"));
            addDefaultItems("carpets", List.of("carpet"));
            addDefaultItems("glazed_terracotta", List.of("glazed_terracotta"));
            addDefaultItems("terracotta", List.of("terracotta"));
            addDefaultItems("concrete_powder", List.of("concrete_powder"));
            addDefaultItems("concrete", List.of("concrete"));
            addDefaultItems("glass_panes", List.of("glass_pane"));
            addDefaultItems("glass", List.of("glass"));
            addDefaultItems("shulker_boxes", List.of("shulker_box"));
            addDefaultItems("candles", List.of("candle"));
            addDefaultItems("banners", List.of("banner"));
            addDefaultItems("beds", List.of("bed"));
        }
        if (selectedTabId.equals("natural")) {
            addDefaultItems("ores", List.of("_ore", "debris", "raw_"));
            addDefaultItems("mushrooms", List.of("mushroom", "fungus"));
            addDefaultItems("saplings", List.of("sapling", "propagule"));
            addDefaultItems("ground_cover", List.of("fern", "_grass", "bush", "_sprouts", "hanging_moss", "_vines"), List.of("_bush"));
            addDefaultItems("seeds", List.of("seeds", "_pod"));
            addDefaultItems("flowers", List.of("dandelion", "poppy", "orchid", "allium", "tulip", "daisy", "cornflower", "torchflower", "azure_bluet", "valley", "cactus_flower", "eyeblossom", "rose", "petals", "wildflower", "crimson_roots", "warped_roots", "sunflower", "peony", "lilac", "pitcher_plant"));
            addDefaultItems("leaves", List.of("leaves"));
            addDefaultItems("coral_blocks", List.of("coral_block"));
            addDefaultItems("coral_decorations", List.of("coral"));
            addDefaultItems("stone", List.of(":stone", "diorite", "andesite", "granite", "tuff", "basalt", "blackstone", "deepslate"));
            addDefaultItems("logs", List.of("log", "stem"));
        }
        if (selectedTabId.equals("functional")) {
            addDefaultItems("lanterns", List.of("lantern"), List.of("sea"));
            addDefaultItems("chains", List.of("chain"));
            addDefaultItems("bulbs", List.of("bulb"));
            addDefaultItems("anvils", List.of("anvil"));
            addDefaultItems("lightning_rods", List.of("lightning_rod"));
            addDefaultItems("shelves", List.of("_shelf"));
            addDefaultItems("hanging_signs", List.of("hanging_sign"));
            addDefaultItems("signs", List.of("sign"));
            addDefaultItems("chests", List.of("chest"));
            addDefaultItems("shulker_boxes", List.of("shulker_box"));
            addDefaultItems("beds", List.of("_bed"));
            addDefaultItems("candles", List.of("candle"));
            addDefaultItems("banners", List.of("banner"));
            addDefaultItems("skulls", List.of("head", "skull"));
            addDefaultItems("golem_statues", List.of("golem_statue"));
            addDefaultItems("infested_stone", List.of("infested"));
            addDefaultItems("paintings", List.of("painting"));
        }
        if (selectedTabId.equals("redstone")) {
            addDefaultItems("bulbs", List.of("bulb"));
            addDefaultItems("pressure_plates", List.of("pressure_plate"));
            addDefaultItems("transport", List.of("minecart", "boat", "_raft"));
            addDefaultItems("chests", List.of("chest"));
            addDefaultItems("rails", List.of("rail"));
        }
        if (selectedTabId.equals("tools")) {
            addDefaultItems("shovels", List.of("shovel"));
            addDefaultItems("pickaxes", List.of("pickaxe"));
            addDefaultItems("axes", List.of("axe"));
            addDefaultItems("hoes", List.of("hoe"));
            addDefaultItems("bundles", List.of("bundle"));
            addDefaultItems("harnesses", List.of("harness"));
            addDefaultItems("chest_boats", List.of("chest_boat", "chest_raft"));
            addDefaultItems("boats", List.of("boat", "_raft"));
            addDefaultItems("rails", List.of("rail"));
            addDefaultItems("minecarts", List.of("minecart"));
            addDefaultItems("discs", List.of("disc"));
            addDefaultItems("goat_horns", List.of("goat_horn"));
        }
        if (selectedTabId.equals("combat")) {
            addDefaultItems("swords", List.of("sword"));
            addDefaultItems("spears", List.of("spear"));
            addDefaultItems("axes", List.of("axe"));
            addDefaultItems("helmets", List.of("helmet"));
            addDefaultItems("chestplates", List.of("chestplate"));
            addDefaultItems("leggings", List.of("leggings"));
            addDefaultItems("boots", List.of("boots"));
            addDefaultItems("horse_armor", List.of("horse_armor"));
            addDefaultItems("nautilus_armor", List.of("nautilus_armor"));
            addDefaultItems("eggs", List.of("egg"));
            addDefaultItems("tipped_arrows", List.of("tipped_arrow"));
            addDefaultItems("firework_rockets", List.of("firework_rocket"));
        }
        if (selectedTabId.equals("foodAndDrink")) {
            addDefaultItems("suspicious_stews", List.of("suspicious_stew"));
            addDefaultItems("ominous_bottles", List.of("ominous_bottle"));
            addDefaultItems("splash_potions", List.of("splash_potion"));
            addDefaultItems("lingering_potions", List.of("lingering_potion"));
            addDefaultItems("potions", List.of("potion"));
            addDefaultItems("cooked_food", List.of("cooked"));
            addDefaultItems("raw_food", List.of("beef", "porkchop", "mutton", "chicken", "rabbit", ":cod", "salmon"), List.of("rabbit_"));
        }
        if (selectedTabId.equals("ingredients")) {
            addDefaultItems("dyes", List.of("dye"));
            addDefaultItems("banner_patterns", List.of("banner_pattern"));
            addDefaultItems("pottery_sherds", List.of("pottery_sherd"));
            addDefaultItems("smithing_templates", List.of("smithing_template"));
            addDefaultItems("enchanted_books", List.of("enchanted_book"));
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

    public static Component getGroupTranslate(RawGroup rawGroup) {
        if (rawGroup.name == null) rawGroup.name = "name";

        if ((Compat.isClothConfigLoaded() && Configs.getConfig().translateGroups) || rawGroup.hasTranslation) {
            return Component.translatable("groupName.inventory_item_groups." + rawGroup.name);
        } else {
            return Component.literal(rawGroup.name);
        }
    }
}
