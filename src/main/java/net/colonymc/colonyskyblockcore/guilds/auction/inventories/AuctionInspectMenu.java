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

public class AuctionInspectMenu implements Listener, InventoryHolder {
	
	Inventory inv;
	Player p;
	Auction a;
	BukkitRunnable update;
	
	public AuctionInspectMenu(Player p, Auction a) {
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

	private void refreshItems() {
		inv.setItem(31, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
		inv.setItem(11, AuctionMenuUtils.getBidHistory(a));
		inv.setItem(13, AuctionMenuUtils.getAuctionItemFromInspect(a, false));
		if(a.hasEnded()) {
			if(a.getTopBidder() != null) {
				inv.setItem(15, new ItemStackBuilder(Material.GOLD_BLOCK).name("&dClaim money")
						.lore("\n&fClick here to claim the money\n&fof this auction which was won by\n&d" 
				+ a.getTopBidder().getPlayer().getGuild().getName() + " &fwith the price of\n&d" + Guild.balance(a.getCurrentBid()) + "&f!")
						.glint(true)
						.build());
			}
			else {
				inv.setItem(15, new ItemStackBuilder(Material.GOLD_BLOCK).name("&dClaim your item back")
						.lore("\n&fThere were no bidders on this auction!\n&fClick here to get your item back!")
						.glint(true)
						.build());
			}
		}
		else {
			inv.setItem(15, new ItemStackBuilder(Material.GOLD_BLOCK).name("&cClaim money")
					.lore("\n&fThis auction hasn't ended yet!\n&fYou cannot currently claim your earnings!")
					.glint(true)
					.build());
		}
	}

	public AuctionInspectMenu() {
		
	}
	
	public Inventory getInventory() {
		return inv;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionInspectMenu) {
			e.setCancelled(true);
			AuctionInspectMenu auc = (AuctionInspectMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 31) {
					new AuctionManageOwnMenu(auc.p);
				}
				else if(e.getSlot() == 11) {
					if(!auc.a.getBidders().isEmpty()) {
						new AuctionBidHistoryMenu(auc.p, auc.a);
					}
					else {
						if(auc.a.hasEnded()) {
							auc.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cNoone bidded on this auction!"));
						}
						else {
							auc.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou don't have any bidders yet!"));
						}
						auc.p.playSound(auc.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 15) {
					if(auc.a.hasEnded()) {
						if(Guild.getByPlayer(auc.p).getGuildPlayer(auc.p).equals(auc.a.getSeller())) {
							auc.p.closeInventory();
							auc.a.claimSeller();
						}
						else {
							auc.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cOnly the seller can claim the auction!"));
							auc.p.playSound(auc.p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
					else {
						auc.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis auction hasn't ended yet!"));
						auc.p.playSound(auc.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionInspectMenu) {
			AuctionInspectMenu auc = (AuctionInspectMenu) e.getInventory().getHolder();
			auc.update.cancel();
		}
	}
	
}
