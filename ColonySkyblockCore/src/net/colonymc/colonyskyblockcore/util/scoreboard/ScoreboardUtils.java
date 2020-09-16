package net.colonymc.colonyskyblockcore.util.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import me.clip.placeholderapi.PlaceholderAPI;
import net.colonymc.api.player.ColonyPlayer;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.war.TeamDeathmatch;

public class ScoreboardUtils {
	
	public static void scoreboardUpdate(Player p) {	
		p.setScoreboard(new NormalBoard(p).sb);
	}
	
	public static void linesUpdate(Player p) {
		org.bukkit.scoreboard.Scoreboard sb = p.getScoreboard();
		if(Guild.getByPlayer(p) != null) {
			if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
				if(sb.getObjective("GuildWar") != null) {
					if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).hasEnded()) {
						if(sb.getTeam("winner") == null) {
							Objective o = sb.getObjective("GuildWar");
							o.getScore(ChatColor.translateAlternateColorCodes('&', "&r&r&r")).setScore(14);
							o.getScore(ChatColor.translateAlternateColorCodes('&', "&0")).setScore(13);
							o.getScore(ChatColor.translateAlternateColorCodes('&', "&1")).setScore(12);
							o.getScore(ChatColor.translateAlternateColorCodes('&', "&r")).setScore(11);
							o.getScore(ChatColor.translateAlternateColorCodes('&', "&2")).setScore(10);
							o.getScore(ChatColor.translateAlternateColorCodes('&', "&3")).setScore(9);
							Team spacer4 = sb.registerNewTeam("spacer4");
							spacer4.addEntry(ChatColor.translateAlternateColorCodes('&', "&r&a"));
							o.getScore(ChatColor.translateAlternateColorCodes('&', "&r&a")).setScore(8);
							Team winner = sb.registerNewTeam("winner");
							winner.addEntry(ChatColor.translateAlternateColorCodes('&', "&8"));
							o.getScore(ChatColor.translateAlternateColorCodes('&', "&8")).setScore(7);
							winner.setPrefix(ChatColor.translateAlternateColorCodes('&', "&fWinner:"));
							Team winnerName = sb.registerNewTeam("winnerName");
							winnerName.addEntry(ChatColor.translateAlternateColorCodes('&', "&9"));
							o.getScore(ChatColor.translateAlternateColorCodes('&', "&9")).setScore(6);
							winnerName.setPrefix(ChatColor.translateAlternateColorCodes('&', " &d- "));
						}
						Guild enemy = TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getWar().getRequested().equals(Guild.getByPlayer(p)) 
								? TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getWar().getRequester() : TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getWar().getRequested();
						sb.getTeam("guildName").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + enemy.getName()));
						sb.getTeam("warType").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getWar().getType().name));
						sb.getTeam("winnerName").setSuffix(ChatColor.translateAlternateColorCodes('&', TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getWinner() == null ? "&7None" : "&d" + TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getWinner().getName()));
						sb.getTeam("timeRemaining").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getTimeLeft()));
						sb.getTeam("enemiesLeft").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).playerLeft(enemy)));
						sb.getTeam("membersLeft").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).playerLeft(Guild.getByPlayer(p))));
					}
					else {
						Guild enemy = TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getWar().getRequested().equals(Guild.getByPlayer(p)) 
								? TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getWar().getRequester() : TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getWar().getRequested();
						sb.getTeam("guildName").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + enemy.getName()));
						sb.getTeam("warType").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getWar().getType().name));
						sb.getTeam("timeRemaining").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getTimeLeft()));
						sb.getTeam("enemiesLeft").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).playerLeft(enemy)));
						sb.getTeam("membersLeft").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).playerLeft(Guild.getByPlayer(p))));
					}
				}
				else {
					p.setScoreboard(new WarBoard().sb);
				}
			}
			else {
				if(sb.getObjective("ColonyMC") != null) {
					sb.getTeam("rank").setSuffix(ChatColor.LIGHT_PURPLE + PlaceholderAPI.setPlaceholders(p, "%vault_rank%"));
					sb.getTeam("balance").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + Guild.formattedBalance(Guild.getByPlayer(p).getGuildPlayer(p).getBalance())));
					sb.getTeam("votes").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + ColonyPlayer.getByPlayer(p).getVotes()));
					sb.getTeam("owner").setSuffix(Guild.getByPlayer(p).getOwner().getPlayer().getName());
					sb.getTeam("level").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + Guild.getByPlayer(p).getLevel()));
					sb.getTeam("members").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + Guild.getByPlayer(p).getOnlineMembers().size()));
					sb.getTeam("online").setSuffix(ChatColor.translateAlternateColorCodes('&', "&d" + Bukkit.getOnlinePlayers().size()));
				}
				else {
					p.setScoreboard(new NormalBoard(p).sb);
				}
			}
		}
		else if(sb.getObjective("Starting") == null){
			p.setScoreboard(new StartingBoard().getScoreboard());
		}
	}
}
