package net.colonymc.colonyskyblockcore.guilds.auction.inventories;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.auction.Auction;

public class AuctionManagePendingMenu implements Listener, InventoryHolder {
	
	Inventory inv;
	Player p;
	
	public AuctionManagePendingMenu(Player p) {
		this.p = p;
		this.inv = Bukkit.createInventory(this, 27, "Manage Auctions");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}
	
	private void fillInventory() {
		AuctionMenuUtils.fillGlasses(this);
		inv.setItem(11, new ItemStackBuilder(Material.BOOK_AND_QUILL).name("&dManage your own auctions").glint(true).build());
		inv.setItem(15, new ItemStackBuilder(Material.GOLD_BLOCK).name("&dManage the auctions you bidded on").glint(true).build());
	}

	public AuctionManagePendingMenu() {
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
	
	public ArrayList<Auction> boughtAuctions() {
		ArrayList<Auction> auctions = new ArrayList<>();
		for(Auction a : Auction.activeAuctions) {
			if(a.getTopBidder() != null && a.getTopBidder().getPlayer().getGuild().equals(Guild.getByPlayer(p)) && a.getTopBidder().hasClaimed()) {
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
		if(e.getInventory().getHolder() instanceof AuctionManagePendingMenu) {
			e.setCancelled(true);
			AuctionManagePendingMenu auc = (AuctionManagePendingMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 11) {
					if(!auc.ownAuctions().isEmpty()) {
						new AuctionManageOwnMenu(auc.p);
					}
					else {
						auc.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou don't have any auctions yet!"));
						auc.p.playSound(auc.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 15) {
					if(!auc.boughtAuctions().isEmpty()) {
						new AuctionManageBoughtMenu(auc.p);
					}
					else {
						auc.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou haven't bidded on any auctions yet!"));
						auc.p.playSound(auc.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
			}
		}
	}

}
