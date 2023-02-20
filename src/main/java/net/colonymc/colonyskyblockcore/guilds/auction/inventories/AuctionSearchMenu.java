package net.colonymc.colonyskyblockcore.guilds.auction.inventories;

import java.util.ArrayList;
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

import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.auction.Auction;

public class AuctionSearchMenu implements Listener, InventoryHolder {
	
	Inventory inv;
	Player p;
	Guild g;
	int page = 0;
	int totalPages;
	ArrayList<Auction> auctionsFound = new ArrayList<>();
	final HashMap<Integer, Auction> auctions = new HashMap<>();
	BukkitRunnable update;
	
	public AuctionSearchMenu(Player p, ArrayList<Auction> auctionsFound) {
		this.p = p;
		this.g = Guild.getByPlayer(p);
		this.auctionsFound = auctionsFound;
		this.inv = Bukkit.createInventory(this, 54, "Auctions found: " + auctionsFound.size());
		fillInventory();
		startUpdating();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}
	
	public void startUpdating() {
		update = new BukkitRunnable() {
			@Override
			public void run() {
				changePage(0);
				p.updateInventory();
			}
		};
		update.runTaskTimerAsynchronously(Main.getInstance(), 0, 3);
	}
	
	public AuctionSearchMenu() {
	}
	
	public void fillInventory() {
		AuctionMenuUtils.fillGlasses(this);
		inv.setItem(49, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
	}
	
	public void changePage(int amount) {
		ArrayList<Auction> unended = unended();
		totalPages = (int) Math.ceil((double) unended.size() / 24);
		page = page + amount;
		auctions.clear();
		int index = page * 45;
		for(int i = 0; i < 45; i++) {
			if(unended.size() > index) {
				Auction a = unended.get(index);
				inv.setItem(i, AuctionMenuUtils.getAuctionItem(a, p, true));
				auctions.put(i, a);
			}
			else {
				inv.setItem(i, new ItemStack(Material.AIR));
			}
			index++;
		}
		if(page > 0) {
			inv.setItem(45, new ItemStackBuilder(Material.ARROW).name("&dPrevious Page").build());
		}
		else {
			inv.setItem(45, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
		if(page + 1 < totalPages) {
			inv.setItem(53, new ItemStackBuilder(Material.ARROW).name("&dNext Page").build());
		}
		else {
			inv.setItem(53, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
	}
	
	public ArrayList<Auction> unended(){
		ArrayList<Auction> auctions = new ArrayList<>();
		for(Auction a : auctionsFound) {
			if(!a.hasEnded()) {
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
		if(e.getInventory().getHolder() instanceof AuctionSearchMenu) {
			e.setCancelled(true);
			AuctionSearchMenu auc = (AuctionSearchMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 53) {
					if(auc.page + 1 < auc.totalPages) {
						auc.changePage(1);
					}
				}
				else if(e.getSlot() == 45) {
					if(auc.page > 0) {
						auc.changePage(-1);
					}
				}
				else if(e.getSlot() == 49) {
					e.getWhoClicked().closeInventory();
					new AuctionHouseMenu(auc.p);
				}
				else if(auc.auctions.containsKey(e.getSlot())) {
					if(auc.auctions.get(e.getSlot()).getSeller().equals(auc.g.getGuildPlayer(auc.p))) {
						new AuctionInspectMenu(auc.p, auc.auctions.get(e.getSlot()));
					}
					else {
						Auction a = auc.auctions.get(e.getSlot());
						int minimumBid = (a.getCurrentBid() == 0) ? a.getStartingBid() : a.getCurrentBid() + 100;
						if(minimumBid <= auc.g.getGuildPlayer(auc.p).getBalance()) {
							if(a.getTopBidder() == null || !a.getTopBidder().getPlayer().getGuild().equals(auc.g)) {
								new AuctionBidMenu(auc.p, auc.auctions.get(e.getSlot()));
							}
							else {
								auc.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild is already the top bidder on this auction!"));
								auc.p.playSound(auc.p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else {
							auc.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford to bid on this auction!"));
							auc.p.playSound(auc.p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionSearchMenu) {
			AuctionSearchMenu auc = (AuctionSearchMenu) e.getInventory().getHolder();
			auc.update.cancel();
		}
		
	}
}
