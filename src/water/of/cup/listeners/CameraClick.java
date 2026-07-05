package water.of.cup.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.papermc.paper.event.player.PlayerStopUsingItemEvent;

import water.of.cup.Camera;
import water.of.cup.CameraProfile;
import water.of.cup.ItemManager;
import water.of.cup.Picture;

public class CameraClick implements Listener {

	// trigger-mode: TWO_STEP state — tracks which camera profile id a player is
	// currently "aiming" (zoomed in, waiting for the second click to actually shoot).
	private final Map<UUID, String> aimingProfileId = new HashMap<>();

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

		String mode = profile.getTriggerMode();

		if ("HOLD".equalsIgnoreCase(mode)) {
			// Apply zoom right as aiming starts. Do nothing else here — let vanilla
			// start its normal "using item" state (the spyglass zoom). The actual photo
			// is taken (and the zoom effect removed) in onStopUsingItem() below, when
			// the player releases right-click.
			applyZoomEffect(p, profile);
			return;
		}

		if ("TWO_STEP".equalsIgnoreCase(mode)) {
			String currentlyAiming = aimingProfileId.get(p.getUniqueId());
			if (profile.getId().equals(currentlyAiming)) {
				// second click: shoot
				aimingProfileId.remove(p.getUniqueId());
				removeZoomEffect(p, profile);
				tryTakePicture(p, profile);
			} else {
				// first click: zoom in and wait for the next click
				aimingProfileId.put(p.getUniqueId(), profile.getId());
				applyZoomEffect(p, profile);
			}
			return;
		}

		// CLICK mode (default): no real "aiming" phase, effect is applied and removed
		// around the same instant the photo is taken, so it's mostly symbolic here.
		applyZoomEffect(p, profile);
		tryTakePicture(p, profile);
		removeZoomEffect(p, profile);
	}

	@EventHandler
	public void onStopUsingItem(PlayerStopUsingItemEvent e) {
		CameraProfile profile = ItemManager.findCameraProfile(e.getItem());
		if (profile == null || !"HOLD".equalsIgnoreCase(profile.getTriggerMode())) {
			return;
		}
		tryTakePicture(e.getPlayer(), profile);
		removeZoomEffect(e.getPlayer(), profile);
	}

	/** If a player switches away from the camera they were mid-"aim" with (TWO_STEP
	 *  mode), cancel the pending shot instead of leaving it to fire on some unrelated
	 *  later click once they switch back. */
	@EventHandler
	public void onItemSwitch(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		String aimingId = aimingProfileId.get(p.getUniqueId());
		if (aimingId == null) {
			return;
		}
		ItemStack newItem = p.getInventory().getItem(e.getNewSlot());
		CameraProfile newProfile = ItemManager.findCameraProfile(newItem);
		if (newProfile == null || !aimingId.equals(newProfile.getId())) {
			aimingProfileId.remove(p.getUniqueId());
			p.removePotionEffect(PotionEffectType.SLOWNESS);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		aimingProfileId.remove(e.getPlayer().getUniqueId());
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

	/** Simulated "zoom": a particle-less Slowness effect while aiming, level configurable
	 *  per camera (0 = off, up to Slowness VII). Purely a movement effect — it doesn't
	 *  touch the actual render FOV, which is its own separate setting. */
	private void applyZoomEffect(Player p, CameraProfile profile) {
		int level = profile.getZoomLevel();
		if (level <= 0) {
			return;
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,
				profile.getZoomSafetyDurationTicks(), level - 1, false, false, true));
	}

	private void removeZoomEffect(Player p, CameraProfile profile) {
		if (profile.getZoomLevel() > 0) {
			p.removePotionEffect(PotionEffectType.SLOWNESS);
		}
	}
}
