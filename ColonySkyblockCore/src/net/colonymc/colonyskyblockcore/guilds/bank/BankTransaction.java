package net.colonymc.colonyskyblockcore.guilds.bank;

import net.colonymc.colonyskyblockcore.guilds.GuildPlayer;

public class BankTransaction {
	
	int amount; 
	Currency type;
	long timestamp;
	GuildPlayer p;
	
	public BankTransaction(GuildPlayer p, int amount, Currency type, long timestamp) {
		this.p = p;
		this.amount = amount;
		this.type = type;
		this.timestamp = timestamp;
	}

	public Currency getCurrency() {
		return type;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public GuildPlayer getPlayer() {
		return p;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

}
