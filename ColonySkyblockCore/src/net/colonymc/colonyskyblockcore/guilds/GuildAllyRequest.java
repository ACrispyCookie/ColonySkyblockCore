package net.colonymc.colonyskyblockcore.guilds;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyskyblockcore.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class GuildAllyRequest {
	
	Guild g1;
	Guild g;
	BukkitRunnable expiration;
	
	public GuildAllyRequest(Guild g1, Guild g) {
		this.g1 = g1;
		this.g = g;
		g.sendGuildSound(Sound.ORB_PICKUP, 1);
		TextComponent toSendOnRequested = new TextComponent(ChatColor.translateAlternateColorCodes('&', 
				"&d&l&k:&d&lALLY REQUEST&k:&r &fYour guild has been requested to be an ally of the guild &d" + g1.getName() + "&f! "));
		toSendOnRequested.addExtra(getMessage("&d&l[CLICK HERE]", "/g ally " + g1.getName()));
		toSendOnRequested.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', " &fto accept this request!")));
		g.sendGuildMessage(toSendOnRequested);
		g1.sendGuildMessage("&fYour guild has requested from &d" + g.getName() + " &fto become allies! They have &d60 seconds &fto accept!");
		g1.sendGuildSound(Sound.ORB_PICKUP, 1);
		expiration = new BukkitRunnable() {
			@Override
			public void run() {
				expire();
			}
		};
		expiration.runTaskLaterAsynchronously(Main.getInstance(), 1200);
		GuildCommand.allyRequests.add(this);
	}

	public void accept() {
		expiration.cancel();
		GuildCommand.allyRequests.remove(GuildCommand.allyRequests.indexOf(this));
		g.setRelation(g1, Relation.ALLY, true);
	}
	
	public void expire() {
		GuildCommand.allyRequests.remove(GuildCommand.allyRequests.indexOf(this));
		g1.sendGuildMessage(ChatColor.translateAlternateColorCodes('&', "&fThe ally request to &d" + g.getName() + " &fhas expired!"));
		g1.sendGuildSound(Sound.NOTE_BASS, 1);
		g.sendGuildMessage(ChatColor.translateAlternateColorCodes('&', "&fThe ally request from &d" + g1.getName() + " &fhas expired!"));
		g.sendGuildSound(Sound.NOTE_BASS, 1);
	}
	
	public TextComponent getMessage(String message, String command) {
		TextComponent t = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return t;
	}
	
	public Guild getGuild() {
		return g;
	}
	
	public Guild getRequested() {
		return g1;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isAlliedBy(Guild g, Guild g1) {
		ArrayList<GuildAllyRequest> requests = (ArrayList<GuildAllyRequest>) GuildCommand.allyRequests.clone();
		for(GuildAllyRequest i : requests) {
			if(i.getGuild().equals(g) && i.getRequested().equals(g1)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static GuildAllyRequest getAllyRequestByGuild(Guild g, Guild g1) {
		ArrayList<GuildAllyRequest> requests = (ArrayList<GuildAllyRequest>) GuildCommand.allyRequests.clone();
		for(GuildAllyRequest i : requests) {
			if(i.getGuild().equals(g) && i.getRequested().equals(g1)) {
				return i;
			}
		}
		return null;
	}

}
