package net.colonymc.colonyskyblockcore.guilds.war;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyspigotapi.api.itemstack.ItemStackBuilder;
import net.colonymc.colonyspigotapi.api.player.PlayerInventory;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;

public class WarLootMenu implements Listener, InventoryHolder {
	
	Inventory inv;
	Player p;
	EndedWar e;
	int totalPages;
	int page = 0;
	BukkitTask update;
	final HashMap<Integer, ItemStack> items = new HashMap<>();
	
	public WarLootMenu(Player p, EndedWar e) {
		this.p = p;
		this.e = e;
		this.inv = Bukkit.createInventory(this, 54, "Looting the war against " + (e.getAnotherGuild() == Guild.getByPlayer(p) ? e.getOneGuild().getName() : e.getAnotherGuild().getName()));
		fillInventory();
		startUpdating();
		openInventory();
	}
	
	public WarLootMenu() {
		
	}
	
	public void changePage(int amount) {
		if(e.getLoot(Guild.getByPlayer(p)).isEmpty()) {
			p.closeInventory();
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have fully looted this war!"));
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 2, 1);
		}
		page = page + amount;
		totalPages = (int) Math.ceil((double) Guild.getByPlayer(p).getEndedWars().size() / 45);
		items.clear();
		for(int i = 0; i < 45; i++) {
			int index = page * 45 + i;
			if(index < e.getLoot(Guild.getByPlayer(p)).size()) {
				items.put(i, e.getLoot(Guild.getByPlayer(p)).get(index));
				ItemStack item = e.getLoot(Guild.getByPlayer(p)).get(index).clone();
				ItemMeta meta = item.getItemMeta();
				if(meta.getLore() != null) {
					List<String> lore = item.getItemMeta().getLore();
					lore.add("\n \n" + ChatColor.translateAlternateColorCodes('&', "&dClick to claim this item!"));
					meta.setLore(lore);
				}
				else {
					ArrayList<String> lore = new ArrayList<>();
					lore.add("\n \n" + ChatColor.translateAlternateColorCodes('&', "&dClick to claim this item!"));
					meta.setLore(lore);
				}
				inv.setItem(i, item);
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
		inv.setItem(49, new ItemStackBuilder(Material.NETHER_STAR).name("&dClaim all items").lore("\n&fClick to claim all\n&fthe items of this war!").build());
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
		if(e.getInventory().getHolder() instanceof WarLootMenu) {
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				WarLootMenu menu = (WarLootMenu) e.getInventory().getHolder();
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
				else if(e.getSlot() == 49) {
					int size = menu.e.getLoot(Guild.getByPlayer(p)).size();
					ArrayList<ItemStack> items = new ArrayList<>();
					for(int i = 0; i < size; i++) {
						ItemStack item = menu.e.getLoot(Guild.getByPlayer(p)).get(0);
						menu.e.removeItem(item, Guild.getByPlayer(menu.p));
						items.add(item);
					}
					PlayerInventory.addItems(items, menu.p);
				}
				else if(menu.items.containsKey(e.getSlot())) {
					ItemStack item = menu.items.get(e.getSlot());
					menu.e.removeItem(item, Guild.getByPlayer(menu.p));
					menu.items.remove(e.getSlot());
					menu.p.playSound(menu.p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
					PlayerInventory.addItem(item, menu.p, item.getAmount());
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof WarLootMenu) {
			WarLootMenu menu = (WarLootMenu) e.getInventory().getHolder();
			menu.update.cancel();
		}
	}
}
