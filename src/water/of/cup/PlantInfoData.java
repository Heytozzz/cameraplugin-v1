package water.of.cup;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public class PlantInfoData {

	public static class Info {
		public final String drops;
		public final String spawnZones;

		public Info(String drops, String spawnZones) {
			this.drops = drops;
			this.spawnZones = spawnZones;
		}
	}

	private static final Map<Material, Info> DATA = new HashMap<>();

	static {
		DATA.put(Material.SHORT_GRASS, new Info("Nothing (rarely wheat seeds)", "Most grassy biomes"));
		DATA.put(Material.TALL_GRASS, new Info("Nothing (rarely wheat seeds)", "Most grassy biomes"));
		DATA.put(Material.FERN, new Info("Nothing (rarely wheat seeds)", "Jungle, taiga"));
		DATA.put(Material.LARGE_FERN, new Info("Nothing (rarely wheat seeds)", "Jungle, taiga"));
		DATA.put(Material.DEAD_BUSH, new Info("Sticks", "Desert, badlands, swamp"));
		DATA.put(Material.DANDELION, new Info("Itself", "Most grassy biomes"));
		DATA.put(Material.POPPY, new Info("Itself", "Most grassy biomes, forest"));
		DATA.put(Material.AZURE_BLUET, new Info("Itself", "Plains, flower forest"));
		DATA.put(Material.OXEYE_DAISY, new Info("Itself", "Plains, flower forest"));
		DATA.put(Material.CORNFLOWER, new Info("Itself", "Plains, flower forest"));
		DATA.put(Material.SUNFLOWER, new Info("Itself (2 per plant)", "Sunflower plains"));
		DATA.put(Material.LILAC, new Info("Itself (2 per plant)", "Forest"));
		DATA.put(Material.ROSE_BUSH, new Info("Itself (2 per plant)", "Forest"));
		DATA.put(Material.PEONY, new Info("Itself (2 per plant)", "Forest"));
		DATA.put(Material.VINE, new Info("Nothing", "Jungle, swamp"));
		DATA.put(Material.WEEPING_VINES, new Info("Nothing", "Nether (crimson forest)"));
		DATA.put(Material.TWISTING_VINES, new Info("Nothing", "Nether (warped forest)"));
		DATA.put(Material.CAVE_VINES, new Info("Glow berries (when fruited)", "Lush caves"));
		DATA.put(Material.LILY_PAD, new Info("Itself", "Swamp"));
		DATA.put(Material.SUGAR_CANE, new Info("Itself", "Near water, most biomes"));
		DATA.put(Material.BAMBOO, new Info("Itself", "Jungle (bamboo jungle)"));
		DATA.put(Material.SEAGRASS, new Info("Nothing", "Ocean, river"));
		DATA.put(Material.TALL_SEAGRASS, new Info("Nothing", "Ocean"));
		DATA.put(Material.KELP, new Info("Itself", "Ocean"));
		DATA.put(Material.GLOW_LICHEN, new Info("Nothing", "Caves, lush caves"));
		DATA.put(Material.SPORE_BLOSSOM, new Info("Nothing", "Lush caves (ceiling)"));
		DATA.put(Material.BIG_DRIPLEAF, new Info("Nothing", "Lush caves"));
		DATA.put(Material.SMALL_DRIPLEAF, new Info("Nothing", "Lush caves"));
		DATA.put(Material.WHEAT, new Info("Wheat + seeds (when grown)", "Farmland (planted)"));
		DATA.put(Material.CARROTS, new Info("Carrots (when grown)", "Farmland (planted)"));
		DATA.put(Material.POTATOES, new Info("Potatoes (when grown)", "Farmland (planted)"));
		DATA.put(Material.BEETROOTS, new Info("Beetroot + seeds (when grown)", "Farmland (planted)"));
		DATA.put(Material.NETHER_WART, new Info("Nether wart (when grown)", "Nether (soul sand, planted)"));
		DATA.put(Material.CRIMSON_FUNGUS, new Info("Itself", "Nether (crimson forest)"));
		DATA.put(Material.WARPED_FUNGUS, new Info("Itself", "Nether (warped forest)"));
		DATA.put(Material.CACTUS, new Info("Itself", "Desert, badlands"));
		DATA.put(Material.MOSS_CARPET, new Info("Itself", "Lush caves"));
		DATA.put(Material.AZALEA, new Info("Itself", "Lush caves (surface patches)"));
		DATA.put(Material.FLOWERING_AZALEA, new Info("Itself", "Lush caves (surface patches)"));
		DATA.put(Material.BROWN_MUSHROOM, new Info("Itself", "Dark areas, forest, swamp"));
		DATA.put(Material.RED_MUSHROOM, new Info("Itself", "Dark areas, forest, swamp"));
		DATA.put(Material.MELON, new Info("Melon slices", "Jungle (naturally), or farmed"));
		DATA.put(Material.PUMPKIN, new Info("Itself / seeds when carved", "Plains, most grassy biomes"));

		// --- tree leaves ---
		DATA.put(Material.OAK_LEAVES, new Info("Sticks, apples (rare), saplings (rare)", "Forest, plains, most temperate biomes"));
		DATA.put(Material.BIRCH_LEAVES, new Info("Sticks, saplings (rare)", "Birch forest"));
		DATA.put(Material.SPRUCE_LEAVES, new Info("Sticks, saplings (rare)", "Taiga"));
		DATA.put(Material.JUNGLE_LEAVES, new Info("Sticks, saplings (rare)", "Jungle"));
		DATA.put(Material.ACACIA_LEAVES, new Info("Sticks, saplings (rare)", "Savanna"));
		DATA.put(Material.DARK_OAK_LEAVES, new Info("Sticks, saplings (rare)", "Dark forest"));
		DATA.put(Material.MANGROVE_LEAVES, new Info("Sticks, propagules (rare)", "Mangrove swamp"));
		DATA.put(Material.CHERRY_LEAVES, new Info("Sticks, saplings (rare)", "Cherry grove"));
		DATA.put(Material.AZALEA_LEAVES, new Info("Sticks, azalea (rare)", "Lush caves"));
		DATA.put(Material.FLOWERING_AZALEA_LEAVES, new Info("Sticks, flowering azalea (rare)", "Lush caves"));
	}

	public static Info get(Material mat) {
		return DATA.get(mat);
	}
}
