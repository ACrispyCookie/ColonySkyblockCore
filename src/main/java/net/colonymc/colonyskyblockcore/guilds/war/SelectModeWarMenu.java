package net.colonymc.colonyskyblockcore.guilds.war;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyspigotapi.itemstacks.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.Relation;

public class SelectModeWarMenu implements Listener, InventoryHolder {
	
	Inventory inv;
	Player p;
	Guild g;
	Guild anotherGuild;
	boolean shouldClose = false;
	BukkitTask cancel;
	
	public SelectModeWarMenu(Player p, Guild anotherGuild) {
		this.p = p;
		this.g = Guild.getByPlayer(p);
		this.anotherGuild = anotherGuild;
		this.inv = Bukkit.createInventory(this, 36, "Select a war type...");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
		cancel = new BukkitRunnable() {
			@Override
			public void run() {
				if(War.isRequested(g) != null) {
					shouldClose = true;
					p.closeInventory();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	
	public SelectModeWarMenu() {
	}
	
	private void fillInventory() {
		inv.setItem(31, new ItemStackBuilder(Material.BARRIER).name("&cCancel War").build());
		inv.setItem(11, new ItemStackBuilder(Material.DIAMOND_SWORD).name("&dNormal War")
				.lore("\n&fGamemode: &cTeam Deathmatch\n \n&fIn this type of war the last guild standing wins!\n "
						+ "\n&fKeep Inventory: &cDisabled\n&fDuration: &d20 minutes\n&fRewards:\n  &f- &d5% of the other guild's balance\n  &f- &d+ 10 power level\n  &f- &dThe loot of the enemies!")
				.build());

		inv.setItem(15, new ItemStackBuilder(Material.DIAMOND_SWORD).name("&dFriendly War")
				.lore("\n&fGamemode: &cTeam Deathmatch\n \n&fIn this type of war the last guild standing wins!\n "
						+ "\n&fKeep Inventory: &aEnabled\n&fDuration: &d10 minutes\n&fRewards:\n  &f- &7None!")
				.build());
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	public void openInventory() {
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
		cancel = new BukkitRunnable() {
			@Override
			public void run() {
				if(War.isRequested(g) != null) {
					shouldClose = true;
					p.closeInventory();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	
	public void closeInventory() {
		shouldClose = true;
		p.closeInventory();
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof SelectModeWarMenu) {
			e.setCancelled(true);
			SelectModeWarMenu menu = (SelectModeWarMenu) e.getInventory().getHolder();
			if(e.getSlot() == 11) {
				if(War.isRequested(menu.g) == null) {
					if(menu.anotherGuild.getRelation(menu.g) == Relation.ENEMY) {
						new War(menu.g, menu.anotherGuild, WarType.NORMAL);
						menu.closeInventory();
					}
					else {
						menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot declare a normal war against a neutral or ally guild! (Try a friendly one)"));
						menu.p.playSound(menu.p.getLocation(), Sound.BAT_DEATH, 2, 1);
					}
				}
			}
			else if(e.getSlot() == 15) {
				if(War.isRequested(menu.g) == null) {
					new War(menu.g, menu.anotherGuild, WarType.FRIENDLY);
					menu.closeInventory();
				}
			}
			else if(e.getSlot() == 31) {
				menu.closeInventory();
				menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe war against the guild " + menu.anotherGuild.getName() + " has been cancelled!"));
				menu.p.playSound(menu.p.getLocation(), Sound.BAT_DEATH, 2, 1);
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof SelectModeWarMenu) {
			SelectModeWarMenu menu = (SelectModeWarMenu) e.getInventory().getHolder();
			menu.cancel.cancel();
			if(!menu.shouldClose) {
				menu.openInventory();
			}
		}
	}

}
