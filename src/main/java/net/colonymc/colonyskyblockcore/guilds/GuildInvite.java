package net.colonymc.colonyskyblockcore.guilds;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyskyblockcore.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class GuildInvite {
	
	final Player p;
	final Guild g;
	final BukkitRunnable expiration;
	
	@SuppressWarnings("deprecation")
	public GuildInvite(Player p, Guild g) {
		this.p = p;
		this.g = g;
		p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
		p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&fNEW INVITE FROM:"), ChatColor.translateAlternateColorCodes('&', "&d" + g.getName()));
		TextComponent text = new TextComponent(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have been invited to the guild &d" + g.getName() + "&f!\n"));
		text.addExtra(getMessage("&d&l[CLICK HERE]", "/g join " + g.getName()));
		text.addExtra(ChatColor.translateAlternateColorCodes('&', " &fto accept the invitation! &fAfter &d60 seconds &fthis invitation will expire!"));
		p.spigot().sendMessage(text);
		expiration = new BukkitRunnable() {
			@Override
			public void run() {
				expire();
			}
		};
		expiration.runTaskLaterAsynchronously(Main.getInstance(), 1200);
		GuildCommand.invitations.add(this);
	}
	
	@SuppressWarnings("deprecation")
	public void accept() {
		expiration.cancel();
		GuildCommand.invitations.remove(this);
		g.sendGuildMessage("&d" + p.getName() + " &fhas joined the guild!");
		g.sendGuildSound(Sound.LEVEL_UP, 1);
		g.addMember(p, Role.MEMBER);
		GuildListeners.isForced.remove(p);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &d&l&k:&d&lJOINED GUILD!&k:&r &fYou have joined the guild &d" + g.getName() + "&f!"));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fType &d/guild go &fto teleport to your island!"));
		p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&d&k:&dJOINED GUILD&k:"), ChatColor.translateAlternateColorCodes('&', "&fYou joined the guild &d" + g.getName()));
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 2, 1);
	}
	
	public void expire() {
		GuildCommand.invitations.remove(this);
		g.sendGuildMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThe invititation to &d" + p.getName() + " &fhas expired!"));
		g.sendGuildSound(Sound.NOTE_BASS, 1);
		if(p.isOnline()) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThe invitiation from &d" + g.getName() + " &fhas expired!"));
			p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
		}
	}
	
	public Guild getGuild() {
		return g;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public TextComponent getMessage(String message, String command) {
		TextComponent t = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return t;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isInvitedBy(Player p, Guild g) {
		ArrayList<GuildInvite> invites = (ArrayList<GuildInvite>) GuildCommand.invitations.clone();
		for(GuildInvite i : invites) {
			if(i.getGuild().equals(g) && i.getPlayer().equals(p)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static GuildInvite getInviteByGuild(Player p, Guild g) {
		ArrayList<GuildInvite> invites = (ArrayList<GuildInvite>) GuildCommand.invitations.clone();
		for(GuildInvite i : invites) {
			if(i.getGuild().equals(g) && i.getPlayer().equals(p)) {
				return i;
			}
		}
		return null;
	}

}
