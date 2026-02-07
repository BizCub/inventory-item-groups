package com.bizcub.inventoryItemGroups;

import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Main {
    public static final String MOD_ID = /*$ mod_id*/ "inventory_item_groups";

    public static ArrayList<Group> groups = new ArrayList<>();

    public static HashMap<CreativeModeTab, Collection<ItemStack>> itemsInTabsMapping = new HashMap<>();
    public static HashMap<String, CreativeModeTab> tabsMapping = new HashMap<>();

    public static boolean tempListChanged;
    public static ArrayList<ItemStack> tempInventoryItemStack = new ArrayList<>();
    public static CreativeModeTab tempSelectedTab;
    public static Group tempGroup;
    public static int tempIndex;
    public static float tempScrollOffs;

    public static void createMapping() {
        CreativeModeTabs.allTabs().forEach(tab ->
            tabsMapping.put(tab.getDisplayName().getString(), tab));
    }

    public static void hideGroups() {
        groups.forEach(group -> {
            group.setVisibility(false);
            group.getItems().forEach(item ->
                group.setItemWithIndex(item, -1));
        });
    }

    public static void itemsChanged(int index) {
        tempListChanged = true;
        tempIndex = index;
        groups.forEach(group -> {
            if (group.getIconIndex() == index)
                tempGroup = group;
        });
    }

    public static Group findGroupByIndex(int index) {
        for (Group group : groups)
            if (group.getItemsWithIndexes().containsValue(index) || group.getIconIndex() == index)
                return group;
        return null;
    }

    public static ArrayList<Group> groupsOnSelectedTab(CreativeModeTab selectedTab) {
        ArrayList<Group> groupsOnSelectedTab = new ArrayList<>();
        groups.forEach(group -> {
            if (selectedTab == tabsMapping.get(group.getTab()))
                groupsOnSelectedTab.add(group);
        });
        return groupsOnSelectedTab;
    }

    public static void setIndexes() {
        ArrayList<Group> groupsOnSelectedTab = groupsOnSelectedTab(tempSelectedTab);
        ArrayList<ItemStack> newStack = new ArrayList<>(tempInventoryItemStack);

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

    public static void createItemsInTabsMapping() {
        if (itemsInTabsMapping.isEmpty()) {
            CreativeModeTabs.allTabs().forEach(creativeModeTab ->
                    itemsInTabsMapping.put(creativeModeTab, creativeModeTab.getDisplayItems())
            );
        }
    }

    public static void createDefaultGroups() {
        createItemsInTabsMapping();
        groups.clear();

        String buildingName = "Building Blocks";
        String coloredName = "Colored Blocks";
        String naturalName = "Natural Blocks";
        String functionalName = "Functional Blocks";
        String redstoneName = "Redstone Blocks";
        String toolName = "Tools & Utilities";
        String combatName = "Combat";
        String foodName = "Food & Drinks";
        String ingredientName = "Ingredients";

        ArrayList<ArrayList<ItemStack>> building = new ArrayList<>();
        ArrayList<ArrayList<ItemStack>> colored = new ArrayList<>();
        ArrayList<ArrayList<ItemStack>> natural = new ArrayList<>();
        ArrayList<ArrayList<ItemStack>> functional = new ArrayList<>();
        ArrayList<ArrayList<ItemStack>> redstone = new ArrayList<>();
        ArrayList<ArrayList<ItemStack>> tool = new ArrayList<>();
        ArrayList<ArrayList<ItemStack>> combat = new ArrayList<>();
        ArrayList<ArrayList<ItemStack>> ingredient = new ArrayList<>();
        ArrayList<ArrayList<ItemStack>> food = new ArrayList<>();
        for (int i = 0; i < 19; i++) building.add(new ArrayList<>());
        for (int i = 0; i < 12; i++) colored.add(new ArrayList<>());
        for (int i = 0; i < 11; i++) natural.add(new ArrayList<>());
        for (int i = 0; i < 17; i++) functional.add(new ArrayList<>());
        for (int i = 0; i < 5; i++) redstone.add(new ArrayList<>());
        for (int i = 0; i < 12; i++) tool.add(new ArrayList<>());
        for (int i = 0; i < 12; i++) combat.add(new ArrayList<>());
        for (int i = 0; i < 7; i++) food.add(new ArrayList<>());
        for (int i = 0; i < 5; i++) ingredient.add(new ArrayList<>());

        itemsInTabsMapping.get(tabsMapping.get(buildingName)).forEach(itemStack -> {
            String item = itemStack.getItem().toString();
            if (item.contains("log") || item.contains("stem") || item.contains("bamboo_block")) {
                if (!item.contains("stripped")) building.get(0).add(itemStack);
                else building.get(1).add(itemStack);
            }
            if (item.contains("wood") || item.contains("hyphae")) {
                if (!item.contains("stripped")) building.get(2).add(itemStack);
                else building.get(3).add(itemStack);
            }
            if (item.contains("stair")) building.get(4).add(itemStack);
            if (item.contains("slab")) building.get(5).add(itemStack);
            if (item.contains("planks") || (item.contains("mosaic"))) building.get(6).add(itemStack);
            if (item.contains("fence_gate")) building.get(7).add(itemStack);
            if (item.contains("fence")) building.get(8).add(itemStack);
            if (item.contains("trapdoor")) building.get(9).add(itemStack);
            if (item.contains("door")) building.get(10).add(itemStack);
            if (item.contains("pressure_plate")) building.get(11).add(itemStack);
            if (item.contains("button")) building.get(12).add(itemStack);
            if (item.contains("bar")) building.get(13).add(itemStack);
            if (item.contains("chain")) building.get(14).add(itemStack);
            if (item.contains("copper")) building.get(15).add(itemStack);
            if (item.contains("wall")) building.get(16).add(itemStack);
            if (item.contains("bricks") || item.contains("chiseled") || item.contains("tiles") || item.contains("polished")) building.get(17).add(itemStack);
            if (item.contains("sandstone")) building.get(18).add(itemStack);
        });
        itemsInTabsMapping.get(tabsMapping.get(coloredName)).forEach(itemStack -> {
            String item = itemStack.getItem().toString();
            if (item.contains("wool")) colored.get(0).add(itemStack);
            if (item.contains("carpet")) colored.get(1).add(itemStack);
            if (item.contains("glazed_terracotta")) colored.get(2).add(itemStack);
            if (item.contains("terracotta")) colored.get(3).add(itemStack);
            if (item.contains("concrete_powder")) colored.get(4).add(itemStack);
            if (item.contains("concrete")) colored.get(5).add(itemStack);
            if (item.contains("glass_pane")) colored.get(6).add(itemStack);
            if (item.contains("glass")) colored.get(7).add(itemStack);
            if (item.contains("shulker_box")) colored.get(8).add(itemStack);
            if (item.contains("candle")) colored.get(9).add(itemStack);
            if (item.contains("banner")) colored.get(10).add(itemStack);
            if (item.contains("bed")) colored.get(11).add(itemStack);
        });
        itemsInTabsMapping.get(tabsMapping.get(naturalName)).forEach(itemStack -> {
            String item = itemStack.getItem().toString();
            if (item.contains("_ore") || item.contains("debris") || item.contains("raw_")) natural.get(0).add(itemStack);
            if (item.contains("mushroom") || item.contains("fungus")) natural.get(1).add(itemStack);
            if (item.contains("sapling") || item.contains("propagule")) natural.get(2).add(itemStack);
            if (item.contains("fern") || item.contains("_grass") || (item.contains("bush") && !item.contains("_bush")) || item.contains("_sprouts") || item.contains("hanging_moss") || item.contains("_vines")) natural.get(3).add(itemStack);
            if (item.contains("seeds") || item.contains("_pod")) natural.get(4).add(itemStack);
            if (item.contains("dandelion") || item.contains("poppy") || item.contains("orchid") || item.contains("allium") || item.contains("tulip") || item.contains("daisy") || item.contains("cornflower") || item.contains("torchflower") || item.contains("azure_bluet") || item.contains("valley") || item.contains("cactus_flower") || item.contains("eyeblossom") || item.contains("rose") || item.contains("petals") || item.contains("wildflower") || item.contains("crimson_roots") || item.contains("warped_roots") || item.contains("sunflower") || item.contains("peony") || item.contains("lilac") || item.contains("pitcher_plant")) natural.get(5).add(itemStack);
            if (item.contains("leaves")) natural.get(6).add(itemStack);
            if (item.contains("coral_block")) natural.get(7).add(itemStack);
            if (item.contains("coral")) natural.get(8).add(itemStack);
            if (item.contains(":stone") || item.contains("diorite") || item.contains("andesite") || item.contains("granite") || item.contains("tuff") || item.contains("basalt") || item.contains("blackstone") || item.contains("deepslate")) natural.get(9).add(itemStack);
            if (item.contains("log") || item.contains("stem")) natural.get(10).add(itemStack);
        });
        itemsInTabsMapping.get(tabsMapping.get(functionalName)).forEach(itemStack -> {
            String item = itemStack.getItem().toString();
            if (item.contains("lantern") && !item.contains("sea")) functional.get(0).add(itemStack);
            if (item.contains("chain")) functional.get(1).add(itemStack);
            if (item.contains("bulb")) functional.get(2).add(itemStack);
            if (item.contains("anvil")) functional.get(3).add(itemStack);
            if (item.contains("lightning_rod")) functional.get(4).add(itemStack);
            if (item.contains("_shelf")) functional.get(5).add(itemStack);
            if (item.contains("hanging_sign")) functional.get(6).add(itemStack);
            if (item.contains("sign")) functional.get(7).add(itemStack);
            if (item.contains("chest")) functional.get(8).add(itemStack);
            if (item.contains("shulker_box")) functional.get(9).add(itemStack);
            if (item.contains("_bed")) functional.get(10).add(itemStack);
            if (item.contains("candle")) functional.get(11).add(itemStack);
            if (item.contains("banner")) functional.get(12).add(itemStack);
            if (item.contains("head") || item.contains("skull")) functional.get(13).add(itemStack);
            if (item.contains("golem_statue")) functional.get(14).add(itemStack);
            if (item.contains("infested")) functional.get(15).add(itemStack);
            if (item.contains("painting")) functional.get(16).add(itemStack);
        });
        itemsInTabsMapping.get(tabsMapping.get(redstoneName)).forEach(itemStack -> {
            String item = itemStack.getItem().toString();
            if (item.contains("bulb")) redstone.get(0).add(itemStack);
            if (item.contains("pressure_plate")) redstone.get(1).add(itemStack);
            if (item.contains("minecart") || item.contains("boat") || item.contains("_raft")) redstone.get(2).add(itemStack);
            if (item.contains("chest")) redstone.get(3).add(itemStack);
            if (item.contains("rail")) redstone.get(4).add(itemStack);
        });
        itemsInTabsMapping.get(tabsMapping.get(toolName)).forEach(itemStack -> {
            String item = itemStack.getItem().toString();
            if (item.contains("shovel")) tool.get(0).add(itemStack);
            if (item.contains("pickaxe")) tool.get(1).add(itemStack);
            if (item.contains("axe")) tool.get(2).add(itemStack);
            if (item.contains("hoe")) tool.get(3).add(itemStack);
            if (item.contains("bundle")) tool.get(4).add(itemStack);
            if (item.contains("harness")) tool.get(5).add(itemStack);
            if (item.contains("chest_boat") || item.contains("chest_raft")) tool.get(6).add(itemStack);
            if (item.contains("boat") || item.contains("_raft")) tool.get(7).add(itemStack);
            if (item.contains("rail")) tool.get(8).add(itemStack);
            if (item.contains("minecart")) tool.get(9).add(itemStack);
            if (item.contains("disc")) tool.get(10).add(itemStack);
            if (item.contains("goat_horn")) tool.get(11).add(itemStack);
        });
        itemsInTabsMapping.get(tabsMapping.get(combatName)).forEach(itemStack -> {
            String item = itemStack.getItem().toString();
            if (item.contains("sword")) combat.get(0).add(itemStack);
            if (item.contains("spear")) combat.get(1).add(itemStack);
            if (item.contains("axe")) combat.get(2).add(itemStack);
            if (item.contains("helmet")) combat.get(3).add(itemStack);
            if (item.contains("chestplate")) combat.get(4).add(itemStack);
            if (item.contains("leggings")) combat.get(5).add(itemStack);
            if (item.contains("boots")) combat.get(6).add(itemStack);
            if (item.contains("horse_armor")) combat.get(7).add(itemStack);
            if (item.contains("nautilus_armor")) combat.get(8).add(itemStack);
            if (item.contains("egg")) combat.get(9).add(itemStack);
            if (item.contains("tipped_arrow")) combat.get(10).add(itemStack);
            if (item.contains("firework_rocket")) combat.get(11).add(itemStack);
        });
        itemsInTabsMapping.get(tabsMapping.get(foodName)).forEach(itemStack -> {
            String item = itemStack.getItem().toString();
            if (item.contains("suspicious_stew")) food.get(0).add(itemStack);
            if (item.contains("ominous_bottle")) food.get(1).add(itemStack);
            if (item.contains("splash_potion")) food.get(2).add(itemStack);
            if (item.contains("lingering_potion")) food.get(3).add(itemStack);
            if (item.contains("potion")) food.get(4).add(itemStack);
            if (item.contains("cooked")) food.get(5).add(itemStack);
            if ((item.contains("beef") || item.contains("porkchop") || item.contains("mutton") || item.contains("chicken") || item.contains("rabbit") || item.contains(":cod") || item.contains("salmon")) && !item.contains("rabbit_")) food.get(6).add(itemStack);
        });
        itemsInTabsMapping.get(tabsMapping.get(ingredientName)).forEach(itemStack -> {
            String item = itemStack.getItem().toString();
            if (item.contains("dye")) ingredient.get(0).add(itemStack);
            if (item.contains("banner_pattern")) ingredient.get(1).add(itemStack);
            if (item.contains("pottery_sherd")) ingredient.get(2).add(itemStack);
            if (item.contains("smithing_template")) ingredient.get(3).add(itemStack);
            if (item.contains("enchanted_book")) ingredient.get(4).add(itemStack);
        });

        building.forEach(itemStacks -> groups.add(new Group(buildingName, itemStacks)));
        colored.forEach(itemStacks -> groups.add(new Group(coloredName, itemStacks)));
        natural.forEach(itemStacks -> groups.add(new Group(naturalName, itemStacks)));
        functional.forEach(itemStacks -> groups.add(new Group(functionalName, itemStacks)));
        redstone.forEach(itemStacks -> groups.add(new Group(redstoneName, itemStacks)));
        tool.forEach(itemStacks -> groups.add(new Group(toolName, itemStacks)));
        combat.forEach(itemStacks -> groups.add(new Group(combatName, itemStacks)));
        food.forEach(itemStacks -> groups.add(new Group(foodName, itemStacks)));
        ingredient.forEach(itemStacks -> groups.add(new Group(ingredientName, itemStacks)));

        validateGroups();
    }

    private static void validateGroups() {
        groups.removeIf(group -> group.getItems().isEmpty());
        groups.removeIf(group -> group.getItems().size() < 3);
    }
}
