package net.colonymc.colonyskyblockcore.guilds.test;

import org.bukkit.inventory.ItemStack;

public abstract class AuctionButton {
	
	int slot;
	ItemStack item;
	public abstract void action(ItemStack i);
	
	public AuctionButton(int slot, ItemStack i) {
		this.slot = slot;
		this.item = i;
	}

}
