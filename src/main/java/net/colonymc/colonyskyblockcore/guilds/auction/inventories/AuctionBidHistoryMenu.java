package net.colonymc.colonyskyblockcore.guilds.auction.inventories;

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
import net.colonymc.colonyskyblockcore.guilds.auction.Auction;
import net.colonymc.colonyskyblockcore.guilds.auction.Bidder;

public class AuctionBidHistoryMenu implements Listener, InventoryHolder {
	
	Inventory inv;
	Player p;
	Auction a;
	int page = 0;
	int totalPages;
	BukkitRunnable update;
	
	public AuctionBidHistoryMenu(Player p, Auction a) {
		this.p = p;
		this.a = a;
		this.inv = Bukkit.createInventory(this, 45, "Bid history");
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

	public AuctionBidHistoryMenu() {
		
	}

	private void fillInventory() {
		AuctionMenuUtils.fillGlasses(this);
		inv.setItem(40, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
		changePage(0);
	}
	
	public void changePage(int amount) {
		totalPages = (int) Math.ceil((double) a.getBidders().size() / 36);
		page = page + amount;
		int index = page * 36;
		for(int i = 0; i < 36; i++) {
			if(a.getBidders().size() > index) {
				Bidder b = a.getBidders().get(index);
				inv.setItem(i, AuctionMenuUtils.getBidHistoryItem(b));
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
			inv.setItem(43, new ItemStackBuilder(Material.ARROW).name("&dNext Page").build());
		}
		else {
			inv.setItem(43, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
	}
	
	public Inventory getInventory() {
		return inv;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionBidHistoryMenu) {
			e.setCancelled(true);
			AuctionBidHistoryMenu auc = (AuctionBidHistoryMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 40) {
					new AuctionInspectMenu(auc.p, auc.a);
				}
				else if(e.getSlot() == 43) {
					if(auc.page + 1 < auc.totalPages) {
						auc.changePage(1);
					}
				}
				else if(e.getSlot() == 36) {
					if(auc.page > 0) {
						auc.changePage(-1);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionBidHistoryMenu) {
			AuctionBidHistoryMenu auc = (AuctionBidHistoryMenu) e.getInventory().getHolder();
			auc.update.cancel();
		}
	}
}
