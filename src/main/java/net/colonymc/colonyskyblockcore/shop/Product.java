package net.colonymc.colonyskyblockcore.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Product {
	
	final Material mat;
	final short data;
	final double buyPriceOfOne;
	final double sellPriceOfOne;
	final int defaultAmount;
	final boolean sellable;
	final boolean buyable;
	final ShopCategory category;
	
	public Product(Material mat, short data, double buyPrice, double sellPrice, int defaultAmount, ShopCategory shop) {
		this.mat = mat;
		this.data = data;
		this.buyPriceOfOne = buyPrice;
		this.sellPriceOfOne = sellPrice;
		this.defaultAmount = defaultAmount;
		this.category = shop;
		sellable = (sellPriceOfOne > 0);
		buyable = (buyPriceOfOne > 0);
		shop.addProduct(this);
	}
	
	public ItemStack getNewItem() {
		ItemStack i = new ItemStack(mat);
		i.setDurability(data);
		return i;
	}
	
	public Material getMaterial() {
		return mat;
	}
	
	public short getData() {
		return data;
	}
	
	public double getBuyPrice() {
		return buyPriceOfOne;
	}
	
	public double getSellPrice() {
		return sellPriceOfOne;
	}
	
	public int getDefaultAmount() {
		return defaultAmount;
	}
	
	public boolean isSellable() {
		return sellable;
	}
	
	public boolean isBuyable() {
		return buyable;
	}
	
	public ShopCategory getCategory() {
		return category;
	}

}
