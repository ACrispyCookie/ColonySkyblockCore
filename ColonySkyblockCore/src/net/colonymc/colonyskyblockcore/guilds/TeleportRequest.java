package net.colonymc.colonyskyblockcore.guilds;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyskyblockcore.Main;

public class TeleportRequest {
	
	Player p;
	Player toTeleport;
	BukkitRunnable expire;
	
	public TeleportRequest(Player p, Player toTeleport) {
		this.p = p;
		this.toTeleport = toTeleport;
		TeleportCommand.requests.put(p, this);
		expire = new BukkitRunnable() {
			@Override
			public void run() {
				expire();
			}
		};
		expire.runTaskLater(Main.getInstance(), 1200L);
	}
	
	public void sendToPlayer() {
		toTeleport.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThe player &d" + p.getName() + " &fwants to teleport to you!"));
		toTeleport.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fType &d/tp accept " + p.getName() + " &fto accept this request! "
				+ "After &d60 seconds &fthis request will expire!"));
		toTeleport.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou requested from &d" + toTeleport.getName() + " &fto teleport to them! They have &d60 seconds &fto accept!"));
		p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
	}
	
	public void accept() {
		this.expire.cancel();
		TeleportCommand.requests.remove(p);
		if(toTeleport.getWorld().equals(Island.getWorld())) {
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
			p.teleport(toTeleport.getLocation());
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have been teleported to &d" + toTeleport.getName() + "&f!"));
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
			toTeleport.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThe player &d" + p.getName() + " &fhas teleported to you!"));
		}
	}
	
	public void expire() {
		TeleportCommand.requests.remove(p);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe teleportation request to " + toTeleport.getName() + " has expired!"));
		p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
		toTeleport.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe teleportation request from " + p.getName() + " has expired!"));
		toTeleport.playSound(toTeleport.getLocation(), Sound.NOTE_BASS, 2, 1);
	}

}
