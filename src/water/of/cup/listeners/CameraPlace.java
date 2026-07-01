package water.of.cup.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class CameraPlace implements Listener {
	@EventHandler
	public void cameraPlaced(BlockPlaceEvent e) {
		//Prevent players from placing Cameras
		
		//Player p = e.getPlayer();
		ItemStack item = e.getItemInHand();
		if (item.getItemMeta() != null
				&& ChatColor.DARK_BLUE.toString().concat("Camera").equals(item.getItemMeta().getDisplayName())) {
			e.setCancelled(true);
		}
	}
}
