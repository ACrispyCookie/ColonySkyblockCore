package net.colonymc.colonyskyblockcore.guilds.test;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventoryCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			if(args.length == 0) {
				new AuctionHouseSelectMenu((Player) sender);
			}
		}
		return false;
	}

}
