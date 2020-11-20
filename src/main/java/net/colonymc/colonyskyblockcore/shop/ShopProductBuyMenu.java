package net.colonymc.colonyskyblockcore.shop;

import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyspigotapi.api.itemstack.ItemStackBuilder;
import net.colonymc.colonyspigotapi.api.player.PlayerInventory;
import net.colonymc.colonyspigotapi.api.itemstack.MaterialName;
import net.colonymc.colonyspigotapi.api.primitive.Numbers;
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

public class ShopProductBuyMenu implements InventoryHolder, Listener {

	Inventory inv;
	Player p;
	Product pr;
	CommandProduct cmdPr;
	int amount;
	
	public ShopProductBuyMenu(Player p, Product pr) {
		this.p = p;
		this.pr = pr;
		this.amount = pr.getDefaultAmount();
		this.inv = Bukkit.createInventory(this, 54, "Buying " + MaterialName.itemName(pr.getNewItem(), false) + "...");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1);
	}
	
	public ShopProductBuyMenu(Player p, CommandProduct pr) {
		this.p = p;
		this.cmdPr = pr;
		this.amount = pr.getDefaultAmount();
		this.inv = Bukkit.createInventory(this, 54, "Buying " + pr.getDisplayItem().getItemMeta().getDisplayName() + "...");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1);
	}
	
	public ShopProductBuyMenu() {
		
	}
	
	public void fillInventory() {
		update();
		ItemStack cancel = new ItemStackBuilder(Material.STAINED_CLAY).name("&cCancel Purchase").build();
		cancel.setDurability((short) 14); 
		inv.setItem(38, cancel);
		ItemStack minus1 = new ItemStackBuilder(Material.STAINED_CLAY).name("&fSubtract &d1 &ffrom the total").build();
		minus1.setDurability((short) 14); 
		inv.setItem(18, minus1);
		ItemStack minus10 = new ItemStackBuilder(Material.STAINED_CLAY).name("&fSubtract &d10 &ffrom the total").build();
		minus10.setDurability((short) 14); 
		inv.setItem(19, minus10);
		ItemStack setTo1 = new ItemStackBuilder(Material.STAINED_CLAY).name("&fSet amount to &d1").build();
		setTo1.setDurability((short) 14); 
		inv.setItem(20, setTo1);
		ItemStack add1 = new ItemStackBuilder(Material.STAINED_CLAY).name("&fAdd &d1 &fto the total").build();
		add1.setDurability((short) 5); 
		inv.setItem(24, add1);
		ItemStack add10 = new ItemStackBuilder(Material.STAINED_CLAY).name("&fAdd &d10 &fto the amount").build();
		add10.setDurability((short) 5);
		inv.setItem(25, add10);
		ItemStack setTo64 = new ItemStackBuilder(Material.STAINED_CLAY).name("&fSet amount to &dmax stack size").build();
		setTo64.setDurability((short) 5); 
		inv.setItem(26, setTo64);
		ItemStack customAmount = new ItemStackBuilder(Material.STAINED_CLAY).name("&dCustom amount").lore("\n&fClick here to select\n&fa custom total amount!").build();
		customAmount.setDurability((short) 5); 
		inv.setItem(35, customAmount);
		inv.setItem(49, new ItemStackBuilder(Material.ARROW).name("&dGo Back").build());
	}
	
	public void update() {
		if(pr != null) {
			ItemStack item = new ItemStackBuilder(pr.getMaterial())
					.name(ChatColor.WHITE + MaterialName.itemName(pr.getNewItem(), false))
					.lore("\n&5» &fBuy price: &d" + Guild.balance(pr.getBuyPrice()) + (pr.isSellable() ? "\n&5» &fSell price: &d" + Guild.balance(pr.getSellPrice()) : ""))
					.build();
			item.setAmount(amount > pr.getMaterial().getMaxStackSize() ? pr.getMaterial().getMaxStackSize() : amount);
			item.setDurability(pr.getData());
			inv.setItem(13, item);
			ItemStack clay = new ItemStackBuilder(Material.STAINED_CLAY).name("&dBuying items...").lore("\n&5» &fCurrent amount: &d" + amount + "\n&5» &fCurrent price: &d" + Guild.balance(pr.getBuyPrice() * amount) + "\n \n&dClick to proceed!").build();
			clay.setDurability((short) 5);
			inv.setItem(42, clay);
		}
		else {
			ItemStack item = new ItemStackBuilder(cmdPr.getDisplayItem().getType())
					.name(cmdPr.getDisplayItem().getItemMeta().getDisplayName())
					.lore("\n&5» &fBuy price: &d" + Guild.balance(cmdPr.getBuyPrice()))
					.build();
			item.setAmount(amount > cmdPr.getDisplayItem().getMaxStackSize() ? cmdPr.getDisplayItem().getMaxStackSize() : amount);
			item.setDurability(cmdPr.getDisplayItem().getDurability());
			inv.setItem(13, item);
			ItemStack clay = new ItemStackBuilder(Material.STAINED_CLAY).name("&dBuying items...").lore("\n&5» &fCurrent amount: &d" + amount + "\n&5» &fCurrent price: &d" + Guild.balance(cmdPr.getBuyPrice() * amount) + "\n \n&dClick to proceed!").build();
			clay.setDurability((short) 5);
			inv.setItem(42, clay);
		}
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
	
	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof ShopProductBuyMenu) {
			e.setCancelled(true);
			ShopProductBuyMenu menu = (ShopProductBuyMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				Player p = menu.p;
				int amount = menu.amount;
				if(e.getSlot() == 18) {
					if(amount > 1) {
						menu.removeAmount(1);
					}
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
					menu.setAmount(1);
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				}
				else if(e.getSlot() == 24) {
					if(menu.pr != null) {
						if(amount < menu.pr.getMaterial().getMaxStackSize()) {
							menu.addAmount(1);
						}
					}
					else {
						if(amount < menu.cmdPr.getDisplayItem().getMaxStackSize()) {
							menu.addAmount(1);
						}
					}
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				}
				else if(e.getSlot() == 25) {
					if(menu.pr != null) {
						if(amount < menu.pr.getMaterial().getMaxStackSize() - 9) {
							menu.addAmount(10);
						}
						else {
							menu.setAmount(menu.pr.getMaterial().getMaxStackSize());
						}
					}
					else {
						if(amount < menu.cmdPr.getDisplayItem().getMaxStackSize() - 9) {
							menu.addAmount(10);
						}
						else {
							menu.setAmount(menu.cmdPr.getDisplayItem().getMaxStackSize());
						}
					}
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				}
				else if(e.getSlot() == 26) {
					if(menu.pr != null) {
						menu.setAmount(menu.pr.getMaterial().getMaxStackSize());
					}
					else {
						menu.setAmount(menu.cmdPr.getDisplayItem().getMaxStackSize());
					}
					p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				}
				else if(e.getSlot() == 38) {
					p.closeInventory();
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou just cancelled your purchase!"));
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
				else if(e.getSlot() == 42) {
					if(menu.pr != null) {
						if(Guild.getByPlayer(p).getGuildPlayer(p).getBalance() >= amount * menu.pr.getBuyPrice()) {
							p.closeInventory();
							Guild.getByPlayer(p).getGuildPlayer(p).removeBalance(amount * menu.pr.getBuyPrice());
							PlayerInventory.addItem(new ItemStack(menu.pr.getMaterial()), p, amount);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
									" &5&l» &fYou just bought &d" + amount + "x &fof &d" + MaterialName.itemName(new ItemStack(menu.pr.getMaterial()), false) + " &ffor &d" + Guild.balance(amount * menu.pr.getBuyPrice()) + "&f!"));
							p.playSound(p.getLocation(), Sound.NOTE_PLING, 2, 1);
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford to make this purchase!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
					else {
						if(Guild.getByPlayer(p).getGuildPlayer(p).getBalance() >= amount * menu.cmdPr.getBuyPrice()) {
							p.closeInventory();
							Guild.getByPlayer(p).getGuildPlayer(p).removeBalance(amount * menu.cmdPr.getBuyPrice());
							if(menu.cmdPr.getCommand().contains("%amount%")) {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), menu.cmdPr.getCommand().replaceAll("%player%", p.getName()).replaceAll("%amount%", String.valueOf(amount)));
							}
							else {
								for(int i = 0; i < amount; i++) {
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), menu.cmdPr.getCommand().replaceAll("%player%", p.getName()));
								}
							}
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
									" &5&l» &fYou just bought &d" + amount + "x &fof &d" + menu.cmdPr.getDisplayItem().getItemMeta().getDisplayName() + " &ffor &d" + Guild.balance(amount * menu.cmdPr.getBuyPrice()) + "&f!"));
							p.playSound(p.getLocation(), Sound.NOTE_PLING, 2, 1);
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford to make this purchase!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
				}
				else if(e.getSlot() == 35) {
					Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^^", "Enter the amount", "you want to buy!"}, (player, lines) -> {
						if(Numbers.isInt(lines[0].replaceAll("\"", ""))) {
							int newAmount = Integer.parseInt(lines[0].replaceAll("\"", ""));
							menu.setAmount(newAmount);
							p.openInventory(menu.inv);
						}
						else {
							p.openInventory(menu.inv);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					});
				}
				else if(e.getSlot() == 49) {
					menu.p.closeInventory();
					if(menu.pr != null) {
						new ShopCategoryMenu(menu.p, menu.pr.getCategory());
					}
					else {
						new ShopCategoryMenu(menu.p, menu.cmdPr.getCategory());
					}
				}
			}
		}
	}

}
