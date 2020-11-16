package net.colonymc.colonyskyblockcore.pouches;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyspigotapi.player.PlayerInventory;
import net.colonymc.colonyspigotapi.primitive.Numbers;
import net.colonymc.colonyspigotapi.primitive.RomanNumber;
import net.colonymc.colonyskyblockcore.MainMessages;

public class PouchCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/pouch <type> [player] [amount] [level]");
			StringBuilder valid = new StringBuilder(ChatColor.translateAlternateColorCodes('&', " &5&l» &fInvalid type! Valid types: &d"));
			for(PouchType t : PouchType.values()) {
				if(t.ordinal() + 1 == PouchType.values().length) {
					valid.append(t.name());
				}
				else {
					valid.append(t.name()).append(", ");
				}
			}
			if(p.hasPermission("*")) {
				if(args.length == 1) {
					if(PouchType.contains(args[0])) {
						PouchItem item = new PouchItem(PouchType.valueOf(args[0]), p, 1);
						PlayerInventory.addItem(item.getItem(), p, 1);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d1x &f" + PouchType.valueOf(args[0]).pouchType + " Pouch &7(&dI&7) &f!"));
					}
					else {
						p.sendMessage(valid.toString());
					}
				}
				else if(args.length == 2) {
					if(PouchType.contains(args[0])) {
						if(Numbers.isInt(args[1])) {
							PouchItem item = new PouchItem(PouchType.valueOf(args[0]), p, 1);
							PlayerInventory.addItem(item.getItem(), p, Integer.parseInt(args[1]));
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d" + args[1] + "x " 
							+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&dI&7) &f!"));
						}
						else if(Bukkit.getPlayerExact(args[1]) != null) {
							PouchItem item = new PouchItem(PouchType.valueOf(args[0]), Bukkit.getPlayerExact(args[1]), 1);
							PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[1]), 1);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d1x &f" 
							+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&dI&7) &fto the player &d" + Bukkit.getPlayerExact(args[1]).getName() + "&f!"));
							Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d1x " 
							+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&dI&7) &f!"));
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
					if(PouchType.contains(args[0])) {
						if(Numbers.isInt(args[2])) {
							if(Bukkit.getPlayerExact(args[1]) != null) {
								PouchItem item = new PouchItem(PouchType.valueOf(args[0]), Bukkit.getPlayerExact(args[1]), 1);
								PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[1]), Integer.parseInt(args[2]));
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + args[2] + "x " 
								+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&dI&7) &fto the player &d" + Bukkit.getPlayerExact(args[1]).getName() + "&f!"));
								Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d" + args[2] + "x " 
								+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&dI&7) &f!"));
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
				else if(args.length == 4) {
					if(PouchType.contains(args[0])) {
						if(Numbers.isInt(args[2]) && Numbers.isInt(args[3])) {
							if(Bukkit.getPlayerExact(args[1]) != null) {
								PouchItem item = new PouchItem(PouchType.valueOf(args[0]), Bukkit.getPlayerExact(args[1]), Integer.parseInt(args[3]));
								PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[1]), Integer.parseInt(args[2]));
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + args[2] + "x " 
								+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&d" + RomanNumber.toRoman(Integer.parseInt(args[3])) + "&7) &fto the player &d" + Bukkit.getPlayerExact(args[1]).getName() + "&f!"));
								Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d" + args[2] + "x " 
								+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&d" + RomanNumber.toRoman(Integer.parseInt(args[3])) + "&7) &f!"));
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
			String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/pouch <type> <player> [amount] [level]");
			StringBuilder valid = new StringBuilder(ChatColor.translateAlternateColorCodes('&', " &5&l» &fInvalid type! Valid types: &d"));
			for(PouchType t : PouchType.values()) {
				if(t.ordinal() + 1 == PouchType.values().length) {
					valid.append(t.name());
				}
				else {
					valid.append(t.name()).append(", ");
				}
			}
			if(args.length == 2) {
				if(Bukkit.getPlayerExact(args[1]) != null) {
					if(PouchType.contains(args[0])) {
						PouchItem item = new PouchItem(PouchType.valueOf(args[0]), Bukkit.getPlayerExact(args[1]), 1);
						PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[1]), 1);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d1x " 
						+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&dI&7) &fto the player &d" + Bukkit.getPlayerExact(args[1]).getName() + "&f!"));
						Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d1x " 
						+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&dI&7) &f!"));
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
				if(Bukkit.getPlayerExact(args[1]) != null) {
					if(PouchType.contains(args[0])) {
						if(Numbers.isInt(args[2])) {
							PouchItem item = new PouchItem(PouchType.valueOf(args[0]), Bukkit.getPlayerExact(args[1]), 1);
							PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[1]), Integer.parseInt(args[2]));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + args[2] + "x " 
							+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&dI&7) &fto the player &d" + Bukkit.getPlayerExact(args[1]).getName() + "&f!"));
							Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d" + args[2] + "x " 
							+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&dI&7) &f!"));
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
			else if(args.length == 4) {
				if(Bukkit.getPlayerExact(args[1]) != null) {
					if(PouchType.contains(args[0])) {
						if(Numbers.isInt(args[2]) && Numbers.isInt(args[3])) {
							PouchItem item = new PouchItem(PouchType.valueOf(args[0]), Bukkit.getPlayerExact(args[1]), Integer.parseInt(args[3]));
							PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[1]), Integer.parseInt(args[2]));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + args[2] + "x " 
							+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&d" + RomanNumber.toRoman(Integer.parseInt(args[3])) + "&7) &fto the player &d" + Bukkit.getPlayerExact(args[1]).getName() + "&f!"));
							Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d" + args[2] + "x " 
							+ PouchType.valueOf(args[0]).pouchType + " Pouch &7(&d" + RomanNumber.toRoman(Integer.parseInt(args[3])) + "&7) &f!"));
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
