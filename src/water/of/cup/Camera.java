package water.of.cup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import water.of.cup.commands.CameraCommands;
import water.of.cup.listeners.CameraClick;
import water.of.cup.listeners.CameraPlace;

public class Camera extends JavaPlugin {

	private static Camera instance;
	List<Integer> mapIDsNotToRender = new ArrayList<>();
	ResourcePackManager resourcePackManager = new ResourcePackManager();
	private final Map<String, CameraProfile> cameraProfiles = new LinkedHashMap<>();
	private File configFile;
	private FileConfiguration config;

	@Override
	public void onEnable() {
		instance = this;

		loadConfig();
		Utils.loadColors();
		loadCameraProfiles();

		for (CameraProfile profile : cameraProfiles.values()) {
			if ("HOLD".equalsIgnoreCase(profile.getTriggerMode())
					&& "VANILLA".equalsIgnoreCase(profile.getItemType())
					&& !"SPYGLASS".equalsIgnoreCase(profile.getVanillaMaterial())) {
				Bukkit.getLogger().warning("[Cameras] camera '" + profile.getId() + "' has trigger-mode HOLD but "
						+ "vanilla-material isn't SPYGLASS — there's no release signal for other items, so it will "
						+ "fire on click instead.");
			}
		}

		// Loading the resource pack (possibly downloading it) is slow I/O — running it
		// synchronously here was what made "Enabling Cameras v0.1" hang the whole server
		// startup. Doing it async lets the server keep booting; CameraClick checks
		// resourcePackManager.isLoaded() and politely asks players to wait if they try
		// to use a camera before it's done.
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> this.resourcePackManager.initialize());

		File folder = new File(getDataFolder(), "maps");
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles == null) {
			listOfFiles = new File[0];
		}

		for (File file : listOfFiles) {
			if (file.isFile()) {
				int mapId = Integer.parseInt(file.getName().split("_")[1].split(Pattern.quote("."))[0]);
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String encodedData = br.readLine();

					MapView mapView = Bukkit.getMap(Integer.valueOf(mapId));

					mapView.setTrackingPosition(false);
					for(MapRenderer renderer : mapView.getRenderers())
						mapView.removeRenderer(renderer);

					mapView.addRenderer(new MapRenderer() {
						@Override
						public void render(MapView mapViewNew, MapCanvas mapCanvas, Player player) {
							if(!mapIDsNotToRender.contains(mapId)) {
								mapIDsNotToRender.add(mapId);

								int x = 0;
								int y = 0;
								int skipsLeft = 0;
								byte colorByte = 0;
								for(int index = 0; index < encodedData.length(); index++) {
									if(skipsLeft == 0) {
										int end = index;

										while(encodedData.charAt(end) != ',')
											end++;

										String str = encodedData.substring(index, end);
										index = end;

										colorByte = Byte.parseByte(str.substring(0, str.indexOf('_')));

										skipsLeft = Integer.parseInt(str.substring(str.indexOf('_') + 1));
									}

									while(skipsLeft != 0) {
										mapCanvas.setPixel(x, y, colorByte);

										y++;
										if(y == 128) {
											y = 0;
											x++;
										}

										skipsLeft -= 1;
									}
								}
							}
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		getCommand("takePicture").setExecutor(new CameraCommands());
		getCommand("cameraReload").setExecutor(new CameraCommands());
		registerListeners(new CameraClick(), new CameraPlace());
	}

	private void registerListeners(Listener... listeners) {
		Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
	}

	public static Camera getInstance() {
		return instance;
	}

	public Map<String, CameraProfile> getCameraProfiles() {
		return cameraProfiles;
	}

	// -----------------------------------------------------------------
	// Camera profiles (multi-camera support)
	// -----------------------------------------------------------------

	private void loadCameraProfiles() {
		// unregister recipes for whatever profiles were loaded before this call
		// (relevant on /camerareload — a plain re-add would just warn/no-op otherwise)
		for (String id : cameraProfiles.keySet()) {
			try {
				getServer().removeRecipe(new NamespacedKey(this, "camera_" + id));
			} catch (Exception ignored) {
			}
		}
		cameraProfiles.clear();

		ConfigurationSection camerasSection = config.getConfigurationSection("cameras");
		if (camerasSection != null) {
			for (String id : camerasSection.getKeys(false)) {
				ConfigurationSection section = camerasSection.getConfigurationSection(id);
				if (section != null) {
					cameraProfiles.put(id, new CameraProfile(id, section));
				}
			}
		}

		for (CameraProfile profile : cameraProfiles.values()) {
			registerRecipeFor(profile);
		}
	}

	private void registerRecipeFor(CameraProfile profile) {
		if (!"VANILLA".equalsIgnoreCase(profile.getItemType())) {
			// Camera item comes from ItemsAdder — crafting/obtaining it is defined
			// over there, we don't register a competing Bukkit recipe for it.
			return;
		}
		if (!profile.isRecipeEnabled()) {
			return;
		}

		ItemStack camera = ItemManager.buildVanillaCameraItem(profile);

		NamespacedKey key = new NamespacedKey(this, "camera_" + profile.getId());
		ShapedRecipe recipe = new ShapedRecipe(key, camera);
		recipe.shape(profile.getRecipeShape().toArray(new String[0]));

		ConfigurationSection ingredients = profile.getRecipeIngredients();
		if (ingredients != null) {
			for (String ingredientKey : ingredients.getKeys(false)) {
				Material mat = Material.matchMaterial(ingredients.getString(ingredientKey));
				if (mat != null) {
					recipe.setIngredient(ingredientKey.charAt(0), mat);
				}
			}
		}

		getServer().addRecipe(recipe);
	}

	/**
	 * Re-reads config.yml, reloads the color tables, and rebuilds every camera profile
	 * (re-registering their recipes) — all without a server restart. The resource pack
	 * (which can involve a slow download) is only refreshed if refreshResourcePack is
	 * true, since most config tweaks don't need it.
	 */
	public void reload(boolean refreshResourcePack) {
		loadConfig();
		Utils.loadColors();
		loadCameraProfiles();

		if (refreshResourcePack) {
			Bukkit.getScheduler().runTaskAsynchronously(this, () -> this.resourcePackManager.initialize());
		}
	}

	// -----------------------------------------------------------------
	// config.yml
	// -----------------------------------------------------------------

	private void loadConfig() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}

		configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		config = YamlConfiguration.loadConfiguration(configFile);

		HashMap<String, Object> defaultConfig = new HashMap<>();

		// --- messages (shared across all cameras) ---
		defaultConfig.put("settings.messages.enabled", true);
		defaultConfig.put("settings.messages.notready", "&cCameras is still loading, please wait.");
		defaultConfig.put("settings.messages.delay", "&cPlease wait before taking another picture.");
		defaultConfig.put("settings.messages.invfull", "&cYou can not take a picture with a full inventory.");
		defaultConfig.put("settings.messages.nopaper", "&cYou must have paper in order to take a picture.");

		// --- delay between pictures (shared) ---
		defaultConfig.put("settings.delay.enabled", true);
		defaultConfig.put("settings.delay.amount", 1000);

		// --- resource pack used for real per-pixel block textures (shared) ---
		// source: LEGACY (bundled-download 1.16.4 pack, small but outdated)
		//         GITHUB (downloads just the /textures folder from a minecraft-assets-style
		//                 GitHub repo at the given ref — much more current, but a bigger
		//                 one-time download)
		defaultConfig.put("settings.camera.resourcepack.source", "LEGACY");
		defaultConfig.put("settings.camera.resourcepack.github-repo", "InventivetalentDev/minecraft-assets");
		defaultConfig.put("settings.camera.resourcepack.github-ref", "1.21.4");

		for (String key : defaultConfig.keySet()) {
			if (!config.contains(key)) {
				config.set(key, defaultConfig.get(key));
			}
		}

		// One-time migration: versions before multi-camera support kept a single
		// camera's settings under flat settings.camera.*/settings.paper.*/settings.render.*
		// keys. Carry those over into cameras.default.* so nobody's tuning gets lost.
		if (!config.contains("cameras") && config.contains("settings.camera.item")) {
			migrateLegacySingleCameraConfig();
		}

		// Brand new install (or migration above didn't apply): ship two example
		// profiles so multi-camera support is visible and usable out of the box.
		if (!config.contains("cameras")) {
			createDefaultCameraProfiles();
		}

		// Keys added in later plugin versions (zoom, max-distance...) won't exist yet
		// in a config.yml that already had a "cameras" section from before — backfill
		// them per-profile so they're visible/editable instead of silently defaulting
		// to "off" forever with no way to discover the option exists.
		backfillCameraProfileDefaults();

		File mapDir = new File(getDataFolder(), "maps");
		if (!mapDir.exists()) {
			mapDir.mkdir();
		}

		this.saveConfig();
	}

	private void backfillCameraProfileDefaults() {
		ConfigurationSection camerasSection = config.getConfigurationSection("cameras");
		if (camerasSection == null) {
			return;
		}
		for (String id : camerasSection.getKeys(false)) {
			String base = "cameras." + id + ".";
			if (!config.contains(base + "zoom.level")) {
				config.set(base + "zoom.level", 0);
			}
			if (!config.contains(base + "zoom.safety-duration-ticks")) {
				config.set(base + "zoom.safety-duration-ticks", 600);
			}
			if (!config.contains(base + "render.max-distance")) {
				config.set(base + "render.max-distance", 256);
			}
		}
	}

	private void migrateLegacySingleCameraConfig() {
		Bukkit.getLogger().info("[Cameras] Migrating your old single-camera settings into cameras.default...");

		config.set("cameras.default.item.type", config.getString("settings.camera.item.type", "VANILLA"));
		config.set("cameras.default.item.vanilla-material",
				config.getString("settings.camera.item.vanilla-material", "PLAYER_HEAD"));
		config.set("cameras.default.item.itemsadder-id",
				config.getString("settings.camera.item.itemsadder-id", "yournamespace:camera"));
		config.set("cameras.default.display-name", "&9Camera");
		config.set("cameras.default.trigger-mode", config.getString("settings.camera.trigger-mode", "CLICK"));
		config.set("cameras.default.zoom.level", 0);
		config.set("cameras.default.zoom.safety-duration-ticks", 600);
		config.set("cameras.default.recipe.enabled", config.getBoolean("settings.camera.recipe.enabled", true));
		List<String> shape = config.getStringList("settings.camera.recipe.shape");
		config.set("cameras.default.recipe.shape", shape.isEmpty() ? Arrays.asList("IGI", "ITI", "IRI") : shape);
		if (config.getConfigurationSection("settings.camera.recipe.ingredients") != null) {
			for (String k : config.getConfigurationSection("settings.camera.recipe.ingredients").getKeys(false)) {
				config.set("cameras.default.recipe.ingredients." + k,
						config.getString("settings.camera.recipe.ingredients." + k));
			}
		}
		config.set("cameras.default.paper.type", config.getString("settings.paper.item.type", "VANILLA"));
		config.set("cameras.default.paper.material", config.getString("settings.paper.item.material", "PAPER"));
		config.set("cameras.default.paper.itemsadder-id",
				config.getString("settings.paper.item.itemsadder-id", "yournamespace:film"));
		config.set("cameras.default.sound.key", "minecraft:block.dispenser.dispense");
		config.set("cameras.default.sound.volume", 1.0);
		config.set("cameras.default.sound.pitch", 1.0);
		config.set("cameras.default.render.shadows", config.getBoolean("settings.render.shadows", true));
		config.set("cameras.default.render.fov", config.getDouble("settings.render.fov", 51.5));
		config.set("cameras.default.render.max-distance", config.getDouble("settings.render.max-distance", 256));
		config.set("cameras.default.render.relief.enabled", config.getBoolean("settings.render.relief.enabled", true));
		config.set("cameras.default.render.relief.strength", config.getDouble("settings.render.relief.strength", 0.18));
		config.set("cameras.default.render.fog.enabled", config.getBoolean("settings.render.fog.enabled", true));
		config.set("cameras.default.render.fog.distance", config.getDouble("settings.render.fog.distance", 180));
		config.set("cameras.default.render.animals.enabled", config.getBoolean("settings.render.animals.enabled", true));
		config.set("cameras.default.render.entity-ray-size", config.getDouble("settings.render.entity-ray-size", -0.15));
		config.set("cameras.default.render.postprocess.brightness",
				config.getDouble("settings.render.postprocess.brightness", 1.0));
		config.set("cameras.default.render.postprocess.contrast",
				config.getDouble("settings.render.postprocess.contrast", 1.0));
		config.set("cameras.default.render.postprocess.saturation",
				config.getDouble("settings.render.postprocess.saturation", 1.0));
		config.set("cameras.default.render.postprocess.grain",
				config.getDouble("settings.render.postprocess.grain", 0.0));

		// clean up the old flat keys so they don't linger around unused/confusing
		config.set("settings.camera.item", null);
		config.set("settings.camera.trigger-mode", null);
		config.set("settings.camera.recipe", null);
		config.set("settings.paper", null);
		config.set("settings.render", null);
	}

	private void createDefaultCameraProfiles() {
		// --- "default": the original reskinned-skull camera, fires on click ---
		config.set("cameras.default.item.type", "VANILLA");
		config.set("cameras.default.item.vanilla-material", "PLAYER_HEAD");
		config.set("cameras.default.item.itemsadder-id", "yournamespace:camera");
		config.set("cameras.default.display-name", "&9Camera");
		config.set("cameras.default.trigger-mode", "CLICK");
		config.set("cameras.default.zoom.level", 0);
		config.set("cameras.default.zoom.safety-duration-ticks", 600);
		config.set("cameras.default.recipe.enabled", true);
		config.set("cameras.default.recipe.shape", Arrays.asList("IGI", "ITI", "IRI"));
		config.set("cameras.default.recipe.ingredients.I", "IRON_INGOT");
		config.set("cameras.default.recipe.ingredients.G", "GLASS_PANE");
		config.set("cameras.default.recipe.ingredients.T", "GLOWSTONE_DUST");
		config.set("cameras.default.recipe.ingredients.R", "REDSTONE");
		config.set("cameras.default.paper.type", "VANILLA");
		config.set("cameras.default.paper.material", "PAPER");
		config.set("cameras.default.paper.itemsadder-id", "yournamespace:film");
		config.set("cameras.default.sound.key", "minecraft:block.dispenser.dispense");
		config.set("cameras.default.sound.volume", 1.0);
		config.set("cameras.default.sound.pitch", 1.0);
		config.set("cameras.default.render.shadows", true);
		config.set("cameras.default.render.fov", 51.5);
		config.set("cameras.default.render.max-distance", 256);
		config.set("cameras.default.render.relief.enabled", true);
		config.set("cameras.default.render.relief.strength", 0.18);
		config.set("cameras.default.render.fog.enabled", true);
		config.set("cameras.default.render.fog.distance", 180);
		config.set("cameras.default.render.animals.enabled", true);
		config.set("cameras.default.render.entity-ray-size", -0.15);
		config.set("cameras.default.render.postprocess.brightness", 1.0);
		config.set("cameras.default.render.postprocess.contrast", 1.0);
		config.set("cameras.default.render.postprocess.saturation", 1.0);
		config.set("cameras.default.render.postprocess.grain", 0.0);

		// --- "polaroid": example second profile — spyglass, fires on release, warmer/grainier look ---
		config.set("cameras.polaroid.item.type", "VANILLA");
		config.set("cameras.polaroid.item.vanilla-material", "SPYGLASS");
		config.set("cameras.polaroid.item.itemsadder-id", "yournamespace:polaroid");
		config.set("cameras.polaroid.display-name", "&6Polaroid Camera");
		config.set("cameras.polaroid.trigger-mode", "HOLD");
		config.set("cameras.polaroid.zoom.level", 4);
		config.set("cameras.polaroid.zoom.safety-duration-ticks", 600);
		config.set("cameras.polaroid.recipe.enabled", true);
		config.set("cameras.polaroid.recipe.shape", Arrays.asList("IGI", "ITI", "ISI"));
		config.set("cameras.polaroid.recipe.ingredients.I", "IRON_INGOT");
		config.set("cameras.polaroid.recipe.ingredients.G", "GLASS_PANE");
		config.set("cameras.polaroid.recipe.ingredients.T", "AMETHYST_SHARD");
		config.set("cameras.polaroid.recipe.ingredients.S", "SUGAR_CANE");
		config.set("cameras.polaroid.paper.type", "VANILLA");
		config.set("cameras.polaroid.paper.material", "PAPER");
		config.set("cameras.polaroid.paper.itemsadder-id", "yournamespace:polaroid-film");
		config.set("cameras.polaroid.sound.key", "minecraft:entity.player.attack.crit");
		config.set("cameras.polaroid.sound.volume", 1.0);
		config.set("cameras.polaroid.sound.pitch", 1.4);
		config.set("cameras.polaroid.render.shadows", true);
		config.set("cameras.polaroid.render.fov", 51.5);
		config.set("cameras.polaroid.render.max-distance", 400);
		config.set("cameras.polaroid.render.relief.enabled", true);
		config.set("cameras.polaroid.render.relief.strength", 0.18);
		config.set("cameras.polaroid.render.fog.enabled", true);
		config.set("cameras.polaroid.render.fog.distance", 180);
		config.set("cameras.polaroid.render.animals.enabled", true);
		config.set("cameras.polaroid.render.entity-ray-size", -0.15);
		config.set("cameras.polaroid.render.postprocess.brightness", 1.05);
		config.set("cameras.polaroid.render.postprocess.contrast", 1.1);
		config.set("cameras.polaroid.render.postprocess.saturation", 0.75);
		config.set("cameras.polaroid.render.postprocess.grain", 0.35);
	}

	@Override
	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public FileConfiguration getConfig() {
		return config;
	}

	public ResourcePackManager getResourcePackManager() {
		return this.resourcePackManager;
	}
}
