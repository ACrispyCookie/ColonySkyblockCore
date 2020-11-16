package net.colonymc.colonyskyblockcore.shop;

import org.bukkit.inventory.ItemStack;

public class CommandProduct {	
	
	final ItemStack display;
	final String command;
	final double buyPriceOfOne;
	final int defaultAmount;
	final boolean buyable;
	final ShopCategory category;
	
	public CommandProduct(ItemStack display, String command, double buyPrice, int defaultAmount, ShopCategory shop) {
		this.display = display;
		this.command = command;
		this.buyPriceOfOne = buyPrice;
		this.defaultAmount = defaultAmount;
		this.category = shop;
		buyable = (buyPriceOfOne > 0);
		shop.addProduct(this);
	}
	
	public ItemStack getDisplayItem() {
		return display.clone();
	}
	
	public String getCommand() {
		return command;
	}
	
	public double getBuyPrice() {
		return buyPriceOfOne;
	}
	
	public int getDefaultAmount() {
		return defaultAmount;
	}
	
	public boolean isBuyable() {
		return buyable;
	}
	
	public ShopCategory getCategory() {
		return category;
	}
	
}
