package net.colonymc.colonyskyblockcore.guilds.war;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.api.itemstacks.SkullItemBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.GuildPlayer;
import net.colonymc.colonyskyblockcore.guilds.inventories.InventoryUtils;

public class ConfirmWarMenu implements Listener, InventoryHolder {

	Player p;
	Inventory inv;
	Guild g;
	Guild anotherGuild;
	War w;
	BukkitTask update;
	
	public ConfirmWarMenu(Player p, War w) {
		this.p = p;
		this.g = Guild.getByPlayer(p);
		if(w.getRequester().equals(g)) {
			this.anotherGuild = w.getRequested();
		}
		else {
			this.anotherGuild = w.getRequester();
		}
		this.w = w;
		this.inv = Bukkit.createInventory(this, 45, "Guild war against " + anotherGuild.getName());
		update = update().runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
		fillInventory();
		openInventory(p);
	}
	
	public ConfirmWarMenu() {
	}
	
	public void openInventory(Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLaterAsynchronously(Main.getInstance(), 1);
	}
	
	private void fillInventory() {
		addOwnMembers();
		addOtherMembers();
		for(int i = 9; i < 45; i += 9) {
			inv.setItem(i + 4, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
		if(w.getAccepted().contains(Guild.getByPlayer(p).getGuildPlayer(p))) {
			inv.setItem(4, new ItemStackBuilder(Material.EMERALD_BLOCK).name("&aYou are ready!")
					.lore("\n&fYou are ready for the war!\n&fClick here to unready!\n&fTime left: &d" + InventoryUtils.getDurationString(w.getTimeLeft() * 50))
					.glint(true)
					.build());
		}
		else {
			inv.setItem(4, new ItemStackBuilder(Material.REDSTONE_BLOCK).name("&cYou are not ready!")
					.lore("\n&fYou are not ready for the war!\n&fClick here to ready up!\n&fYou have &d" + InventoryUtils.getDurationString(w.getTimeLeft() * 50) + " &funtil the request expires!")
					.glint(true)
					.build());
		}
	}
	
	public BukkitRunnable update() {
		return new BukkitRunnable() {
			@Override
			public void run() {
				fillInventory();
				if(w.timeLeft == 0 || w.accepted) {
					p.closeInventory();
				}
			}
		};
	}
	
	private void addOtherMembers() {
		ArrayList<GuildPlayer> guildPlayers = new ArrayList<GuildPlayer>();
		guildPlayers.addAll(anotherGuild.getMemberUuids().values());
		for(int i = 0; i < 20; i++) {
			if(i < guildPlayers.size()) {
				int row = i / 4;
				int column = (i - (row * 4)) + 5;
				int slot = 9 * row + column;
				inv.setItem(slot, new SkullItemBuilder().playerName(guildPlayers.get(i).getPlayer().getName()).name("&d" + guildPlayers.get(i).getPlayer().getName())
						.lore("&fStatus: " + ((w.getAccepted().contains(guildPlayers.get(i))) ? "&aReady" : "&cNot Ready"))
						.build());
			}
			else {
				int row = i / 4;
				int column = (i - (row * 4)) + 5;
				int slot = 9 * row + column;
				inv.setItem(slot, new ItemStack(Material.AIR));
			}
		}
		
	}

	private void addOwnMembers() {
		ArrayList<GuildPlayer> guildPlayers = new ArrayList<GuildPlayer>();
		guildPlayers.addAll(g.getMemberUuids().values());
		for(int i = 0; i < 20; i++) {
			if(i < guildPlayers.size()) {
				int row = i / 4;
				int column = i - (row * 4);
				int slot = 9 * row + column;
				inv.setItem(slot, new SkullItemBuilder().playerName(guildPlayers.get(i).getPlayer().getName()).name("&d" + guildPlayers.get(i).getPlayer().getName())
						.lore("&fStatus: " + ((w.getAccepted().contains(guildPlayers.get(i))) ? "&aReady" : "&cNot Ready"))
						.build());
			}
			else {
				int row = i / 4;
				int column = i - (row * 4);
				int slot = 9 * row + column;
				inv.setItem(slot, new ItemStack(Material.AIR));
			}
		}
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof ConfirmWarMenu) {
			e.setCancelled(true);
			ConfirmWarMenu menu = (ConfirmWarMenu) e.getInventory().getHolder();
			if(e.getSlot() == 4) {
				menu.w.toggleReady(menu.p);
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof ConfirmWarMenu) {
			ConfirmWarMenu menu = (ConfirmWarMenu) e.getInventory().getHolder();
			menu.update.cancel();
		}
	}

}
