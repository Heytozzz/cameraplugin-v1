package water.of.cup;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * One camera's independent settings: item, trigger mode, recipe, film item, shutter
 * sound and render/post-processing. Backed directly by its ConfigurationSection under
 * "cameras.<id>" in config.yml, so editing the yml and running /camerareload is enough
 * to pick up changes — nothing here is cached beyond the section reference itself.
 */
public class CameraProfile {

	private final String id;
	private final ConfigurationSection section;

	public CameraProfile(String id, ConfigurationSection section) {
		this.id = id;
		this.section = section;
	}

	public String getId() {
		return id;
	}

	// --- item ---

	public String getItemType() {
		return section.getString("item.type", "VANILLA");
	}

	public String getVanillaMaterial() {
		return section.getString("item.vanilla-material", "PLAYER_HEAD");
	}

	public String getItemsAdderId() {
		return section.getString("item.itemsadder-id", "");
	}

	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', section.getString("display-name", "&9Camera"));
	}

	/** CLICK: fires immediately on right-click.
	 *  HOLD: fires when the player releases right-click — only works with a real
	 *        release signal, i.e. vanilla-material: SPYGLASS.
	 *  TWO_STEP: first click zooms in (and waits), second click actually takes the
	 *            photo — works with any item, no release detection needed. */
	public String getTriggerMode() {
		return section.getString("trigger-mode", "CLICK");
	}

	// --- zoom (simulated via a particle-less Slowness effect while aiming) ---

	/** 0 = disabled, 1-7 = Slowness I through VII (Slowness's real max useful level). */
	public int getZoomLevel() {
		int level = section.getInt("zoom.level", 0);
		if (level < 0) return 0;
		return Math.min(level, 7);
	}

	/** Safety cap in case a release/complete event is ever missed — the effect always
	 *  wears off on its own after this many ticks even if our cleanup code doesn't run. */
	public int getZoomSafetyDurationTicks() {
		return section.getInt("zoom.safety-duration-ticks", 600);
	}

	// --- recipe (only used when item.type is VANILLA) ---

	public boolean isRecipeEnabled() {
		return section.getBoolean("recipe.enabled", true);
	}

	public List<String> getRecipeShape() {
		List<String> shape = section.getStringList("recipe.shape");
		return shape.isEmpty() ? List.of("IGI", "ITI", "IRI") : shape;
	}

	public ConfigurationSection getRecipeIngredients() {
		return section.getConfigurationSection("recipe.ingredients");
	}

	// --- filters ---

	/** This camera's own default filter (NONE, SEPIA, GRAYSCALE, VINTAGE, COOL, WARM, NOIR).
	 *  Used as-is unless a film-variants entry (see below) overrides it for that shot. */
	public Utils.Filter getFilter() {
		return Utils.Filter.fromString(section.getString("filter", "NONE"));
	}

	// --- "paper"/film item consumed per picture ---

	public String getPaperType() {
		return section.getString("paper.type", "VANILLA");
	}

	public String getPaperMaterial() {
		return section.getString("paper.material", "PAPER");
	}

	public String getPaperItemsAdderId() {
		return section.getString("paper.itemsadder-id", "");
	}

	/** One kind of film this camera accepts: what item it is, and which filter using it applies. */
	public static class FilmVariant {
		public final String id;
		public final String type;
		public final String material;
		public final String itemsAdderId;
		public final Utils.Filter filter;

		public FilmVariant(String id, String type, String material, String itemsAdderId, Utils.Filter filter) {
			this.id = id;
			this.type = type;
			this.material = material;
			this.itemsAdderId = itemsAdderId;
			this.filter = filter;
		}
	}

	/** Returns every film variant this camera accepts. If a "film-variants" section is
	 *  configured, each entry there is its own item + filter (e.g. plain paper = NONE,
	 *  a dye = SEPIA) — this is the "depends on the paper you use" mode. Otherwise falls
	 *  back to a single variant built from the legacy paper.* keys plus this camera's
	 *  own top-level filter. */
	public List<FilmVariant> getFilmVariants() {
		List<FilmVariant> variants = new ArrayList<>();
		ConfigurationSection variantsSection = section.getConfigurationSection("film-variants");
		if (variantsSection != null) {
			for (String variantId : variantsSection.getKeys(false)) {
				ConfigurationSection v = variantsSection.getConfigurationSection(variantId);
				if (v == null) {
					continue;
				}
				variants.add(new FilmVariant(
						variantId,
						v.getString("type", "VANILLA"),
						v.getString("material", "PAPER"),
						v.getString("itemsadder-id", ""),
						Utils.Filter.fromString(v.getString("filter", "NONE"))));
			}
		}
		if (variants.isEmpty()) {
			variants.add(new FilmVariant("default", getPaperType(), getPaperMaterial(), getPaperItemsAdderId(), getFilter()));
		}
		return variants;
	}

	// --- shutter sound ---

	public String getSoundKey() {
		return section.getString("sound.key", "minecraft:block.dispenser.dispense");
	}

	public float getSoundVolume() {
		return (float) section.getDouble("sound.volume", 1.0);
	}

	public float getSoundPitch() {
		return (float) section.getDouble("sound.pitch", 1.0);
	}

	// --- render / post-processing ---

	public boolean isShadowsEnabled() {
		return section.getBoolean("render.shadows", true);
	}

	/** How far (in blocks) rays travel before giving up and treating the pixel as sky.
	 *  Higher values fill in distant landscape backgrounds better but cost a bit more
	 *  per photo since more rays travel further before resolving. */
	public double getMaxDistance() {
		return section.getDouble("render.max-distance", 256);
	}

	/** How many of the 128 image columns get raycast per render() tick. Lower values
	 *  spread the cost of a photo across more ticks (less lag per tick, photo takes a
	 *  little longer to finish "developing"); higher values finish faster but cost more
	 *  per tick. 128 = old single-tick behavior. */
	public int getColumnsPerTick() {
		return section.getInt("render.columns-per-tick", 16);
	}

	public double getFov() {
		return section.getDouble("render.fov", 51.5);
	}

	public boolean isReliefEnabled() {
		return section.getBoolean("render.relief.enabled", true);
	}

	public double getReliefStrength() {
		return section.getDouble("render.relief.strength", 0.18);
	}

	public boolean isFogEnabled() {
		return section.getBoolean("render.fog.enabled", true);
	}

	public double getFogDistance() {
		return section.getDouble("render.fog.distance", 180);
	}

	public boolean isEntitiesEnabled() {
		return section.getBoolean("render.animals.enabled", true);
	}

	public double getEntityRaySize() {
		return section.getDouble("render.entity-ray-size", -0.15);
	}

	public Utils.PostFX getPostFx() {
		return new Utils.PostFX(
				section.getDouble("render.postprocess.brightness", 1.0),
				section.getDouble("render.postprocess.contrast", 1.0),
				section.getDouble("render.postprocess.saturation", 1.0),
				section.getDouble("render.postprocess.grain", 0.0));
	}
}
