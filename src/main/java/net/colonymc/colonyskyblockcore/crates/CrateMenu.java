package net.colonymc.colonyskyblockcore.crates;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CrateMenu implements Listener, InventoryHolder {

	Inventory inv;
	Player p;
	
	public CrateMenu(Player p) {
		this.p = p;
		this.inv = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', "&d&lCRATES"));
	}
	
	public CrateMenu() {
		
	}
	
	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof CrateMenu) {
			if(e.getClickedInventory() != null) {
				e.setCancelled(true);
			}
		}
	}

}
