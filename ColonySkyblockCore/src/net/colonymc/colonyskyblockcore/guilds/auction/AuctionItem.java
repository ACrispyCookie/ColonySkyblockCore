package net.colonymc.colonyskyblockcore.guilds.auction;

import java.util.ArrayList;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import net.colonymc.api.primitive.GetNames;
import net.colonymc.api.primitive.RomanNumber;

public class AuctionItem {
	
	ItemStack item;
	String displayName;
	String lore;
	
	public AuctionItem(ItemStack i) {
		this.item = i;
		if(i.hasItemMeta()) {
			if(i.getItemMeta().getLore() != null) {
				this.lore = "";
				ArrayList<String> lines = new ArrayList<String>();
				lines.addAll(i.getItemMeta().getLore());
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
				ArrayList<Enchantment> enchants = new ArrayList<Enchantment>();
				enchants.addAll(i.getEnchantments().keySet());
				for(int in = 0; in < i.getEnchantments().size(); in++) {
					Enchantment e = enchants.get(in);
					if(in == 0) {
						this.lore = lore + ("&7" + GetNames.enchantmentName(e, false) + " " + RomanNumber.toRoman(i.getEnchantments().get(e)));
					}
					else {
						this.lore = lore + "\n" + ("&7" + GetNames.enchantmentName(e, false) + " " + RomanNumber.toRoman(i.getEnchantments().get(e)));
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
				this.displayName = GetNames.itemName(i, false);
			}
		}
		else {
			this.displayName = GetNames.itemName(i, false);
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
