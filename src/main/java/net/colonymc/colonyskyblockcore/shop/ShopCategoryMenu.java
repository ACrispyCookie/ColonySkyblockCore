package net.colonymc.colonyskyblockcore.shop;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyspigotapi.itemstacks.ItemStackBuilder;
import net.colonymc.colonyspigotapi.primitive.GetNames;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionMenuUtils;
import net.md_5.bungee.api.ChatColor;

public class ShopCategoryMenu implements InventoryHolder, Listener {

	int page = 0;
	int totalPages;
	Inventory inv;
	Player p;
	ShopCategory cat;
	final HashMap<Integer, Object> products = new HashMap<>();
	
	public ShopCategoryMenu(Player p, ShopCategory cat) {
		this.p = p;
		this.cat = cat;
		this.inv = Bukkit.createInventory(this, 54, "Category: " + cat.getName().substring(0,1).toUpperCase() + cat.getName().substring(1) + " (Page: " + (page + 1) + ")");
		changePage(0);
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1);
	}
	
	public ShopCategoryMenu() {
		
	}
	
	public void changePage(int amount) {
		page = page + amount;
		totalPages = (int) Math.ceil((double) cat.getProducts().size() / 45);
		products.clear();
		p.closeInventory();
		this.inv = Bukkit.createInventory(this, 54, "Category: " + cat.getName().substring(0,1).toUpperCase() + cat.getName().substring(1) + " (Page: " + (page + 1) + ")");
		AuctionMenuUtils.fillGlasses(this);
		for(int i = 0; i < 45; i++) {
			int index = page * 45 + i;
			if(index < cat.getProducts().size()) {
				Object o = cat.getProducts().get(index);
				products.put(i, o);
				if(o instanceof Product) {
					Product p = (Product) o;
					ItemStack item = new ItemStackBuilder(p.getMaterial())
							.name(ChatColor.WHITE + GetNames.itemName(p.getNewItem(), false))
							.lore((p.isBuyable() ? "\n&5» &fBuy price: &d" + Guild.balance(p.getBuyPrice()) : "") + 
							(p.isSellable() ? "\n&5» &fSell price: &d" + Guild.balance(p.getSellPrice()) : "") + "\n " +
							(p.isBuyable() ? "\n&dLeft-Click to buy this item!" : "")
							+ (p.isSellable() ? "\n&dRight-Click to sell this item!"
							+ "\n&dMiddle-Click to sell all of this item!" : ""))
							.build();
					item.setAmount(p.getDefaultAmount());
					item.setDurability(p.getData());
					inv.setItem(i, item);
				}
				else if(o instanceof CommandProduct) {
					CommandProduct p = (CommandProduct) o;
					ItemStack item = new ItemStackBuilder(p.getDisplayItem().getType())
							.name(p.getDisplayItem().getItemMeta().getDisplayName())
							.lore((p.isBuyable() ? "\n&5» &fBuy price: &d" + Guild.balance(p.getBuyPrice()) : "") + "\n " + (p.isBuyable() ? "\n&dLeft-Click to buy this item!" : ""))
							.build();
					item.setAmount(p.getDefaultAmount());
					item.setDurability(p.getDisplayItem().getDurability());
					inv.setItem(i, item);
				}
			}
			else {
				inv.setItem(i, new ItemStack(Material.AIR));
			}
		}
		if(page > 0) {
			inv.setItem(45, new ItemStackBuilder(Material.ARROW).name(ChatColor.translateAlternateColorCodes('&', "&dPrevious Page")).build());
		}
		else {
			inv.setItem(45, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
		if(page + 1 < totalPages) {
			inv.setItem(53, new ItemStackBuilder(Material.ARROW).name(ChatColor.translateAlternateColorCodes('&', "&dNext Page")).build());
		}
		else {
			inv.setItem(53, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
		inv.setItem(49, new ItemStackBuilder(Material.ARROW).name(ChatColor.translateAlternateColorCodes('&', "&dGo Back")).build());
		p.openInventory(inv);
		p.updateInventory();
	}
	
	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof ShopCategoryMenu) {
			e.setCancelled(true);
			ShopCategoryMenu menu = (ShopCategoryMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(menu.products.containsKey(e.getSlot())) {
					Object o = menu.products.get(e.getSlot());
					if(o instanceof Product) {
						Product pr = (Product) o;
						if(e.getClick() == ClickType.LEFT) {
							if(pr.isBuyable()) {
								menu.p.closeInventory();
								new ShopProductBuyMenu(menu.p, pr);
							}
						}
						else if(e.getClick() == ClickType.RIGHT) {
							if(pr.isSellable()) {
								menu.p.closeInventory();
								new ShopProductSellMenu(menu.p, pr);
							}
						}
						else if(e.getClick() == ClickType.MIDDLE) {
							if(pr.isSellable()) {
								if(ShopProductSellMenu.getAmountAvailable(menu.p, pr.getMaterial()) > 0) {
									int amount = ShopProductSellMenu.getAmountAvailable(menu.p, pr.getMaterial());
									Guild.getByPlayer(menu.p).getGuildPlayer(menu.p).addBalance(amount * pr.getSellPrice());
									menu.p.getInventory().removeItem(new ItemStack(pr.getMaterial(), amount));
									menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou just sold &d" + amount + "x &fof &d" + GetNames.itemName(new ItemStack(pr.getMaterial()), false) + " &ffor &d" + Guild.balance(amount * pr.getSellPrice()) + "&f!"));
									menu.p.playSound(menu.p.getLocation(), Sound.NOTE_PLING, 2, 1);
								}
								else {
									menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou don't have any items of this kind to sell!"));
									menu.p.playSound(menu.p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
							}
						}
					}
					else if(o instanceof CommandProduct) {
						CommandProduct pr = (CommandProduct) o;
						if(e.getClick() == ClickType.LEFT) {
							if(pr.isBuyable()) {
								menu.p.closeInventory();
								new ShopProductBuyMenu(menu.p, pr);
							}
						}
					}
				}
				else if(e.getSlot() == 49) {
					menu.p.closeInventory();
					new ShopCategoriesMenu(menu.p);
				}
				else if(e.getSlot() == 45) {
					if(menu.page > 0) {
						menu.changePage(-1);
					}
				}
				else if(e.getSlot() == 53) {
					if(menu.page + 1 < menu.totalPages) {
						menu.changePage(1);
					}
				}
			}
		}
	}

}
