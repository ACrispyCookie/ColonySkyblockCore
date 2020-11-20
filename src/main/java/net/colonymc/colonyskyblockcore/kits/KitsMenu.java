package net.colonymc.colonyskyblockcore.kits;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyspigotapi.api.itemstack.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.SkyblockPlayer;

public class KitsMenu implements Listener, InventoryHolder {

	Inventory inv;
	Player p;
	BukkitTask update;
	final HashMap<Integer, Kit> kits = new HashMap<>();
	
	public KitsMenu(Player p) {
		this.p = p;
		this.inv = Bukkit.createInventory(this, 36, ChatColor.translateAlternateColorCodes('&', "&d&lKITS"));
		update = new BukkitRunnable() {
			@Override
			public void run() {
				fillInventory();
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
		openInventory();
	}
	
	public KitsMenu() {
		
	}
	
	private void fillInventory() {
		int i = 0;
		for(Kit kit : Kit.getKits()) {
			inv.setItem(i, getKitItem(kit));
			kits.put(i, kit);
			i++;
		}
	}
	
	private void openInventory() {
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(getInventory());
			}
		}.runTaskLater(Main.getInstance(), 1);
	}
	
	private ItemStack getKitItem(Kit kit) {
		ItemStackBuilder builder = new ItemStackBuilder(kit.getDisplayMat());
		builder.name("&d" + kit.getName());
		builder.lore(getLore(kit));
		return builder.build();
	}
	
	private String getLore(Kit kit) {
		SkyblockPlayer sbp = SkyblockPlayer.getByPlayer(p);
		String lore = "\n&fAvailable: ";
		if(p.hasPermission(kit.getPermission())) {
			if(sbp.getKits().containsKey(kit) && sbp.getKits().get(kit) <= System.currentTimeMillis()) {
				lore = lore + "&a✔";
			}
			else if(sbp.getKits().containsKey(kit) && sbp.getKits().get(kit) > System.currentTimeMillis()) {
				lore = lore + "&dOn cooldown!";
			}
			else {
				lore = lore + "&a✔";
			}
		}
		else {
			lore = lore + "&cX";
		}
		lore = lore + "\n&fCooldown: ";
		if(!p.hasPermission(kit.getPermission())) {
			lore = lore + "&cX";
		}
		else {
			if(sbp.getKits().containsKey(kit) && sbp.getKits().get(kit) <= System.currentTimeMillis()) {
				lore = lore + "&aReady!";
			}
			else if(sbp.getKits().containsKey(kit) && sbp.getKits().get(kit) > System.currentTimeMillis()) {
				lore = lore + "&d" + getCooldownString(sbp, kit);
			}
			else {
				lore = lore + "&aReady!";
			}
		}
		lore = lore + "\n \n";
		if(p.hasPermission(kit.getPermission())) {
			if(sbp.getKits().containsKey(kit) && sbp.getKits().get(kit) <= System.currentTimeMillis()) {
				lore = lore + "&dClick to claim this kit!";
			}
			else if(sbp.getKits().containsKey(kit) && sbp.getKits().get(kit) > System.currentTimeMillis()) {
				lore = lore + "&cYou must wait " + getCooldownString(sbp, kit) + " before \n&cclaiming this kit again!";
			}
			else {
				lore = lore + "&dClick to claim this kit!";
			}
		}
		else {
			lore = lore + "&cYou cannot claim this kit!";
		}
		lore = lore + "\n&dRight-click to preview this kit!";
		return lore;
	}
	
	private String getCooldownString(SkyblockPlayer p, Kit kit) {
		long diff = (p.getKits().get(kit) - System.currentTimeMillis()) / 1000;
		String formatted = "";
		long years = diff/31536000;
		diff = diff - years * 31536000;
		long months = diff/2592000;
		diff = diff - months * 2592000;
		long weeks = diff/604800;
		diff = diff - weeks * 604800;
		long days = diff / 86400;
		diff = diff - days * 86400;
		long hours = diff / 3600;
		diff = diff - hours * 3600;
		long minutes = diff / 60;
		diff = diff - minutes * 60;
		long seconds = diff;
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
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof KitsMenu) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			KitsMenu menu = (KitsMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(menu.kits.containsKey(e.getSlot())) {
					Kit kit = menu.kits.get(e.getSlot());
					SkyblockPlayer sbp = SkyblockPlayer.getByPlayer(p);
					if(e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
						p.closeInventory();
						new PreviewMenu(p, kit);
					}
					else {
						if(p.hasPermission(kit.getPermission())) {
							if(sbp.getKits().containsKey(kit) && sbp.getKits().get(kit) <= System.currentTimeMillis()) {
								kit.claim(p);
							}
							else if(!sbp.getKits().containsKey(kit)) {
								kit.claim(p);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof KitsMenu) {
			KitsMenu menu = (KitsMenu) e.getInventory().getHolder();
			menu.update.cancel();
		}
	}
	
}
