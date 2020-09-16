package net.colonymc.colonyskyblockcore.kits;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.colonymc.colonyskyblockcore.MainMessages;

public class DeleteKitCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("*")) {
			if(args.length == 1) {
				if(Kit.getByName(args[0]) != null) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou deleted the kit &d" + Kit.getByName(args[0]).name + "&f!"));
					Kit.getByName(args[0]).delete();
				}
				else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis kit doesn't exist!"));
				}
			}
			else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/deletekit <kit>"));
			}
		}
		else {
			sender.sendMessage(MainMessages.noPerm);
		}
		return false;
	}

}
