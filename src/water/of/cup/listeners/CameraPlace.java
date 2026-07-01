package water.of.cup.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import water.of.cup.ItemManager;

public class CameraPlace implements Listener {
	@EventHandler
	public void cameraPlaced(BlockPlaceEvent e) {
		//Prevent players from placing Cameras
		ItemStack item = e.getItemInHand();
		if (ItemManager.isCameraItem(item)) {
			e.setCancelled(true);
		}
	}
}
