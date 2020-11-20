package net.colonymc.colonyskyblockcore.spawners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.colonymc.colonyspigotapi.api.player.PlayerInventory;
import net.colonymc.colonyspigotapi.api.primitive.Numbers;
import net.colonymc.colonyskyblockcore.MainMessages;

public class SpawnerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> alive = new ArrayList<>();
        for (EntityType value : EntityType.values()) {
            if(value.isAlive() && value != EntityType.ARMOR_STAND && value != EntityType.PLAYER) {
                alive.add(value.name());
            }
        }
		if(sender instanceof Player) {
			Player p = (Player) sender;
			String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/spanwer <type> [player] [amount]");
			StringBuilder valid = new StringBuilder(ChatColor.translateAlternateColorCodes('&', " &5&l» &fInvalid type! Valid types: &d"));
			for(int i = 0; i < alive.size(); i++) {
				if(i + 1 == alive.size()) {
					valid.append(alive.get(i));
				}
				else {
					valid.append(alive.get(i)).append(", ");
				}
			}
			if(p.hasPermission("*")) {
				if(args.length == 1) {
					if(alive.contains(args[0].toUpperCase())) {
						SpawnerItem item = new SpawnerItem(EntityType.valueOf(args[0].toUpperCase()));
						PlayerInventory.addItem(item.getItem(), p, 1);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d1x &f" + item.getItem().getItemMeta().getDisplayName() + "&f!"));
					}
					else {
						p.sendMessage(valid.toString());
					}
				}
				else if(args.length == 2) {
					if(alive.contains(args[0].toUpperCase())) {
						if(Numbers.isInt(args[1])) {
							SpawnerItem item = new SpawnerItem(EntityType.valueOf(args[0].toUpperCase()));
							PlayerInventory.addItem(item.getItem(), p, Integer.parseInt(args[1]));
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d" + args[1] + "x &f" + item.getItem().getItemMeta().getDisplayName() + "&f!"));
						}
						else if(Bukkit.getPlayerExact(args[1]) != null) {
							SpawnerItem item = new SpawnerItem(EntityType.valueOf(args[0].toUpperCase()));
							PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[1]), 1);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d1x &f" 
									+ item.getItem().getItemMeta().getDisplayName() + " &fto the player &d" + Bukkit.getPlayerExact(args[1]).getName() + "&f!"));
							Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d1x " 
							+ item.getItem().getItemMeta().getDisplayName() + "&f!"));
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
					if(alive.contains(args[0].toUpperCase())) {
						if(Numbers.isInt(args[2])) {
							if(Bukkit.getPlayerExact(args[1]) != null) {
								SpawnerItem item = new SpawnerItem(EntityType.valueOf(args[0].toUpperCase()));
								PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[1]), Integer.parseInt(args[2]));
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + args[2] + "x " 
								+ item.getItem().getItemMeta().getDisplayName() + " &fto the player &d" + Bukkit.getPlayerExact(args[1]).getName() + "&f!"));
								Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d" + args[2] + "x " 
								+ item.getItem().getItemMeta().getDisplayName() + "&f!"));
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
			String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/spawner <type> <player> [amount]");
			StringBuilder valid = new StringBuilder(ChatColor.translateAlternateColorCodes('&', " &5&l» &fInvalid type! Valid types: &d"));
			for(int i = 0; i < alive.size(); i++) {
				if(i + 1 == alive.size()) {
					valid.append(alive.get(i));
				}
				else {
					valid.append(alive.get(i)).append(", ");
				}
			}
			if(args.length == 2) {
				if(Bukkit.getPlayerExact(args[1]) != null) {
					if(alive.contains(args[0].toUpperCase())) {
						SpawnerItem item = new SpawnerItem(EntityType.valueOf(args[0].toUpperCase()));
						PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[1]), 1);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d1x " 
						+ item.getItem().getItemMeta().getDisplayName() + " &fto the player &d" + Bukkit.getPlayerExact(args[1]).getName() + "&f!"));
						Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d1x " 
						+ item.getItem().getItemMeta().getDisplayName() + "&f!"));
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
					if(alive.contains(args[0].toUpperCase())) {
						if(Numbers.isInt(args[2])) {
							SpawnerItem item = new SpawnerItem(EntityType.valueOf(args[0].toUpperCase()));
							PlayerInventory.addItem(item.getItem(), Bukkit.getPlayerExact(args[1]), Integer.parseInt(args[2]));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou gave &d" + args[2] + "x " 
							+ item.getItem().getItemMeta().getDisplayName() + " &fto the player &d" + Bukkit.getPlayerExact(args[1]).getName() + "&f!"));
							Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou received &d" + args[2] + "x " 
							+ item.getItem().getItemMeta().getDisplayName() + "&f!"));
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
