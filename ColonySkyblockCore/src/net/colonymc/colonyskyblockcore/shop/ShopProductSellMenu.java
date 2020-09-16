package net.colonymc.colonyskyblockcore.shop;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.api.primitive.GetNames;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;

public class ShopProductSellMenu implements InventoryHolder, Listener {

	Inventory inv;
	Player p;
	Product pr;
	int amount;
	
	public ShopProductSellMenu(Player p, Product pr) {
		this.p = p;
		this.pr = pr;
		this.amount = pr.getDefaultAmount();
		this.inv = Bukkit.createInventory(this, 54, "Selling " + GetNames.itemName(pr.getNewItem(), false) + "...");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1);
	}
	
	public ShopProductSellMenu() {
		
	}
	
	public void fillInventory() {
		update();
		ItemStack cancel = new ItemStackBuilder(Material.STAINED_CLAY).name("&cCancel Selling").build();
		cancel.setDurability((short) 14); 
		inv.setItem(38, cancel);
		ItemStack setTo1 = new ItemStackBuilder(Material.STAINED_CLAY).name("&fSet amount to &d1").build();
		setTo1.setDurability((short) 14); 
		inv.setItem(18, setTo1);
		ItemStack minus10 = new ItemStackBuilder(Material.STAINED_CLAY).name("&fSubtract &d10 &ffrom the amount").build();
		minus10.setDurability((short) 14); 
		inv.setItem(19, minus10);
		ItemStack minus1 = new ItemStackBuilder(Material.STAINED_CLAY).name("&fSubtract &d1 &ffrom the amount").build();
		minus1.setDurability((short) 14); 
		inv.setItem(20, minus1);
		ItemStack add1 = new ItemStackBuilder(Material.STAINED_CLAY).name("&fAdd &d1 &fto the amount").build();
		add1.setDurability((short) 5); 
		inv.setItem(24, add1);
		ItemStack add10 = new ItemStackBuilder(Material.STAINED_CLAY).name("&fAdd &d10 &fto the amount").build();
		add10.setDurability((short) 5);
		inv.setItem(25, add10);
		ItemStack setTo64 = new ItemStackBuilder(Material.STAINED_CLAY).name("&fSet to &dmaximum amount").build();
		setTo64.setDurability((short) 5); 
		inv.setItem(26, setTo64);
		inv.setItem(49, new ItemStackBuilder(Material.ARROW).name("&dGo Back").build());
	}
	
	public void update() {
		ItemStack item = new ItemStackBuilder(pr.getMaterial())
				.name(ChatColor.WHITE + GetNames.itemName(pr.getNewItem(), false))
				.lore((pr.isBuyable() ? "\n&5» &fBuy price: &d" + Guild.balance(pr.getBuyPrice()) : "") + "\n&5» &fSell price: &d" + Guild.balance(pr.getSellPrice()))
				.build();
		item.setAmount(amount);
		item.setDurability(pr.getData());
		inv.setItem(13, item);
		ItemStack clay = new ItemStackBuilder(Material.STAINED_CLAY)
				.name("&dSelling items...")
				.lore("\n&5» &fCurrent amount: &d" + amount + "\n&5» &fCurrent price: &d" + Guild.balance(pr.getSellPrice() * amount) + "\n \n&dClick to proceed!")
				.build();
		clay.setDurability((short) 5);
		inv.setItem(42, clay);
	}
	
	public void addAmount(int am) {
		amount = amount + am;
		update();
	}
	
	public void removeAmount(int am) {
		amount = amount - am;
		update();
	}
	
	public void setAmount(int am) {
		amount = am;
		update();
	}
	
	public static int getAmountAvailable(Player p, Material mat) {
		int amount = 0;
		for(ItemStack i : p.getInventory().getContents()) {
			if(i != null && i.getType() == mat) {
				amount = amount + i.getAmount();
			}
		}
		return amount;
	}
	
	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof ShopProductSellMenu) {
			e.setCancelled(true);
			ShopProductSellMenu menu = (ShopProductSellMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				Player p = menu.p;
				int amount = menu.amount;
				if(e.getSlot() == 18) {
					menu.setAmount(1);
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				}
				else if(e.getSlot() == 19) {
					if(amount > 10) {
						menu.removeAmount(10);
					}
					else {
						menu.setAmount(1);
					}
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				}
				else if(e.getSlot() == 20) {
					if(amount > 1) {
						menu.removeAmount(1);
					}
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				}
				else if(e.getSlot() == 24) {
					if(amount < menu.pr.getMaterial().getMaxStackSize()) {
						menu.addAmount(1);
					}
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				}
				else if(e.getSlot() == 25) {
					if(amount < menu.pr.getMaterial().getMaxStackSize() - 9) {
						menu.addAmount(10);
					}
					else {
						menu.setAmount(menu.pr.getMaterial().getMaxStackSize());
					}
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				}
				else if(e.getSlot() == 26) {
					if(getAmountAvailable(p, menu.pr.getMaterial()) > 0) {
						menu.setAmount(getAmountAvailable(p, menu.pr.getMaterial()));
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou don't have any items of this kind to sell!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 38) {
					p.closeInventory();
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou just cancelled your selling process!"));
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
				else if(e.getSlot() == 42) {
					if(getAmountAvailable(p, menu.pr.getMaterial()) >= amount) {
						p.closeInventory();
						Guild.getByPlayer(p).getGuildPlayer(p).addBalance(amount * menu.pr.getSellPrice());
						p.getInventory().removeItem(new ItemStack(menu.pr.getMaterial(), amount));
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
								" &5&l» &fYou just sold &d" + amount + "x &fof &d" + GetNames.itemName(new ItemStack(menu.pr.getMaterial()), false) + " &ffor &d" + Guild.balance(amount * menu.pr.getSellPrice()) + "&f!"));
						p.playSound(p.getLocation(), Sound.NOTE_PLING, 2, 1);
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou don't have the required amount of items to sell!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 49) {
					menu.p.closeInventory();
					new ShopCategoryMenu(menu.p, menu.pr.getCategory());
				}
			}
		}
	}
	
}
