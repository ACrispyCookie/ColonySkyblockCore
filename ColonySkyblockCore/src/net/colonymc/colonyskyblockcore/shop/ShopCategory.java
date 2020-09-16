package net.colonymc.colonyskyblockcore.shop;

import java.util.ArrayList;

import org.bukkit.Material;

public class ShopCategory {
	
	Material displayMat;
	String name;
	String displayName;
	String lore;
	ArrayList<Product> products = new ArrayList<Product>();
	ArrayList<CommandProduct> cmdProducts = new ArrayList<CommandProduct>();
	public static ArrayList<ShopCategory> categories = new ArrayList<ShopCategory>();
	
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
		ArrayList<Object> prod = new ArrayList<Object>();
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
