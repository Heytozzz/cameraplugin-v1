package water.of.cup;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.map.MapPalette;

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
		blocksMap.put(Material.SHORT_GRASS, new Color(49,101,25));
		blocksMap.put(Material.TALL_GRASS, new Color(49,101,25));
		blocksMap.put(Material.LARGE_FERN, new Color(49,101,25));
		blocksMap.put(Material.FERN, new Color(49,101,25));
		blocksMap.put(Material.COBBLESTONE, new Color(130,130,130));
		blocksMap.put(Material.COBBLESTONE_STAIRS, new Color(130,130,130));
		blocksMap.put(Material.COBBLESTONE_SLAB, new Color(130,130,130));
		blocksMap.put(Material.FURNACE, new Color(130,130,130));
		blocksMap.put(Material.STONE, new Color(117,117,117));
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
		blocksMap.put(Material.SEAGRASS, new Color(67,101,165));
		blocksMap.put(Material.BUBBLE_COLUMN, new Color(67,101,165));
		blocksMap.put(Material.TALL_SEAGRASS, new Color(67,101,165));
		blocksMap.put(Material.KELP, new Color(67,101,165));
		blocksMap.put(Material.GRASS_BLOCK, new Color(82,129,69));
		blocksMap.put(Material.DIRT, new Color(168,120,83));
		blocksMap.put(Material.SAND, new Color(222,215,172));
		blocksMap.put(Material.SANDSTONE, new Color(213,207,162));
		blocksMap.put(Material.RED_SAND, new Color(191,108,49));
		blocksMap.put(Material.RED_SANDSTONE, new Color(180,100,50));
		blocksMap.put(Material.ACACIA_LEAVES, new Color(73,181,24));
		blocksMap.put(Material.BIRCH_LEAVES, new Color(114,149,76));
		blocksMap.put(Material.DARK_OAK_LEAVES, new Color(72,186,18));
		blocksMap.put(Material.JUNGLE_LEAVES, new Color(74,185,25));
		blocksMap.put(Material.OAK_LEAVES, new Color(73,183,24));
		blocksMap.put(Material.SPRUCE_LEAVES, new Color(55,91,56));
		blocksMap.put(Material.AZALEA_LEAVES, new Color(102,142,64));
		blocksMap.put(Material.FLOWERING_AZALEA_LEAVES, new Color(130,150,80));
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
		blocksMap.put(Material.MANGROVE_LEAVES, new Color(68,110,63));
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
	}

	// -----------------------------------------------------------------
	// Public API used by Renderer
	// -----------------------------------------------------------------

	private static final Color FOG_COLOR = new Color(190, 205, 220);

	/**
	 * Resolves the pixel color for a block, applying:
	 *  - shade: a brightness multiplier (vanilla light level combined with relief/edge shading)
	 *  - fogBlend: 0 = no fog (use the shaded color as-is), 1 = fully replaced by the fog color
	 * then matches the result to the closest vanilla map-palette color.
	 */
	public static byte colorFromType(Block block, double shade, double fogBlend) {
		Color base = resolveBlockColor(block.getType());
		if (base == null) {
			base = new Color(140, 140, 140); // neutral, not a harsh flat gray
		}
		Color shaded = applyShade(base, shade);
		Color foggy = blend(shaded, FOG_COLOR, 1 - fogBlend);
		return MapPalette.matchColor(foggy);
	}

	/** Resolves the pixel color for an entity (used for the "capture animals" feature). */
	public static byte colorFromEntity(Entity entity, double shade, double fogBlend) {
		Color base = resolveEntityColor(entity);
		Color shaded = applyShade(base, shade);
		Color foggy = blend(shaded, FOG_COLOR, 1 - fogBlend);
		return MapPalette.matchColor(foggy);
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
				Material base = Material.matchMaterial(name.substring(0, name.length() - suf.length()));
				if (base != null) {
					Color c = blocksMap.get(base);
					if (c != null) return c;
				}
			}
		}

		// 2) Generic material-family fallback (covers anything future Minecraft
		//    updates add that we haven't explicitly mapped yet).
		if (name.contains("LEAVES")) return new Color(65, 125, 55);
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
	// Resource-pack texture average color (fallback of last resort)
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
