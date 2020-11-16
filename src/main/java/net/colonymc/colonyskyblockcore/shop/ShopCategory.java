package net.colonymc.colonyskyblockcore.shop;

import java.util.ArrayList;

import org.bukkit.Material;

public class ShopCategory {
	
	final Material displayMat;
	final String name;
	final String displayName;
	final String lore;
	final ArrayList<Product> products = new ArrayList<>();
	final ArrayList<CommandProduct> cmdProducts = new ArrayList<>();
	public static final ArrayList<ShopCategory> categories = new ArrayList<>();
	
	public ShopCategory(Material displayMat, String name, String displayName, String lore) {
		this.displayMat = displayMat;
		this.name = name;
		this.displayName = displayName;
		this.lore = lore;
		categories.add(this);
	}
	
	public Material getDisplayMaterial() {
		return displayMat;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getDisplayLore() {
		return lore;
	}
	
	public ArrayList<Object> getProducts() {
		ArrayList<Object> prod = new ArrayList<>();
		prod.addAll(products);
		prod.addAll(cmdProducts);
		return prod;
	}
	
	public void addProduct(Product prod) {
		products.add(prod);
	}
	
	public void addProduct(CommandProduct prod) {
		cmdProducts.add(prod);
	}
	
	public static ShopCategory getByName(String s) {
		for(ShopCategory cat : categories) {
			if(s.equals(cat.name)) {
				return cat;
			}
		}
		return null;
	}

}
