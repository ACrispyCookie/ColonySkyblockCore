package net.colonymc.colonyskyblockcore.guilds.inventories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.api.itemstacks.SkullItemBuilder;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;

public class GuildSelectLootMenu implements Listener, InventoryHolder {
	
	Inventory inv;
	Player p;
	int totalPages;
	int page = 0;
	BukkitTask update;
	HashMap<Integer, String> items = new HashMap<Integer, String>();
	
	public GuildSelectLootMenu(Player p) {
		this.p = p;
		this.inv = Bukkit.createInventory(this, 54, "Select a player to loot...");
		fillInventory();
		startUpdating();
		openInventory();
	}
	
	public GuildSelectLootMenu() {
		
	}
	
	public void changePage(int amount) {
		page = page + amount;
		totalPages = (int) Math.ceil((double) Guild.getByPlayer(p).getUnclaimedItems().size() / 45);
		ArrayList<String> pUuids = new ArrayList<String>();
		pUuids.addAll(Guild.getByPlayer(p).getUnclaimedItems().keySet());
		items.clear();
		for(int i = 0; i < 45; i++) {
			int index = page * 45 + i;
			if(index < pUuids.size()) {
				items.put(i, pUuids.get(index));
				int itemsLeft = 0;
				for(ItemStack item : Guild.getByPlayer(p).getUnclaimedItems().get(pUuids.get(index))) {
					itemsLeft = itemsLeft + item.getAmount();
				}
				inv.setItem(i, new SkullItemBuilder().playerUuid(UUID.fromString(pUuids.get(index)))
						.name("&d" + Database.getName(pUuids.get(index)))
						.lore("&fItems left: &d" + itemsLeft + "\n \n&dClick here to loot the member!")
						.build());
			}
			else {
				inv.setItem(i, new ItemStack(Material.AIR));
			}
		}
		if(page > 0) {
			inv.setItem(45, new ItemStackBuilder(Material.ARROW).name("&dPrevious Page").build());
		}
		else {
			inv.setItem(45, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
		if(totalPages > 1 && page < totalPages - 1) {
			inv.setItem(53, new ItemStackBuilder(Material.ARROW).name("&dNext Page").build());
		}
		else {
			inv.setItem(53, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
		p.updateInventory();
	}
	
	private void fillInventory() {
		for(int i = 45; i < 54; i++) {
			inv.setItem(i, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
	}
	
	private void startUpdating() {
		update = new BukkitRunnable() {
			@Override
			public void run() {
				changePage(0);
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	public void openInventory() {
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof GuildSelectLootMenu) {
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				GuildSelectLootMenu menu = (GuildSelectLootMenu) e.getInventory().getHolder();
				e.setCancelled(true);
				if(e.getSlot() == 45) {
					if(menu.page > 0) {
						menu.changePage(-1);
					}
				}
				else if(e.getSlot() == 53) {
					if(menu.page < menu.totalPages - 1) {
						menu.changePage(1);
					}
				}
				else if(menu.items.containsKey(e.getSlot())) {
					menu.p.closeInventory();
					new GuildLootMenu(menu.p, menu.items.get(e.getSlot()));
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof GuildSelectLootMenu) {
			GuildSelectLootMenu menu = (GuildSelectLootMenu) e.getInventory().getHolder();
			menu.update.cancel();
		}
	}
}
