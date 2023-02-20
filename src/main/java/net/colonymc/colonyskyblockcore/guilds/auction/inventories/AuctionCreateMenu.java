package net.colonymc.colonyskyblockcore.guilds.auction.inventories;

import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.auction.Auction;
import net.colonymc.colonyskyblockcore.guilds.auction.AuctionItem;
import net.colonymc.colonyskyblockcore.guilds.inventories.InventoryUtils;
import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;
import net.colonymc.colonyspigotlib.lib.player.PlayerInventory;
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

public class AuctionCreateMenu implements Listener, InventoryHolder {

	Inventory inv;
	Player p;
	Guild g;
	ItemStack selectedItem;
	boolean selected = false;
	boolean cancelled = false;
	int startingPrice = 0;
	long duration = 0;
	
	public AuctionCreateMenu(Player p) {
		this.p = p;
		this.g = Guild.getByPlayer(p);
		this.inv = Bukkit.createInventory(this, 45, "Create auction...");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}

	public AuctionCreateMenu() {
	}
	
	private void fillInventory() {
		AuctionMenuUtils.fillGlasses(this);
		inv.setItem(13, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&dSelect an item").lore("\n&fClick an item on your\n&finventory to select it").durability((short) 0).build());
		inv.setItem(31, new ItemStackBuilder(Material.WATCH).name("&dSet the duration").lore("\n&fClick here to set the duration\n&fof this auction!").build());
		inv.setItem(29, new ItemStackBuilder(Material.IRON_INGOT).name("&dSet the starting price").lore("\n&fClick here to set the starting\n&fprice of this auction!").build());
		inv.setItem(33, new ItemStackBuilder(Material.EMERALD_BLOCK).name("&cPublish auction")
				.lore("\n  &5» &fSelected item - &cX\n  &5» &fSet starting price - &cX\n  &5» &fSet duration - &cX")
				.build());
	}
	
	public void selectItem(ItemStack i) {
		this.selectedItem = i;
		this.selected = true;
		checkReady();
		p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
		inv.setItem(13, i);
	}
	
	public void deselectItem() {
		this.selected = false;
		PlayerInventory.addItem(selectedItem, p, selectedItem.getAmount());
		this.selectedItem = null;
		checkReady();
		p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
		inv.setItem(13, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&dSelect an item").lore("\n&fClick an item on your\n&finventory to select it").durability((short) 0).build());
	}
	
	public void setPrice(int amount) {
		this.startingPrice = amount;
		checkReady();
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have set the starting price to &d" + Guild.balance(amount) + "&f!"));
		inv.setItem(29, new ItemStackBuilder(Material.IRON_INGOT).name("&dStarting price: " + Guild.balance(amount))
				.lore("&aStarting price set!\n \n&fClick here to reset the starting\n&fprice of this auction!")
				.build());
	}
	
	public void setTime(long duration) {
		this.duration = duration;
		checkReady();
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have set the duration of the auction to &d" + InventoryUtils.getDurationString(duration) + "&f!"));
		inv.setItem(31, new ItemStackBuilder(Material.WATCH).name("&dAuction duration: " + InventoryUtils.getDurationString(duration))
				.lore("&aDuration set!\n \n&fClick here to reset the duration\n&fof this auction!")
				.build());
	}
	
	public void checkReady() {
		String line = "";
		boolean allReady = true;
		if(selected) {
			line = line + "\n  &5» &fSelected item - &a✔";
		}
		else {
			allReady = false;
			line = line + "\n  &5» &fSelected item - &cX";
		}
		if(startingPrice > 0) {
			line = line + "\n  &5» &fSet starting price - &a✔";
		}
		else {
			allReady = false;
			line = line + "\n  &5» &fSet starting price - &cX";
		}
		if(duration > 0) {
			line = line + "\n  &5» &fSet duration - &a✔";
		}
		else {
			allReady = false;
			line = line + "\n  &5» &fSet duration - &cX";
		}
		if(allReady) {
			inv.setItem(33, new ItemStackBuilder(Material.EMERALD_BLOCK).name("&aPublish auction").lore(line + "\n  &aReady!").glint(true).build());
		}
		else {
			inv.setItem(33, new ItemStackBuilder(Material.EMERALD_BLOCK).name("&aPublish auction").lore(line).build());
		}
		p.updateInventory();
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionCreateMenu) {
			e.setCancelled(true);
			AuctionCreateMenu auc = (AuctionCreateMenu) e.getInventory().getHolder();
			Player p = auc.p;
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 33) {
					if(auc.startingPrice != 0 && auc.selected && auc.duration > 0) {
						if(auc.duration == 43200000) {
							if(Guild.getByPlayer(p).getGuildPlayer(p).getBalance() >= 100) {
								Guild.getByPlayer(p).getGuildPlayer(p).removeBalance(100);
								Auction au = new Auction(Guild.getByPlayer(p).getGuildPlayer(p), new AuctionItem(auc.selectedItem), auc.duration, auc.startingPrice, false, null);
								au.startAuction();
								auc.cancelled = true;
								p.closeInventory();
							}
							else {
								auc.duration = 0;
								checkReady();
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford this duration!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else if(auc.duration == 86400000) {
							if(Guild.getByPlayer(p).getGuildPlayer(p).getBalance() >= 250) {
								Guild.getByPlayer(p).getGuildPlayer(p).removeBalance(250);
								Auction au = new Auction(Guild.getByPlayer(p).getGuildPlayer(p), new AuctionItem(auc.selectedItem), auc.duration, auc.startingPrice, false, null);
								au.startAuction();
								auc.cancelled = true;
								p.closeInventory();
							}
							else {
								auc.duration = 0;
								checkReady();
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford this duration!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else {
							Auction au = new Auction(Guild.getByPlayer(p).getGuildPlayer(p), new AuctionItem(auc.selectedItem), auc.duration, auc.startingPrice, false, null);
							au.startAuction();
							auc.cancelled = true;
							p.closeInventory();
						}
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou must select an item, a starting price and a duration!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 29) {
					auc.cancelled = true;
					p.closeInventory();
					Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^", "Enter the", "starting price"}, (player, lines) -> {
						auc.cancelled = false;
						if(Guild.getByPlayer(p) != null) {
							String msg = lines[0].replaceAll("\"", "");
							p.openInventory(auc.getInventory());
							if(isInt(msg) && Integer.parseInt(msg) >= 1 && Integer.parseInt(msg) <= 1000000000) {
								auc.setPrice(Integer.parseInt(msg));
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number between 1 and 1,000,000,000"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the auction house!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					});
				}
				else if(e.getSlot() == 31) {
					auc.cancelled = true;
					p.closeInventory();
					new AuctionSetDurationMenu(p, auc);
				}
				else if(e.getSlot() == 13) {
					if(auc.selected) {
						auc.deselectItem();
					}
				}
			}
			else {
				if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
					if(!auc.selected) {
						auc.selectItem(e.getCurrentItem());
						p.getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou have already selected an item!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionCreateMenu) {
			AuctionCreateMenu auc = (AuctionCreateMenu) e.getInventory().getHolder();
			if(!auc.cancelled) {
				if(auc.selected) {
					PlayerInventory.addItem(auc.selectedItem, auc.p, auc.selectedItem.getAmount());
				}
				auc.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou just cancelled your auction!"));
				auc.p.playSound(auc.p.getLocation(), Sound.NOTE_BASS, 2, 1);
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