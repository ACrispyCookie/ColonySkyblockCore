package net.colonymc.colonyskyblockcore.guilds.trade;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyskyblockcore.guilds.GuildPlayer;

public class Trader {
	
	Trade t;
	GuildPlayer p;
	int silver = 0;
	int dust = 0;
	boolean ready = false;
	ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	
	public Trader(GuildPlayer oneTrader, Trade t) {
		this.t = t;
		this.p = oneTrader;
	}
	
	public void addItem(ItemStack i) {
		this.items.add(i);
	}
	
	public void removeItem(ItemStack i) {
		if(items.contains(i)) {
			this.items.remove(items.indexOf(i));
		}
	}
	
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	public void addSilver(int amount) {
		silver = silver + amount;
	}
	
	public void addDust(int amount) {
		dust = dust + amount;
	}
	
	public GuildPlayer getPlayer() {
		return p;
	}
	
	public ArrayList<ItemStack> getItems() {
		return items;
	}
	
	public int getSilver() {
		return silver;
	}
	
	public int getDust() {
		return dust;
	}

	public boolean isReady() {
		return ready;
	}
	
	public Trade getTrade() {
		return t;
	}

}
