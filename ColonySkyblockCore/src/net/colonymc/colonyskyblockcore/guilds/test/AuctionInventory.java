package net.colonymc.colonyskyblockcore.guilds.test;

import java.util.HashMap;

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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionMenuUtils;

public abstract class AuctionInventory implements Listener, InventoryHolder{
	
	Inventory inv;
	Player viewer;
	BukkitRunnable updateRunnable;
	BukkitTask update;
	HashMap<Integer, AuctionButton> buttons = new HashMap<Integer, AuctionButton>();
	int page = 0;
	int totalPages;
	public abstract void changePage(int amount);
	public abstract void addButtons();
	public abstract void onClose();
	
	public AuctionInventory(Player p, int size, String name) {
		this.viewer = p;
		this.inv = Bukkit.createInventory(this, size, name);
		fill();
		addButtons();
		updateRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(Guild.getByPlayer(viewer) != null) {
					changePage(0);
				}
				else {
					cancel();
					viewer.closeInventory();
					viewer.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the auction house!"));
					viewer.playSound(viewer.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
			}
		};
		openInventory();
	}
	
	public AuctionInventory() {
	}
	
	public void openInventory() {
		new BukkitRunnable() {
			@Override
			public void run() {
				viewer.openInventory(inv);
				viewer.updateInventory();
				update = updateRunnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}
	
	public void closeInventory() {
		update.cancel();
		viewer.closeInventory();
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	public void addButton(AuctionButton b) {
		this.buttons.put(b.slot, b);
		if(b.slot != -1 && b.item != null) {
			inv.setItem(b.slot, b.item);
		}
	}
	
	public void removeButton(int slot) {
		this.buttons.remove(slot);
		inv.setItem(slot, new ItemStack(Material.AIR));
	}
	
	public void fill() {
		AuctionMenuUtils.fillGlasses(this);
	}

	@EventHandler 
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionInventory) {
			e.setCancelled(true);
			AuctionInventory auc = (AuctionInventory) e.getInventory().getHolder();
			if(e.getClickedInventory() != null) {
				if(e.getClickedInventory().getType() != InventoryType.PLAYER) {
					AuctionButton b = null;
					if(auc.buttons.containsKey(e.getSlot())) {
						b = auc.buttons.get(e.getSlot());
					}
					if(b != null) {
						b.action(e.getCurrentItem());
					}
				}
				else {
					AuctionButton b = null;
					if(auc.buttons.containsKey(-1)) {
						b = auc.buttons.get(-1);
					}
					if(b != null) {
						b.action(e.getCurrentItem());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionInventory) {
			AuctionInventory auc = (AuctionInventory) e.getInventory().getHolder();
			auc.update.cancel();
			auc.onClose();
		}
	}
	
}
