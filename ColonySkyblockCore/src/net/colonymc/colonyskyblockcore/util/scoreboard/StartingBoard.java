package net.colonymc.colonyskyblockcore.util.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class StartingBoard {
	
	Scoreboard sb;
	
	public StartingBoard() {
		setupScoreboard();
	}

	private void setupScoreboard() {
		ScoreboardManager m = Bukkit.getScoreboardManager();
		org.bukkit.scoreboard.Scoreboard b = m.getNewScoreboard();
		Objective o = b.registerNewObjective("Starting", "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dCreate a guild"));
		Team spacer = b.registerNewTeam("spacer");
		spacer.addEntry(ChatColor.translateAlternateColorCodes('&', "&r&r&r"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r&r&r")).setScore(10);
		Team welcome = b.registerNewTeam("welcome");
		welcome.addEntry(ChatColor.translateAlternateColorCodes('&', "&0"));
		welcome.setPrefix(ChatColor.translateAlternateColorCodes('&', "&fWelcome to "));
		welcome.setSuffix(ChatColor.translateAlternateColorCodes('&', "&5&lColonyMC&f!"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&0")).setScore(9);
		Team start = b.registerNewTeam("start");
		start.addEntry(ChatColor.translateAlternateColorCodes('&', "&1"));
		start.setPrefix(ChatColor.translateAlternateColorCodes('&', "&fTo start your "));
		start.setSuffix(ChatColor.translateAlternateColorCodes('&', "&fjourney you "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&1")).setScore(8);
		Team create = b.registerNewTeam("create");
		create.addEntry(ChatColor.translateAlternateColorCodes('&', "&2"));
		create.setPrefix(ChatColor.translateAlternateColorCodes('&', "&fmust create or"));
		create.setSuffix(ChatColor.translateAlternateColorCodes('&', "&fjoin a guild!"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&2")).setScore(7);
		Team spacer1 = b.registerNewTeam("spacer1");
		spacer1.addEntry(ChatColor.translateAlternateColorCodes('&', "&r"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r")).setScore(6);
		Team type = b.registerNewTeam("type");
		type.addEntry(ChatColor.translateAlternateColorCodes('&', "&3"));
		type.setPrefix(ChatColor.translateAlternateColorCodes('&', "&fType &d/guild "));
		type.setSuffix(ChatColor.translateAlternateColorCodes('&', "&dcreate <name> "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&3")).setScore(5);
		Team typeJoin = b.registerNewTeam("typeJoin");
		typeJoin.addEntry(ChatColor.translateAlternateColorCodes('&', "&4"));
		typeJoin.setPrefix(ChatColor.translateAlternateColorCodes('&', "&for &d/guild "));
		typeJoin.setSuffix(ChatColor.translateAlternateColorCodes('&', "&djoin <name>"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&4")).setScore(4);
		Team toJoin = b.registerNewTeam("toJoin");
		toJoin.addEntry(ChatColor.translateAlternateColorCodes('&', "&5"));
		toJoin.setPrefix(ChatColor.translateAlternateColorCodes('&', "&fto create/join"));
		toJoin.setSuffix(ChatColor.translateAlternateColorCodes('&', "&f a new guild!"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&5")).setScore(3);
		Team spacer2 = b.registerNewTeam("spacer2");
		spacer2.addEntry(ChatColor.translateAlternateColorCodes('&', "&r&r"));
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
