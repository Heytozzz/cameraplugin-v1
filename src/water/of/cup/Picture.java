package water.of.cup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;

public class Picture {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
		mapMeta.setLore(buildLore(p));
		itemStack.setItemMeta(mapMeta);

		// Hides the raw "map_id: 123" line the client would otherwise show in the
		// tooltip — purely cosmetic info nobody taking a photo needs to see.
		TooltipDisplay tooltipDisplay = TooltipDisplay.tooltipDisplay()
				.addHiddenComponents(DataComponentTypes.MAP_ID)
				.build();
		itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplay);

		p.getInventory().addItem(itemStack);

		playShutterSound(p, profile);

		return true;
	}

	private static List<String> buildLore(Player p) {
		Location loc = p.getLocation();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + LocalDateTime.now().format(DATE_FORMAT));
		lore.add(ChatColor.GRAY + String.format("%.0f, %.0f, %.0f (%s)",
				loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName()));
		return lore;
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
