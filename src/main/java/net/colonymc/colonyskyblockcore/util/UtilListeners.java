package net.colonymc.colonyskyblockcore.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class UtilListeners implements Listener {
	
	@EventHandler
	public void onCraft(PrepareItemCraftEvent e) {
		if(!e.getInventory().getViewers().get(0).hasPermission("*")) {
			if(e.getRecipe().getResult().getType() == Material.BOOK_AND_QUILL) {
				e.getInventory().setResult(null);
			}
		}
	}
	
	@EventHandler
	public void onBookEdit(PlayerEditBookEvent e) {
		if(!e.getPlayer().hasPermission("*")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cBook editing is currently disabled!"));
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.NOTE_BASS, 2, 1);
		}
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		if(e.getItem().getItemStack().getType() == Material.BOOK_AND_QUILL && !e.getPlayer().hasPermission("*")) {
			ItemStack i = e.getItem().getItemStack();
			i.setType(Material.BOOK);
			e.getItem().setItemStack(i);
		}
	}
	
	@EventHandler
	public void onPickup(PlayerDropItemEvent e) {
		if(e.getItemDrop().getItemStack().getType() == Material.BOOK_AND_QUILL && !e.getPlayer().hasPermission("*")) {
			ItemStack i = e.getItemDrop().getItemStack();
			i.setType(Material.BOOK);
			e.getItemDrop().setItemStack(i);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPickup(InventoryClickEvent e) {
		if(e.getClickedInventory() == null) {
			Player p = (Player) e.getWhoClicked();
			if(!p.hasPermission("*")) {
				if(e.getCursor().getType() == Material.BOOK_AND_QUILL) {
					ItemStack i = e.getCursor();
					i.setType(Material.BOOK);
					e.setCursor(i);
				}
			}
		}
		else {
			if(e.getClickedInventory().getType() == InventoryType.PLAYER) {
				Player p = (Player) e.getWhoClicked();
				if(!p.hasPermission("*")) {
					if(e.getCursor().getType() == Material.BOOK_AND_QUILL) {
						ItemStack i = e.getCursor();
						i.setType(Material.BOOK);
						e.setCursor(i);
					}
					if(e.getCurrentItem().getType() == Material.BOOK_AND_QUILL) {
						ItemStack i = e.getCurrentItem();
						i.setType(Material.BOOK);
						e.setCurrentItem(i);
					}
				}
			}
		}
	}
	
}
