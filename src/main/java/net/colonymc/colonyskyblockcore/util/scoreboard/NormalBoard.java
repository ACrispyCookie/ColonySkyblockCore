package net.colonymc.colonyskyblockcore.util.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class NormalBoard {
	
	Scoreboard sb;
	final Player p;
	
	public NormalBoard(Player p) {
		this.p = p;
		setupScoreboard();
	}

	private void setupScoreboard() {
		ScoreboardManager m = Bukkit.getScoreboardManager();
		org.bukkit.scoreboard.Scoreboard b = m.getNewScoreboard();
		Objective o = b.registerNewObjective("ColonyMC", "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d&lCOLONY&F&LMC"));
		
		Team header = b.registerNewTeam("header");
		header.addEntry(ChatColor.translateAlternateColorCodes('&', "&7Skyblock Dimension"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&7Skyblock Dimension")).setScore(13);
		
		Team spacer2 = b.registerNewTeam("spacer2");
		spacer2.addEntry(ChatColor.translateAlternateColorCodes('&', "&r&r&r"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r&r&r")).setScore(12);
		
		Team player = b.registerNewTeam("player");
		player.addEntry(ChatColor.translateAlternateColorCodes('&', "&0&d"));
		player.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5┏━ "));
		player.setSuffix(ChatColor.translateAlternateColorCodes('&', p.getName()));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&0&d")).setScore(11);
		
		Team rank = b.registerNewTeam("rank");
		rank.addEntry(ChatColor.translateAlternateColorCodes('&', "&d"));
		rank.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5┃ &7Rank » "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&d")).setScore(10);
		
		Team balance = b.registerNewTeam("balance");
		balance.addEntry(ChatColor.translateAlternateColorCodes('&', "&2"));
		balance.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5┃ &7Balance » "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&2")).setScore(9);
		
		Team votes = b.registerNewTeam("votes");
		votes.addEntry(ChatColor.translateAlternateColorCodes('&', "&3"));
		votes.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5┃ &7Votes » "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&3")).setScore(8);

		Team island = b.registerNewTeam("island");
		island.addEntry(ChatColor.translateAlternateColorCodes('&', "&5┣━ &d&lIsland"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&5┣━ &d&lIsland")).setScore(7);
		
		Team owner = b.registerNewTeam("owner");
		owner.addEntry(ChatColor.translateAlternateColorCodes('&', "&d"));
		owner.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5┃ &7Owner » "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&d")).setScore(6);
		
		Team level = b.registerNewTeam("level");
		level.addEntry(ChatColor.translateAlternateColorCodes('&', "&5"));
		level.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5┃ &7Level » "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&5")).setScore(5);
		
		Team members = b.registerNewTeam("members");
		members.addEntry(ChatColor.translateAlternateColorCodes('&', "&6"));
		members.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5┃ &7Members » "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&6")).setScore(4);
		
		Team silver = b.registerNewTeam("online");
		silver.addEntry(ChatColor.translateAlternateColorCodes('&', "&7"));
		silver.setPrefix(ChatColor.translateAlternateColorCodes('&', "&5┗━ &dOnline: "));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&7")).setScore(3);

		Team spacer = b.registerNewTeam("spacer");
		spacer.addEntry(ChatColor.translateAlternateColorCodes('&', "&r&r"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&r&r")).setScore(2);
		
		Team scrollinfo = b.registerNewTeam("scrollinfo");
		scrollinfo.addEntry(ChatColor.translateAlternateColorCodes('&', "&d&nstore.colonymc.net"));
		o.getScore(ChatColor.translateAlternateColorCodes('&', "&d&nstore.colonymc.net")).setScore(1);
		
		this.sb = b;
	}
	
	public Scoreboard getScoreboard() {
		return sb;
	}

}
