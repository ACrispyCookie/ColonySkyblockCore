package net.colonymc.colonyskyblockcore.guilds.war;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Cooldown;
import net.colonymc.colonyskyblockcore.guilds.CooldownType;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.GuildPlayer;

public class War implements Listener {
	
	Guild oneGuild;
	Guild anotherGuild;
	WarType type;
	BukkitTask shouldStart;
	BukkitTask cancelTask;
	int timeLeft = 6000;
	ArrayList<GuildPlayer> acceptedPlayers = new ArrayList<GuildPlayer>();
	boolean accepted = false;
	public static ArrayList<War> activeWars = new ArrayList<War>();
	
	public War(Guild oneGuild, Guild anotherGuild, WarType type) {
		this.oneGuild = oneGuild;
		this.anotherGuild = anotherGuild;
		this.type = type;
		request();
		activeWars.add(this);
		this.cancelTask = new BukkitRunnable() {
			@Override
			public void run() {
				if(timeLeft == 0) {
					expire();
					cancel();
				}
				else {
					timeLeft--;
				}
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
		this.shouldStart = new BukkitRunnable() {
			@Override
			public void run() {
				boolean shouldStart = true;
				for(GuildPlayer gp : oneGuild.getMemberUuids().values()) {
					if(!acceptedPlayers.contains(gp)) {
						shouldStart = false;
					}
				}
				for(GuildPlayer gp : anotherGuild.getMemberUuids().values()) {
					if(!acceptedPlayers.contains(gp)) {
						shouldStart = false;
					}
				}
				if(shouldStart) {
					start();
					cancelTask.cancel();
					accepted = true;
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	
	public void request() {
		oneGuild.sendGuildMessage("&fYour guild owner &d" + oneGuild.getOwner().getPlayer().getName() + " &fhas requested from the guild &d" + anotherGuild.getName() + " &fto go on a &d" + type.name + " War&f! "
				+ "&fType &d/guild war &fto agree/disagree to the war! &fAll members of the guild must agree to the war! Both guilds have 5 minutes to accept before this request expires!");
		oneGuild.sendGuildSound(Sound.WITHER_SPAWN, 1);
		anotherGuild.sendGuildMessage("&fThe guild &d" + oneGuild.getName() + " &fhas requested from your guild to go on a &d" + type.name + 
				" War&f! &fType &d/guild war &fto agree/disagree to the war! &fAll members must agree to the war! Both guilds have 5 minutes to accept before this request expires!");
		anotherGuild.sendGuildSound(Sound.WITHER_SPAWN, 1);
	}
	
	public void expire() {
		oneGuild.sendGuildMessage("&fThe war request to the guild &d" + anotherGuild.getName() + " &ffor a &d" + type.name + " war &fhas &cexpired!");
		oneGuild.sendGuildSound(Sound.VILLAGER_NO, 1);
		anotherGuild.sendGuildMessage("&fThe war request from the guild &d" + oneGuild.getName() + " &ffor a &d" + type.name + " war &fhas &cexpired!");
		anotherGuild.sendGuildSound(Sound.VILLAGER_NO, 1);
		activeWars.remove(activeWars.indexOf(this));
	}
	
	public void toggleReady(Player p) {
		if(acceptedPlayers.contains(Guild.getByPlayer(p).getGuildPlayer(p))) {
			acceptedPlayers.remove(acceptedPlayers.indexOf(Guild.getByPlayer(p).getGuildPlayer(p)));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have changed your status to &cNot Ready&f!"));
			p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
		}
		else {
			acceptedPlayers.add(Guild.getByPlayer(p).getGuildPlayer(p));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have changed your status to &aReady&f!"));
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
			boolean shouldStart = true;
			for(GuildPlayer gp : oneGuild.getMemberUuids().values()) {
				if(!acceptedPlayers.contains(gp)) {
					shouldStart = false;
				}
			}
			for(GuildPlayer gp : anotherGuild.getMemberUuids().values()) {
				if(!acceptedPlayers.contains(gp)) {
					shouldStart = false;
				}
			}
			if(shouldStart) {
				start();
				this.shouldStart.cancel();
				cancelTask.cancel();
				accepted = true;
			}
		}
	}
	
	public void start() {
		oneGuild.sendGuildMessage("&fBoth guilds have accepted the war! Teleporting all players!");
		oneGuild.sendGuildSound(Sound.LEVEL_UP, 1);
		anotherGuild.sendGuildMessage("&fBoth guilds have accepted the war! Teleporting all players!");
		anotherGuild.sendGuildSound(Sound.LEVEL_UP, 1);
		if(!oneGuild.getOwner().getPlayer().getPlayer().hasPermission("*")) {
			new Cooldown(oneGuild.getOwner().getPlayer().getUniqueId().toString(), CooldownType.WAR);
		}
		if(!anotherGuild.getOwner().getPlayer().getPlayer().hasPermission("*")) {
			new Cooldown(anotherGuild.getOwner().getPlayer().getUniqueId().toString(), CooldownType.WAR);
		}
		new TeamDeathmatch(this);
	}
	
	public Guild getRequester() {
		return oneGuild;
	}
	
	public Guild getRequested() {
		return anotherGuild;
	}
	
	public boolean isAccepted() {
		return accepted;
	}
	
	public ArrayList<GuildPlayer> getAccepted() {
		return acceptedPlayers;
	}
	
	public int getTimeLeft() {
		return timeLeft;
	}
	
	public WarType getType() {
		return type;
	}
	
	public static boolean hasAccepted(Guild guild) {
		for(War w : activeWars) {
			if(w.oneGuild.equals(guild) || w.anotherGuild.equals(guild)) {
				if(w.accepted) {
					return true;
				}
				else {
					return false;
				}
			}
		}
		return false;
	}
	
	public static War isRequested(Guild guild) {
		for(War w : activeWars) {
			if(w.oneGuild.equals(guild) || w.anotherGuild.equals(guild)) {
				return w;
			}
		}
		return null;
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if(isRequested(Guild.getByPlayer(e.getPlayer())) != null) {
			War w = isRequested(Guild.getByPlayer(e.getPlayer()));
			if(w.acceptedPlayers.contains(Guild.getByPlayer(e.getPlayer()).getGuildPlayer(e.getPlayer()))) {
				w.toggleReady(e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerKickEvent e) {
		if(isRequested(Guild.getByPlayer(e.getPlayer())) != null) {
			War w = isRequested(Guild.getByPlayer(e.getPlayer()));
			if(w.acceptedPlayers.contains(Guild.getByPlayer(e.getPlayer()).getGuildPlayer(e.getPlayer()))) {
				w.toggleReady(e.getPlayer());
			}
		}
	}
}
