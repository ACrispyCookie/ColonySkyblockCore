package net.colonymc.colonyskyblockcore.guilds.test;

import org.bukkit.inventory.ItemStack;

public abstract class AuctionButton {
	
	final int slot;
	final ItemStack item;
	public abstract void action(ItemStack i);
	
	public AuctionButton(int slot, ItemStack i) {
		this.slot = slot;
		this.item = i;
	}

}
