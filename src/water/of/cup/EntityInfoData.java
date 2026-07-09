package water.of.cup;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class EntityInfoData {

	public static class Info {
		public final double maxHealth;
		public final String spawnBiomes;
		public final List<Material> food;
		public final List<Material> breedsWith;

		public Info(double maxHealth, String spawnBiomes, List<Material> food, List<Material> breedsWith) {
			this.maxHealth = maxHealth;
			this.spawnBiomes = spawnBiomes;
			this.food = food;
			this.breedsWith = breedsWith;
		}

		public Info(double maxHealth, String spawnBiomes) {
			this(maxHealth, spawnBiomes, Collections.emptyList(), Collections.emptyList());
		}
	}

	private static final Map<EntityType, Info> DATA = new HashMap<>();

	static {
		// --- animals ---
		DATA.put(EntityType.COW, new Info(10.0, "Plains and most grassy biomes",
				List.of(Material.WHEAT), List.of(Material.WHEAT)));
		DATA.put(EntityType.MOOSHROOM, new Info(10.0, "Mushroom Fields only",
				List.of(Material.WHEAT), List.of(Material.WHEAT)));
		DATA.put(EntityType.PIG, new Info(10.0, "Plains and most grassy biomes",
				List.of(Material.CARROT, Material.POTATO, Material.BEETROOT),
				List.of(Material.CARROT, Material.POTATO, Material.BEETROOT)));
		DATA.put(EntityType.CHICKEN, new Info(4.0, "Plains and most grassy biomes",
				List.of(Material.WHEAT_SEEDS, Material.MELON_SEEDS, Material.PUMPKIN_SEEDS, Material.BEETROOT_SEEDS),
				List.of(Material.WHEAT_SEEDS, Material.MELON_SEEDS, Material.PUMPKIN_SEEDS, Material.BEETROOT_SEEDS)));
		DATA.put(EntityType.SHEEP, new Info(8.0, "Plains and most grassy biomes",
				List.of(Material.WHEAT), List.of(Material.WHEAT)));
		DATA.put(EntityType.RABBIT, new Info(3.0, "Plains, deserts, taiga, snowy biomes",
				List.of(Material.CARROT, Material.GOLDEN_CARROT, Material.DANDELION),
				List.of(Material.CARROT, Material.GOLDEN_CARROT, Material.DANDELION)));
		DATA.put(EntityType.TURTLE, new Info(30.0, "Beaches",
				List.of(Material.SEAGRASS), List.of(Material.SEAGRASS)));
		DATA.put(EntityType.PANDA, new Info(20.0, "Jungle (bamboo jungle)",
				List.of(Material.BAMBOO), List.of(Material.BAMBOO)));
		DATA.put(EntityType.FOX, new Info(10.0, "Taiga, snowy taiga",
				List.of(Material.SWEET_BERRIES, Material.GLOW_BERRIES),
				List.of(Material.SWEET_BERRIES, Material.GLOW_BERRIES)));
		DATA.put(EntityType.POLAR_BEAR, new Info(30.0, "Frozen/snowy biomes"));
		DATA.put(EntityType.GOAT, new Info(10.0, "Mountains, snowy slopes",
				List.of(Material.WHEAT), List.of(Material.WHEAT)));
		DATA.put(EntityType.LLAMA, new Info(15.0, "Savanna, mountains",
				List.of(Material.WHEAT, Material.HAY_BLOCK), List.of(Material.HAY_BLOCK)));
		DATA.put(EntityType.DONKEY, new Info(15.0, "Plains",
				List.of(Material.GOLDEN_APPLE, Material.GOLDEN_CARROT), List.of(Material.GOLDEN_APPLE, Material.GOLDEN_CARROT)));
		DATA.put(EntityType.MULE, new Info(15.0, "Doesn't spawn naturally (bred from horse + donkey)"));
		DATA.put(EntityType.CAMEL, new Info(32.0, "Desert (village)"));
		DATA.put(EntityType.FROG, new Info(10.0, "Swamp, mangrove swamp",
				List.of(Material.SLIME_BALL), List.of(Material.SLIME_BALL)));
		DATA.put(EntityType.AXOLOTL, new Info(14.0, "Lush caves, underground water",
				List.of(Material.TROPICAL_FISH_BUCKET), List.of(Material.TROPICAL_FISH_BUCKET)));
		DATA.put(EntityType.BEE, new Info(10.0, "Plains, flower forest, sunflower plains, meadow",
				List.of(Material.DANDELION, Material.POPPY), List.of(Material.DANDELION, Material.POPPY)));
		DATA.put(EntityType.SQUID, new Info(10.0, "Ocean"));
		DATA.put(EntityType.GLOW_SQUID, new Info(10.0, "Underground water, lush/dripstone caves"));
		DATA.put(EntityType.DOLPHIN, new Info(10.0, "Ocean"));
		DATA.put(EntityType.PARROT, new Info(6.0, "Jungle"));
		DATA.put(EntityType.SNIFFER, new Info(14.0, "Doesn't spawn naturally (Sniffer Egg only)",
				List.of(Material.TORCHFLOWER_SEEDS), List.of(Material.TORCHFLOWER_SEEDS)));
		DATA.put(EntityType.ARMADILLO, new Info(12.0, "Savanna",
				List.of(Material.SPIDER_EYE), List.of(Material.SPIDER_EYE)));
		DATA.put(EntityType.STRIDER, new Info(20.0, "Nether (on lava)",
				List.of(Material.WARPED_FUNGUS), List.of(Material.WARPED_FUNGUS)));
		DATA.put(EntityType.BAT, new Info(6.0, "Caves, dark areas underground"));
		DATA.put(EntityType.CAT, new Info(10.0, "Tamed from ocelots (jungle) or found as strays in villages",
				List.of(Material.COD, Material.SALMON), List.of(Material.COD, Material.SALMON)));
		DATA.put(EntityType.WOLF, new Info(8.0, "Forest, taiga",
				List.of(Material.PORKCHOP, Material.BEEF, Material.CHICKEN, Material.RABBIT, Material.MUTTON),
				List.of(Material.PORKCHOP, Material.BEEF, Material.CHICKEN, Material.RABBIT, Material.MUTTON)));

		// --- hostile mobs (health + spawn conditions only) ---
		DATA.put(EntityType.ZOMBIE, new Info(20.0, "Overworld, dark areas at night"));
		DATA.put(EntityType.HUSK, new Info(20.0, "Desert, at night"));
		DATA.put(EntityType.DROWNED, new Info(20.0, "Ocean, rivers"));
		DATA.put(EntityType.ZOMBIE_VILLAGER, new Info(20.0, "Rare zombie spawn, or a villager converted by a zombie"));
		DATA.put(EntityType.SKELETON, new Info(20.0, "Overworld, dark areas at night"));
		DATA.put(EntityType.STRAY, new Info(20.0, "Snowy/icy biomes"));
		DATA.put(EntityType.WITHER_SKELETON, new Info(20.0, "Nether fortresses"));
		DATA.put(EntityType.CREEPER, new Info(20.0, "Overworld, dark areas at night"));
		DATA.put(EntityType.SPIDER, new Info(16.0, "Overworld, dark areas at night"));
		DATA.put(EntityType.CAVE_SPIDER, new Info(12.0, "Abandoned mineshaft spawners"));
		DATA.put(EntityType.ENDERMAN, new Info(40.0, "Overworld, Nether, the End — dark areas"));
		DATA.put(EntityType.ENDERMITE, new Info(8.0, "Rarely, when throwing ender pearls"));
		DATA.put(EntityType.SILVERFISH, new Info(8.0, "Infested stone blocks"));
		DATA.put(EntityType.WITCH, new Info(26.0, "Swamp huts; villagers struck by lightning"));
		DATA.put(EntityType.PILLAGER, new Info(24.0, "Pillager outposts, raids"));
		DATA.put(EntityType.VINDICATOR, new Info(24.0, "Woodland mansions, raids"));
		DATA.put(EntityType.EVOKER, new Info(24.0, "Woodland mansions, raids"));
		DATA.put(EntityType.RAVAGER, new Info(100.0, "Raids"));
		DATA.put(EntityType.ILLUSIONER, new Info(32.0, "Very rare, mostly summon-only"));
		DATA.put(EntityType.PIGLIN, new Info(16.0, "Nether wastes, crimson forest"));
		DATA.put(EntityType.PIGLIN_BRUTE, new Info(50.0, "Bastion remnants"));
		DATA.put(EntityType.ZOMBIFIED_PIGLIN, new Info(20.0, "Nether wastes"));
		DATA.put(EntityType.HOGLIN, new Info(40.0, "Crimson forest, nether wastes"));
		DATA.put(EntityType.ZOGLIN, new Info(40.0, "Converted from a Hoglin in the Overworld/End"));
		DATA.put(EntityType.GHAST, new Info(10.0, "Nether"));
		DATA.put(EntityType.BLAZE, new Info(20.0, "Nether fortresses"));
		DATA.put(EntityType.MAGMA_CUBE, new Info(16.0, "Nether (varies with size)"));
		DATA.put(EntityType.SLIME, new Info(16.0, "Swamps, slime chunks underground (varies with size)"));
		DATA.put(EntityType.SHULKER, new Info(30.0, "End cities"));
		DATA.put(EntityType.PHANTOM, new Info(20.0, "Overworld sky, at night, if you haven't slept"));
		DATA.put(EntityType.GUARDIAN, new Info(30.0, "Ocean monuments"));
		DATA.put(EntityType.ELDER_GUARDIAN, new Info(80.0, "Ocean monuments (3 fixed spawns)"));
		DATA.put(EntityType.WARDEN, new Info(500.0, "Deep Dark, summoned by sculk shriekers"));

		// --- bosses ---
		DATA.put(EntityType.ENDER_DRAGON, new Info(200.0, "The End"));
		DATA.put(EntityType.WITHER, new Info(300.0, "Summoned by the player (soul sand/soil + wither skulls)"));
	}

	public static Info get(EntityType type) {
		return DATA.get(type);
	}
}
