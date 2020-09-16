package net.colonymc.colonyskyblockcore.shop;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyskyblockcore.MainMessages;

public class ShopCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(args.length >= 1) {
				if(ShopCategory.getByName(args[0]) != null) {
					ShopCategory cat = ShopCategory.getByName(args[0]);
					new ShopCategoryMenu(p, cat);
				}
				else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis shop category doesn't exist!"));
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
			}
			else {
				new ShopCategoriesMenu(p);
			}
		}
		else {
			sender.sendMessage(MainMessages.onlyPlayers);
		}
		return false;
	}

}
