package net.colonymc.colonyskyblockcore.kits;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyspigotapi.api.itemstack.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;

public class PreviewMenu implements Listener, InventoryHolder {

	Player p;
	Inventory inv;
	Kit kit;
	
	public PreviewMenu(Player p, Kit kit) {
		this.p = p;
		this.kit = kit;
		this.inv = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', "&8Previewing kit &d" + kit.getName() + "&8..."));
		fillInventory();
		openInventory();
	}
	
	public PreviewMenu() {
		
	}
	
	private void fillInventory() {
		for(int i = 45; i < 54; i++) {
			inv.setItem(i, new ItemStackBuilder(Material.STAINED_GLASS_PANE).durability((short) 2).name(" ").build());
		}
		inv.setItem(49, new ItemStackBuilder(Material.ARROW).name("&dGo Back").build());
		for(int i = 0; i < 45; i++) {
			if(i < kit.getItems().size()) {
				inv.setItem(i, kit.getItems().get(i));
			}
			else if(i - kit.getItems().size() < kit.getCommands().values().size()) {
				inv.setItem(i, kit.getCommands().values().iterator().next());
			}
		}
	}
	
	public void openInventory() {
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1);
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof PreviewMenu) {
			e.setCancelled(true);
			if(e.getSlot() == 49) {
				e.getWhoClicked().closeInventory();
				new KitsMenu((Player) e.getWhoClicked());
			}
		}
	}
	
}
