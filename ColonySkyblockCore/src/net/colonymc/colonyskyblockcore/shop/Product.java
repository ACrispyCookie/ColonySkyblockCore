package net.colonymc.colonyskyblockcore.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Product {
	
	Material mat;
	short data;
	double buyPriceOfOne;
	double sellPriceOfOne;
	int defaultAmount;
	boolean sellable;
	boolean buyable;
	ShopCategory category;
	
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
