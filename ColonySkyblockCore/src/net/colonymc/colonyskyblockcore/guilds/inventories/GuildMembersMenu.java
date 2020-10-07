package net.colonymc.colonyskyblockcore.guilds.inventories;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import net.colonymc.api.itemstacks.SkullItemBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.GuildPlayer;
import net.colonymc.colonyskyblockcore.guilds.GuildPlayerComparator;

public class GuildMembersMenu implements InventoryHolder, Listener {

	Inventory inv;
	Player p;
	Guild g;
	HashMap<Integer, GuildPlayer> slots = new HashMap<Integer, GuildPlayer>();
	
	public GuildMembersMenu(Player p, boolean back) {
		this.p = p;
		this.g = Guild.getByPlayer(p);
		inv = Bukkit.createInventory(this, 45, "Your guild's members");
		fillInventory(back);
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}
	
	public GuildMembersMenu() {
		
	}
	
	private void fillInventory(boolean back) {
		ArrayList<GuildPlayer> members = new ArrayList<GuildPlayer>();
		members.addAll(g.getMemberUuids().values());
		Collections.sort(members, new GuildPlayerComparator());
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
		for(int i = 0; i < members.size(); i++) {
			GuildPlayer p = members.get(i);
			ItemStack member = new SkullItemBuilder().playerUuid(p.getPlayer().getUniqueId()).name("&d" + p.getPlayer().getName()).lore(
					"\n  &5» &fGuild Rank: " + p.getRole().color + p.getRole().name + 
					"\n  &5» &fJoin Timestamp: &d" + sdf.format(new Date(p.getJoinTimestamp())) + 
					"\n \n&dClick to manage " + p.getPlayer().getName())
					.build();
			inv.setItem(10 + i, member);
			slots.put(10 + i, p);
		}
		if(back) {
			inv.setItem(40, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
		}
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof GuildMembersMenu) {
			e.setCancelled(true);
			GuildMembersMenu menu = (GuildMembersMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(menu.slots.containsKey(e.getSlot())) {
					new GuildMemberMenu((Player) e.getWhoClicked(), menu.slots.get(e.getSlot()), true);
				}
				else if(e.getSlot() == 40) {
					new GuildMainMenu((Player) e.getWhoClicked(), null);
				}
			}
		}
	}
	
}
