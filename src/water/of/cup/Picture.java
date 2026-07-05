package water.of.cup;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Picture {

	public static boolean takePicture(Player p, CameraProfile profile) {

		ItemStack itemStack = new ItemStack(Material.FILLED_MAP); // requires api-version: 1.13 in plugin.yml
		MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
		MapView mapView = Bukkit.createMap(p.getWorld());
		mapView.setTrackingPosition(false);
		for (MapRenderer renderer : mapView.getRenderers()) {
			mapView.removeRenderer(renderer);
		}

		Renderer customRenderer = new Renderer(profile);
		mapView.addRenderer(customRenderer);

		mapMeta.setMapView(mapView);
		itemStack.setItemMeta(mapMeta);
		p.getInventory().addItem(itemStack);

		playShutterSound(p, profile);

		return true;
	}

	private static void playShutterSound(Player p, CameraProfile profile) {
		String key = profile.getSoundKey();
		if (key == null || key.isEmpty()) {
			return;
		}
		try {
			// The String overload (as opposed to the Sound enum) is what lets this play
			// either a vanilla sound ("minecraft:block.dispenser.dispense") or a fully
			// custom one defined in a resource pack's sounds.json ("customsounds:shutter").
			p.getWorld().playSound(p.getLocation(), key, SoundCategory.PLAYERS,
					profile.getSoundVolume(), profile.getSoundPitch());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[Cameras] Couldn't play shutter sound '" + key
					+ "' for camera '" + profile.getId() + "': " + e.getMessage());
		}
	}
}
