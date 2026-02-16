package com.bizcub.inventoryItemGroups;

import com.bizcub.inventoryItemGroups.config.Configs;
import net.minecraft.world.item.*;

import java.util.*;

public class Main {
    public static final String MOD_ID = /*$ mod_id*/ "inventory_item_groups";

    public static ArrayList<Group> groups = new ArrayList<>();
    public static CreativeModeTab selectedTab;
    public static GroupVisibilityToggle itemsChanged = new GroupVisibilityToggle();
    public static ArrayList<ItemStack> tempItemStacks = new ArrayList<>();

    public static ArrayList<ArrayList<ItemStack>> rawDefaultGroups = new ArrayList<>();

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

        if (Configs.load().addGroupsOverOld) Main.createDefaultGroups();

        for (Configs.ItemGroup group : Configs.load().groups) {
            List<String> tempListOfItems = group.itemNames.stream().map(Object::toString).toList();
            if (convertComponentToId(selectedTab.getDisplayName().getContents().toString()).equals(group.tabName))
                addItems(tempListOfItems);
        }

        rawDefaultGroups.forEach(itemStacks -> groups.add(new Group(selectedTab, itemStacks)));
        validateGroups();
    }

    public static void createDefaultGroups() {
        String selectedTabId = convertComponentToId(selectedTab.getDisplayName().getContents().toString());
        if (selectedTabId.equals("buildingBlocks")) {
            addItems(List.of("log", "stem", "bamboo_block"), List.of("stripped"));
            addItems(List.of("wood", "hyphae"), List.of("stripped"));
            addItems(List.of("log", "stem", "bamboo_block"));
            addItems(List.of("wood", "hyphae"));
            addItems(List.of("stair"));
            addItems(List.of("slab"));
            addItems(List.of("planks", "mosaic"));
            addItems(List.of("fence_gate"));
            addItems(List.of("fence"));
            addItems(List.of("trapdoor"));
            addItems(List.of("door"));
            addItems(List.of("pressure_plate"));
            addItems(List.of("button"));
            addItems(List.of("bar"));
            addItems(List.of("chain"));
            addItems(List.of("copper"));
            addItems(List.of("wall"));
            addItems(List.of("bricks", "chiseled", "tiles", "polished"));
            addItems(List.of("sandstone"));
        }
        if (selectedTabId.equals("coloredBlocks")) {
            addItems(List.of("wool"));
            addItems(List.of("carpet"));
            addItems(List.of("glazed_terracotta"));
            addItems(List.of("terracotta"));
            addItems(List.of("concrete_powder"));
            addItems(List.of("concrete"));
            addItems(List.of("glass_pane"));
            addItems(List.of("glass"));
            addItems(List.of("shulker_box"));
            addItems(List.of("candle"));
            addItems(List.of("banner"));
            addItems(List.of("bed"));
        }
        if (selectedTabId.equals("natural")) {
            addItems(List.of("_ore", "debris", "raw_"));
            addItems(List.of("mushroom", "fungus"));
            addItems(List.of("sapling", "propagule"));
            addItems(List.of("fern", "_grass", "bush", "_sprouts", "hanging_moss", "_vines"), List.of("_bush"));
            addItems(List.of("seeds", "_pod"));
            addItems(List.of("dandelion", "poppy", "orchid", "allium", "tulip", "daisy", "cornflower", "torchflower", "azure_bluet", "valley", "cactus_flower", "eyeblossom", "rose", "petals", "wildflower", "crimson_roots", "warped_roots", "sunflower", "peony", "lilac", "pitcher_plant"));
            addItems(List.of("leaves"));
            addItems(List.of("coral_block"));
            addItems(List.of("coral"));
            addItems(List.of(":stone", "diorite", "andesite", "granite", "tuff", "basalt", "blackstone", "deepslate"));
            addItems(List.of("log", "stem"));
        }
        if (selectedTabId.equals("functional")) {
            addItems(List.of("lantern", "sea"));
            addItems(List.of("chain"));
            addItems(List.of("bulb"));
            addItems(List.of("anvil"));
            addItems(List.of("lightning_rod"));
            addItems(List.of("_shelf"));
            addItems(List.of("hanging_sign"));
            addItems(List.of("sign"));
            addItems(List.of("chest"));
            addItems(List.of("shulker_box"));
            addItems(List.of("_bed"));
            addItems(List.of("candle"));
            addItems(List.of("banner"));
            addItems(List.of("head", "skull"));
            addItems(List.of("golem_statue"));
            addItems(List.of("infested"));
            addItems(List.of("painting"));
        }
        if (selectedTabId.equals("redstone")) {
            addItems(List.of("bulb"));
            addItems(List.of("pressure_plate"));
            addItems(List.of("minecart", "boat", "_raft"));
            addItems(List.of("chest"));
            addItems(List.of("rail"));
        }
        if (selectedTabId.equals("tools")) {
            addItems(List.of("shovel"));
            addItems(List.of("pickaxe"));
            addItems(List.of("axe"));
            addItems(List.of("hoe"));
            addItems(List.of("bundle"));
            addItems(List.of("harness"));
            addItems(List.of("chest_boat", "chest_raft"));
            addItems(List.of("boat", "_raft"));
            addItems(List.of("rail"));
            addItems(List.of("minecart"));
            addItems(List.of("disc"));
            addItems(List.of("goat_horn"));
        }
        if (selectedTabId.equals("combat")) {
            addItems(List.of("sword"));
            addItems(List.of("spear"));
            addItems(List.of("axe"));
            addItems(List.of("helmet"));
            addItems(List.of("chestplate"));
            addItems(List.of("leggings"));
            addItems(List.of("boots"));
            addItems(List.of("horse_armor"));
            addItems(List.of("nautilus_armor"));
            addItems(List.of("egg"));
            addItems(List.of("tipped_arrow"));
            addItems(List.of("firework_rocket"));
        }
        if (selectedTabId.equals("foodAndDrink")) {
            addItems(List.of("suspicious_stew"));
            addItems(List.of("ominous_bottle"));
            addItems(List.of("splash_potion"));
            addItems(List.of("lingering_potion"));
            addItems(List.of("potion"));
            addItems(List.of("cooked"));
            addItems(List.of("beef", "porkchop", "mutton", "chicken", "rabbit", ":cod", "salmon"), List.of("rabbit_"));
        }
        if (selectedTabId.equals("ingredients")) {
            addItems(List.of("dye"));
            addItems(List.of("banner_pattern"));
            addItems(List.of("pottery_sherd"));
            addItems(List.of("smithing_template"));
            addItems(List.of("enchanted_book"));
        }
    }

    private static void addItems(List<String> containedItems) {
        addItemsToList(containedItems, List.of());
    }

    private static void addItems(List<String> containedItems, List<String> nonContainedItems) {
        addItemsToList(containedItems, nonContainedItems);
    }

    private static void addItemsToList(List<String> containedItems, List<String> nonContainedItems) {
        rawDefaultGroups.add(new ArrayList<>());
        if (nonContainedItems.isEmpty()) nonContainedItems = List.of("1111111");

        for (ItemStack itemStack : selectedTab.getDisplayItems()) {
            String itemName = itemStack.getItem().toString();
            boolean flag = false;

            for (String containedItem : containedItems) {
                for (String nonContainedItem : nonContainedItems) {
                    if (itemName.contains(containedItem) && !itemName.contains(nonContainedItem)) {
                        rawDefaultGroups.get(rawDefaultGroups.size()-1).add(itemStack);
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
