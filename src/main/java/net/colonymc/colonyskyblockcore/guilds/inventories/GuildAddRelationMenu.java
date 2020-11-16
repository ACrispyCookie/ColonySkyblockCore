package net.colonymc.colonyskyblockcore.guilds.inventories;

import java.util.ArrayList;
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

import net.colonymc.colonyspigotapi.itemstacks.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.GuildAllyRequest;
import net.colonymc.colonyskyblockcore.guilds.Relation;

public class GuildAddRelationMenu implements InventoryHolder, Listener {

	int page = 0;
	int totalPages;
	final HashMap<Integer, Guild> guildSlot = new HashMap<>();
	ArrayList<Guild> onlineGuilds;
	Relation toAdd;
	Inventory inv;
	Player p;
	Guild g;
	
	public GuildAddRelationMenu(Player p, Relation r) {
		this.p = p;
		this.g = Guild.getByPlayer(p);
		this.toAdd = r;
		inv = Bukkit.createInventory(this, 54, "Select a guild...");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}
	
	private void fillInventory() {
		inv.setItem(49, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
		onlineGuilds = getOnlineGuilds();
		totalPages = onlineGuilds.size() / 36;
		for(int i = 0; i < 36; i++) {
			if(onlineGuilds.size() - 1 >= i) {
				Guild g = onlineGuilds.get(i);
				ItemStack item = null;
				if(toAdd == Relation.ALLY) {
					if(GuildAllyRequest.isAlliedBy(this.g, g)) {
						item = new ItemStackBuilder(Material.DIAMOND_AXE)
								.name("&d" + g.getName() + " Guild")
								.lore("\n  &5» &fGuild's place: &d#" + g.getTopPlace() + 
								"\n  &5» &fTotal members: &d" + g.getMemberUuids().size() + "\n  &5» &fOnline members: &d" + g.getOnlineMembers().size() + 
								"\n \n&dClick here to accept the ally request from them!")
								.glint(true)
								.build();
					}
					else {
						item = new ItemStackBuilder(Material.DIAMOND_AXE)
								.name("&d" + g.getName() + " Guild")
								.lore("\n  &5» &fGuild's place: &d#" + g.getTopPlace() + 
										"\n  &5» &fTotal members: &d" + g.getMemberUuids().size() + "\n  &5» &fOnline members: &d" + g.getOnlineMembers().size() + 
										"\n \n&dClick here to request from them to become allies!")
								.glint(true)
								.build();
					}
				}
				else if(toAdd == Relation.ENEMY) {
					item = new ItemStackBuilder(Material.DIAMOND_AXE)
							.name("&d" + g.getName() + " Guild")
							.lore("\n  &5» &fGuild's place: &d" + g.getTopPlace() + 
									"\n  &5» &fTotal members: &d" + g.getMemberUuids().size() + "\n  &5» &fOnline members: &d" + g.getOnlineMembers().size() + 
									"\n \n&dClick here to become enemies with this guild!")
							.glint(true)
							.build();
				}
				inv.setItem(i, item);
				guildSlot.put(i, g);
			}
		}
		if(totalPages > 1 && page + 1 < totalPages) {
			inv.setItem(44, new ItemStackBuilder(Material.ARROW).name("&dNext Page").build());
		}
		else {
			inv.setItem(44, new ItemStack(Material.AIR));
		}
		if(page > 0) {
			inv.setItem(36, new ItemStackBuilder(Material.ARROW).name("&dPrevious Page").build());
		}
		else {
			inv.setItem(36, new ItemStack(Material.AIR));
		}
	}
	
	private void changePage(int amount) {
		page = page + amount;
		checkPages();
		for(int i = 0; i < 36; i++) {
			if(onlineGuilds.size() - 1 >= page * i) {
				Guild g = onlineGuilds.get(page * i);
				ItemStack item = null;
				if(toAdd == Relation.ALLY) {
					if(GuildAllyRequest.isAlliedBy(this.g, g)) {
						item = new ItemStackBuilder(Material.DIAMOND_AXE)
								.name("&d" + g.getName() + " Guild")
								.lore("\n  &5» &fGuild's place: &d#" + g.getTopPlace() + 
								"\n  &5» &fTotal members: &d" + g.getMemberUuids().size() + "\n  &5» &fOnline members: &d" + g.getOnlineMembers().size() + 
								"\n \n&dClick here to accept the ally request from them!")
								.glint(true)
								.build();
					}
					else {
						item = new ItemStackBuilder(Material.DIAMOND_AXE)
								.name("&d" + g.getName() + " Guild")
								.lore("\n  &5» &fGuild's place: &d#" + g.getTopPlace() + 
										"\n  &5» &fTotal members: &d" + g.getMemberUuids().size() + "\n  &5» &fOnline members: &d" + g.getOnlineMembers().size() + 
										"\n \n&dClick here to request from them to become allies!")
								.glint(true)
								.build();
					}
				}
				else if(toAdd == Relation.ENEMY) {
					item = new ItemStackBuilder(Material.DIAMOND_AXE)
							.name("&d" + g.getName() + " Guild")
							.lore("\n  &5» &fGuild's place: &d#" + g.getTopPlace() + 
									"\n  &5» &fTotal members: &d" + g.getMemberUuids().size() + "\n  &5» &fOnline members: &d" + g.getOnlineMembers().size() + 
									"\n \n&dClick here to become enemies with this guild!")
							.glint(true)
							.build();
				}
				inv.setItem(i, item);
				guildSlot.put(i, g);
			}
		}
	}
	
	private void checkPages() {
		onlineGuilds = getOnlineGuilds();
		totalPages = onlineGuilds.size() / 36;
		if(page < totalPages - 1) {
			inv.setItem(44, new ItemStackBuilder(Material.ARROW).name("&dNext Page").build());
		}
		else {
			inv.setItem(44, new ItemStack(Material.AIR));
		}
		if(page > 0) {
			inv.setItem(36, new ItemStackBuilder(Material.ARROW).name("&dPrevious Page").build());
		}
		else {
			inv.setItem(44, new ItemStack(Material.AIR));
		}
	}
	
	public ArrayList<Guild> getOnlineGuilds() {
		ArrayList<Guild> guilds = new ArrayList<>();
		for(Guild g : Guild.loadedGuilds) {
			if(!this.g.equals(g)) {
				if(this.g.getRelation(g) == Relation.NEUTRAL) {
					if(g.getOwner().getPlayer().isOnline()) {
						guilds.add(g);
					}
				}
			}
		}
		return guilds;
	}

	public GuildAddRelationMenu() {
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof GuildAddRelationMenu) {
			e.setCancelled(true);
			GuildAddRelationMenu gm = (GuildAddRelationMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 49) {
					new GuildRelationsMenu(gm.p, true);
				}
				else if(gm.totalPages > 1 && e.getSlot() == 44) {
					gm.changePage(1);
				}
				else if(gm.totalPages > 1 && gm.page > 0 && e.getSlot() == 36) {
					gm.changePage(-1);
				}
				else if (gm.guildSlot.containsKey(e.getSlot())) {
					gm.p.closeInventory();
					if(gm.toAdd == Relation.ALLY) {
						gm.g.ally(gm.guildSlot.get(e.getSlot()), gm.p);
					}
					else if(gm.toAdd == Relation.ENEMY) {
						gm.g.setRelation(gm.guildSlot.get(e.getSlot()), Relation.ENEMY, true);
					}
				}
			}
		}
	}

}
