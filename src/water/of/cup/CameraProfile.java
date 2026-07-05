package water.of.cup;

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
