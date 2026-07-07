package water.of.cup;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

/**
 * Resolves camera / "paper" (film) items against a specific CameraProfile. Since
 * multiple independently-configured cameras can exist at once, lookups return which
 * profile (if any) an ItemStack matches, rather than a plain boolean.
 *
 * IMPORTANT: the ItemsAdder classes (CustomStack) are only ever touched inside
 * matchesItemsAdderId(), and only when a profile explicitly says "ITEMSADDER". If a
 * server doesn't have ItemsAdder installed and no profile uses ITEMSADDER mode, these
 * classes are never loaded by the JVM, so there's no hard dependency at runtime.
 */
public class ItemManager {

	// ---------------------------------------------------------------
	// Camera item
	// ---------------------------------------------------------------

	/** Returns the CameraProfile this ItemStack matches, or null if it isn't any known camera. */
	public static CameraProfile findCameraProfile(ItemStack stack) {
		if (stack == null || stack.getType() == Material.AIR) {
			return null;
		}
		for (CameraProfile profile : Camera.getInstance().getCameraProfiles().values()) {
			if (matchesCameraProfile(stack, profile)) {
				return profile;
			}
		}
		return null;
	}

	private static boolean matchesCameraProfile(ItemStack stack, CameraProfile profile) {
		if ("ITEMSADDER".equalsIgnoreCase(profile.getItemType())) {
			return matchesItemsAdderId(stack, profile.getItemsAdderId());
		}
		if (stack.getItemMeta() == null || stack.getItemMeta().getDisplayName() == null) {
			return false;
		}
		return profile.getDisplayName().equals(stack.getItemMeta().getDisplayName());
	}

	/** Builds the configured vanilla camera item for a profile — either the reskinned
	 *  player-head skull (default) or a plain spyglass (needed for trigger-mode: HOLD,
	 *  since it's the only one of the two with a native "hold to use / release" state). */
	public static ItemStack buildVanillaCameraItem(CameraProfile profile) {
		if ("SPYGLASS".equalsIgnoreCase(profile.getVanillaMaterial())) {
			ItemStack camera = new ItemStack(Material.SPYGLASS);
			var meta = camera.getItemMeta();
			meta.setDisplayName(profile.getDisplayName());
			camera.setItemMeta(meta);
			return camera;
		}

		ItemStack camera = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta cameraMeta = (SkullMeta) camera.getItemMeta();
		cameraMeta.setDisplayName(profile.getDisplayName());

		PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID());
		playerProfile.setProperty(new ProfileProperty("textures",
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZiNWVlZTQwYzNkZDY2ODNjZWM4ZGQxYzZjM2ZjMWIxZjAxMzcxNzg2NjNkNzYxMDljZmUxMmVkN2JmMjc4ZSJ9fX0=="));
		cameraMeta.setOwnerProfile(playerProfile);
		camera.setItemMeta(cameraMeta);
		return camera;
	}

	// ---------------------------------------------------------------
	// "Paper"/film item — consumed once per picture
	// ---------------------------------------------------------------

	/** Returns the first film variant this profile accepts that the player is
	 *  currently carrying, or null if they have none of them. Variants are checked
	 *  in the order they're defined in config.yml. */
	public static CameraProfile.FilmVariant findFilmVariant(CameraProfile profile, Player player) {
		for (CameraProfile.FilmVariant variant : profile.getFilmVariants()) {
			if (hasVariantItem(variant, player)) {
				return variant;
			}
		}
		return null;
	}

	/** Removes exactly one unit of the given film variant's item from the player's inventory. */
	public static void removeOneFilmVariant(CameraProfile.FilmVariant variant, Player player) {
		if ("ITEMSADDER".equalsIgnoreCase(variant.type)) {
			for (ItemStack stack : player.getInventory().getContents()) {
				if (matchesItemsAdderId(stack, variant.itemsAdderId)) {
					stack.setAmount(stack.getAmount() - 1);
					return;
				}
			}
			return;
		}
		Material mat = resolveVariantMaterial(variant);
		for (ItemStack stack : player.getInventory().all(mat).values()) {
			stack.setAmount(stack.getAmount() - 1);
			return;
		}
	}

	private static boolean hasVariantItem(CameraProfile.FilmVariant variant, Player player) {
		if ("ITEMSADDER".equalsIgnoreCase(variant.type)) {
			for (ItemStack stack : player.getInventory().getContents()) {
				if (matchesItemsAdderId(stack, variant.itemsAdderId)) {
					return true;
				}
			}
			return false;
		}
		Material mat = resolveVariantMaterial(variant);
		return player.getInventory().contains(mat);
	}

	private static Material resolveVariantMaterial(CameraProfile.FilmVariant variant) {
		Material mat = Material.matchMaterial(variant.material);
		return mat != null ? mat : Material.PAPER;
	}

	// ---------------------------------------------------------------
	// ItemsAdder bridge — isolated here so these classes are only ever
	// loaded when a profile actually configures ITEMSADDER mode.
	// ---------------------------------------------------------------

	private static boolean matchesItemsAdderId(ItemStack stack, String namespacedId) {
		if (stack == null || stack.getType() == Material.AIR || namespacedId == null || namespacedId.isEmpty()) {
			return false;
		}
		try {
			dev.lone.itemsadder.api.CustomStack customStack = dev.lone.itemsadder.api.CustomStack.byItemStack(stack);
			return customStack != null && namespacedId.equalsIgnoreCase(customStack.getNamespacedID());
		} catch (Throwable t) {
			// ItemsAdder not installed, or its data isn't loaded yet — treat as no match
			// instead of crashing the click/inventory-scan that triggered this check.
			Bukkit.getLogger().warning("[Cameras] config asks for an ITEMSADDER item ("
					+ namespacedId + ") but ItemsAdder doesn't seem to be available: " + t.getMessage());
			return false;
		}
	}
}
