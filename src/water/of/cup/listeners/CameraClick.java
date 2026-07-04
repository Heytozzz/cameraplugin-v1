package water.of.cup.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import water.of.cup.Camera;
import water.of.cup.ItemManager;
import water.of.cup.Picture;

public class CameraClick implements Listener {
	@EventHandler
	public void cameraClicked(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		ItemStack heldItem = e.getItem();
		if (!ItemManager.isCameraItem(heldItem)) {
			return;
		}

		boolean messages = Camera.getInstance().getConfig().getBoolean("settings.messages.enabled", true);

		if (!Camera.getInstance().getResourcePackManager().isLoaded()) {
			if (messages) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						Camera.getInstance().getConfig().getString("settings.messages.notready")));
			}
			return;
		}

		if (p.getInventory().firstEmpty() == -1) {
			if (messages) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						Camera.getInstance().getConfig().getString("settings.messages.invfull")));
			}
			return;
		}

		if (ItemManager.hasFilmItem(p)) {
			ItemManager.removeOneFilmItem(p);
			Picture.takePicture(p);
		} else if (messages) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
					Camera.getInstance().getConfig().getString("settings.messages.nopaper")));
		}
	}
}
