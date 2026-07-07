package water.of.cup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * Tracks, per player, every plant/animal/hostile mob/boss/biome that has shown up in
 * one of their photos, and turns that into a written (signed) book — a Pokedex-style
 * collection album that fills in over time.
 */
public class AlbumManager {

	private static final Set<EntityType> ANIMAL_TYPES = EnumSet.of(
			EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.SHEEP,
			EntityType.RABBIT, EntityType.TURTLE, EntityType.PANDA, EntityType.FOX, EntityType.POLAR_BEAR,
			EntityType.GOAT, EntityType.LLAMA, EntityType.DONKEY, EntityType.MULE, EntityType.CAMEL,
			EntityType.FROG, EntityType.AXOLOTL, EntityType.BEE, EntityType.SQUID, EntityType.GLOW_SQUID,
			EntityType.DOLPHIN, EntityType.PARROT, EntityType.SNIFFER, EntityType.ARMADILLO, EntityType.STRIDER,
			EntityType.BAT, EntityType.CAT, EntityType.WOLF, EntityType.HORSE);

	private static final Set<EntityType> HOSTILE_TYPES = EnumSet.of(
			EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER,
			EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON,
			EntityType.CREEPER, EntityType.SPIDER, EntityType.CAVE_SPIDER,
			EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.WITCH,
			EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.EVOKER, EntityType.RAVAGER, EntityType.ILLUSIONER,
			EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.ZOMBIFIED_PIGLIN, EntityType.HOGLIN, EntityType.ZOGLIN,
			EntityType.GHAST, EntityType.BLAZE, EntityType.MAGMA_CUBE, EntityType.SLIME, EntityType.SHULKER,
			EntityType.PHANTOM, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN, EntityType.WARDEN);

	private static final Set<EntityType> BOSS_TYPES = EnumSet.of(EntityType.ENDER_DRAGON, EntityType.WITHER);

	private static File albumsFile;
	private static FileConfiguration albumsConfig;

	public static void load() {
		File dataFolder = Camera.getInstance().getDataFolder();
		albumsFile = new File(dataFolder, "albums.yml");
		if (!albumsFile.exists()) {
			try {
				albumsFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		albumsConfig = YamlConfiguration.loadConfiguration(albumsFile);
	}

	private static void save() {
		try {
			albumsConfig.save(albumsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Classifies whatever showed up in a photo and merges any new discoveries into
	 *  the photographer's persistent album, sending a chat notification for each. */
	public static void recordDiscoveries(Player player, Set<Material> blocks, Set<EntityType> entities) {
		if (albumsConfig == null) {
			load();
		}
		String base = "players." + player.getUniqueId() + ".";

		Set<String> plants = new HashSet<>(albumsConfig.getStringList(base + "plants"));
		Set<String> animals = new HashSet<>(albumsConfig.getStringList(base + "animals"));
		Set<String> hostiles = new HashSet<>(albumsConfig.getStringList(base + "hostiles"));
		Set<String> bosses = new HashSet<>(albumsConfig.getStringList(base + "bosses"));
		Set<String> biomes = new HashSet<>(albumsConfig.getStringList(base + "biomes"));

		boolean changed = false;

		for (Material mat : blocks) {
			if (Utils.isPlantMaterial(mat) && plants.add(mat.toString())) {
				changed = true;
				notifyDiscovery(player, "Planta", prettyName(mat.toString()));
			}
		}

		for (EntityType type : entities) {
			if (ANIMAL_TYPES.contains(type) && animals.add(type.toString())) {
				changed = true;
				notifyDiscovery(player, "Animal", prettyName(type.toString()));
			} else if (HOSTILE_TYPES.contains(type) && hostiles.add(type.toString())) {
				changed = true;
				notifyDiscovery(player, "Mob hostil", prettyName(type.toString()));
			} else if (BOSS_TYPES.contains(type) && bosses.add(type.toString())) {
				changed = true;
				notifyDiscovery(player, "\u00a76\u00a7lJEFE", prettyName(type.toString()));
			}
		}

		Biome biome = player.getLocation().getBlock().getBiome();
		if (biomes.add(biome.toString())) {
			changed = true;
			notifyDiscovery(player, "Bioma", prettyName(biome.toString()));
		}

		if (changed) {
			albumsConfig.set(base + "plants", new ArrayList<>(plants));
			albumsConfig.set(base + "animals", new ArrayList<>(animals));
			albumsConfig.set(base + "hostiles", new ArrayList<>(hostiles));
			albumsConfig.set(base + "bosses", new ArrayList<>(bosses));
			albumsConfig.set(base + "biomes", new ArrayList<>(biomes));
			save();
		}
	}

	private static void notifyDiscovery(Player player, String category, String name) {
		player.sendMessage(ChatColor.GOLD + "\u2605 Nuevo descubrimiento (" + category + ChatColor.GOLD + "): "
				+ ChatColor.WHITE + name);
	}

	private static String prettyName(String enumName) {
		String[] words = enumName.toLowerCase().split("_");
		StringBuilder sb = new StringBuilder();
		for (String w : words) {
			if (w.isEmpty()) continue;
			sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(' ');
		}
		return sb.toString().trim();
	}

	/** Builds an up-to-date written book for this player, reflecting their current
	 *  discoveries against the total possible in each category. */
	public static ItemStack generateBook(Player player) {
		if (albumsConfig == null) {
			load();
		}
		String base = "players." + player.getUniqueId() + ".";
		List<String> plants = albumsConfig.getStringList(base + "plants");
		List<String> animals = albumsConfig.getStringList(base + "animals");
		List<String> hostiles = albumsConfig.getStringList(base + "hostiles");
		List<String> bosses = albumsConfig.getStringList(base + "bosses");
		List<String> biomes = albumsConfig.getStringList(base + "biomes");

		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.setTitle(ChatColor.DARK_GREEN + "Album de " + player.getName());
		meta.setAuthor(player.getName());

		StringBuilder summary = new StringBuilder();
		summary.append(ChatColor.BOLD).append("Album de Fotos\n\n").append(ChatColor.RESET);
		summary.append("Plantas: ").append(plants.size()).append('\n');
		summary.append("Animales: ").append(animals.size()).append('\n');
		summary.append("Mobs hostiles: ").append(hostiles.size()).append('\n');
		summary.append("Jefes: ").append(bosses.size()).append('/').append(BOSS_TYPES.size()).append('\n');
		summary.append("Biomas: ").append(biomes.size()).append('/').append(Biome.values().length).append('\n');
		meta.addPage(summary.toString());

		for (String page : buildCategoryPages("Plantas", plants)) meta.addPage(page);
		for (String page : buildCategoryPages("Animales", animals)) meta.addPage(page);
		for (String page : buildCategoryPages("Mobs hostiles", hostiles)) meta.addPage(page);
		for (String page : buildCategoryPages("Jefes", bosses)) meta.addPage(page);
		for (String page : buildCategoryPages("Biomas", biomes)) meta.addPage(page);

		book.setItemMeta(meta);
		return book;
	}

	// Minecraft book pages have a character limit — this keeps well under it so long
	// category lists (lots of discoveries) spill onto additional pages instead of
	// getting silently truncated.
	private static final int MAX_ENTRIES_PER_PAGE = 12;

	private static List<String> buildCategoryPages(String title, List<String> entries) {
		List<String> pages = new ArrayList<>();
		List<String> sorted = new ArrayList<>(entries);
		sorted.sort(String::compareTo);

		if (sorted.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append(ChatColor.BOLD).append(title).append('\n').append(ChatColor.RESET).append('\n');
			sb.append(ChatColor.GRAY).append("(nada todavia)");
			pages.add(sb.toString());
			return pages;
		}

		for (int i = 0; i < sorted.size(); i += MAX_ENTRIES_PER_PAGE) {
			StringBuilder sb = new StringBuilder();
			sb.append(ChatColor.BOLD).append(title);
			if (sorted.size() > MAX_ENTRIES_PER_PAGE) {
				sb.append(" (").append(i + 1).append('-').append(Math.min(i + MAX_ENTRIES_PER_PAGE, sorted.size()))
						.append('/').append(sorted.size()).append(')');
			}
			sb.append('\n').append(ChatColor.RESET).append('\n');
			for (int j = i; j < Math.min(i + MAX_ENTRIES_PER_PAGE, sorted.size()); j++) {
				sb.append("- ").append(prettyName(sorted.get(j))).append('\n');
			}
			pages.add(sb.toString());
		}
		return pages;
	}
}
