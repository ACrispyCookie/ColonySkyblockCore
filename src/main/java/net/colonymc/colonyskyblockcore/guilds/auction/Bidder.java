package net.colonymc.colonyskyblockcore.guilds.auction;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.GuildPlayer;

public class Bidder {
	
	final GuildPlayer gp;
	final int amount;
	final long timeBidded;
	boolean hasClaimed;
	
	public Bidder(Player p, int amount, long timeBidded, boolean hasClaimed) {
		this.gp = Guild.getByPlayer(p).getGuildPlayer(p);
		this.amount = amount;
		this.timeBidded = timeBidded;
		this.hasClaimed = false;
	}
	
	public Bidder(OfflinePlayer p, int amount, long timeBidded, boolean hasClaimed) {
		this.gp = Guild.getByPlayer(p).getGuildPlayer(p);
		this.amount = amount;
		this.timeBidded = timeBidded;
		this.hasClaimed = false;
	}
	
	public GuildPlayer getPlayer() {
		return gp;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public long getTimeBidded() {
		return timeBidded;
	}
	
	public boolean hasClaimed() {
		return !hasClaimed;
	}

}
