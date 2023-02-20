package net.colonymc.colonyskyblockcore.shop;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;

public class ShopCategoriesMenu implements InventoryHolder, Listener {
	
	Inventory inv;
	Player p;
	final HashMap<Integer, ShopCategory> categories = new HashMap<>();
	
	public ShopCategoriesMenu(Player p) {
		this.p = p;
		this.inv = Bukkit.createInventory(this, 36, "Shop");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1);
	}
	
	public ShopCategoriesMenu() {
		
	}
	
	public void fillInventory() {
		for(int i = 0; i < 14; i++) {
			int line = (int) (Math.ceil((i + 1)/7.0) - 1);
			int row = i - line * 7 + 1;
			int slot = 9 + line * 9 + row;
			if(i < ShopCategory.categories.size()) {
				ShopCategory cat = ShopCategory.categories.get(i);
				categories.put(slot, cat);
				inv.setItem(slot, new ItemStackBuilder(cat.getDisplayMaterial())
						.name(ChatColor.translateAlternateColorCodes('&', cat.getDisplayName()))
						.lore(ChatColor.translateAlternateColorCodes('&', cat.getDisplayLore()))
						.build());
			}
			else {
				inv.setItem(slot, new ItemStack(Material.AIR));
			}
		}
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof ShopCategoriesMenu) {
			e.setCancelled(true);
			ShopCategoriesMenu menu = (ShopCategoriesMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(menu.categories.containsKey(e.getSlot())) {
					ShopCategory cat = menu.categories.get(e.getSlot());
					menu.p.closeInventory();
					new ShopCategoryMenu(menu.p, cat);
				}
			}
		}
	}

}
