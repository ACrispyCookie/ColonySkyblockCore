package net.colonymc.colonyskyblockcore.util.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class WarBoard {
	
	Scoreboard sb;
	
	public WarBoard() {
		setupScoreboard();
	}

	private void setupScoreboard() {
		ScoreboardManager m = Bukkit.getScoreboardManager();
		org.bukkit.scoreboard.Scoreboard b = m.getNewScoreboard();
		Objective o = b.registerNewObjective("GuildWar", "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&5&lGuild War"));
		Team spacer = b.registerNewTeam("spacer");
		spacer.addEntry(ChatColor.translateAlternateColorCodes('&', "&r&r&r"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r&r&r")).setScore(11);
		Team guild = b.registerNewTeam("enemy");
		guild.addEntry(ChatColor.translateAlternateColorCodes('&', "&0"));
		guild.setPrefix(ChatColor.translateAlternateColorCodes('&', "&fYour Enemy:"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&0")).setScore(10);
		Team guildName = b.registerNewTeam("guildName");
		guildName.addEntry(ChatColor.translateAlternateColorCodes('&', "&1"));
		guildName.setPrefix(ChatColor.translateAlternateColorCodes('&', " &d- "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&1")).setScore(9);
		Team spacer1 = b.registerNewTeam("spacer1");
		spacer1.addEntry(ChatColor.translateAlternateColorCodes('&', "&r"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r")).setScore(8);
		Team guild1 = b.registerNewTeam("warType");
		guild1.addEntry(ChatColor.translateAlternateColorCodes('&', "&2"));
		guild1.setPrefix(ChatColor.translateAlternateColorCodes('&', "&fWar Type: "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&2")).setScore(7);
		Team guildRole = b.registerNewTeam("timeRemaining");
		guildRole.addEntry(ChatColor.translateAlternateColorCodes('&', "&3"));
		guildRole.setPrefix(ChatColor.translateAlternateColorCodes('&', "&fTime left: "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&3")).setScore(6);
		Team spacer2 = b.registerNewTeam("spacer2");
		spacer2.addEntry(ChatColor.translateAlternateColorCodes('&', "&r&r&r&r"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r&r&r&r")).setScore(5);
		Team guild2 = b.registerNewTeam("enemiesLeft");
		guild2.addEntry(ChatColor.translateAlternateColorCodes('&', "&6"));
		guild2.setPrefix(ChatColor.translateAlternateColorCodes('&', "&fEnemies left: "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&6")).setScore(4);
		Team guildTop = b.registerNewTeam("membersLeft");
		guildTop.addEntry(ChatColor.translateAlternateColorCodes('&', "&7"));
		guildTop.setPrefix(ChatColor.translateAlternateColorCodes('&', "&fTeam members: "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&7")).setScore(3);
		Team spacer3 = b.registerNewTeam("spacer3");
		spacer3.addEntry(ChatColor.translateAlternateColorCodes('&', "&r&r"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r&r")).setScore(2);
		Team scrollinfo = b.registerNewTeam("scrollinfo");
		scrollinfo.addEntry(ChatColor.translateAlternateColorCodes('&', "&c"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&c")).setScore(1);
		scrollinfo.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5» &fIP: &dplay"));
		scrollinfo.setSuffix(ChatColor.translateAlternateColorCodes('&', "&d.colonymc.net"));
		this.sb = b;
	}
	
	public Scoreboard getScoreboard() {
		return sb;
	}

}
