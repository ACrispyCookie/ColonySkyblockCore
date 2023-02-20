package net.colonymc.colonyskyblockcore.guilds.auction.inventories;

import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.auction.Auction;
import net.colonymc.colonyskyblockcore.guilds.auction.Bidder;
import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;
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

public class AuctionBidMenu implements Listener, InventoryHolder {
	
	Inventory inv;
	Player p;
	Auction a;
	int amount = 0;
	BukkitRunnable update;
	boolean settingAmount = false;
	
	public AuctionBidMenu(Player p, Auction a) {
		this.p = p;
		this.a = a;
		this.inv = Bukkit.createInventory(this, 27, "Bidding on " + a.getSeller().getGuild().getName() + "'s auction...");
		AuctionMenuUtils.fillGlasses(this);
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
				if(!settingAmount) {
					refreshItems();
					p.updateInventory();
				}
			}
		};
		update.runTaskTimerAsynchronously(Main.getInstance(), 0, 3);
	}

	public AuctionBidMenu() {
		
	}
	
	private void fillInventory() {
		int minimumBid = (a.getCurrentBid() == 0) ? a.getStartingBid() : a.getCurrentBid() + 100;
		inv.setItem(11, new ItemStackBuilder(Material.IRON_INGOT)
				.name("&dSet bid amount")
				.lore("\n&fClick to set your bid amount!\n&fIt must be at least &d" + Guild.balance(minimumBid))
				.build());
		inv.setItem(15, new ItemStackBuilder(Material.EMERALD_BLOCK)
				.name("&cPlace bid")
				.lore("\n  &5» &fSet starting price - &cX")
				.build());
		inv.setItem(13, AuctionMenuUtils.getAuctionItem(a, p, false));
	}

	private void refreshItems() {
		int minimumBid = (a.getCurrentBid() == 0) ? a.getStartingBid() : a.getCurrentBid() + 100;
		inv.setItem(11, new ItemStackBuilder(Material.IRON_INGOT)
				.name("&dSet bid amount")
				.lore("\n&fClick to set your bid amount!\n&fIt must be at least &d" + Guild.balance(minimumBid))
				.build());
		inv.setItem(13, AuctionMenuUtils.getAuctionItem(a, p, false));
	}
	
	public void setPrice(int amount) {
		this.amount = amount;
		p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have set the bid amount to &d" + Guild.balance(amount) + "&f!"));
		inv.setItem(11, new ItemStackBuilder(Material.IRON_INGOT)
				.name("&dBid amount: " + Guild.balance(amount))
				.lore("&aBid amount set!\n \n&fClick here to reset the bid amount!")
				.build());
		checkReady();
	}
	
	public void checkReady() {
		if(amount > 0) {
			inv.setItem(15, new ItemStackBuilder(Material.EMERALD_BLOCK)
					.name("&aPlace bid")
					.lore("\n  &5» &fSet starting price - &a✔\n  &aReady!")
					.build());
		}
		else if(amount == 0) {
			inv.setItem(15, new ItemStackBuilder(Material.EMERALD_BLOCK)
					.name("&cPlace bid")
					.lore("\n  &5» &fSet starting price - &cX")
					.build());
		}
		p.updateInventory();
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionBidMenu) {
			e.setCancelled(true);
			AuctionBidMenu auc = (AuctionBidMenu) e.getInventory().getHolder();
			Player p = auc.p;
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 11) {
					auc.settingAmount = true;
					Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^^", "Enter the amount", "of your bid!"}, (player, lines) -> {
						auc.settingAmount = false;
						if(!auc.a.hasEnded()) {
							if(Guild.getByPlayer(p) != null) {
								String msg = lines[0].replaceAll("\"", "");
								int minimumBid = (auc.a.getCurrentBid() == 0) ? auc.a.getStartingBid() : auc.a.getCurrentBid() + 100;
								if(Guild.getByPlayer(p).getGuildPlayer(p).getBalance() >= minimumBid) {
									p.openInventory(auc.getInventory());
									if(isInt(msg)) {
										if(Guild.getByPlayer(auc.p).getGuildPlayer(auc.p).getBalance() >= Integer.parseInt(msg)) {
											if(Integer.parseInt(msg) <= 1000000000 && Integer.parseInt(msg) >= minimumBid) {
												auc.setPrice(Integer.parseInt(msg));
											}
											else {
												p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour bid must be at least " + Guild.balance(minimumBid) + " and smaller than 1,000,000,000g!"));
												p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
											}
										}
										else {
											p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford to place this bid!"));
											p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
										}
									}
									else {
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number  than " + Guild.balance(minimumBid) + "g!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
								}
								else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford to place a bid on this auction!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the auction house!!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis auction has ended!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					});
				}
				else if(e.getSlot() == 15) {
					if(auc.amount > 0) {
						if(Guild.getByPlayer(auc.p).getGuildPlayer(auc.p).getBalance() >= auc.amount) {
							p.closeInventory();
							if(!auc.a.hasEnded()) {
								auc.a.newBid(new Bidder(auc.p, auc.amount, System.currentTimeMillis(), false), auc.amount);
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis auction has ended!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else {
							p.closeInventory();
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford to place a bid on this auction!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a bid amount!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionBidMenu) {
			AuctionBidMenu auc = (AuctionBidMenu) e.getInventory().getHolder();
			if(!settingAmount) {
				auc.update.cancel();
			}
		}
	}
	
	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

}
