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

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.api.player.PlayerInventory;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.util.signs.SignGUI.SignGUIListener;

public class AuctionSetDurationMenu implements InventoryHolder, Listener {
	
	Player p;
	Inventory inv;
	AuctionCreateMenu auc;
	
	public AuctionSetDurationMenu(Player p, AuctionCreateMenu auc) {
		this.p = p;
		this.auc = auc;
		this.inv = Bukkit.createInventory(this, 27, "Setting duration...");
		auc.cancelled = false;
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
		Guild g = Guild.getByPlayer(p);
		inv.setItem(11, new ItemStackBuilder(Material.WATCH).name("&d6 hours").lore("&fCost: &aFREE").build());
		if(g.getGuildPlayer(p).getBalance() >= 100) {
			inv.setItem(13, new ItemStackBuilder(Material.WATCH).name("&d12 hours").lore("&fCost: &d+$100").build());
		}
		else {
			inv.setItem(13, new ItemStackBuilder(Material.WATCH).name("&c12 hours").lore("&fCost: &d+$100g\n \n&cCannot afford this!").build());
		}
		if(g.getGuildPlayer(p).getBalance() >= 250) {
			inv.setItem(15, new ItemStackBuilder(Material.WATCH).name("&d24 hours").lore("&fCost: &d+$250g").build());
		}
		else {
			inv.setItem(15, new ItemStackBuilder(Material.WATCH).name("&c24 hours").lore("&fCost: &d+$250g\n \n&cCannot afford this!").build());
		}
		if(p.hasPermission("*")) {
			inv.setItem(26, new ItemStackBuilder(Material.WATCH).name("&dCustom Duration &c(Admins only)").lore("&fCost: &aFREE").glint(true).build());
		}
	}

	public AuctionSetDurationMenu() {
	}
	
	public Inventory getInventory() {
		return inv;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionSetDurationMenu) {
			e.setCancelled(true);
			AuctionSetDurationMenu auc = (AuctionSetDurationMenu) e.getInventory().getHolder();
			Player p = auc.p;
			Guild g = Guild.getByPlayer(p);
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 11) {
					auc.auc.cancelled = true;
					auc.auc.setTime(21600000);
					p.openInventory(auc.auc.inv);
					auc.auc.cancelled = false;
				}
				else if(e.getSlot() == 13) {
					auc.auc.cancelled = true;
					if(g.getGuildPlayer(p).getBalance() >= 100) {
						auc.auc.setTime(43200000);
						p.openInventory(auc.auc.inv);
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford this duration!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
					auc.auc.cancelled = false;
				}
				else if(e.getSlot() == 15) {
					auc.auc.cancelled = true;
					if(g.getGuildPlayer(p).getBalance() >= 250) {
						auc.auc.setTime(86400000);
						p.openInventory(auc.auc.inv);
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford this duration!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
					auc.auc.cancelled = false;
				}
				else if(e.getSlot() == 26 && auc.p.hasPermission("*")) {
					auc.auc.cancelled = true;
					Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^", "Enter a duration", "in seconds!"},  new SignGUIListener() {
						@Override
						public void onSignDone(Player player, String[] lines) {
							if(Guild.getByPlayer(p) != null) {
								String msg = lines[0].replaceAll("\"", "");
								if(isLong(msg)) {
									long seconds = Long.parseLong(msg) * 1000;
									auc.auc.setTime(seconds);
									p.openInventory(auc.auc.getInventory());
								}
								else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number in seconds!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the auction house!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
					});
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionSetDurationMenu) {
			AuctionSetDurationMenu auc1 = (AuctionSetDurationMenu) e.getInventory().getHolder();
			AuctionCreateMenu auc = auc1.auc;
			if(!auc.cancelled) {
				if(auc.selected) {
					PlayerInventory.addItem(auc.selectedItem, auc.p, auc.selectedItem.getAmount());
				}
				auc.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou just cancelled your auction!"));
				auc.p.playSound(auc.p.getLocation(), Sound.NOTE_BASS, 2, 1);
			}
		}
	}
	
	public boolean isLong(String s) {
		try {
			Long.parseLong(s);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
}
