package water.of.cup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Tracks, per player, every plant/tree/animal/hostile mob/boss/biome that has shown up
 * in one of their photos, and turns that into a written (signed) book — a Pokedex-style
 * collection album that fills in over time, with clickable category/entry links and
 * per-entity info pages.
 */
public class AlbumManager {

	private static final NamespacedKey ALBUM_TAG = new NamespacedKey("cameras", "album_book");
	private static final int MAX_ENTRIES_PER_PAGE = 12;

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

	/** Classifies whatever showed up in a photo (already distance-filtered by Renderer)
	 *  and merges any new discoveries into the photographer's persistent album, sending
	 *  a chat notification and live-updating any copy of the book they're carrying. */
	public static void recordDiscoveries(Player player, Set<Material> blocks, Set<EntityType> entities) {
		if (albumsConfig == null) {
			load();
		}
		String base = "players." + player.getUniqueId() + ".";

		Set<String> plants = new HashSet<>(albumsConfig.getStringList(base + "plants"));
		Set<String> trees = new HashSet<>(albumsConfig.getStringList(base + "trees"));
		Set<String> animals = new HashSet<>(albumsConfig.getStringList(base + "animals"));
		Set<String> hostiles = new HashSet<>(albumsConfig.getStringList(base + "hostiles"));
		Set<String> bosses = new HashSet<>(albumsConfig.getStringList(base + "bosses"));
		Set<String> biomes = new HashSet<>(albumsConfig.getStringList(base + "biomes"));

		boolean changed = false;

		for (Material mat : blocks) {
			if (Utils.isTreeMaterial(mat) && trees.add(mat.toString())) {
				changed = true;
				notifyDiscovery(player, Lang.get(player, "discovery.tree"), prettyName(mat.toString()));
			} else if (Utils.isPlantMaterial(mat) && plants.add(mat.toString())) {
				changed = true;
				notifyDiscovery(player, Lang.get(player, "discovery.plant"), prettyName(mat.toString()));
			}
		}

		for (EntityType type : entities) {
			if (ANIMAL_TYPES.contains(type) && animals.add(type.toString())) {
				changed = true;
				notifyDiscovery(player, Lang.get(player, "discovery.animal"), prettyName(type.toString()));
			} else if (HOSTILE_TYPES.contains(type) && hostiles.add(type.toString())) {
				changed = true;
				notifyDiscovery(player, Lang.get(player, "discovery.hostile"), prettyName(type.toString()));
			} else if (BOSS_TYPES.contains(type) && bosses.add(type.toString())) {
				changed = true;
				notifyDiscovery(player, Lang.get(player, "discovery.boss"), prettyName(type.toString()));
			}
		}

		// Biome#toString() on the CraftBukkit wrapper prints an ugly internal dump
		// (CraftBiome{holder=...}) — the key's path (e.g. "plains") is the clean name.
		Biome biome = player.getLocation().getBlock().getBiome();
		String biomeName = biome.getKey().getKey();
		if (biomes.add(biomeName)) {
			changed = true;
			notifyDiscovery(player, Lang.get(player, "discovery.biome"), prettyName(biomeName));
		}

		if (changed) {
			albumsConfig.set(base + "plants", new ArrayList<>(plants));
			albumsConfig.set(base + "trees", new ArrayList<>(trees));
			albumsConfig.set(base + "animals", new ArrayList<>(animals));
			albumsConfig.set(base + "hostiles", new ArrayList<>(hostiles));
			albumsConfig.set(base + "bosses", new ArrayList<>(bosses));
			albumsConfig.set(base + "biomes", new ArrayList<>(biomes));
			save();
			refreshHeldBooks(player);
		}
	}

	private static void notifyDiscovery(Player player, String category, String name) {
		player.sendMessage(ChatColor.GOLD + "\u2605 " + category + ": " + ChatColor.WHITE + name);
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

	// -----------------------------------------------------------------
	// Book generation
	// -----------------------------------------------------------------

	/** Gives the player a fresh copy of their album book (adds to inventory, or drops
	 *  it at their feet if their inventory is full). */
	public static ItemStack generateBook(Player player) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		applyBookContent(book, player);
		return book;
	}

	/** Finds any copy of the album book the player is currently carrying (tagged via
	 *  persistent data) and rewrites its contents in place, so it updates live instead
	 *  of needing to be re-fetched with /cameraalbum after every new discovery. */
	private static void refreshHeldBooks(Player player) {
		PlayerInventory inv = player.getInventory();
		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (isAlbumBook(stack)) {
				applyBookContent(stack, player);
				inv.setItem(i, stack);
			}
		}
	}

	private static boolean isAlbumBook(ItemStack stack) {
		if (stack == null || stack.getType() != Material.WRITTEN_BOOK || !stack.hasItemMeta()) {
			return false;
		}
		return stack.getItemMeta().getPersistentDataContainer().has(ALBUM_TAG, PersistentDataType.BOOLEAN);
	}

	private static void applyBookContent(ItemStack book, Player player) {
		if (albumsConfig == null) {
			load();
		}
		String base = "players." + player.getUniqueId() + ".";
		List<String> plants = sorted(albumsConfig.getStringList(base + "plants"));
		List<String> trees = sorted(albumsConfig.getStringList(base + "trees"));
		List<String> animals = sorted(albumsConfig.getStringList(base + "animals"));
		List<String> hostiles = sorted(albumsConfig.getStringList(base + "hostiles"));
		List<String> bosses = sorted(albumsConfig.getStringList(base + "bosses"));
		List<String> biomes = sorted(albumsConfig.getStringList(base + "biomes"));

		// --- work out page numbers for every section up front, so the index and list
		//     entries can embed the right changePage() targets ---
		int page = 1; // page 1 = index

		int plantListStart = page + 1;
		int plantPages = listPageCount(plants.size());
		page += plantPages;

		int treeListStart = page + 1;
		int treePages = listPageCount(trees.size());
		page += treePages;

		int animalListStart = page + 1;
		int animalPages = listPageCount(animals.size());
		page += animalPages;

		int hostileListStart = page + 1;
		int hostilePages = listPageCount(hostiles.size());
		page += hostilePages;

		int bossListStart = page + 1;
		int bossPages = listPageCount(bosses.size());
		page += bossPages;

		int biomeListStart = page + 1;
		int biomePages = listPageCount(biomes.size());
		page += biomePages;

		int animalDetailStart = page + 1;
		page += animals.size();

		int hostileDetailStart = page + 1;
		page += hostiles.size();

		int bossDetailStart = page + 1;
		page += bosses.size();

		List<Component> pages = new ArrayList<>();
		pages.add(buildIndexPage(player, plants.size(), trees.size(), animals.size(), hostiles.size(),
				bosses.size(), biomes.size(), plantListStart, treeListStart, animalListStart,
				hostileListStart, bossListStart, biomeListStart));

		pages.addAll(buildListPages(player, "book.plants", plants, null, 1));
		pages.addAll(buildListPages(player, "book.trees", trees, null, 1));
		pages.addAll(buildListPages(player, "book.animals", animals, animalDetailStart, 1));
		pages.addAll(buildListPages(player, "book.hostiles", hostiles, hostileDetailStart, 1));
		pages.addAll(buildListPages(player, "book.bosses", bosses, bossDetailStart, 1));
		pages.addAll(buildListPages(player, "book.biomes", biomes, null, 1));

		for (int i = 0; i < animals.size(); i++) {
			pages.add(buildDetailPage(player, animals.get(i), animalListStart));
		}
		for (int i = 0; i < hostiles.size(); i++) {
			pages.add(buildDetailPage(player, hostiles.get(i), hostileListStart));
		}
		for (int i = 0; i < bosses.size(); i++) {
			pages.add(buildDetailPage(player, bosses.get(i), bossListStart));
		}

		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.title(Component.text(Lang.get(player, "book.title")));
		meta.author(Component.text(player.getName()));
		meta.addPages(pages.toArray(new Component[0]));
		meta.getPersistentDataContainer().set(ALBUM_TAG, PersistentDataType.BOOLEAN, true);
		book.setItemMeta(meta);
	}

	private static List<String> sorted(List<String> list) {
		List<String> copy = new ArrayList<>(list);
		copy.sort(String::compareTo);
		return copy;
	}

	private static int listPageCount(int entryCount) {
		return Math.max(1, (int) Math.ceil(entryCount / (double) MAX_ENTRIES_PER_PAGE));
	}

	private static Component buildIndexPage(Player player, int plantCount, int treeCount, int animalCount,
			int hostileCount, int bossCount, int biomeCount, int plantPage, int treePage, int animalPage,
			int hostilePage, int biomePage, int bossListPage) {
		Component page = Component.text(Lang.get(player, "book.index"), NamedTextColor.DARK_BLUE, TextDecoration.BOLD)
				.appendNewline().appendNewline();

		page = page.append(indexLine(player, "book.plants", plantCount, Utils.getTotalPlantMaterials(), plantPage))
				.append(indexLine(player, "book.trees", treeCount, Utils.getTotalTreeMaterials(), treePage))
				.append(indexLine(player, "book.animals", animalCount, ANIMAL_TYPES.size(), animalPage))
				.append(indexLine(player, "book.hostiles", hostileCount, HOSTILE_TYPES.size(), hostilePage))
				.append(indexLine(player, "book.bosses", bossCount, BOSS_TYPES.size(), bossListPage))
				.append(indexLine(player, "book.biomes", biomeCount, Biome.values().length, biomePage));

		return page;
	}

	private static Component indexLine(Player player, String labelKey, int have, int total, int targetPage) {
		String label = Lang.get(player, labelKey);
		return Component.text(label + ": ", NamedTextColor.BLACK)
				.append(Component.text(have + "/" + total, NamedTextColor.DARK_GREEN))
				.clickEvent(ClickEvent.changePage(targetPage))
				.hoverEvent(HoverEvent.showText(Component.text(Lang.get(player, "book.clickhint"), NamedTextColor.GRAY)))
				.decorate(TextDecoration.UNDERLINED)
				.appendNewline();
	}

	/** detailStart == null means entries aren't clickable to a detail page (plants, trees, biomes). */
	private static List<Component> buildListPages(Player player, String titleKey, List<String> entries,
			Integer detailStart, int backPage) {
		List<Component> result = new ArrayList<>();
		String title = Lang.get(player, titleKey);

		if (entries.isEmpty()) {
			result.add(Component.text(title, NamedTextColor.DARK_BLUE, TextDecoration.BOLD)
					.appendNewline().appendNewline()
					.append(Component.text(Lang.get(player, "book.empty"), NamedTextColor.GRAY))
					.appendNewline().appendNewline()
					.append(backLink(player, backPage)));
			return result;
		}

		for (int i = 0; i < entries.size(); i += MAX_ENTRIES_PER_PAGE) {
			int end = Math.min(i + MAX_ENTRIES_PER_PAGE, entries.size());
			Component page = Component.text(title, NamedTextColor.DARK_BLUE, TextDecoration.BOLD);
			if (entries.size() > MAX_ENTRIES_PER_PAGE) {
				page = page.append(Component.text(" (" + (i + 1) + "-" + end + "/" + entries.size() + ")", NamedTextColor.DARK_GRAY));
			}
			page = page.appendNewline().appendNewline();

			for (int j = i; j < end; j++) {
				String raw = entries.get(j);
				Component nameComp = Component.text("- " + prettyName(raw), NamedTextColor.DARK_GREEN);
				if (detailStart != null) {
					EntityInfoData.Info info = tryGetEntityInfo(raw);
					Component hover = info != null
							? Component.text(heartsString(info.maxHealth) + " (" + (int) Math.round(info.maxHealth / 2.0)
									+ " " + Lang.get(player, "info.hearts") + ")", NamedTextColor.RED)
							: Component.text(prettyName(raw));
					nameComp = nameComp.decorate(TextDecoration.UNDERLINED)
							.clickEvent(ClickEvent.changePage(detailStart + j))
							.hoverEvent(HoverEvent.showText(hover));
				}
				page = page.append(nameComp).appendNewline();
			}
			page = page.appendNewline().append(backLink(player, backPage));
			result.add(page);
		}
		return result;
	}

	private static Component buildDetailPage(Player player, String rawEntityType, int backPage) {
		EntityInfoData.Info info = tryGetEntityInfo(rawEntityType);
		Component page = Component.text(prettyName(rawEntityType), NamedTextColor.DARK_RED, TextDecoration.BOLD)
				.appendNewline().appendNewline();

		if (info != null) {
			page = page.append(Component.text(Lang.get(player, "info.health") + ": ", NamedTextColor.BLACK))
					.append(Component.text(heartsString(info.maxHealth), NamedTextColor.RED))
					.appendNewline()
					.append(Component.text(Lang.get(player, "info.biomes") + ": ", NamedTextColor.BLACK))
					.append(Component.text(info.spawnBiomes, NamedTextColor.DARK_GREEN))
					.appendNewline();

			if (!info.food.isEmpty()) {
				page = page.append(Component.text(Lang.get(player, "info.food") + ": ", NamedTextColor.BLACK))
						.append(Component.text(materialListText(info.food), NamedTextColor.GOLD))
						.appendNewline();
			} else {
				page = page.append(Component.text(Lang.get(player, "info.nofood"), NamedTextColor.GRAY)).appendNewline();
			}

			if (!info.breedsWith.isEmpty()) {
				page = page.append(Component.text(Lang.get(player, "info.breeding") + ": ", NamedTextColor.BLACK))
						.append(Component.text(materialListText(info.breedsWith), NamedTextColor.LIGHT_PURPLE))
						.appendNewline();
			} else {
				page = page.append(Component.text(Lang.get(player, "info.notbreedable"), NamedTextColor.GRAY)).appendNewline();
			}
		}

		page = page.appendNewline().append(backLink(player, backPage));
		return page;
	}

	private static Component backLink(Player player, int backPage) {
		return Component.text(Lang.get(player, "book.back"), NamedTextColor.BLUE, TextDecoration.UNDERLINED)
				.clickEvent(ClickEvent.changePage(backPage));
	}

	private static EntityInfoData.Info tryGetEntityInfo(String rawEntityType) {
		try {
			return EntityInfoData.get(EntityType.valueOf(rawEntityType));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private static String materialListText(List<Material> materials) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < materials.size(); i++) {
			if (i > 0) sb.append(", ");
			sb.append(prettyName(materials.get(i).toString()));
		}
		return sb.toString();
	}

	/** Hearts-as-sprites: a book can't embed real texture icons, so this uses the closest
	 *  practical equivalent — repeated heart glyphs, same idea as vanilla's health bar. */
	private static String heartsString(double maxHealth) {
		int hearts = (int) Math.round(maxHealth / 2.0);
		int shown = Math.min(hearts, 20);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < shown; i++) {
			sb.append('\u2764');
		}
		if (hearts > shown) {
			sb.append(" x").append(hearts);
		}
		return sb.toString();
	}
}
