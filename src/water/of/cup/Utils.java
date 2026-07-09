package water.of.cup;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.map.MapPalette;
import org.bukkit.util.Vector;

public class Utils {

	static Map<Material, Color> blocksMap = new HashMap<Material, Color>();
	static Map<EntityType, Color> animalColorMap = new HashMap<EntityType, Color>();
	// Caches average-texture colors we already computed from the resource pack,
	// so we don't re-scan the same PNG on every single pixel/frame.
	private static Map<Material, Color> textureColorCache = new HashMap<>();

	public static void loadColors() {
		loadBlockColors();
		loadAnimalColors();
	}

	private static void loadBlockColors() {
		// --- classic terrain / building blocks (kept from the original list) ---
		blocksMap.put(Material.SHORT_GRASS, new Color(52,88,28));
		blocksMap.put(Material.TALL_GRASS, new Color(52,88,28));
		blocksMap.put(Material.LARGE_FERN, new Color(48,84,30));
		blocksMap.put(Material.FERN, new Color(48,84,30));
		blocksMap.put(Material.COBBLESTONE, new Color(130,130,130));
		blocksMap.put(Material.COBBLESTONE_STAIRS, new Color(130,130,130));
		blocksMap.put(Material.COBBLESTONE_SLAB, new Color(130,130,130));
		blocksMap.put(Material.FURNACE, new Color(130,130,130));
		blocksMap.put(Material.STONE, new Color(100,100,102));
		blocksMap.put(Material.STONE_SLAB, new Color(117,117,117));
		blocksMap.put(Material.IRON_ORE, new Color(140,130,120));
		blocksMap.put(Material.GOLD_ORE, new Color(150,135,90));
		blocksMap.put(Material.REDSTONE_ORE, new Color(140,100,95));
		blocksMap.put(Material.DIAMOND_ORE, new Color(120,150,150));
		blocksMap.put(Material.COAL_ORE, new Color(90,90,90));
		blocksMap.put(Material.EMERALD_ORE, new Color(110,150,120));
		blocksMap.put(Material.LAPIS_ORE, new Color(100,110,140));
		blocksMap.put(Material.IRON_BLOCK, new Color(236,236,236));
		blocksMap.put(Material.GOLD_BLOCK, new Color(243,223,75));
		blocksMap.put(Material.REDSTONE_BLOCK, new Color(196,25,16));
		blocksMap.put(Material.DIAMOND_BLOCK, new Color(95,233,217));
		blocksMap.put(Material.COAL_BLOCK, new Color(19,19,19));
		blocksMap.put(Material.EMERALD_BLOCK, new Color(71,213,105));
		blocksMap.put(Material.LAPIS_BLOCK, new Color(42,80,139));
		blocksMap.put(Material.WATER, new Color(67,101,165));
		blocksMap.put(Material.SEAGRASS, new Color(42,112,48));
		blocksMap.put(Material.BUBBLE_COLUMN, new Color(80,115,175));
		blocksMap.put(Material.TALL_SEAGRASS, new Color(42,112,48));
		blocksMap.put(Material.KELP, new Color(58,108,42));
		blocksMap.put(Material.KELP_PLANT, new Color(58,108,42));
		blocksMap.put(Material.GRASS_BLOCK, new Color(91,127,61));
		blocksMap.put(Material.DIRT, new Color(168,120,83));
		blocksMap.put(Material.SAND, new Color(222,215,172));
		blocksMap.put(Material.SANDSTONE, new Color(213,207,162));
		blocksMap.put(Material.RED_SAND, new Color(191,108,49));
		blocksMap.put(Material.RED_SANDSTONE, new Color(180,100,50));
		blocksMap.put(Material.ACACIA_LEAVES, new Color(56,102,32));
		blocksMap.put(Material.BIRCH_LEAVES, new Color(80,100,52));
		blocksMap.put(Material.DARK_OAK_LEAVES, new Color(34,72,24));
		blocksMap.put(Material.JUNGLE_LEAVES, new Color(44,96,30));
		blocksMap.put(Material.OAK_LEAVES, new Color(42,88,28));
		blocksMap.put(Material.SPRUCE_LEAVES, new Color(38,60,42));
		blocksMap.put(Material.AZALEA_LEAVES, new Color(58,90,42));
		blocksMap.put(Material.FLOWERING_AZALEA_LEAVES, new Color(72,88,48));
		blocksMap.put(Material.VINE, new Color(38,78,26));
		blocksMap.put(Material.WEEPING_VINES, new Color(112,32,26));
		blocksMap.put(Material.WEEPING_VINES_PLANT, new Color(112,32,26));
		blocksMap.put(Material.TWISTING_VINES, new Color(38,120,90));
		blocksMap.put(Material.TWISTING_VINES_PLANT, new Color(38,120,90));
		blocksMap.put(Material.CAVE_VINES, new Color(60,110,30));
		blocksMap.put(Material.CAVE_VINES_PLANT, new Color(60,110,30));
		blocksMap.put(Material.LILY_PAD, new Color(38,110,42));
		blocksMap.put(Material.DIRT_PATH, new Color(170,148,89));
		blocksMap.put(Material.COARSE_DIRT, new Color(104,75,51));
		blocksMap.put(Material.ANDESITE, new Color(136,136,138));
		blocksMap.put(Material.DIORITE, new Color(181,181,181));
		blocksMap.put(Material.DEAD_BUSH, new Color(144,97,39));
		blocksMap.put(Material.CACTUS, new Color(76,107,35));
		blocksMap.put(Material.DANDELION, new Color(247,229,77));
		blocksMap.put(Material.POPPY, new Color(230,47,43));
		blocksMap.put(Material.CORNFLOWER, new Color(70,106,235));
		blocksMap.put(Material.AZURE_BLUET, new Color(210,215,223));
		blocksMap.put(Material.OXEYE_DAISY, new Color(187,188,189));
		blocksMap.put(Material.LAVA, new Color(211,124,40));
		blocksMap.put(Material.GRANITE, new Color(156,111,91));
		blocksMap.put(Material.REDSTONE_LAMP, new Color(123,73,33));
		blocksMap.put(Material.GRAVEL, new Color(139,135,134));
		blocksMap.put(Material.SPRUCE_LOG, new Color(48,34,25));
		blocksMap.put(Material.OAK_LOG, new Color(58,35,9));
		blocksMap.put(Material.BIRCH_LOG, new Color(196,195,193));
		blocksMap.put(Material.JUNGLE_LOG, new Color(89,76,37));
		blocksMap.put(Material.ACACIA_LOG, new Color(95,95,85));
		blocksMap.put(Material.DARK_OAK_LOG, new Color(35,27,16));
		blocksMap.put(Material.SPRUCE_PLANKS, new Color(100,78,47));
		blocksMap.put(Material.OAK_PLANKS, new Color(172,140,88));
		blocksMap.put(Material.BIRCH_PLANKS, new Color(202,185,131));
		blocksMap.put(Material.JUNGLE_PLANKS, new Color(172,124,89));
		blocksMap.put(Material.ACACIA_PLANKS, new Color(178,102,60));
		blocksMap.put(Material.DARK_OAK_PLANKS, new Color(62,41,18));
		blocksMap.put(Material.CRAFTING_TABLE, new Color(33,23,57));
		blocksMap.put(Material.ENCHANTING_TABLE, new Color(33,23,57));
		blocksMap.put(Material.BOOKSHELF, new Color(172,140,88));
		blocksMap.put(Material.SUGAR_CANE, new Color(71,139,42));
		blocksMap.put(Material.BEDROCK, new Color(47,47,47));
		blocksMap.put(Material.TORCH, new Color(206,173,26));
		blocksMap.put(Material.PUMPKIN, new Color(222,141,28));
		blocksMap.put(Material.CARVED_PUMPKIN, new Color(222,141,28));
		blocksMap.put(Material.JACK_O_LANTERN, new Color(222,141,28));
		blocksMap.put(Material.TNT, new Color(203,49,26));
		blocksMap.put(Material.SNOW, new Color(232,240,239));
		blocksMap.put(Material.SNOW_BLOCK, new Color(232,240,239));
		blocksMap.put(Material.ICE, new Color(158,178,251));
		blocksMap.put(Material.PACKED_ICE, new Color(141,164,241));
		blocksMap.put(Material.BLUE_ICE, new Color(116,144,253));
		blocksMap.put(Material.SPONGE, new Color(196,192,60));
		blocksMap.put(Material.WET_SPONGE, new Color(170,170,55));
		blocksMap.put(Material.QUARTZ_BLOCK, new Color(235,229,222));
		blocksMap.put(Material.SMOOTH_QUARTZ, new Color(235,229,222));
		blocksMap.put(Material.QUARTZ_PILLAR, new Color(235,229,222));
		blocksMap.put(Material.HONEY_BLOCK, new Color(247,180,32));
		blocksMap.put(Material.HONEYCOMB_BLOCK, new Color(219,110,26));

		// --- 1.17+ caves & cliffs ---
		blocksMap.put(Material.DEEPSLATE, new Color(65,65,66));
		blocksMap.put(Material.COBBLED_DEEPSLATE, new Color(70,70,72));
		blocksMap.put(Material.POLISHED_DEEPSLATE, new Color(74,74,78));
		blocksMap.put(Material.DEEPSLATE_BRICKS, new Color(70,70,74));
		blocksMap.put(Material.DEEPSLATE_TILES, new Color(60,60,64));
		blocksMap.put(Material.CHISELED_DEEPSLATE, new Color(68,68,70));
		blocksMap.put(Material.REINFORCED_DEEPSLATE, new Color(95,90,100));
		blocksMap.put(Material.TUFF, new Color(108,108,101));
		blocksMap.put(Material.TUFF_BRICKS, new Color(104,104,98));
		blocksMap.put(Material.CALCITE, new Color(224,224,215));
		blocksMap.put(Material.DRIPSTONE_BLOCK, new Color(151,107,79));
		blocksMap.put(Material.POINTED_DRIPSTONE, new Color(151,107,79));
		blocksMap.put(Material.MOSS_BLOCK, new Color(89,102,42));
		blocksMap.put(Material.MOSS_CARPET, new Color(89,102,42));
		blocksMap.put(Material.MUD, new Color(60,49,41));
		blocksMap.put(Material.MUD_BRICKS, new Color(146,116,89));
		blocksMap.put(Material.PACKED_MUD, new Color(150,111,76));
		blocksMap.put(Material.AMETHYST_BLOCK, new Color(133,96,208));
		blocksMap.put(Material.BUDDING_AMETHYST, new Color(122,88,192));
		blocksMap.put(Material.AZALEA, new Color(90,120,55));
		blocksMap.put(Material.FLOWERING_AZALEA, new Color(120,130,70));
		blocksMap.put(Material.BIG_DRIPLEAF, new Color(60,120,50));
		blocksMap.put(Material.SMALL_DRIPLEAF, new Color(60,130,55));
		blocksMap.put(Material.HANGING_ROOTS, new Color(110,80,60));
		blocksMap.put(Material.SPORE_BLOSSOM, new Color(220,80,140));
		blocksMap.put(Material.GLOW_LICHEN, new Color(150,170,150));

		// --- copper (1.17+) ---
		blocksMap.put(Material.COPPER_BLOCK, new Color(194,116,86));
		blocksMap.put(Material.EXPOSED_COPPER, new Color(146,144,120));
		blocksMap.put(Material.WEATHERED_COPPER, new Color(109,143,120));
		blocksMap.put(Material.OXIDIZED_COPPER, new Color(82,162,132));
		blocksMap.put(Material.CUT_COPPER, new Color(194,116,86));
		blocksMap.put(Material.EXPOSED_CUT_COPPER, new Color(146,144,120));
		blocksMap.put(Material.WEATHERED_CUT_COPPER, new Color(109,143,120));
		blocksMap.put(Material.OXIDIZED_CUT_COPPER, new Color(82,162,132));
		blocksMap.put(Material.COPPER_ORE, new Color(151,124,100));
		blocksMap.put(Material.DEEPSLATE_COPPER_ORE, new Color(110,100,90));

		// --- mangrove / cherry / bamboo (1.19+) ---
		blocksMap.put(Material.MANGROVE_LOG, new Color(83,53,43));
		blocksMap.put(Material.MANGROVE_PLANKS, new Color(117,53,39));
		blocksMap.put(Material.MANGROVE_ROOTS, new Color(95,66,53));
		blocksMap.put(Material.MUDDY_MANGROVE_ROOTS, new Color(95,73,58));
		blocksMap.put(Material.MANGROVE_LEAVES, new Color(46,84,48));
		blocksMap.put(Material.CHERRY_LOG, new Color(54,26,22));
		blocksMap.put(Material.CHERRY_PLANKS, new Color(234,193,189));
		blocksMap.put(Material.CHERRY_LEAVES, new Color(234,143,177));
		blocksMap.put(Material.BAMBOO_BLOCK, new Color(135,183,26));
		blocksMap.put(Material.BAMBOO_PLANKS, new Color(195,157,60));
		blocksMap.put(Material.BAMBOO_MOSAIC, new Color(195,157,60));

		// --- sculk (1.19+) ---
		blocksMap.put(Material.SCULK, new Color(16,20,27));
		blocksMap.put(Material.SCULK_VEIN, new Color(20,26,33));
		blocksMap.put(Material.SCULK_CATALYST, new Color(30,36,45));
		blocksMap.put(Material.SCULK_SHRIEKER, new Color(46,52,58));

		// --- nether ---
		blocksMap.put(Material.NETHERRACK, new Color(110,53,51));
		blocksMap.put(Material.NETHER_BRICKS, new Color(44,22,27));
		blocksMap.put(Material.RED_NETHER_BRICKS, new Color(70,10,10));
		blocksMap.put(Material.SOUL_SAND, new Color(81,62,50));
		blocksMap.put(Material.SOUL_SOIL, new Color(75,58,47));
		blocksMap.put(Material.BASALT, new Color(77,77,84));
		blocksMap.put(Material.POLISHED_BASALT, new Color(90,90,96));
		blocksMap.put(Material.SMOOTH_BASALT, new Color(80,80,86));
		blocksMap.put(Material.BLACKSTONE, new Color(42,36,40));
		blocksMap.put(Material.POLISHED_BLACKSTONE, new Color(52,46,50));
		blocksMap.put(Material.GILDED_BLACKSTONE, new Color(80,60,45));
		blocksMap.put(Material.CRIMSON_STEM, new Color(114,26,45));
		blocksMap.put(Material.CRIMSON_PLANKS, new Color(130,45,58));
		blocksMap.put(Material.CRIMSON_NYLIUM, new Color(140,32,33));
		blocksMap.put(Material.WARPED_STEM, new Color(56,109,105));
		blocksMap.put(Material.WARPED_PLANKS, new Color(39,136,127));
		blocksMap.put(Material.WARPED_NYLIUM, new Color(23,105,102));
		blocksMap.put(Material.SHROOMLIGHT, new Color(242,150,77));
		blocksMap.put(Material.NETHER_GOLD_ORE, new Color(140,68,53));
		blocksMap.put(Material.NETHER_QUARTZ_ORE, new Color(150,110,100));
		blocksMap.put(Material.MAGMA_BLOCK, new Color(181,64,9));
		blocksMap.put(Material.GLOWSTONE, new Color(172,133,79));
		blocksMap.put(Material.CRYING_OBSIDIAN, new Color(39,10,58));
		blocksMap.put(Material.OBSIDIAN, new Color(20,18,29));

		// --- the end ---
		blocksMap.put(Material.END_STONE, new Color(219,208,159));
		blocksMap.put(Material.END_STONE_BRICKS, new Color(219,208,159));
		blocksMap.put(Material.PURPUR_BLOCK, new Color(169,125,169));
		blocksMap.put(Material.PURPUR_PILLAR, new Color(169,125,169));
		blocksMap.put(Material.END_ROD, new Color(219,208,201));
		blocksMap.put(Material.CHORUS_PLANT, new Color(94,68,94));
		blocksMap.put(Material.CHORUS_FLOWER, new Color(154,120,154));

		// --- ocean / prismarine ---
		blocksMap.put(Material.PRISMARINE, new Color(99,157,152));
		blocksMap.put(Material.PRISMARINE_BRICKS, new Color(99,171,158));
		blocksMap.put(Material.DARK_PRISMARINE, new Color(51,89,74));
		blocksMap.put(Material.SEA_LANTERN, new Color(197,209,196));

		// --- wool / banners / concrete / walls / wall-banners (16-color families) ---
		for (DyeColor dc : DyeColor.values()) {
			Color c = toAwt(dc.getColor());
			putIfPresent(name(dc, "_WOOL"), c);
			putIfPresent(name(dc, "_BANNER"), c);
			putIfPresent(name(dc, "_WALL_BANNER"), c);
			putIfPresent(name(dc, "_CARPET"), c);
			putIfPresent(name(dc, "_BED"), c);
			putIfPresent(name(dc, "_CANDLE"), c);
			putIfPresent(name(dc, "_CONCRETE"), shade(c, 0.9));
			putIfPresent(name(dc, "_CONCRETE_POWDER"), shade(c, 1.05));
			putIfPresent(name(dc, "_STAINED_GLASS"), shade(c, 1.15));
			putIfPresent(name(dc, "_STAINED_GLASS_PANE"), shade(c, 1.15));
			putIfPresent(name(dc, "_TERRACOTTA"), blend(c, new Color(152,94,68), 0.55));
			putIfPresent(name(dc, "_GLAZED_TERRACOTTA"), blend(c, new Color(210,200,190), 0.4));
		}

		// --- mushroom blocks ---
		blocksMap.put(Material.RED_MUSHROOM_BLOCK, new Color(150,40,35));
		blocksMap.put(Material.BROWN_MUSHROOM_BLOCK, new Color(120,90,60));
		blocksMap.put(Material.MUSHROOM_STEM, new Color(220,215,200));
		blocksMap.put(Material.RED_MUSHROOM, new Color(180,50,45));
		blocksMap.put(Material.BROWN_MUSHROOM, new Color(140,105,75));

		// --- functional / utility blocks ---
		blocksMap.put(Material.SMOKER, new Color(90,90,90));
		blocksMap.put(Material.BLAST_FURNACE, new Color(110,115,120));
		blocksMap.put(Material.BARREL, new Color(120,90,55));
		blocksMap.put(Material.CAMPFIRE, new Color(120,90,60));
		blocksMap.put(Material.SOUL_CAMPFIRE, new Color(90,110,120));
		blocksMap.put(Material.COMPOSTER, new Color(140,105,65));
		blocksMap.put(Material.BEEHIVE, new Color(200,165,100));
		blocksMap.put(Material.BEE_NEST, new Color(190,150,90));
		blocksMap.put(Material.LODESTONE, new Color(120,120,125));
		blocksMap.put(Material.RESPAWN_ANCHOR, new Color(60,40,90));
		blocksMap.put(Material.LADDER, new Color(150,115,70));
		blocksMap.put(Material.SCAFFOLDING, new Color(190,160,100));
		blocksMap.put(Material.BONE_BLOCK, new Color(220,215,195));
		blocksMap.put(Material.DRIED_KELP_BLOCK, new Color(60,75,45));
		blocksMap.put(Material.TARGET, new Color(230,225,215));
		blocksMap.put(Material.CHISELED_BOOKSHELF, new Color(150,115,75));
		blocksMap.put(Material.DECORATED_POT, new Color(170,100,70));
		blocksMap.put(Material.SNIFFER_EGG, new Color(190,160,130));
		blocksMap.put(Material.TURTLE_EGG, new Color(230,225,205));
		blocksMap.put(Material.JUKEBOX, new Color(110,75,50));
		blocksMap.put(Material.NOTE_BLOCK, new Color(100,65,40));
		blocksMap.put(Material.CAULDRON, new Color(60,60,65));
		blocksMap.put(Material.ANVIL, new Color(55,55,55));
		blocksMap.put(Material.GRINDSTONE, new Color(150,150,150));
		blocksMap.put(Material.STONECUTTER, new Color(130,130,130));
		blocksMap.put(Material.SMITHING_TABLE, new Color(70,70,80));
		blocksMap.put(Material.FLETCHING_TABLE, new Color(200,190,160));
		blocksMap.put(Material.CARTOGRAPHY_TABLE, new Color(140,110,80));
		blocksMap.put(Material.LOOM, new Color(150,120,85));
		blocksMap.put(Material.LECTERN, new Color(150,115,75));
		blocksMap.put(Material.BREWING_STAND, new Color(80,80,85));
		blocksMap.put(Material.SPAWNER, new Color(50,80,80));
		blocksMap.put(Material.TRIAL_SPAWNER, new Color(70,95,90));
		blocksMap.put(Material.VAULT, new Color(60,120,110));

		// --- ores / raw blocks ---
		blocksMap.put(Material.RAW_IRON_BLOCK, new Color(190,145,110));
		blocksMap.put(Material.RAW_COPPER_BLOCK, new Color(160,105,75));
		blocksMap.put(Material.RAW_GOLD_BLOCK, new Color(210,175,60));
		blocksMap.put(Material.ANCIENT_DEBRIS, new Color(90,60,50));
		blocksMap.put(Material.NETHERITE_BLOCK, new Color(70,65,65));

		// --- ocean / coral / sea life ---
		blocksMap.put(Material.TUBE_CORAL, new Color(60,90,220));
		blocksMap.put(Material.TUBE_CORAL_BLOCK, new Color(60,90,220));
		blocksMap.put(Material.BRAIN_CORAL, new Color(220,110,160));
		blocksMap.put(Material.BRAIN_CORAL_BLOCK, new Color(220,110,160));
		blocksMap.put(Material.BUBBLE_CORAL, new Color(180,50,160));
		blocksMap.put(Material.BUBBLE_CORAL_BLOCK, new Color(180,50,160));
		blocksMap.put(Material.FIRE_CORAL, new Color(190,40,40));
		blocksMap.put(Material.FIRE_CORAL_BLOCK, new Color(190,40,40));
		blocksMap.put(Material.HORN_CORAL, new Color(210,190,50));
		blocksMap.put(Material.HORN_CORAL_BLOCK, new Color(210,190,50));
		blocksMap.put(Material.DEAD_TUBE_CORAL, new Color(150,145,140));
		blocksMap.put(Material.DEAD_BRAIN_CORAL, new Color(150,145,140));
		blocksMap.put(Material.DEAD_BUBBLE_CORAL, new Color(150,145,140));
		blocksMap.put(Material.DEAD_FIRE_CORAL, new Color(150,145,140));
		blocksMap.put(Material.DEAD_HORN_CORAL, new Color(150,145,140));
		blocksMap.put(Material.SEA_PICKLE, new Color(150,165,60));

		// --- candles (undyed) ---
		blocksMap.put(Material.CANDLE, new Color(230,220,200));

		// --- wood "block of wood" (bark on all sides) + stripped variants, derived from
		//     the LOG colors already set above, instead of hand-typing every combination ---
		String[] woodTypes = { "OAK", "SPRUCE", "BIRCH", "JUNGLE", "ACACIA", "DARK_OAK",
				"MANGROVE", "CHERRY", "CRIMSON", "WARPED" };
		for (String type : woodTypes) {
			Material log = Material.matchMaterial(type + "_LOG");
			Material stem = Material.matchMaterial(type + "_STEM"); // crimson/warped use "_STEM" instead of "_LOG"
			Color base = log != null ? blocksMap.get(log) : (stem != null ? blocksMap.get(stem) : null);
			if (base != null) {
				putIfPresent(type + "_WOOD", base);
				putIfPresent(type + "_HYPHAE", base); // crimson/warped's "_WOOD" equivalent
				Color stripped = shade(base, 1.35); // stripped wood shows the lighter inner wood
				putIfPresent("STRIPPED_" + type + "_LOG", stripped);
				putIfPresent("STRIPPED_" + type + "_WOOD", stripped);
				putIfPresent("STRIPPED_" + type + "_STEM", stripped);
				putIfPresent("STRIPPED_" + type + "_HYPHAE", stripped);
			}
		}
		blocksMap.put(Material.STRIPPED_BAMBOO_BLOCK, shade(new Color(135, 183, 26), 1.2));

		// --- metal doors/trapdoors (the wood-plank fallback above doesn't apply to these) ---
		blocksMap.put(Material.IRON_DOOR, new Color(210, 210, 212));
		blocksMap.put(Material.IRON_TRAPDOOR, new Color(200, 200, 202));
		putIfPresent("COPPER_DOOR", new Color(194, 116, 86));
		putIfPresent("EXPOSED_COPPER_DOOR", new Color(146, 144, 120));
		putIfPresent("WEATHERED_COPPER_DOOR", new Color(109, 143, 120));
		putIfPresent("OXIDIZED_COPPER_DOOR", new Color(82, 162, 132));
		putIfPresent("COPPER_TRAPDOOR", new Color(194, 116, 86));
		putIfPresent("EXPOSED_COPPER_TRAPDOOR", new Color(146, 144, 120));
		putIfPresent("WEATHERED_COPPER_TRAPDOOR", new Color(109, 143, 120));
		putIfPresent("OXIDIZED_COPPER_TRAPDOOR", new Color(82, 162, 132));
		putIfPresent("COPPER_GRATE", new Color(180, 110, 82));
		putIfPresent("EXPOSED_COPPER_GRATE", new Color(140, 138, 116));
		putIfPresent("WEATHERED_COPPER_GRATE", new Color(105, 138, 116));
		putIfPresent("OXIDIZED_COPPER_GRATE", new Color(80, 156, 128));
		putIfPresent("COPPER_BULB", new Color(200, 130, 95));
		putIfPresent("EXPOSED_COPPER_BULB", new Color(150, 148, 124));
		putIfPresent("WEATHERED_COPPER_BULB", new Color(112, 146, 122));
		putIfPresent("OXIDIZED_COPPER_BULB", new Color(86, 165, 135));

		// --- redstone category ---
		blocksMap.put(Material.REDSTONE_WIRE, new Color(160, 20, 15));
		blocksMap.put(Material.REDSTONE_TORCH, new Color(200, 30, 20));
		blocksMap.put(Material.REDSTONE_WALL_TORCH, new Color(200, 30, 20));
		blocksMap.put(Material.REPEATER, new Color(150, 145, 140));
		blocksMap.put(Material.COMPARATOR, new Color(155, 150, 145));
		blocksMap.put(Material.LEVER, new Color(120, 118, 115));
		blocksMap.put(Material.TRIPWIRE, new Color(180, 180, 175));
		blocksMap.put(Material.TRIPWIRE_HOOK, new Color(140, 110, 70));
		blocksMap.put(Material.DAYLIGHT_DETECTOR, new Color(150, 130, 100));
		blocksMap.put(Material.OBSERVER, new Color(100, 100, 100));
		blocksMap.put(Material.PISTON, new Color(140, 125, 90));
		blocksMap.put(Material.STICKY_PISTON, new Color(120, 135, 80));
		blocksMap.put(Material.PISTON_HEAD, new Color(140, 125, 90));
		blocksMap.put(Material.MOVING_PISTON, new Color(140, 125, 90));
		blocksMap.put(Material.DISPENSER, new Color(105, 105, 105));
		blocksMap.put(Material.DROPPER, new Color(105, 105, 105));
		blocksMap.put(Material.HOPPER, new Color(75, 75, 78));
		blocksMap.put(Material.TARGET, new Color(230, 225, 215));
		blocksMap.put(Material.SCULK_SENSOR, new Color(30, 90, 90));
		blocksMap.put(Material.CALIBRATED_SCULK_SENSOR, new Color(35, 95, 95));
		blocksMap.put(Material.CRAFTER, new Color(110, 108, 100));
	}

	private static void loadAnimalColors() {
		animalColorMap.put(EntityType.COW, new Color(96,68,52));
		animalColorMap.put(EntityType.MOOSHROOM, new Color(168,60,50));
		animalColorMap.put(EntityType.PIG, new Color(222,145,152));
		animalColorMap.put(EntityType.CHICKEN, new Color(235,235,225));
		animalColorMap.put(EntityType.RABBIT, new Color(170,140,110));
		animalColorMap.put(EntityType.TURTLE, new Color(60,140,60));
		animalColorMap.put(EntityType.PANDA, new Color(235,235,235));
		animalColorMap.put(EntityType.FOX, new Color(200,110,60));
		animalColorMap.put(EntityType.POLAR_BEAR, new Color(240,240,240));
		animalColorMap.put(EntityType.GOAT, new Color(150,130,100));
		animalColorMap.put(EntityType.LLAMA, new Color(200,180,150));
		animalColorMap.put(EntityType.DONKEY, new Color(110,85,60));
		animalColorMap.put(EntityType.MULE, new Color(95,70,50));
		animalColorMap.put(EntityType.CAMEL, new Color(200,160,110));
		animalColorMap.put(EntityType.FROG, new Color(150,190,60));
		animalColorMap.put(EntityType.AXOLOTL, new Color(240,150,180));
		animalColorMap.put(EntityType.BEE, new Color(235,190,50));
		animalColorMap.put(EntityType.SQUID, new Color(60,80,140));
		animalColorMap.put(EntityType.GLOW_SQUID, new Color(90,150,180));
		animalColorMap.put(EntityType.DOLPHIN, new Color(110,140,160));
		animalColorMap.put(EntityType.PARROT, new Color(200,60,50));
		animalColorMap.put(EntityType.SNIFFER, new Color(190,130,90));
		animalColorMap.put(EntityType.ARMADILLO, new Color(150,120,90));
		animalColorMap.put(EntityType.STRIDER, new Color(140,60,55));
		animalColorMap.put(EntityType.BAT, new Color(60,55,60));
		animalColorMap.put(EntityType.CAT, new Color(200,170,140));

		// --- hostiles ---
		animalColorMap.put(EntityType.ZOMBIE, new Color(58,120,72));
		animalColorMap.put(EntityType.HUSK, new Color(150,130,80));
		animalColorMap.put(EntityType.DROWNED, new Color(70,120,110));
		animalColorMap.put(EntityType.ZOMBIE_VILLAGER, new Color(90,120,80));
		animalColorMap.put(EntityType.SKELETON, new Color(196,196,184));
		animalColorMap.put(EntityType.STRAY, new Color(180,200,200));
		animalColorMap.put(EntityType.WITHER_SKELETON, new Color(45,40,40));
		animalColorMap.put(EntityType.CREEPER, new Color(68,160,68));
		animalColorMap.put(EntityType.SPIDER, new Color(35,28,22));
		animalColorMap.put(EntityType.CAVE_SPIDER, new Color(30,80,75));
		animalColorMap.put(EntityType.ENDERMAN, new Color(25,18,28));
		animalColorMap.put(EntityType.ENDERMITE, new Color(40,30,50));
		animalColorMap.put(EntityType.SILVERFISH, new Color(120,120,120));
		animalColorMap.put(EntityType.WITCH, new Color(70,90,55));
		animalColorMap.put(EntityType.PILLAGER, new Color(140,130,110));
		animalColorMap.put(EntityType.VINDICATOR, new Color(120,130,130));
		animalColorMap.put(EntityType.EVOKER, new Color(150,140,120));
		animalColorMap.put(EntityType.RAVAGER, new Color(110,90,80));
		animalColorMap.put(EntityType.ILLUSIONER, new Color(140,130,110));
		animalColorMap.put(EntityType.PIGLIN, new Color(210,150,120));
		animalColorMap.put(EntityType.PIGLIN_BRUTE, new Color(180,110,90));
		animalColorMap.put(EntityType.ZOMBIFIED_PIGLIN, new Color(205,140,110));
		animalColorMap.put(EntityType.HOGLIN, new Color(150,100,95));
		animalColorMap.put(EntityType.ZOGLIN, new Color(160,150,145));
		animalColorMap.put(EntityType.GHAST, new Color(230,230,230));
		animalColorMap.put(EntityType.BLAZE, new Color(230,150,40));
		animalColorMap.put(EntityType.MAGMA_CUBE, new Color(200,90,40));
		animalColorMap.put(EntityType.SLIME, new Color(120,180,110));
		animalColorMap.put(EntityType.SHULKER, new Color(150,110,140));
		animalColorMap.put(EntityType.PHANTOM, new Color(60,70,90));
		animalColorMap.put(EntityType.GUARDIAN, new Color(100,150,140));
		animalColorMap.put(EntityType.ELDER_GUARDIAN, new Color(110,150,150));
		animalColorMap.put(EntityType.WITHER, new Color(30,25,25));
		animalColorMap.put(EntityType.ENDER_DRAGON, new Color(40,30,50));

		// --- villagers / golems / players ---
		animalColorMap.put(EntityType.VILLAGER, new Color(170,140,110));
		animalColorMap.put(EntityType.WANDERING_TRADER, new Color(110,130,150));
		animalColorMap.put(EntityType.IRON_GOLEM, new Color(170,170,160));
		animalColorMap.put(EntityType.SNOW_GOLEM, new Color(230,235,235));
		animalColorMap.put(EntityType.ALLAY, new Color(120,170,220));
		animalColorMap.put(EntityType.PLAYER, new Color(210,175,150));
	}

	// -----------------------------------------------------------------
	// Public API used by Renderer
	// -----------------------------------------------------------------

	/** Whether Renderer's entity raytrace should consider this entity type at all.
	 *  We now capture every entity by default; this only excludes a handful of
	 *  non-visual/marker entity types that shouldn't show up in a photo. */
	public static boolean isCapturableEntity(EntityType type) {
		return type != EntityType.ARMOR_STAND && type != EntityType.ITEM
				&& type != EntityType.EXPERIENCE_ORB && type != EntityType.FALLING_BLOCK
				&& type != EntityType.AREA_EFFECT_CLOUD && type != EntityType.LIGHTNING_BOLT;
	}

	// Vanilla stores these textures either biome-tinted at runtime (the PNG itself is
	// an untinted grayscale/white template — sampling it directly gives white/gray,
	// not green) or as a multi-frame animation strip stacked in one tall PNG (sampling
	// naively reads random frames instead of the block's actual look). For both cases
	// real per-pixel sampling does more harm than good, so these always use the
	// hand-picked flat color + speckle instead, same as materials with no texture at all.
	// Bukkit's raytrace only knows a block's simplified "outline" bounding shape, not its
	// real visual mesh — so a cross-shaped plant's raycast hit currently looks like a
	// solid cube, when visually most of that cube is empty space between the "blades".
	// This table says what fraction of hits on a given plant should actually count as
	// hitting it; a "miss" makes Renderer look past it to whatever's behind, and a "hit"
	// keeps this block's color — over many rays, this reads as gaps/see-through foliage.
	private static final Map<Material, Double> SPARSE_PLANT_COVERAGE = new HashMap<>();
	static {
		SPARSE_PLANT_COVERAGE.put(Material.SHORT_GRASS, 0.45);
		SPARSE_PLANT_COVERAGE.put(Material.TALL_GRASS, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.FERN, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.LARGE_FERN, 0.55);
		SPARSE_PLANT_COVERAGE.put(Material.DEAD_BUSH, 0.4);
		SPARSE_PLANT_COVERAGE.put(Material.DANDELION, 0.22);
		SPARSE_PLANT_COVERAGE.put(Material.POPPY, 0.22);
		SPARSE_PLANT_COVERAGE.put(Material.AZURE_BLUET, 0.2);
		SPARSE_PLANT_COVERAGE.put(Material.OXEYE_DAISY, 0.22);
		SPARSE_PLANT_COVERAGE.put(Material.CORNFLOWER, 0.22);
		SPARSE_PLANT_COVERAGE.put(Material.ALLIUM, 0.22);
		SPARSE_PLANT_COVERAGE.put(Material.BLUE_ORCHID, 0.22);
		SPARSE_PLANT_COVERAGE.put(Material.ORANGE_TULIP, 0.2);
		SPARSE_PLANT_COVERAGE.put(Material.PINK_TULIP, 0.2);
		SPARSE_PLANT_COVERAGE.put(Material.RED_TULIP, 0.2);
		SPARSE_PLANT_COVERAGE.put(Material.WHITE_TULIP, 0.2);
		SPARSE_PLANT_COVERAGE.put(Material.LILY_OF_THE_VALLEY, 0.2);
		SPARSE_PLANT_COVERAGE.put(Material.SUNFLOWER, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.LILAC, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.ROSE_BUSH, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.PEONY, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.VINE, 0.55);
		SPARSE_PLANT_COVERAGE.put(Material.WEEPING_VINES, 0.4);
		SPARSE_PLANT_COVERAGE.put(Material.WEEPING_VINES_PLANT, 0.4);
		SPARSE_PLANT_COVERAGE.put(Material.TWISTING_VINES, 0.4);
		SPARSE_PLANT_COVERAGE.put(Material.TWISTING_VINES_PLANT, 0.4);
		SPARSE_PLANT_COVERAGE.put(Material.CAVE_VINES, 0.4);
		SPARSE_PLANT_COVERAGE.put(Material.CAVE_VINES_PLANT, 0.4);
		SPARSE_PLANT_COVERAGE.put(Material.GLOW_LICHEN, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.HANGING_ROOTS, 0.4);
		SPARSE_PLANT_COVERAGE.put(Material.SPORE_BLOSSOM, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.BIG_DRIPLEAF, 0.6);
		SPARSE_PLANT_COVERAGE.put(Material.SMALL_DRIPLEAF, 0.4);
		SPARSE_PLANT_COVERAGE.put(Material.SUGAR_CANE, 0.6);
		SPARSE_PLANT_COVERAGE.put(Material.BAMBOO, 0.6);
		SPARSE_PLANT_COVERAGE.put(Material.SEAGRASS, 0.4);
		SPARSE_PLANT_COVERAGE.put(Material.TALL_SEAGRASS, 0.45);
		SPARSE_PLANT_COVERAGE.put(Material.KELP, 0.4);
		SPARSE_PLANT_COVERAGE.put(Material.KELP_PLANT, 0.4);
		SPARSE_PLANT_COVERAGE.put(Material.LILY_PAD, 0.9); // a solid disc, barely "sparse"
		SPARSE_PLANT_COVERAGE.put(Material.WHEAT, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.CARROTS, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.POTATOES, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.BEETROOTS, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.NETHER_WART, 0.5);
		SPARSE_PLANT_COVERAGE.put(Material.CRIMSON_FUNGUS, 0.3);
		SPARSE_PLANT_COVERAGE.put(Material.WARPED_FUNGUS, 0.3);
		SPARSE_PLANT_COVERAGE.put(Material.CRIMSON_ROOTS, 0.3);
		SPARSE_PLANT_COVERAGE.put(Material.WARPED_ROOTS, 0.3);
		SPARSE_PLANT_COVERAGE.put(Material.NETHER_SPROUTS, 0.3);
		SPARSE_PLANT_COVERAGE.put(Material.TORCH, 0.25);
		SPARSE_PLANT_COVERAGE.put(Material.SOUL_TORCH, 0.25);
		for (Material mat : Material.values()) {
			String name = mat.toString();
			if (name.endsWith("_SAPLING") || name.equals("MANGROVE_PROPAGULE")) {
				SPARSE_PLANT_COVERAGE.put(mat, 0.3);
			}
		}
	}

	/** Null if this material isn't a "sparse" plant — Renderer treats that as always-solid. */
	public static Double getSparsePlantCoverage(Material mat) {
		return SPARSE_PLANT_COVERAGE.get(mat);
	}

	/** Whether a block counts as "a plant" for the collection album — the sparse-plant
	 *  list already covers most of them (grass, flowers, crops, vines...), plus a few
	 *  solid plant-ish blocks that aren't sparse (mushrooms, cactus...). Tree leaves are
	 *  their own "trees" subcategory (see isTreeMaterial), not counted here. */
	public static boolean isPlantMaterial(Material mat) {
		if (isTreeMaterial(mat)) {
			return false;
		}
		if (SPARSE_PLANT_COVERAGE.containsKey(mat)) {
			return true;
		}
		String name = mat.toString();
		if (name.contains("MUSHROOM") || name.equals("CACTUS")
				|| name.contains("MOSS") || name.contains("AZALEA") || name.equals("BAMBOO_BLOCK")
				|| name.equals("SUGAR_CANE") || name.equals("MELON") || name.equals("PUMPKIN")
				|| name.equals("SNIFFER_EGG") || name.equals("TURTLE_EGG")) {
			return true;
		}
		return false;
	}

	/** Tree leaves — their own subcategory in the collection album, separate from plants. */
	public static boolean isTreeMaterial(Material mat) {
		return mat.toString().endsWith("_LEAVES");
	}

	private static Integer plantMaterialCount = null;
	private static Integer treeMaterialCount = null;

	/** Total number of distinct plant materials that exist, for the album's X/Y display. */
	public static int getTotalPlantMaterials() {
		if (plantMaterialCount == null) {
			int count = 0;
			for (Material mat : Material.values()) {
				if (isPlantMaterial(mat)) count++;
			}
			plantMaterialCount = count;
		}
		return plantMaterialCount;
	}

	/** Total number of distinct tree (leaf) materials that exist, for the album's X/Y display. */
	public static int getTotalTreeMaterials() {
		if (treeMaterialCount == null) {
			int count = 0;
			for (Material mat : Material.values()) {
				if (isTreeMaterial(mat)) count++;
			}
			treeMaterialCount = count;
		}
		return treeMaterialCount;
	}

	/** Deterministic pseudo-random value in 0..1 from world-space coordinates, exposed
	 *  for Renderer's sparse-plant pass-through roll (same coordinates always roll the
	 *  same way, so a locked/already-taken photo can't change on a later render). */
	public static double deterministicRandom(double a, double b, double c) {
		return hashNoise(a, b, c);
	}

	private static final Set<Material> SKIP_TEXTURE_SAMPLE = buildSkipTextureSampleSet();

	private static Set<Material> buildSkipTextureSampleSet() {
		Set<Material> set = EnumSet.of(
				Material.SHORT_GRASS, Material.TALL_GRASS, Material.FERN, Material.LARGE_FERN,
				Material.GRASS_BLOCK, Material.MYCELIUM, Material.PODZOL,
				Material.OAK_LEAVES, Material.BIRCH_LEAVES, Material.SPRUCE_LEAVES, Material.JUNGLE_LEAVES,
				Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES, Material.MANGROVE_LEAVES, Material.CHERRY_LEAVES,
				Material.AZALEA_LEAVES, Material.FLOWERING_AZALEA_LEAVES,
				Material.VINE, Material.WEEPING_VINES, Material.WEEPING_VINES_PLANT,
				Material.TWISTING_VINES, Material.TWISTING_VINES_PLANT, Material.CAVE_VINES, Material.CAVE_VINES_PLANT,
				Material.LILY_PAD, Material.SUGAR_CANE, Material.KELP, Material.KELP_PLANT,
				Material.SEAGRASS, Material.TALL_SEAGRASS, Material.GLOW_LICHEN,
				Material.WATER, Material.LAVA, Material.BUBBLE_COLUMN, Material.FIRE, Material.SOUL_FIRE,
				Material.REDSTONE_WIRE, Material.STONE);
		// Doors/trapdoors are thin panels, not full cubes — our face-based UV mapping
		// only really makes sense for full-cube blocks, so real texture sampling on
		// these just produces a distorted/cropped-looking slice of the texture instead
		// (part of why a door could look "off"/disconnected between its two halves).
		for (Material mat : Material.values()) {
			String name = mat.toString();
			if (name.endsWith("_DOOR") || name.endsWith("_TRAPDOOR")) {
				set.add(mat);
			}
		}
		return set;
	}

	private static final Color FOG_COLOR = new Color(190, 205, 220);

	/**
	 * Resolves the pixel color for a block, applying:
	 *  - a real texture-pixel sample if the resource pack has a texture for this block
	 *    AND it isn't a biome-tinted/animated material (see SKIP_TEXTURE_SAMPLE)
	 *    (mapped from the exact hit position on the hit face, so it's genuinely
	 *    pixelated detail, not a flat average)
	 *  - otherwise a deterministic "speckle" pattern over the flat color, so plain
	 *    colors (grass, leaves, stone...) aren't perfectly uniform either
	 *  - shade: a brightness multiplier (vanilla light level combined with relief/edge shading)
	 *  - fogBlend: 0 = no fog (use the shaded color as-is), 1 = fully replaced by the fog color
	 * then matches the result to the closest vanilla map-palette color.
	 */
	public static byte colorFromType(Block block, Vector hitPos, BlockFace face, double shade, double fogBlend, PostFX fx) {
		double[] uv = computeUV(block, hitPos, face);
		BufferedImage tex = SKIP_TEXTURE_SAMPLE.contains(block.getType()) ? null : getTextureImage(block.getType());
		Color color;
		if (tex != null) {
			Color sampled = sampleTexturePixel(tex, uv[0], uv[1]);
			if (sampled != null) {
				color = sampled;
			} else {
				// The texture exists, but this exact pixel was rejected (transparent, or a
				// near-white/background artifact some older texture packs bake in where the
				// real graphic should be see-through, e.g. leaves). Rather than show that as
				// a bright/white speck, fill it with a strongly darkened version of the
				// block's own known color — reads as "shadow gap inside the foliage" instead.
				Color flat = resolveBlockColor(block.getType());
				if (flat == null) {
					flat = new Color(140, 140, 140);
				}
				color = shade(flat, 0.5);
			}
		} else {
			Color flat = resolveBlockColor(block.getType());
			if (flat == null) {
				flat = new Color(140, 140, 140); // neutral, not a harsh flat gray
			}
			String typeName = block.getType().toString();
			if (typeName.endsWith("_DOOR") || typeName.endsWith("_TRAPDOOR")) {
				// Keep these perfectly flat — a door's two blocks (upper/lower half)
				// should read as one continuous surface, not two independently
				// speckled/noisy textures that make the seam between them obvious.
				color = flat;
			} else {
				color = applySpeckle(flat, block.getX() + uv[0], block.getY() + uv[1], block.getZ());
			}
		}
		Color shaded = applyShade(color, shade);
		Color foggy = blend(shaded, FOG_COLOR, 1 - fogBlend);
		Color finalColor = applyPostFx(foggy, fx, hitPos.getX(), hitPos.getZ());
		return MapPalette.matchColor(finalColor);
	}

	/** Resolves the pixel color for an entity (used for the "capture animals" feature). */
	public static byte colorFromEntity(Entity entity, Vector hitPos, double shade, double fogBlend, PostFX fx) {
		Color base = resolveEntityColor(entity);
		Color speckled = applySpeckle(base, hitPos.getX() * 4, hitPos.getY() * 4, hitPos.getZ() * 4 + entity.getEntityId());
		Color shaded = applyShade(speckled, shade);
		Color foggy = blend(shaded, FOG_COLOR, 1 - fogBlend);
		Color finalColor = applyPostFx(foggy, fx, hitPos.getX(), hitPos.getZ());
		return MapPalette.matchColor(finalColor);
	}

	/** Final-image adjustments applied uniformly to every pixel, like a camera filter. */
	public static class PostFX {
		public final double brightness;
		public final double contrast;
		public final double saturation;
		public final double grain;
		public final boolean sepia;
		public final Color tintColor;
		public final double tintStrength;

		public PostFX(double brightness, double contrast, double saturation, double grain) {
			this(brightness, contrast, saturation, grain, false, null, 0);
		}

		public PostFX(double brightness, double contrast, double saturation, double grain,
				boolean sepia, Color tintColor, double tintStrength) {
			this.brightness = brightness;
			this.contrast = contrast;
			this.saturation = saturation;
			this.grain = grain;
			this.sepia = sepia;
			this.tintColor = tintColor;
			this.tintStrength = tintStrength;
		}
	}

	/** Predefined camera filters. Each one's brightness/contrast/saturation/grain
	 *  MULTIPLY on top of whatever the camera's own postprocess settings already are
	 *  (so a filter layers on top of manual tuning instead of replacing it) — sepia
	 *  and tint are filter-only effects with no manual equivalent. */
	public enum Filter {
		NONE(1.0, 1.0, 1.0, 0.0, false, null, 0),
		SEPIA(1.0, 1.05, 1.0, 0.12, true, null, 0),
		GRAYSCALE(1.0, 1.1, 0.0, 0.0, false, null, 0),
		VINTAGE(1.05, 1.05, 0.55, 0.3, false, new Color(200, 150, 90), 0.18),
		COOL(1.0, 1.05, 1.1, 0.0, false, new Color(120, 160, 230), 0.12),
		WARM(1.05, 1.0, 1.05, 0.0, false, new Color(235, 160, 90), 0.12),
		NOIR(1.0, 1.35, 0.0, 0.25, false, null, 0);

		public final double brightness, contrast, saturation, grain, tintStrength;
		public final boolean sepia;
		public final Color tintColor;

		Filter(double brightness, double contrast, double saturation, double grain,
				boolean sepia, Color tintColor, double tintStrength) {
			this.brightness = brightness;
			this.contrast = contrast;
			this.saturation = saturation;
			this.grain = grain;
			this.sepia = sepia;
			this.tintColor = tintColor;
			this.tintStrength = tintStrength;
		}

		public static Filter fromString(String name) {
			if (name == null) {
				return NONE;
			}
			try {
				return Filter.valueOf(name.trim().toUpperCase());
			} catch (IllegalArgumentException e) {
				return NONE;
			}
		}
	}

	/** Combines a camera's own postprocess settings with a filter preset — brightness/
	 *  contrast/saturation/grain multiply together, sepia/tint come only from the filter. */
	public static PostFX combine(PostFX base, Filter filter) {
		return new PostFX(
				base.brightness * filter.brightness,
				base.contrast * filter.contrast,
				base.saturation * filter.saturation,
				Math.min(1.0, base.grain + filter.grain),
				filter.sepia,
				filter.tintColor,
				filter.tintStrength);
	}

	private static Color applyPostFx(Color c, PostFX fx, double seedA, double seedB) {
		float r = c.getRed();
		float g = c.getGreen();
		float b = c.getBlue();

		r *= fx.brightness;
		g *= fx.brightness;
		b *= fx.brightness;

		r = (float) ((r - 128) * fx.contrast + 128);
		g = (float) ((g - 128) * fx.contrast + 128);
		b = (float) ((b - 128) * fx.contrast + 128);

		r = clampF(r);
		g = clampF(g);
		b = clampF(b);

		if (fx.saturation != 1.0) {
			float[] hsb = Color.RGBtoHSB((int) r, (int) g, (int) b, null);
			float newSat = (float) Math.max(0, Math.min(1, hsb[1] * fx.saturation));
			int rgb = Color.HSBtoRGB(hsb[0], newSat, hsb[2]);
			r = (rgb >> 16) & 0xff;
			g = (rgb >> 8) & 0xff;
			b = rgb & 0xff;
		}

		if (fx.sepia) {
			float sr = 0.393f * r + 0.769f * g + 0.189f * b;
			float sg = 0.349f * r + 0.686f * g + 0.168f * b;
			float sb = 0.272f * r + 0.534f * g + 0.131f * b;
			r = clampF(sr);
			g = clampF(sg);
			b = clampF(sb);
		}

		if (fx.tintColor != null && fx.tintStrength > 0) {
			r = clampF((float) (r * (1 - fx.tintStrength) + fx.tintColor.getRed() * fx.tintStrength));
			g = clampF((float) (g * (1 - fx.tintStrength) + fx.tintColor.getGreen() * fx.tintStrength));
			b = clampF((float) (b * (1 - fx.tintStrength) + fx.tintColor.getBlue() * fx.tintStrength));
		}

		if (fx.grain > 0) {
			double n = (hashNoise(seedA * 17.0, seedB * 29.0, 8.0) - 0.5) * 2; // -1..1
			double offset = n * fx.grain * 45;
			r = clampF((float) (r + offset));
			g = clampF((float) (g + offset));
			b = clampF((float) (b + offset));
		}

		return new Color((int) r, (int) g, (int) b);
	}

	private static float clampF(float v) {
		if (v < 0) return 0;
		if (v > 255) return 255;
		return v;
	}

	// -----------------------------------------------------------------
	// Block color resolution chain
	// -----------------------------------------------------------------

	private static Color resolveBlockColor(Material mat) {
		Color direct = blocksMap.get(mat);
		if (direct != null) {
			return direct;
		}

		String name = mat.toString();

		// 1) Strip a shape suffix (stairs/slab/wall/door/etc.) and retry on the base block.
		String[] shapeSuffixes = { "_STAIRS", "_SLAB", "_WALL", "_FENCE_GATE", "_FENCE", "_DOOR", "_TRAPDOOR",
				"_BUTTON", "_PRESSURE_PLATE", "_SIGN", "_HANGING_SIGN", "_WALL_SIGN", "_WALL_HANGING_SIGN" };
		for (String suf : shapeSuffixes) {
			if (name.endsWith(suf)) {
				String stem = name.substring(0, name.length() - suf.length());

				// try the stem directly — works for non-wood shapes like COBBLESTONE_STAIRS
				Material base = Material.matchMaterial(stem);
				if (base != null) {
					Color c = blocksMap.get(base);
					if (c != null) return c;
				}

				// try stem + "_PLANKS" — this is what wood actually needs: OAK_STAIRS's
				// stem is "OAK", which isn't a material on its own, but "OAK_PLANKS" is.
				// Every wood-type stairs/slab/fence/door/etc. was silently falling through
				// to gray before this, since the bare stem never matched anything.
				Material planksBase = Material.matchMaterial(stem + "_PLANKS");
				if (planksBase != null) {
					Color c = blocksMap.get(planksBase);
					if (c != null) return c;
				}
			}
		}

		// 2) Generic material-family fallback (covers anything future Minecraft
		//    updates add that we haven't explicitly mapped yet).
		if (name.contains("LEAVES")) return new Color(44, 86, 30);
		if (name.endsWith("_LOG") || name.endsWith("_WOOD") || name.endsWith("_STEM") || name.endsWith("_HYPHAE"))
			return new Color(90, 65, 40);
		if (name.endsWith("_PLANKS")) return new Color(170, 140, 90);
		if (name.contains("DEEPSLATE") && name.contains("_ORE")) return new Color(90, 90, 92);
		if (name.contains("_ORE")) return new Color(135, 130, 125);
		if (name.contains("ICE")) return new Color(150, 180, 255);
		if (name.contains("SANDSTONE") || name.equals("SAND")) return new Color(219, 210, 160);
		if (name.contains("GLASS")) return new Color(255, 255, 255);
		if (name.contains("CORAL")) return new Color(230, 140, 150);
		if (name.contains("MUSHROOM")) return new Color(150, 90, 70);

		// 3) Last resort: sample the average color from the (optional) resource pack
		//    texture for this block, instead of falling to a flat gray.
		Color texColor = averageTextureColor(mat);
		if (texColor != null) {
			return texColor;
		}

		return null;
	}

	private static Color resolveEntityColor(Entity entity) {
		if (entity instanceof Sheep) {
			DyeColor dc = ((Sheep) entity).getColor();
			return dc != null ? toAwt(dc.getColor()) : new Color(230, 230, 230);
		}
		if (entity instanceof Horse) {
			return horseColor(((Horse) entity).getColor());
		}
		if (entity instanceof Wolf) {
			return ((Wolf) entity).isTamed() ? new Color(150, 120, 90) : new Color(170, 170, 170);
		}
		Color mapped = animalColorMap.get(entity.getType());
		if (mapped != null) {
			return mapped;
		}
		return new Color(150, 110, 90); // generic warm "animal" default
	}

	private static Color horseColor(Horse.Color hc) {
		if (hc == null) return new Color(150, 110, 80);
		switch (hc) {
			case WHITE: return new Color(235, 235, 230);
			case CREAMY: return new Color(230, 200, 150);
			case CHESTNUT: return new Color(140, 80, 50);
			case BROWN: return new Color(90, 55, 35);
			case BLACK: return new Color(35, 30, 30);
			case GRAY: return new Color(150, 150, 150);
			case DARK_BROWN: return new Color(60, 40, 30);
			default: return new Color(150, 110, 80);
		}
	}

	// -----------------------------------------------------------------
	// Real texture sampling (maps the exact hit position on the hit face to a
	// pixel in the resource-pack texture, instead of one flat color per block)
	// -----------------------------------------------------------------

	/** Returns [u, v] in 0..1 range representing where on the hit face the ray landed. */
	private static double[] computeUV(Block block, Vector hitPos, BlockFace face) {
		double lx = hitPos.getX() - block.getX();
		double ly = hitPos.getY() - block.getY();
		double lz = hitPos.getZ() - block.getZ();
		double u, v;
		switch (face) {
			case UP:
			case DOWN:
				u = lx; v = lz;
				break;
			case NORTH:
			case SOUTH:
				u = lx; v = 1 - ly;
				break;
			case EAST:
			case WEST:
				u = lz; v = 1 - ly;
				break;
			default:
				u = lx; v = lz;
		}
		return new double[] { clamp01(u), clamp01(v) };
	}

	/** Samples one pixel from an already-loaded texture. Returns null if that pixel
	 *  looks like it should actually be see-through (real alpha transparency, or —
	 *  since some older/legacy texture packs bake "holes" as opaque near-white
	 *  instead of real alpha — a near-white, low-saturation pixel too). */
	private static Color sampleTexturePixel(BufferedImage tex, double u, double v) {
		int px = Math.min(tex.getWidth() - 1, (int) (u * tex.getWidth()));
		int py = Math.min(tex.getHeight() - 1, (int) (v * tex.getHeight()));
		int argb = tex.getRGB(px, py);
		int alpha = (argb >> 24) & 0xff;
		if (alpha < 32) {
			return null; // real transparency (e.g. a hole in a leaves texture)
		}
		int r = (argb >> 16) & 0xff;
		int g = (argb >> 8) & 0xff;
		int b = argb & 0xff;
		int max = Math.max(r, Math.max(g, b));
		int min = Math.min(r, Math.min(g, b));
		if (max > 235 && (max - min) < 25) {
			return null; // near-white/flat pixel — almost always a baked-in "hole", not real detail
		}
		return new Color(r, g, b);
	}

	private static BufferedImage getTextureImage(Material mat) {
		ResourcePackManager rpm = Camera.getInstance().getResourcePackManager();
		if (rpm == null || !rpm.isLoaded()) {
			return null;
		}
		return rpm.getImageHashMap().get(mat);
	}

	/**
	 * Deterministic pseudo-random darker "speckle" applied to a fraction of sampled
	 * points, so flat colors (used whenever we don't have a real texture) get some
	 * mottling instead of being perfectly uniform. Same coordinates always produce
	 * the same speckle, so it doesn't look like random noise/static.
	 */
	private static Color applySpeckle(Color base, double a, double b, double c) {
		double h = hashNoise(a, b, c);
		if (h < 0.30) {
			double darken = 0.55 + (h / 0.30) * 0.30; // between 0.55x and 0.85x brightness
			return shade(base, darken);
		}
		if (h > 0.93) {
			return shade(base, 1.12); // occasional lighter fleck too
		}
		return base;
	}

	private static double hashNoise(double a, double b, double c) {
		long bits = Double.doubleToLongBits(a * 374761393.0 + b * 668265263.0 + c * 2147483647.0 + 12345.0);
		bits ^= (bits >>> 33);
		bits *= 0xff51afd7ed558ccdL;
		bits ^= (bits >>> 33);
		return (bits & 0xFFFFFFFFL) / (double) 0xFFFFFFFFL;
	}

	private static double clamp01(double v) {
		if (v < 0) return 0;
		if (v > 1) return 1;
		return v;
	}

	// -----------------------------------------------------------------
	// Resource-pack texture average color (fallback used inside resolveBlockColor,
	// for materials with a texture but where per-pixel sampling isn't in play,
	// e.g. blocks reached only through the generic-family fallback)
	// -----------------------------------------------------------------

	private static Color averageTextureColor(Material mat) {
		if (textureColorCache.containsKey(mat)) {
			return textureColorCache.get(mat);
		}
		ResourcePackManager rpm = Camera.getInstance().getResourcePackManager();
		if (rpm == null || !rpm.isLoaded()) {
			return null;
		}
		File textureFile = rpm.getTextureByMaterial(mat);
		if (textureFile == null) {
			textureColorCache.put(mat, null);
			return null;
		}
		try {
			BufferedImage img = javax.imageio.ImageIO.read(textureFile);
			if (img == null) {
				textureColorCache.put(mat, null);
				return null;
			}
			long r = 0, g = 0, b = 0, count = 0;
			for (int x = 0; x < img.getWidth(); x++) {
				for (int y = 0; y < img.getHeight(); y++) {
					int argb = img.getRGB(x, y);
					int alpha = (argb >> 24) & 0xff;
					if (alpha < 32) continue; // skip mostly-transparent pixels
					r += (argb >> 16) & 0xff;
					g += (argb >> 8) & 0xff;
					b += argb & 0xff;
					count++;
				}
			}
			if (count == 0) {
				textureColorCache.put(mat, null);
				return null;
			}
			Color avg = new Color((int) (r / count), (int) (g / count), (int) (b / count));
			textureColorCache.put(mat, avg);
			return avg;
		} catch (Exception e) {
			textureColorCache.put(mat, null);
			return null;
		}
	}

	// -----------------------------------------------------------------
	// Small helpers
	// -----------------------------------------------------------------

	private static void putIfPresent(String materialName, Color color) {
		Material mat = Material.matchMaterial(materialName);
		if (mat != null) {
			blocksMap.put(mat, color);
		}
	}

	private static String name(DyeColor dc, String suffix) {
		return dc.toString() + suffix;
	}

	private static Color toAwt(org.bukkit.Color bukkitColor) {
		return new Color(bukkitColor.getRed(), bukkitColor.getGreen(), bukkitColor.getBlue());
	}

	private static Color shade(Color c, double factor) {
		return new Color(clamp(c.getRed() * factor), clamp(c.getGreen() * factor), clamp(c.getBlue() * factor));
	}

	private static Color blend(Color a, Color b, double weightA) {
		double weightB = 1 - weightA;
		return new Color(
				clamp(a.getRed() * weightA + b.getRed() * weightB),
				clamp(a.getGreen() * weightA + b.getGreen() * weightB),
				clamp(a.getBlue() * weightA + b.getBlue() * weightB));
	}

	private static Color applyShade(Color c, double shade) {
		return new Color(clamp(c.getRed() * shade), clamp(c.getGreen() * shade), clamp(c.getBlue() * shade));
	}

	private static int clamp(double v) {
		if (v < 0) return 0;
		if (v > 255) return 255;
		return (int) v;
	}
}
