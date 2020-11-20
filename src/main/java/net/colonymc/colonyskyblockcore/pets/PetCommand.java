package net.colonymc.colonyskyblockcore.pets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyspigotapi.api.player.PlayerInventory;
import net.colonymc.colonyspigotapi.api.primitive.Numbers;
import net.colonymc.colonyskyblockcore.MainMessages;

public class PetCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/pet <type> [player] [amount]");
			StringBuilder valid = new StringBuilder(ChatColor.translateAlternateColorCodes('&', " &5&l» &fInvalid type! Valid types: &d"));
			for(PetType t : PetType.values()) {
				if(t.ordinal() + 1 == PetType.values().length) {
					valid.append(t.name());
				}
				else {
					valid.append(t.name()).append(", ");
				}
			}
			if(p.hasPermission("*")) {
				if(args.length == 1) {
					if(PetType.contains(args[0])) {
						PetItem item = new PetItem(PetType.valueOf(args[0]), p);
						PlayerInventory.addItem(item.getItem(), p, 1);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d1x &fof " + PetType.valueOf(args[0]).name + " Pet&f!"));
					}
					else {
						p.sendMessage(valid.toString());
					}
				}
				else if(args.length == 2) {
					if(PetType.contains(args[0])) {
						if(Numbers.isInt(args[1])) {
							PetItem item = new PetItem(PetType.valueOf(args[0]), p);
							PlayerInventory.addItem(item.getItem(), p, Integer.parseInt(args[1]));
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d" + args[1] + "x &fof " 
							+ PetType.valueOf(args[0]).name + " Pet&f!"));
						}
						else if(Bukkit.getPlayerExact(args[1]) != null) {
							PetItem item = new PetItem(PetType.valueOf(args[0]), Bukkit.getPlayerExact(args[1]));
							PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[1]), 1);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d1x &fof " 
							+ PetType.valueOf(args[0]).name + " Pet &fto the player &d" + Bukkit.getPlayerExact(args[1]).getName() + "&f!"));
							Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d1x &fof " 
							+ PetType.valueOf(args[0]).name + " Pet&f!"));
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number!"));
						}
					}
					else {
						p.sendMessage(valid.toString());
					}
				}
				else if(args.length == 3) {
					if(PetType.contains(args[0])) {
						if(Numbers.isInt(args[1])) {
							if(Bukkit.getPlayerExact(args[2]) != null) {
								PetItem item = new PetItem(PetType.valueOf(args[0]), Bukkit.getPlayerExact(args[2]));
								PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[2]), Integer.parseInt(args[1]));
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + args[1] + "x &fof " 
								+ PetType.valueOf(args[0]).name + " Pet &fto the player &d" + Bukkit.getPlayerExact(args[2]).getName() + "&f!"));
								Bukkit.getPlayerExact(args[2]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d" + args[1] + "x &fof " 
								+ PetType.valueOf(args[0]).name + " Pet&f!"));
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online!"));
							}
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number!"));
						}
					}
					else {
						p.sendMessage(valid.toString());
					}
				}
				else {
					p.sendMessage(usage);
				}
			}
			else {
				p.sendMessage(MainMessages.noPerm);
			}
		}
		else {
			String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/pet <player> <type> [amount]");
			StringBuilder valid = new StringBuilder(ChatColor.translateAlternateColorCodes('&', " &5&l» &fInvalid type! Valid types: &d"));
			for(PetType t : PetType.values()) {
				if(t.ordinal() + 1 == PetType.values().length) {
					valid.append(t.name);
				}
				else {
					valid.append(t.name).append(", ");
				}
			}
			if(args.length == 2) {
				if(Bukkit.getPlayerExact(args[0]) != null) {
					if(PetType.contains(args[0])) {
						PetItem item = new PetItem(PetType.valueOf(args[1]), Bukkit.getPlayerExact(args[0]));
						PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[0]), 1);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d1x &fof " 
						+ PetType.valueOf(args[1]).name + " Pet &fto the player &d" + Bukkit.getPlayerExact(args[0]).getName() + "&f!"));
						Bukkit.getPlayerExact(args[0]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d1x &fof " 
						+ PetType.valueOf(args[1]).name + " Pet&f!"));
					}
					else {
						sender.sendMessage(valid.toString());
					}
				}
				else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online!"));
				}
			}
			else if(args.length == 3) {
				if(Bukkit.getPlayerExact(args[0]) != null) {
					if(PetType.contains(args[0])) {
						if(Numbers.isInt(args[2])) {
							PetItem item = new PetItem(PetType.valueOf(args[1]), Bukkit.getPlayerExact(args[0]));
							PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[0]), Integer.parseInt(args[2]));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + args[2] + "x &fof " 
							+ PetType.valueOf(args[1]).name + " Pet &fto the player &d" + Bukkit.getPlayerExact(args[0]).getName() + "&f!"));
							Bukkit.getPlayerExact(args[0]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d" + args[2] + "x &fof " 
							+ PetType.valueOf(args[1]).name + " Pet&f!"));
						}
						else {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number!"));
						}
					}
					else {
						sender.sendMessage(valid.toString());
					}
				}
				else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online!"));
				}
			}
			else {
				sender.sendMessage(usage);
			}
		}
		return false;
	}

}
