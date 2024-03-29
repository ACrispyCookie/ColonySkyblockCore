package net.colonymc.colonyskyblockcore.guilds.auction.inventories;

import java.util.ArrayList;
import java.util.HashMap;

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

import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.auction.Auction;

public class AuctionManageOwnMenu implements Listener, InventoryHolder {
	
	Inventory inv;
	Player p;
	int page = 0;
	int totalPages;
	BukkitRunnable update;
	final HashMap<Integer, Auction> auctions = new HashMap<>();
	
	public AuctionManageOwnMenu(Player p) {
		this.p = p;
		this.inv = Bukkit.createInventory(this, 45, "Your own auctions...");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
		update = new BukkitRunnable() {
			@Override
			public void run() {
				changePage(0);
				p.updateInventory();
			}
		};
		update.runTaskTimerAsynchronously(Main.getInstance(), 0, 3);
	}
	
	public AuctionManageOwnMenu() {
		
	}
	
	private void fillInventory() {
		AuctionMenuUtils.fillGlasses(this);
		inv.setItem(40, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
		changePage(0);
	}

	public void changePage(int amount) {
		ArrayList<Auction> ownAuctions = ownAuctions();
		totalPages = (int) Math.ceil((double) ownAuctions.size() / 36);
		page = page + amount;
		auctions.clear();
		int index = page * 36;
		for(int i = 0; i < 36; i++) {
			if(ownAuctions.size() > index) {
				inv.setItem(i, AuctionMenuUtils.getAuctionItemFromInspect(ownAuctions.get(index), true));
				auctions.put(i, ownAuctions.get(index));
			}
			else {
				inv.setItem(i, new ItemStack(Material.AIR));
			}
			index++;
		}
		if(page > 0) {
			inv.setItem(36, new ItemStackBuilder(Material.ARROW).name("&dPrevious Page").build());
		}
		else {
			inv.setItem(36, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
		if(page + 1 < totalPages) {
			inv.setItem(44, new ItemStackBuilder(Material.ARROW).name("&dNext Page").build());
		}
		else {
			inv.setItem(44, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
	}
	
	public ArrayList<Auction> ownAuctions() {
		ArrayList<Auction> auctions = new ArrayList<>();
		for(Auction a : Auction.activeAuctions) {
			if(a.getSeller().equals(Guild.getByPlayer(p).getGuildPlayer(p)) && a.hasSellerClaimed()) {
				auctions.add(a);
			}
		}
		return auctions;
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionManageOwnMenu) {
			e.setCancelled(true);
			AuctionManageOwnMenu auc = (AuctionManageOwnMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 44) {
					if(auc.page + 1 < auc.totalPages) {
						auc.changePage(1);
					}
				}
				else if(e.getSlot() == 36) {
					if(auc.page > 0) {
						auc.changePage(-1);
					}
				}
				else if(e.getSlot() == 40) {
					new AuctionManagePendingMenu(auc.p);
				}
				else if(auc.auctions.containsKey(e.getSlot())) {
					new AuctionInspectMenu(auc.p, auc.auctions.get(e.getSlot()));
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionManageOwnMenu) {
			AuctionManageOwnMenu auc = (AuctionManageOwnMenu) e.getInventory().getHolder();
			auc.update.cancel();
		}
	}
}