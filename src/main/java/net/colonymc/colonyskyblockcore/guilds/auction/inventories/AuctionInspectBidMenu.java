package net.colonymc.colonyskyblockcore.guilds.auction.inventories;

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
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyspigotapi.api.itemstack.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.auction.Auction;
import net.colonymc.colonyskyblockcore.guilds.auction.Bidder;

public class AuctionInspectBidMenu implements Listener, InventoryHolder {
	
	Inventory inv;
	Player p;
	Auction a;
	BukkitRunnable update;
	
	public AuctionInspectBidMenu(Player p, Auction a) {
		this.p = p;
		this.a = a;
		this.inv = Bukkit.createInventory(this, 36, "Managing auction...");
		AuctionMenuUtils.fillGlasses(this);
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
		update = new BukkitRunnable() {
			@Override
			public void run() {
				refreshItems();
				p.updateInventory();
			}
		};
		update.runTaskTimerAsynchronously(Main.getInstance(), 0, 3);
	}

	public AuctionInspectBidMenu() {
		
	}

	private void refreshItems() {
		inv.setItem(31, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
		inv.setItem(13, AuctionMenuUtils.getBoughtItemFromInspect(a, p, false));
		if(a.hasEnded()) {
			inv.setItem(15, new ItemStackBuilder(Material.GOLD_BLOCK).name("&dClaim item")
					.lore("\n&fClick here to claim your item\n&fyou bought from this auction!")
					.glint(true)
					.build());
		}
		else {
			inv.setItem(15, new ItemStackBuilder(Material.GOLD_BLOCK).name("&dClaim item")
					.lore("\n&fThis auction hasn't ended yet!\n&fYou cannot claim the item!")
					.glint(true)
					.build());
		}
		if(!a.getTopBidder().getPlayer().getGuild().equals(Guild.getByPlayer(p)) && getSilverSpent() != 0) {
			inv.setItem(11, AuctionMenuUtils.getSpentSilver(a, getSilverSpent(), p));
		}
		else {
			inv.setItem(11, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
	}
	
	private int getSilverSpent() {
		int silver = 0;
		for(Bidder b : a.getBidders()) {
			if(b.getPlayer().getGuild().equals(Guild.getByPlayer(p))) {
				silver = silver + b.getAmount();
			}
		}
		return silver;
	}
	
	public Inventory getInventory() {
		return inv;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionInspectBidMenu) {
			e.setCancelled(true);
			AuctionInspectBidMenu auc = (AuctionInspectBidMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 31) {
					new AuctionManageBoughtMenu(auc.p);
				}
				else if(e.getSlot() == 15) {
					if(auc.a.hasEnded()) {
						auc.p.closeInventory();
						auc.a.claimBuyer();
					}
					else {
						auc.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis auction hasn't ended yet!"));
						auc.p.playSound(auc.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 11) {
					auc.p.closeInventory();
					auc.a.claimSilverLeft(auc.p, getSilverSpent());
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionInspectBidMenu) {
			AuctionInspectBidMenu auc = (AuctionInspectBidMenu) e.getInventory().getHolder();
			auc.update.cancel();
		}
	}
	
}
