package water.of.cup;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

/**
 * Resolves the "camera" item and the "paper"/film item consumed per picture,
 * based on config.yml. Both can independently be:
 *  - VANILLA: a plain Bukkit item (camera = the built-in skull item; paper = any Material)
 *  - ITEMSADDER: a custom item already registered in ItemsAdder, referenced by namespaced id
 *
 * IMPORTANT: the ItemsAdder classes (CustomStack) are only ever touched inside the
 * two isItemsAdder*() branches below, and only when config explicitly says "ITEMSADDER".
 * If a server doesn't have ItemsAdder installed and never configures ITEMSADDER mode,
 * these classes are never loaded by the JVM, so there's no hard dependency at runtime.
 */
public class ItemManager {

	private static final String CAMERA_DISPLAY_NAME = ChatColor.DARK_BLUE + "Camera";

	// ---------------------------------------------------------------
	// Camera item
	// ---------------------------------------------------------------

	public static boolean isCameraItem(ItemStack stack) {
		if (stack == null || stack.getType() == Material.AIR) {
			return false;
		}
		String type = Camera.getInstance().getConfig().getString("settings.camera.item.type", "VANILLA");
		if ("ITEMSADDER".equalsIgnoreCase(type)) {
			String id = Camera.getInstance().getConfig().getString("settings.camera.item.itemsadder-id");
			return matchesItemsAdderId(stack, id);
		}
		// VANILLA: identified by display name, same as the crafted skull item
		if (stack.getItemMeta() == null || stack.getItemMeta().getDisplayName() == null) {
			return false;
		}
		return CAMERA_DISPLAY_NAME.equals(stack.getItemMeta().getDisplayName());
	}

	/** Builds the default vanilla camera item (a player-head skull with a camera texture). */
	public static ItemStack buildVanillaCameraItem() {
		ItemStack camera = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta cameraMeta = (SkullMeta) camera.getItemMeta();
		cameraMeta.setDisplayName(CAMERA_DISPLAY_NAME);

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

	/** Whether the player has at least one of the configured film item. */
	public static boolean hasFilmItem(Player player) {
		String type = Camera.getInstance().getConfig().getString("settings.paper.item.type", "VANILLA");
		if ("ITEMSADDER".equalsIgnoreCase(type)) {
			String id = Camera.getInstance().getConfig().getString("settings.paper.item.itemsadder-id");
			for (ItemStack stack : player.getInventory().getContents()) {
				if (matchesItemsAdderId(stack, id)) {
					return true;
				}
			}
			return false;
		}
		Material mat = resolveVanillaPaperMaterial();
		return player.getInventory().contains(mat);
	}

	/** Removes exactly one unit of the configured film item from the player's inventory. */
	public static void removeOneFilmItem(Player player) {
		String type = Camera.getInstance().getConfig().getString("settings.paper.item.type", "VANILLA");
		if ("ITEMSADDER".equalsIgnoreCase(type)) {
			String id = Camera.getInstance().getConfig().getString("settings.paper.item.itemsadder-id");
			for (ItemStack stack : player.getInventory().getContents()) {
				if (matchesItemsAdderId(stack, id)) {
					stack.setAmount(stack.getAmount() - 1);
					return;
				}
			}
			return;
		}
		Material mat = resolveVanillaPaperMaterial();
		for (ItemStack stack : player.getInventory().all(mat).values()) {
			stack.setAmount(stack.getAmount() - 1);
			return;
		}
	}

	private static Material resolveVanillaPaperMaterial() {
		String matName = Camera.getInstance().getConfig().getString("settings.paper.item.material", "PAPER");
		Material mat = Material.matchMaterial(matName);
		return mat != null ? mat : Material.PAPER;
	}

	// ---------------------------------------------------------------
	// ItemsAdder bridge — isolated here so these classes are only ever
	// loaded when a server actually configures ITEMSADDER mode.
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
			Bukkit.getLogger().warning("[Cameras] settings.yml asks for an ITEMSADDER item ("
					+ namespacedId + ") but ItemsAdder doesn't seem to be available: " + t.getMessage());
			return false;
		}
	}
}
