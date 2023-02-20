package net.colonymc.colonyskyblockcore.guilds.auction;

import java.util.ArrayList;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyspigotlib.lib.itemstack.MaterialName;
import net.colonymc.colonyspigotlib.lib.primitive.RomanNumber;

public class AuctionItem {
	
	final ItemStack item;
	final String displayName;
	String lore;
	
	public AuctionItem(ItemStack i) {
		this.item = i;
		if(i.hasItemMeta()) {
			if(i.getItemMeta().getLore() != null) {
				this.lore = "";
				ArrayList<String> lines = new ArrayList<>(i.getItemMeta().getLore());
				for(int in = 0; in < i.getItemMeta().getLore().size(); in++) {
					String s = lines.get(in);
					if(in == 0) {
						this.lore = lore + s;
					}
					else {
						this.lore = lore + "\n" + s;
					}
				}
				this.lore = lore + "\n&f&m-----------------------------&r\n ";
			}
			else if(!i.getEnchantments().isEmpty()) {
				this.lore = "";
				ArrayList<Enchantment> enchants = new ArrayList<>(i.getEnchantments().keySet());
				for(int in = 0; in < i.getEnchantments().size(); in++) {
					Enchantment e = enchants.get(in);
					if(in == 0) {
						this.lore = lore + ("&7" + MaterialName.enchantmentName(e, false) + " " + RomanNumber.toRoman(i.getEnchantments().get(e)));
					}
					else {
						this.lore = lore + "\n" + ("&7" + MaterialName.enchantmentName(e, false) + " " + RomanNumber.toRoman(i.getEnchantments().get(e)));
					}
				}
				this.lore = lore + "\n&f&m-----------------------------&r\n ";
			}
			else {
				this.lore = "";
			}
			if(i.getItemMeta().getDisplayName() != null) {
				this.displayName = i.getItemMeta().getDisplayName();
			}
			else {
				this.displayName = MaterialName.itemName(i, false);
			}
		}
		else {
			this.displayName = MaterialName.itemName(i, false);
			this.lore = "";
		}
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public String getName() {
		return displayName;
	}
	
	public String getLore() {
		return lore;
	}

}
