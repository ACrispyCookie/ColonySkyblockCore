package net.colonymc.colonyskyblockcore.crates;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyskyblockcore.MainMessages;
import net.colonymc.colonyskyblockcore.guilds.Guild;

public class CrateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command name, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(Guild.getByPlayer(p) != null) {
				p.openInventory(new CrateMenu(p).getInventory());
			}
			else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create or join a guild in order to access the crates!"));
			}
		}
		else {
			sender.sendMessage(MainMessages.onlyPlayers);
		}
		return false;
	}

}
