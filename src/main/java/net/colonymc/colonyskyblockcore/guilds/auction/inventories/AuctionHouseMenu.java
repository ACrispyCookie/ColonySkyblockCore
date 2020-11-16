package net.colonymc.colonyskyblockcore.guilds.auction.inventories;

import net.colonymc.colonyspigotapi.itemstacks.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.auction.Auction;
import org.apache.commons.lang.StringUtils;
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

import java.util.ArrayList;
import java.util.HashMap;

public class AuctionHouseMenu implements InventoryHolder, Listener{

	Inventory inv;
	Player p;
	Guild g;
	int page = 0;
	int totalPages;
	final HashMap<Integer, Auction> auctions = new HashMap<>();
	BukkitRunnable update;
	
	public AuctionHouseMenu(Player p) {
		this.p = p;
		this.g = Guild.getByPlayer(p);
		this.inv = Bukkit.createInventory(this, 54, "Auction House");
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
	
	public AuctionHouseMenu() {
	}
	
	public void fillInventory() {
		AuctionMenuUtils.fillGlasses(this);
		inv.setItem(9, new ItemStackBuilder(Material.IRON_INGOT).name("&dYour balance")
				.lore("\n&fYour current balance\n&fis &d" + Guild.balance(g.getGuildPlayer(p).getBalance()))
				.glint(true)
				.build());
		inv.setItem(18, new ItemStack(Material.AIR));
		inv.setItem(27, new ItemStack(Material.AIR));
		inv.setItem(36, new ItemStackBuilder(Material.SIGN).name("&dSearch items")
				.lore("\n&fClick here to search for items\n&fin the auction house!")
				.build());
	}
	
	public void changePage(int amount) {
		ArrayList<Auction> unended = unended();
		totalPages = (int) Math.ceil((double) unended.size() / 24);
		page = page + amount;
		auctions.clear();
		int index = page * 24;
		for(int i = 11; i < 44; i++) {
			if((i + 1) % 9 != 0) {
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
			else {
				i = i + 2;
			}
		}
		if(page > 0) {
			inv.setItem(48, new ItemStackBuilder(Material.ARROW).name("&dPrevious Page").build());
		}
		else {
			inv.setItem(48, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
		if(page + 1 < totalPages) {
			inv.setItem(51, new ItemStackBuilder(Material.ARROW).name("&dNext Page").build());
		}
		else {
			inv.setItem(51, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
	}
	
	public ArrayList<Auction> unended(){
		ArrayList<Auction> auctions = new ArrayList<>();
		for(Auction a : Auction.activeAuctions) {
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
		if(e.getInventory().getHolder() instanceof AuctionHouseMenu) {
			e.setCancelled(true);
			AuctionHouseMenu auc = (AuctionHouseMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 51) {
					if(auc.page + 1 < auc.totalPages) {
						auc.changePage(1);
					}
				}
				else if(e.getSlot() == 48) {
					if(auc.page > 0) {
						auc.changePage(-1);
					}
				}
				else if(e.getSlot() == 36) {
					auc.p.closeInventory();
					Main.getSignGui().open(auc.p, new String[] {"", "^^^^^^^^^^^^^^^^", "Please enter an", "item name!"}, (player, lines) -> {
						if(Guild.getByPlayer(player) != null) {
							String query = lines[0].replaceAll("\"", "");
							ArrayList<Auction> auctions = new ArrayList<>();
							for(Auction a : Auction.activeAuctions) {
								if(!a.hasEnded() && StringUtils.containsIgnoreCase(a.getItemSold().getName(), query)) {
									auctions.add(a);
								}
							}
							new AuctionSearchMenu(auc.p, auctions);
						}
						else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the auction house!"));
							player.playSound(player.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					});
				}
				else if(auc.auctions.containsKey(e.getSlot())) {
					if(auc.auctions.get(e.getSlot()).getSeller().equals(Guild.getByPlayer(auc.p).getGuildPlayer(auc.p))) {
						new AuctionInspectMenu(auc.p, auc.auctions.get(e.getSlot()));
					}
					else if(auc.auctions.get(e.getSlot()).getSeller().getGuild().equals(Guild.getByPlayer(auc.p))) {
						auc.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThis auction is created by the member of your guild &d" + auc.auctions.get(e.getSlot()).getSeller().getPlayer().getName() + " &fand therefore you cannot bid on it!"));
						auc.p.playSound(auc.p.getLocation(), Sound.NOTE_BASS, 2, 1);
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
		if(e.getInventory().getHolder() instanceof AuctionHouseMenu) {
			AuctionHouseMenu auc = (AuctionHouseMenu) e.getInventory().getHolder();
			auc.update.cancel();
		}
		
	}
	
}
