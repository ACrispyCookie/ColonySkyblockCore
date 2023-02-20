package net.colonymc.colonyskyblockcore.guilds;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyspigotlib.lib.primitive.Numbers;

public class TeleportCommand implements CommandExecutor {
	
	static final HashMap<Player, TeleportRequest> requests = new HashMap<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(args.length == 1) {
				if(p.hasPermission("staff.store")) {
					if(Bukkit.getPlayerExact(args[0]) != null) {
						Player toTeleport = Bukkit.getPlayerExact(args[0]);
						if(toTeleport.getWorld().equals(Island.getWorld())) {
							if(!p.equals(toTeleport)) {
								Island i = Island.visitors.get(toTeleport);
								if(!i.equals(Island.visitors.get(p))) {
									i.sendPlayer(p, true);
								}
								p.teleport(toTeleport.getLocation());
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have been teleported to &d" + toTeleport.getName() + "&f!"));
								p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
								toTeleport.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThe player &d" + p.getName() + " &fhas teleported to you!"));
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot teleport to yourself!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else {
							if(!p.equals(toTeleport)) {
								p.teleport(toTeleport.getLocation());
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have been teleported to &d" + toTeleport.getName() + "&f!"));
								p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
								toTeleport.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThe player &d" + p.getName() + " &fhas teleported to you!"));
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot teleport to yourself!"));
								p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
							}
						}
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else {
					if(Bukkit.getPlayerExact(args[0]) != null) {
						if(!p.equals(Bukkit.getPlayerExact(args[0]))) {
							new TeleportRequest(p, Bukkit.getPlayerExact(args[0])).sendToPlayer();
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot teleport to yourself!"));
							p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
						}
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
			}
			else if(args.length == 2) {
				if(!args[0].equals("accept") && args[1].equals("-s") && p.hasPermission("staff.store")) {
					if(Bukkit.getPlayerExact(args[0]) != null) {
						Player toTeleport = Bukkit.getPlayerExact(args[0]);
						if(toTeleport.getWorld().equals(Island.getWorld())) {
							if(!p.equals(toTeleport)) {
								Island i = Island.visitors.get(toTeleport);
								i.sendPlayer(p, true);
								p.teleport(toTeleport.getLocation());
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have been teleported to &d" + toTeleport.getName() + "&f!"));
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot teleport to yourself!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else {
							if(!p.equals(toTeleport)) {
								p.teleport(toTeleport.getLocation());
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have been teleported to &d" + toTeleport.getName() + "&f!"));
								p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot teleport to yourself!"));
								p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
							}
						}
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(args[0].equals("accept")) {
					if(Bukkit.getPlayerExact(args[1]) != null) {
						if(requests.containsKey(Bukkit.getPlayerExact(args[1]))) {
							TeleportRequest r = requests.get(Bukkit.getPlayerExact(args[1]));
							r.accept();
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou have no teleportation request from this player!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(Bukkit.getPlayerExact(args[0]) != null && Bukkit.getPlayerExact(args[1]) != null && p.hasPermission("staff.store")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have teleported &d" + Bukkit.getPlayerExact(args[0]).getName() + " &fto the player &d" + Bukkit.getPlayerExact(args[1]).getName()));
					if(Bukkit.getPlayerExact(args[1]).getWorld().equals(Island.getWorld())) {
						Island i = Island.visitors.get(Bukkit.getPlayerExact(args[1]));
						i.sendPlayer(Bukkit.getPlayerExact(args[0]), true);
						Bukkit.getPlayerExact(args[0]).teleport(Bukkit.getPlayerExact(args[1]));
					}
					else {
						Bukkit.getPlayerExact(args[0]).teleport(Bukkit.getPlayerExact(args[1]));
					}
				}
				else {
					if(p.hasPermission("staff.store")) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/" + label + " <name> [-s]/[name] &fOR &d/" + label + " <x> <y> <z> <yaw> <pitch>"));
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/" + label + " <name>"));
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
					}
				}
			}
			else if(args.length == 3 || args.length == 5) {
				boolean allAreInts = true;
				for(String s : args) {
					if(!Numbers.isDouble(s)) {
						allAreInts = false;
					}
				}
				if(allAreInts) {
					Location loc = args.length == 5 ? new Location(p.getWorld(), Float.parseFloat(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]), Float.parseFloat(args[4])) 
							: new Location(p.getWorld(), Float.parseFloat(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]));
					p.teleport(loc);
				}
				else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter valid numbers!"));
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
			}
			else {
				if(p.hasPermission("staff.store")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/" + label + " <name> [-s]/[name] &fOR &d/" + label + " <x> <y> <z> <yaw> <pitch>"));
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				}
				else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/" + label + " <name>"));
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				}
			}
		}
		return false;
	}
}
