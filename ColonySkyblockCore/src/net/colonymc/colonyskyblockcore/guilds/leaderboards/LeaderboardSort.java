package net.colonymc.colonyskyblockcore.guilds.leaderboards;

public enum LeaderboardSort {
	POWER_LEVEL("Power level"),
	GUILD_BALANCE("Guild balance"),
	WARS_WON("Wars won");
	
	public String name;
	
	LeaderboardSort(String name) {
		this.name = name;
	}
}
