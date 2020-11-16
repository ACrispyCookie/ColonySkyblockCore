package net.colonymc.colonyskyblockcore.kits;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyspigotapi.itemstacks.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;

public class ChooseCooldownMenu implements Listener, InventoryHolder {
	
	Player p;
	Inventory inv;
	BukkitTask cancel;
	boolean shouldClose = false;
	int seconds = 0;
	
	public ChooseCooldownMenu(Player p) {
		this.p = p;
		this.inv = Bukkit.createInventory(this, 27, "Select a cooldown...");
		fillInventory();
		openInventory();
	}

	public ChooseCooldownMenu() {
	}
	
	private void fillInventory() {
		inv.setItem(1, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&a+1 second").durability((short) 5).build());
		inv.setItem(2, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&a+1 minute").durability((short) 5).build());
		inv.setItem(3, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&a+1 hour").durability((short) 5).build());
		inv.setItem(4, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&a+1 day").durability((short) 5).build());
		inv.setItem(5, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&a+1 week").durability((short) 5).build());
		inv.setItem(6, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&a+1 month").durability((short) 5).build());
		inv.setItem(7, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&a+1 year").durability((short) 5).build());
		inv.setItem(19, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&c-1 second").durability((short) 14).build());
		inv.setItem(20, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&c-1 minute").durability((short) 14).build());
		inv.setItem(21, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&c-1 hour").durability((short) 14).build());
		inv.setItem(22, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&c-1 day").durability((short) 14).build());
		inv.setItem(23, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&c-1 week").durability((short) 14).build());
		inv.setItem(24, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&c-1 month").durability((short) 14).build());
		inv.setItem(25, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&c-1 year").durability((short) 14).build());
		inv.setItem(13, new ItemStackBuilder(Material.WATCH)
				.name("&fDuration: &d" + getDurationString(seconds))
				.lore("\n&fClick the buttons above and below to\n&fchange the cooldown of the kit!\n\n&dClick here &fto confirm the cooldown!")
				.build());
	}
	
	public void openInventory() {
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLaterAsynchronously(Main.getInstance(), 1);
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof ChooseCooldownMenu) {
			ChooseCooldownMenu menu = (ChooseCooldownMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				e.setCancelled(true);
				Player p = menu.p;
				int slot = e.getSlot();
				if(slot == 19) {
					if(menu.seconds - 1 >= 0) {
						menu.seconds--;
					}
					else {
						menu.seconds = 0;
					}
				}
				else if(slot == 20) {
					if(menu.seconds - 60 >= 0) {
						menu.seconds = menu.seconds - 60;
					}
					else {
						menu.seconds = 0;
					}
				}
				else if(slot == 21) {
					if(menu.seconds - 3600 >= 0) {
						menu.seconds = menu.seconds - 3600;
					}
					else {
						menu.seconds = 0;
					}
				}
				else if(slot == 22) {
					if(menu.seconds - 86400 >= 0) {
						menu.seconds = menu.seconds - 86400;
					}
					else {
						menu.seconds = 0;
					}
				}
				else if(slot == 23) {
					if(menu.seconds - 604800 >= 0) {
						menu.seconds = menu.seconds - 604800;
					}
					else {
						menu.seconds = 0;
					}
				}
				else if(slot == 24) {
					if(menu.seconds - 2592000 >= 0) {
						menu.seconds = menu.seconds - 2592000;
					}
					else {
						menu.seconds = 0;
					}
				}
				else if(slot == 25) {
					if(menu.seconds - 31536000 >= 0) {
						menu.seconds = menu.seconds - 31536000;
					}
					else {
						menu.seconds = 0;
					}
				}
				else if(slot == 1) {
					if(menu.seconds + 1 <= 63072000) {
						menu.seconds++;
					}
					else if(menu.seconds == 63072000) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe maximum cooldown for a kit is 2 years!"));
					}
					else {
						menu.seconds = 63072000;
					}
				}
				else if(slot == 2) {
					if(menu.seconds + 60 <= 63072000) {
						menu.seconds = menu.seconds + 60;
					}
					else if(menu.seconds == 63072000) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe maximum cooldown for a kit is 2 years!"));
					}
					else {
						menu.seconds = 63072000;
					}
				}
				else if(slot == 3) {
					if(menu.seconds + 3600 <= 63072000) {
						menu.seconds = menu.seconds + 3600;
					}
					else if(menu.seconds == 63072000) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe maximum cooldown for a kit is 2 years!"));
					}
					else {
						menu.seconds = 63072000;
					}
				}
				else if(slot == 4) {
					if(menu.seconds + 86400 <= 63072000) {
						menu.seconds = menu.seconds + 86400;
					}
					else if(menu.seconds == 63072000) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe maximum cooldown for a kit is 2 years!"));
					}
					else {
						menu.seconds = 63072000;
					}
				}
				else if(slot == 5) {
					if(menu.seconds + 604800 <= 63072000) {
						menu.seconds = menu.seconds + 604800;
					}
					else if(menu.seconds == 63072000) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe maximum cooldown for a kit is 2 years!"));
					}
					else {
						menu.seconds = 63072000;
					}
				}
				else if(slot == 6) {
					if(menu.seconds + 2592000 <= 63072000) {
						menu.seconds = menu.seconds + 2592000;
					}
					else if(menu.seconds == 63072000) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe maximum cooldown for a kit is 2 years!"));
					}
					else {
						menu.seconds = 63072000;
					}
				}
				else if(slot == 7) {
					if(menu.seconds + 31536000 <= 63072000) {
						menu.seconds = menu.seconds + 31536000;
					}
					else if(menu.seconds == 63072000) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe maximum cooldown for a kit is 2 years!"));
					}
					else {
						menu.seconds = 63072000;
					}
				}
				else if(e.getSlot() == 13) {
					KitCreator.getByPlayer(p).setCooldown(menu.seconds);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fYou have set the cooldown of the kit to &d" + getDurationString(menu.seconds) + "&f!"));
					KitCreator.getByPlayer(p).stage(true);
					menu.shouldClose = true;
					p.closeInventory();
				}
				menu.inv.setItem(13, new ItemStackBuilder(Material.WATCH)
						.name("&fDuration: &d" + getDurationString(menu.seconds))
						.lore("\n&fClick the buttons above and below to\n&fchange the cooldown of the kit!\n\n&dClick here &fto confirm the cooldown!")
						.build());
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof ChooseCooldownMenu) {
			ChooseCooldownMenu menu = (ChooseCooldownMenu) e.getInventory().getHolder();
			if(!menu.shouldClose) {
				menu.openInventory();
			}
		}
	}
	
	private String getDurationString(int diff) {
		String formatted = "";
		int years = diff/31536000;
		diff = diff - years * 31536000;
		int months = diff/2592000;
		diff = diff - months * 2592000;
		int weeks = diff/604800;
		diff = diff - weeks * 604800;
		int days = diff / 86400;
		diff = diff - days * 86400;
		int hours = diff / 3600;
		diff = diff - hours * 3600;
		int minutes = diff / 60;
		diff = diff - minutes * 60;
		int seconds = diff;
		if(years >= 1) {
			formatted = formatted + years + "y ";
		}
		if(months >= 1) {
			formatted = formatted + months + "mo ";
		}
		if(weeks >= 1) {
			formatted = formatted + weeks + "w ";
		}
		if(days >= 1) {
			formatted = formatted + days + "d ";
		}
		if(hours >= 1) {
			formatted = formatted + hours + "h ";
		}
		if(minutes >= 1) {
			formatted = formatted + minutes + "m ";
		}
		if(seconds >= 1) {
			formatted = formatted + seconds + "s";
		}
		if(formatted.isEmpty()) {
			formatted = "0s";
		}
		else {
			if(formatted.endsWith(" ")) {
				formatted = formatted.substring(0, formatted.length() - 1);
			}
		}
		return formatted;
	}
}
