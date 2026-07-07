package water.of.cup.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.AlbumManager;
import water.of.cup.Camera;
import water.of.cup.Picture;

public class CameraCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (cmd.getName().equalsIgnoreCase("camerareload")) {
			if (!(sender.isOp() || sender.hasPermission("cameras.reload"))) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
				return true;
			}
			boolean refreshResourcePack = args.length > 0 && args[0].equalsIgnoreCase("resourcepack");
			Camera.getInstance().reload(refreshResourcePack);
			sender.sendMessage(ChatColor.GREEN + "[Cameras] Config, colors and recipe reloaded."
					+ (refreshResourcePack ? " Resource pack refresh started in the background." : ""));
			return true;
		}

		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("cameraalbum")) {
			ItemStack book = AlbumManager.generateBook(p);
			if (p.getInventory().firstEmpty() == -1) {
				p.getWorld().dropItem(p.getLocation(), book);
			} else {
				p.getInventory().addItem(book);
			}
			p.sendMessage(ChatColor.GREEN + "[Cameras] Here's your up-to-date photo album.");
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("takepicture") && p.isOp()) {
			water.of.cup.CameraProfile profile = Camera.getInstance().getCameraProfiles().get("default");
			if (profile == null && !Camera.getInstance().getCameraProfiles().isEmpty()) {
				profile = Camera.getInstance().getCameraProfiles().values().iterator().next();
			}
			if (profile != null) {
				Picture.takePicture(p, profile, profile.getFilter());
			}
		}
		return true;
	}

}
