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

		this.resourcePackManager.initialize();

		// Resource pack manager test
		File grassFile = this.resourcePackManager.getTextureByMaterial(Material.SHORT_GRASS);
		if(grassFile != null)
			Bukkit.getLogger().info("Loaded grass texture " + grassFile.getName());

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
					Bukkit.getLogger().info("Reading MapID: " + mapId);

					MapView mapView = Bukkit.getMap(Integer.valueOf(mapId));

					mapView.setTrackingPosition(false);
					for(MapRenderer renderer : mapView.getRenderers())
						mapView.removeRenderer(renderer);

					mapView.addRenderer(new MapRenderer() {
						@Override
						public void render(MapView mapViewNew, MapCanvas mapCanvas, Player player) {
							if(!mapIDsNotToRender.contains(mapId)) {
								mapIDsNotToRender.add(mapId);

								Bukkit.getLogger().info("Starting render... " + mapId);
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
								Bukkit.getLogger().info("Ending render... " + mapId);
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
		defaultConfig.put("settings.render.relief.enabled", true);
		// how strongly neighbouring-pixel depth differences lighten/darken a pixel
		defaultConfig.put("settings.render.relief.strength", 0.18);
		defaultConfig.put("settings.render.fog.enabled", true);
		// blocks of distance at which fog starts to noticeably darken/desaturate colors
		defaultConfig.put("settings.render.fog.distance", 180);
		defaultConfig.put("settings.render.animals.enabled", true);

		// --- camera item ---
		// type: VANILLA (default player-head skull, crafted with the recipe below)
		//       ITEMSADDER (an item you already defined in ItemsAdder, referenced by its namespaced id)
		defaultConfig.put("settings.camera.item.type", "VANILLA");
		defaultConfig.put("settings.camera.item.itemsadder-id", "yournamespace:camera");
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
