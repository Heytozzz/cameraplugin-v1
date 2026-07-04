package water.of.cup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

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
	private File configFile;
	private FileConfiguration config;

	@Override
	public void onEnable() {
		instance = this;

		loadConfig();
		Utils.loadColors();

		if ("HOLD".equalsIgnoreCase(config.getString("settings.camera.trigger-mode", "CLICK"))
				&& "VANILLA".equalsIgnoreCase(config.getString("settings.camera.item.type", "VANILLA"))
				&& !"SPYGLASS".equalsIgnoreCase(config.getString("settings.camera.item.vanilla-material", "PLAYER_HEAD"))) {
			Bukkit.getLogger().warning("[Cameras] settings.camera.trigger-mode is HOLD but vanilla-material isn't "
					+ "SPYGLASS — there's no release signal for other items, so the camera will fire on click instead.");
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
//
//								for (int x = 0; x < 128; x++) {
//									for (int y = 0; y < 128; y++) {
//										mapCanvas.setPixel(x, y, (byte) 7);
//									}
//								}

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

//										Bukkit.getLogger().info("MapID debug: " + mapId + " substr: " + str + " color: " + colorByte + " skipLefts: " + skipsLeft);
									}

									// fix something up here
									while(skipsLeft != 0) {
//										Bukkit.getLogger().info("MapID debug: " + mapId + " x: " + x + " y: " + y + " skipsLeft: " + skipsLeft);
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
		addCameraRecipe();

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

	public void addCameraRecipe() {
		if (!"VANILLA".equalsIgnoreCase(config.getString("settings.camera.item.type", "VANILLA"))) {
			// Camera item comes from ItemsAdder — crafting/obtaining it is defined
			// over there, we don't register a competing Bukkit recipe for it.
			return;
		}
		if (!config.getBoolean("settings.camera.recipe.enabled", true)) {
			return;
		}

		ItemStack camera = ItemManager.buildVanillaCameraItem();

		NamespacedKey key = new NamespacedKey(this, "camera");
		ShapedRecipe recipe = new ShapedRecipe(key, camera);

		List<String> shapeList = config.getStringList("settings.camera.recipe.shape");
		recipe.shape(shapeList.toArray(new String[0]));

		if (config.getConfigurationSection("settings.camera.recipe.ingredients") != null) {
			for (String ingredientKey : config.getConfigurationSection("settings.camera.recipe.ingredients").getKeys(false)) {
				Material mat = Material.matchMaterial(config.getString("settings.camera.recipe.ingredients." + ingredientKey));
				if (mat != null) {
					recipe.setIngredient(ingredientKey.charAt(0), mat);
				}
			}
		}

		getServer().addRecipe(recipe);
	}

	/**
	 * Re-reads config.yml, reloads the color tables, and re-registers the crafting
	 * recipe — all without a server restart. The resource pack (which can involve a
	 * slow download) is only refreshed if refreshResourcePack is true, since most
	 * config tweaks (colors, FOV, post-processing, trigger mode...) don't need it.
	 */
	public void reload(boolean refreshResourcePack) {
		try {
			getServer().removeRecipe(new NamespacedKey(this, "camera"));
		} catch (Exception ignored) {
			// no recipe was registered (e.g. item.type was ITEMSADDER) — fine
		}

		loadConfig();
		Utils.loadColors();
		addCameraRecipe();

		if (refreshResourcePack) {
			Bukkit.getScheduler().runTaskAsynchronously(this, () -> this.resourcePackManager.initialize());
		}
	}

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

		// --- messages ---
		defaultConfig.put("settings.messages.enabled", true);
		defaultConfig.put("settings.messages.notready", "&cCameras is still loading, please wait.");
		defaultConfig.put("settings.messages.delay", "&cPlease wait before taking another picture.");
		defaultConfig.put("settings.messages.invfull", "&cYou can not take a picture with a full inventory.");
		defaultConfig.put("settings.messages.nopaper", "&cYou must have paper in order to take a picture.");

		// --- delay between pictures ---
		defaultConfig.put("settings.delay.enabled", true);
		defaultConfig.put("settings.delay.amount", 1000);

		// --- rendering behaviour ---
		defaultConfig.put("settings.render.shadows", true);
		// field of view in degrees. Vanilla-ish default; higher = wider/more scene
		// visible per photo, but this uses a simple angular-offset projection (not true
		// perspective), so very high values (120+) will start to look fisheye-warped
		// toward the edges rather than a clean wide-angle shot.
		defaultConfig.put("settings.render.fov", 51.5);
		defaultConfig.put("settings.render.relief.enabled", true);
		// how strongly neighbouring-pixel depth differences lighten/darken a pixel
		defaultConfig.put("settings.render.relief.strength", 0.18);
		defaultConfig.put("settings.render.fog.enabled", true);
		// blocks of distance at which fog starts to noticeably darken/desaturate colors
		defaultConfig.put("settings.render.fog.distance", 180);
		// captures ALL entities (mobs, players, etc.), not just animals, despite the key name
		defaultConfig.put("settings.render.animals.enabled", true);
		// negative = shrink each entity's hitbox before testing rays against it, so a mob
		// standing right in front of the camera doesn't swallow the whole picture.
		// Positive would expand it instead (useful if small/thin mobs are getting missed).
		defaultConfig.put("settings.render.entity-ray-size", -0.15);

		// --- final image post-processing (applied as the very last step, like a camera filter) ---
		defaultConfig.put("settings.render.postprocess.brightness", 1.0); // 1.0 = no change
		defaultConfig.put("settings.render.postprocess.contrast", 1.0);   // 1.0 = no change, >1 = punchier
		defaultConfig.put("settings.render.postprocess.saturation", 1.0); // 1.0 = no change, 0 = grayscale
		defaultConfig.put("settings.render.postprocess.grain", 0.0);      // 0 = none, 1 = heavy film grain

		// --- resource pack used for real per-pixel block textures ---
		// source: LEGACY (bundled-download 1.16.4 pack, small but outdated)
		//         GITHUB (downloads just the /textures folder from a minecraft-assets-style
		//                 GitHub repo at the given ref — much more current, but a bigger
		//                 one-time download)
		defaultConfig.put("settings.camera.resourcepack.source", "LEGACY");
		defaultConfig.put("settings.camera.resourcepack.github-repo", "InventivetalentDev/minecraft-assets");
		defaultConfig.put("settings.camera.resourcepack.github-ref", "1.21.4");

		// --- camera item ---
		// type: VANILLA (crafted item, see vanilla-material below)
		//       ITEMSADDER (an item you already defined in ItemsAdder, referenced by its namespaced id)
		defaultConfig.put("settings.camera.item.type", "VANILLA");
		// vanilla-material: PLAYER_HEAD (default reskinned skull) or SPYGLASS.
		// Only SPYGLASS supports trigger-mode: HOLD below — it's the only one of the two
		// with a native "hold to use / release to stop" state in vanilla Minecraft, which
		// is what makes a real release-triggered shutter possible instead of just an
		// approximation. Bonus: right-click zooms in like a viewfinder while composing the shot.
		defaultConfig.put("settings.camera.item.vanilla-material", "PLAYER_HEAD");
		defaultConfig.put("settings.camera.item.itemsadder-id", "yournamespace:camera");
		// trigger-mode: CLICK (takes the photo immediately on right-click, default)
		//               HOLD  (takes the photo when the player releases right-click —
		//                      requires vanilla-material: SPYGLASS to actually detect a release;
		//                      with any other item it silently behaves like CLICK, since
		//                      Bukkit has no "release" signal for a generic custom item)
		defaultConfig.put("settings.camera.trigger-mode", "CLICK");
		defaultConfig.put("settings.camera.recipe.enabled", true);
		defaultConfig.put("settings.camera.recipe.shape", new ArrayList<String>() {
			{
				add("IGI");
				add("ITI");
				add("IRI");
			}
		});
		HashMap<String, String> defaultRecipe = new HashMap<>();
		defaultRecipe.put("I", Material.IRON_INGOT.toString());
		defaultRecipe.put("G", Material.GLASS_PANE.toString());
		defaultRecipe.put("T", Material.GLOWSTONE_DUST.toString());
		defaultRecipe.put("R", Material.REDSTONE.toString());
		if (!config.contains("settings.camera.recipe.ingredients")) {
			for (String key : defaultRecipe.keySet()) {
				defaultConfig.put("settings.camera.recipe.ingredients." + key, defaultRecipe.get(key));
			}
		}

		// --- "paper"/film item consumed per picture ---
		// type: VANILLA (checks/consumes a plain Material, PAPER by default)
		//       ITEMSADDER (checks/consumes an ItemsAdder custom item, referenced by its namespaced id)
		defaultConfig.put("settings.paper.item.type", "VANILLA");
		defaultConfig.put("settings.paper.item.material", Material.PAPER.toString());
		defaultConfig.put("settings.paper.item.itemsadder-id", "yournamespace:film");

		for (String key : defaultConfig.keySet()) {
			if (!config.contains(key)) {
				config.set(key, defaultConfig.get(key));
			}
		}

		File mapDir = new File(getDataFolder(), "maps");
		if (!mapDir.exists()) {
			mapDir.mkdir();
		}

		this.saveConfig();
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
