package net.colonymc.colonyskyblockcore.util.currencies;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyspigotapi.primitive.Numbers;
import net.colonymc.colonyskyblockcore.MainMessages;
import net.colonymc.colonyskyblockcore.guilds.Guild;

public class SilverCommand implements CommandExecutor {

	final String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/balance give/take/set <player> <amount>");
	final String youHave = ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou currently have &d");
	final String invalidNumber = ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number!");
	final String nerverJoined = ChatColor.translateAlternateColorCodes('&', " &5&l» &fThis player is not online!");
	final String numberBiggerThanZero = ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a number bigger than 0!");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(args.length == 3) {
			if(sender.hasPermission("*")) {
				if(args[0].equals("give") || args[0].equals("take") || args[0].equals("set")) {
					if(Bukkit.getPlayerExact(args[1]) != null) {
						Player anotherP = Bukkit.getPlayerExact(args[1]);
						if(Numbers.isDouble(args[2])) {
							switch(args[0]) {
							case "give":
								if(Double.parseDouble(args[2]) > 0) {
									Guild.getByPlayer(anotherP).getGuildPlayer(anotherP).addBalance(Double.parseDouble(args[2]));
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + anotherP.getName() + " &d$" + args[2] + "&f!"));
								}
								else {
									sender.sendMessage(numberBiggerThanZero);
								}
								break;
							case "take":
								if(Double.parseDouble(args[2]) > 0) {
									if(Guild.getByPlayer(anotherP).getGuildPlayer(anotherP).getBalance() >= Double.parseDouble(args[2])) {
										Guild.getByPlayer(anotherP).getGuildPlayer(anotherP).removeBalance(Double.parseDouble(args[2]));
										sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou took &d$" + args[2] + " &ffrom &d" + anotherP.getName() + "&f!"));
									}
									else {
										sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThis player currently only has &d" + Guild.balance(Guild.getByPlayer(anotherP).getGuildPlayer(anotherP).getBalance()) + "&f!"));
									}
								}
								else {
									sender.sendMessage(numberBiggerThanZero);
								}
								break;
							case "set":
								if(Double.parseDouble(args[2]) >= 0) {
									Guild.getByPlayer(anotherP).getGuildPlayer(anotherP).setBalance(Double.parseDouble(args[2]));
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou set &d" + anotherP.getName() + "'s &fbalance to &d" + args[2] + "g&f!"));
								}
								else {
									sender.sendMessage(numberBiggerThanZero);
								}
								break;
							}
						}
						else {
							sender.sendMessage(invalidNumber);
						}
					}
					else {
						sender.sendMessage(nerverJoined);
					}
				}
				else {
					sender.sendMessage(usage);
				}
			}
			else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot execute this command!"));
			}
		}
		else {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				sender.sendMessage(youHave + Guild.balance(Guild.getByPlayer(p).getGuildPlayer(p).getBalance()) + ChatColor.translateAlternateColorCodes('&', "&f."));
			}
			else {
				sender.sendMessage(MainMessages.onlyPlayers);
			}
		}
		return false;
	}
	
	

}
