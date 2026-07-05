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

	public String getTriggerMode() {
		return section.getString("trigger-mode", "CLICK");
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
