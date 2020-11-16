package net.colonymc.colonyskyblockcore.util.currencies;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyspigotapi.primitive.Numbers;
import net.colonymc.colonyskyblockcore.MainMessages;
import net.colonymc.colonyskyblockcore.guilds.Guild;

public class DwarfDustCommand implements CommandExecutor {

	final String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/dwarfdust give/take/set <player> <amount>");
	final String youHave = ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou currently have &d");
	final String invalidNumber = ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number!");
	final String nerverJoined = ChatColor.translateAlternateColorCodes('&', " &5&l» &fThis player has never joined the server!");
	final String numberBiggerThanZero = ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a number bigger than 0!");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(cmd.getName().equals("dust")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(p.hasPermission("*")) {
					sender.sendMessage(youHave + Guild.balance(Guild.getByPlayer(p).getGuildPlayer(p).getDust()) + ChatColor.translateAlternateColorCodes('&', " of dwarf dust&f."));
				}
				else {
					((Player) sender).playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&oWhat is this mysterious dust..."));
				}
			}
			else {
				sender.sendMessage(MainMessages.onlyPlayers);
			}
		}
		else {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.hasPermission("*")) {
					if(args.length == 3) {
						if(args[0].equals("give") || args[0].equals("take") || args[0].equals("set")) {
							if(Bukkit.getPlayerExact(args[1]) != null) {
								Player p = Bukkit.getPlayerExact(args[1]);
								if(Numbers.isDouble(args[2])) {
									switch(args[0]) {
									case "give":
										if(Double.parseDouble(args[2]) > 0) {
											Guild.getByPlayer(p).getGuildPlayer(p).addDust(Double.parseDouble(args[2]));
											sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + p.getName() + " " + args[2] + "g &fof dwarf dust!"));
										}
										else {
											sender.sendMessage(numberBiggerThanZero);
										}
										break;
									case "take":
										if(Double.parseDouble(args[2]) > 0) {
											if(Guild.getByPlayer(p).getGuildPlayer(p).getDust() >= Double.parseDouble(args[2])) {
												Guild.getByPlayer(p).getGuildPlayer(p).removeDust(Double.parseDouble(args[2]));
												sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou took &d" + args[2] + "g &fdwarf dust from &d" + p.getName() + "&f!"));
											}
											else {
												sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThis player currently only has &d" + Guild.getByPlayer(p).getGuildPlayer(p).getDust() + "g &fof dwarf dust!"));
											}
										}
										else {
											sender.sendMessage(numberBiggerThanZero);
										}
										break;
									case "set":
										if(Double.parseDouble(args[2]) >= 0) {
											Guild.getByPlayer(p).getGuildPlayer(p).setDust(Double.parseDouble(args[2]));
											sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou set &d" + p.getName() + "'s &fdwarf dust to &d" + args[2] + "&f!"));
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
						if(sender instanceof Player) {
							Player p = (Player) sender;
							sender.sendMessage(youHave + Guild.balance(Guild.getByPlayer(p).getGuildPlayer(p).getDust()) + ChatColor.translateAlternateColorCodes('&', " of dwarf dust&f."));
						}
						else {
							sender.sendMessage(MainMessages.onlyPlayers);
						}
					}
				}
				else {
					player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&oWhat is this mysterious dust..."));
				}
			}
		}
		return false;
	}
	
	

}
