package water.of.cup.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import io.papermc.paper.event.player.PlayerStopUsingItemEvent;

import water.of.cup.Camera;
import water.of.cup.CameraProfile;
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
		CameraProfile profile = ItemManager.findCameraProfile(heldItem);
		if (profile == null) {
			return;
		}

		if ("HOLD".equalsIgnoreCase(profile.getTriggerMode())) {
			// Do nothing here — let vanilla start its normal "using item" state (the
			// spyglass zoom). The actual photo is taken in onStopUsingItem() below, when
			// the player releases right-click.
			return;
		}

		tryTakePicture(p, profile);
	}

	@EventHandler
	public void onStopUsingItem(PlayerStopUsingItemEvent e) {
		CameraProfile profile = ItemManager.findCameraProfile(e.getItem());
		if (profile == null || !"HOLD".equalsIgnoreCase(profile.getTriggerMode())) {
			return;
		}
		tryTakePicture(e.getPlayer(), profile);
	}

	private void tryTakePicture(Player p, CameraProfile profile) {
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

		if (ItemManager.hasFilmItem(profile, p)) {
			ItemManager.removeOneFilmItem(profile, p);
			Picture.takePicture(p, profile);
		} else if (messages) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
					Camera.getInstance().getConfig().getString("settings.messages.nopaper")));
		}
	}
}
