package net.colonymc.colonyskyblockcore.guilds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(Island.getVisitorMap().containsKey(p)) {
				Island.getVisitorMap().remove(p);
			}
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fTeleporting you to the spawn..."));
			p.teleport(new Location(Bukkit.getWorld("hub"), 0.5, 70, 0.5, 270, 0));
		}
		else {
			sender.sendMessage("Only players can use this command!");
		}
		return false;
	}

}
