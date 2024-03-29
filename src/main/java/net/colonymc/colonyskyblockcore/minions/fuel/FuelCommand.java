package net.colonymc.colonyskyblockcore.minions.fuel;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.colonymc.colonyspigotlib.lib.player.PlayerInventory;
import net.colonymc.colonyspigotlib.lib.primitive.Numbers;
import net.colonymc.colonyskyblockcore.MainMessages;

public class FuelCommand implements CommandExecutor, TabExecutor {
	
	final String youGave = ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have gave &d");
	final String received = ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have received &d");
	final String notOnline = ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online!");
	final String invalidAmount = ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid amount bigger than 0!");
	String invalidLevel = ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid level bigger than 0 and smaller than 11!");
	final String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/fuel <type> [player] [amount]");
	String invalidType = ChatColor.translateAlternateColorCodes('&', " &5&l» &fInvalid item type! Valid item types: &d");
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length == 1) {
			List<String> matches = new ArrayList<>();
			String search = args[0].toLowerCase();
			for (FuelType t : FuelType.values()) {
	            if(t.className.startsWith(search)) {
	        		matches.add(t.className);
	            }
			}
			return matches;
		}
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender.hasPermission("*")) {
			switch(args.length) {
			case 1:
				if(FuelChecker.typeFromEncodedName(args[0]) != null) {
					if(sender instanceof Player) {
						Player target = (Player) sender;
						FuelType type = FuelChecker.typeFromEncodedName(args[0]);
						Fuel item = Fuel.createNewFromType(type);
						PlayerInventory.addItem(item.getItem(), target, 1);
						target.sendMessage(received + "1x " + item.getName());
					}
					else {
						sender.sendMessage(MainMessages.onlyPlayers);
					}
				}
				else {
					for(FuelType t : FuelType.values()) {
						if(t.ordinal() > 0) {
							invalidType = invalidType + ", " + t.className;
						}
						else {
							invalidType = invalidType + t.className;
						}
					}
					sender.sendMessage(invalidType);
				}
				break;
			case 2:
				if(FuelChecker.typeFromEncodedName(args[0]) != null) {
					if(Bukkit.getPlayer(args[1]) != null) {
						Player target = Bukkit.getPlayer(args[1]);
						FuelType type = FuelChecker.typeFromEncodedName(args[0]);
						Fuel item = Fuel.createNewFromType(type);
						PlayerInventory.addItem(item.getItem(), target, 1);
						target.sendMessage(received + "1x " + item.getName());
						sender.sendMessage(youGave + "1x " + item.getName() + ChatColor.translateAlternateColorCodes('&', " &fto &d" + target.getName()));
					}
					else {
						sender.sendMessage(notOnline);
					}
				}
				else {
					for(FuelType t : FuelType.values()) {
						if(t.ordinal() > 0) {
							invalidType = invalidType + ", " + t.className;
						}
						else {
							invalidType = invalidType + t.className;
						}
					}
					sender.sendMessage(invalidType);
				}
				break;
			case 3:
				if(FuelChecker.typeFromEncodedName(args[0]) != null) {
					if(Bukkit.getPlayer(args[1]) != null) {
						if(Numbers.isInt(args[2]) && Integer.parseInt(args[2]) > 0) {
							Player target = Bukkit.getPlayer(args[1]);
							FuelType type = FuelChecker.typeFromEncodedName(args[0]);
							Fuel item = Fuel.createNewFromType(type);
							PlayerInventory.addItem(item.getItem(), target, Integer.parseInt(args[2]));
							target.sendMessage(received + args[2] + "x " + item.getName());
							sender.sendMessage(youGave + args[2] + "x " + item.getName() + ChatColor.translateAlternateColorCodes('&', " &fto &d" + target.getName()));
						}
						else{
							sender.sendMessage(invalidAmount);
						}
					}
					else {
						sender.sendMessage(notOnline);
					}
				}
				else {
					for(FuelType t : FuelType.values()) {
						if(t.ordinal() > 0) {
							invalidType = invalidType + ", " + t.className;
						}
						else {
							invalidType = invalidType + t.className;
						}
					}
					sender.sendMessage(invalidType);
				}
				break;
			default:
				sender.sendMessage(usage);
			}
		}
		else {
			sender.sendMessage(MainMessages.noPerm);
		}
		invalidType = ChatColor.translateAlternateColorCodes('&', " &5&l» &fInvalid item type! Valid item types: &d");
		return false;
	}
}
