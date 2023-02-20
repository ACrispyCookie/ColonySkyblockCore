package net.colonymc.colonyskyblockcore.minions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;
import net.colonymc.colonyspigotlib.lib.player.PlayerInventory;
import net.colonymc.colonyspigotlib.lib.primitive.RomanNumber;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionMenuUtils;
import net.colonymc.colonyskyblockcore.minions.fuel.Fuel;
import net.colonymc.colonyskyblockcore.minions.fuel.FuelChecker;

public class MinionMenu implements Listener, InventoryHolder {

	Player p;
	MinionBlock b;
	Inventory inv;
	BukkitTask update;
	final HashMap<Integer, ItemStack> slots = new HashMap<>();
	
	public MinionMenu(Player p, MinionBlock b) {
		this.p = p;
		this.b = b;
		this.inv = Bukkit.createInventory(this, 54, b.getMinion().getMaterial().className + " Minion (" + RomanNumber.toRoman(b.getMinion().getLevel()) + ")");
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
				fillInventory();
				p.updateInventory();
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	
	public MinionMenu() {
		
	}
	
	private void fillInventory() {
		AuctionMenuUtils.fillGlasses(this);
		inv.setItem(16, new ItemStackBuilder(Material.NETHER_STAR).name("&dPickup items").lore("\n&fClick here to pick all the items\n&ffrom the minion's inventory!").build());
		inv.setItem(34, new ItemStackBuilder(Material.BARRIER).name("&cDestroy Minion").lore("\n&fClick here to destroy the\n&fminion and pick it up!").build());
		if(b.getFuel() != null) {
			ItemStack item = new ItemStackBuilder(b.getFuel().getType().mat)
					.name(b.getFuel().getName())
					.lore("\n&fBoost percentage: &d" + b.getFuel().getPercentage() + "%\n&fDuration left: &d" + getDurationString(b.getFuel().getTimeLeft() * 1000) + "\n \n" + 
					"&dClick to get the fuel back!")
					.glint(true)
					.build();
			item.setAmount(b.getFuel().getItem().getAmount());
			inv.setItem(25, item);
		}
		else {
			inv.setItem(25, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&cNo fuel added").lore("\n&fClick an item from your inventory\n&fto add it as a fuel!").build());
		}
		ArrayList<ItemStack> items = new ArrayList<>();
		for(ItemStack item : b.getItems().keySet()) {
			int amount = b.getItems().get(item);
			int stacksToAdd = (int) Math.floor((double) amount/64);
			int leftToAdd = amount - stacksToAdd * 64;
			for(int i = 0; i < stacksToAdd; i++) {
				ItemStack toAdd = item.clone();
				toAdd.setAmount(64);
				items.add(toAdd);
			}
			if(leftToAdd > 0) {
				ItemStack toAdd = item.clone();
				toAdd.setAmount(leftToAdd);
				items.add(toAdd);
			}
		}
		for(int i = 0; i < 20; i++) {
			int line = (int) (Math.ceil((i + 1)/5.0) - 1);
			int row = i - line * 5 + 1;
			int slot = 9 + line * 9 + row;
			if(i < b.getMinion().getSlots()) {
				if(i < items.size()) {
					inv.setItem(slot, items.get(i));
					slots.put(slot, items.get(i));
				}
				else {
					inv.setItem(slot, new ItemStack(Material.AIR));
				}
			}
			else {
				inv.setItem(slot, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name("&cSlot locked").lore("\n&fUpgrade the minion to\n&funlock more inventory slots!").durability((short) 14).build());
			}
		}
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof MinionMenu) {
			e.setCancelled(true);
			MinionMenu menu = (MinionMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(menu.slots.containsKey(e.getSlot())) {
					menu.b.removeItem(menu.slots.get(e.getSlot()), menu.slots.get(e.getSlot()).getAmount(), menu.p);
					menu.p.playSound(menu.p.getLocation(), Sound.ORB_PICKUP, 2, 2);
				}
				else if(e.getSlot() == 16) {
					if(!menu.b.items.isEmpty()) {
						ArrayList<ItemStack> items = new ArrayList<>(menu.b.items.keySet());
						for(int i = 0; i < 20; i++) {
							if(i < items.size()) {
								menu.b.removeItem(items.get(i), menu.b.items.get(items.get(i)), menu.p);
							}
						}
						menu.p.closeInventory();
						menu.p.playSound(menu.p.getLocation(), Sound.ORB_PICKUP, 2, 2);
					}
					else {
						menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThere are no items to pickup!"));
					}
				}
				else if(e.getSlot() == 34) {
					menu.b.breakMinion(menu.p);
					menu.p.closeInventory();
				}
				else if(e.getSlot() == 25) {
					if(menu.b.getFuel() != null) {
						if(menu.b.getFuel().getTimeLeft() > 1) {
							PlayerInventory.addItems(menu.b.getFuel().getItemsToDrop(), menu.p);
						}
						menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou removed the fuel from your " + menu.b.getMinion().getName() + "&f!"));
						menu.p.playSound(menu.p.getLocation(), Sound.VILLAGER_HIT, 2, 2);
						menu.b.setFuel(null, true);
					}
				}
			}
			else {
				if(FuelChecker.isFuel(e.getCurrentItem())) {
					if(menu.b.getFuel() == null) {
						Fuel f = FuelChecker.getFuel(e.getCurrentItem());
						menu.b.setFuel(f, true);
						e.setCurrentItem(null);
						menu.p.playSound(menu.p.getLocation(), Sound.ORB_PICKUP, 2, 2);
						menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have added &d" + f.getItem().getAmount() + "x " + f.getName() + " &fto your " + menu.b.getMinion().getName() + "&f!"));
					}
					else {
						menu.p.playSound(menu.p.getLocation(), Sound.VILLAGER_NO, 2, 1);
						menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThis minion already has &d" + menu.b.getFuel().getItem().getAmount() + "x " + menu.b.getFuel().getName() + " &fas a fuel!"));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof MinionMenu) {
			MinionMenu menu = (MinionMenu) e.getInventory().getHolder();
			menu.update.cancel();
		}
	}
	
	private String getDurationString(long duration) {
		String durationString = null;
		if(duration == -1) {
			durationString = "Never";
			return durationString;
		}
		if(TimeUnit.MILLISECONDS.toDays(duration) > 0) {
			durationString = String.format("%dd, %dh, %dm, %ds",
					TimeUnit.MILLISECONDS.toDays(duration),
					TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)),
					TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
					TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
					);
			
		}
		if(TimeUnit.MILLISECONDS.toDays(duration) == 0) {
			durationString = String.format("%dh, %dm, %ds", 
					TimeUnit.MILLISECONDS.toHours(duration),
					TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
					TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
					);
		}
		if(TimeUnit.MILLISECONDS.toHours(duration) == 0) {
			durationString = String.format("%dm, %ds", 
					TimeUnit.MILLISECONDS.toMinutes(duration),
					TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
					);
		}
		if(TimeUnit.MILLISECONDS.toMinutes(duration) == 0) {
			durationString = String.format("%ds", 
					TimeUnit.MILLISECONDS.toSeconds(duration)
					);
		}
		return durationString;
	}

}
