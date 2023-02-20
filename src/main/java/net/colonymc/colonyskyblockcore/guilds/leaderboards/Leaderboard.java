package net.colonymc.colonyskyblockcore.guilds.leaderboards;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Location;

import net.colonymc.colonyspigotlib.lib.holograms.PublicHologram;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.war.EndedWar;

public class Leaderboard {

	final PublicHologram holo;
	final Location loc;
	final LeaderboardSort sortBy;
	public static final ArrayList<Leaderboard> leaderboards = new ArrayList<>();
	
	public Leaderboard(Location loc, LeaderboardSort sortBy) {
		this.loc = loc;
		this.sortBy = sortBy;
		this.holo = new PublicHologram("&5&lTop 10 guilds\n&fSorted by: &d" + sortBy.name, loc.clone());
		holo.show();
		addLines();
		leaderboards.add(this);
	}
	
	public void addLines() {
		ArrayList<Guild> topGuilds = new ArrayList<>();
		try {
			ResultSet rs = null;
			if(sortBy == LeaderboardSort.POWER_LEVEL) {
				rs = Database.getResultSet("SELECT id FROM GuildInfo ORDER BY guildLevel DESC LIMIT 10;");
			}
			else if(sortBy == LeaderboardSort.GUILD_BALANCE) {
				rs = Database.getResultSet("SELECT id FROM GuildInfo ORDER BY bankSilver DESC LIMIT 10;");
			}
			else if(sortBy == LeaderboardSort.WARS_WON) {
				rs = Database.getResultSet("SELECT winnerId, COUNT(*) AS topWinners FROM GuildWars WHERE type='NORMAL' GROUP BY winnerId ORDER BY topWinners DESC LIMIT 10");
			}
			while(rs.next()) {
				int id = rs.getInt(1);
				if(Guild.getById(id) != null) {
					topGuilds.add(Guild.getById(id));
				}			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < 10; i++) {
			String amount = "";
			if(i < topGuilds.size()) {
				if(sortBy == LeaderboardSort.POWER_LEVEL) {
					amount = Guild.balance(topGuilds.get(i).getLevel()).replaceAll("$", "");
				}
				else if(sortBy == LeaderboardSort.GUILD_BALANCE) {
					amount = Guild.balance(topGuilds.get(i).getBalance());
				}
				else if(sortBy == LeaderboardSort.WARS_WON) {
					int warsWon = 0;
					for(EndedWar e : topGuilds.get(i).getEndedWars()) {
						if(e.getWinner() != null && e.getWinner().equals(topGuilds.get(i))) {
							warsWon++;
						}
					}
					amount = Guild.balance(warsWon).replaceAll("$", "");
				}
			}
			holo.addLine("&d#" + (i + 1) + " " + (i < topGuilds.size() ? "&d" + topGuilds.get(i).getName() + " &f- &d" + amount : "&7None"));
		}
	}
	
	public void destroy() {
		holo.destroy();
	}

}
