package water.of.cup;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

/**
 * Lightweight translation lookup. Covers plugin-generated UI text (album book labels,
 * discovery messages, entity info field labels) in English and Spanish. Does NOT
 * translate proper names (block/entity/biome names) — those come from Minecraft's own
 * naming, not this plugin's text, so translating them accurately would need a full
 * copy of Minecraft's own language files; out of scope here.
 */
public class Lang {

	private static final Map<String, Map<String, String>> MESSAGES = new HashMap<>();

	static {
		Map<String, String> en = new HashMap<>();
		en.put("discovery.plant", "New discovery (Plant)");
		en.put("discovery.tree", "New discovery (Tree)");
		en.put("discovery.animal", "New discovery (Animal)");
		en.put("discovery.hostile", "New discovery (Hostile mob)");
		en.put("discovery.boss", "New discovery (BOSS)");
		en.put("discovery.biome", "New discovery (Biome)");
		en.put("book.title", "Photo Album");
		en.put("book.index", "Photo Album");
		en.put("book.plants", "Plants");
		en.put("book.trees", "Trees");
		en.put("book.animals", "Animals");
		en.put("book.hostiles", "Hostile mobs");
		en.put("book.bosses", "Bosses");
		en.put("book.biomes", "Biomes");
		en.put("book.empty", "(nothing yet)");
		en.put("book.back", "< Back to index");
		en.put("book.clickhint", "(click to open)");
		en.put("info.health", "Max health");
		en.put("info.drops", "Drops");
		en.put("info.biomes", "Spawns in");
		en.put("info.food", "Eats");
		en.put("info.breeding", "Breeds with");
		en.put("info.notbreedable", "Doesn't breed");
		en.put("info.nofood", "Doesn't eat");
		en.put("info.hearts", "hearts");
		MESSAGES.put("en", en);

		Map<String, String> es = new HashMap<>();
		es.put("discovery.plant", "Nuevo descubrimiento (Planta)");
		es.put("discovery.tree", "Nuevo descubrimiento (Arbol)");
		es.put("discovery.animal", "Nuevo descubrimiento (Animal)");
		es.put("discovery.hostile", "Nuevo descubrimiento (Mob hostil)");
		es.put("discovery.boss", "Nuevo descubrimiento (JEFE)");
		es.put("discovery.biome", "Nuevo descubrimiento (Bioma)");
		es.put("book.title", "Album de Fotos");
		es.put("book.index", "Album de Fotos");
		es.put("book.plants", "Plantas");
		es.put("book.trees", "Arboles");
		es.put("book.animals", "Animales");
		es.put("book.hostiles", "Mobs hostiles");
		es.put("book.bosses", "Jefes");
		es.put("book.biomes", "Biomas");
		es.put("book.empty", "(nada todavia)");
		es.put("book.back", "< Volver al indice");
		es.put("book.clickhint", "(click para abrir)");
		es.put("info.health", "Vida maxima");
		es.put("info.drops", "Suelta");
		es.put("info.biomes", "Se genera en");
		es.put("info.food", "Come");
		es.put("info.breeding", "Se reproduce con");
		es.put("info.notbreedable", "No se reproduce");
		es.put("info.nofood", "No come");
		es.put("info.hearts", "corazones");
		MESSAGES.put("es", es);
	}

	public static String get(Player player, String key) {
		String lang = resolveLang(player);
		Map<String, String> map = MESSAGES.getOrDefault(lang, MESSAGES.get("en"));
		String value = map.get(key);
		if (value != null) {
			return value;
		}
		return MESSAGES.get("en").getOrDefault(key, key);
	}

	private static String resolveLang(Player player) {
		String configured = Camera.getInstance().getConfig().getString("settings.language", "auto");
		if (!"auto".equalsIgnoreCase(configured)) {
			return configured.toLowerCase();
		}
		String locale = player.getLocale();
		if (locale != null && locale.toLowerCase().startsWith("es")) {
			return "es";
		}
		return "en";
	}
}
