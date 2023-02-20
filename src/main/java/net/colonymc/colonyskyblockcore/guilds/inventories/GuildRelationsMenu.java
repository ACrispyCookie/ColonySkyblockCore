package net.colonymc.colonyskyblockcore.guilds.inventories;

import java.util.ArrayList;
import java.util.HashMap;

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

import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.Relation;

public class GuildRelationsMenu implements InventoryHolder, Listener {
	
	Player p;
	Guild g;
	Inventory inv;
	int totalPages;
	int page;
	ArrayList<Guild> guildsRelated;
	final HashMap<Integer, Guild> guildSlot = new HashMap<>();
	
	public GuildRelationsMenu(Player p, boolean back) {
		this.p = p;
		this.g = Guild.getByPlayer(p);
		this.page = 1;
		this.guildsRelated = new ArrayList<>();
		guildsRelated.addAll(g.getRelations().keySet());
		inv = Bukkit.createInventory(this, 54, "Your guild's relations");
		fillInventory(back);
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}
	
	private void fillInventory(boolean back) {
		if(back) {
			inv.setItem(49, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
		}
		inv.setItem(39, new ItemStackBuilder(Material.INK_SACK).name("&dAdd new ally").durability((short) 13).glint(true).build());
		inv.setItem(41, new ItemStackBuilder(Material.INK_SACK).name("&cAdd new enemy").durability((short) 1).glint(true).build());
		totalPages = guildsRelated.size() / 14;
		for(int i = 0; i < 16; i++) {
			if(guildsRelated.size() - 1 >= i) {
				Guild g1 = guildsRelated.get(i);
				ItemStack guild = null;
				if(g.getRelation(g1) == Relation.ENEMY) {
					guild = new ItemStackBuilder(Material.INK_SACK).name("&c" + g1.getName())
							.lore("\n  &5» &fGuild place: &d#" + g1.getTopPlace() + 
							"\n  &5» &fTotal members: &d" + g1.getMemberUuids().size() + "\n  &5» &fTotal online: &d" + g1.getOnlineMembers().size() + 
							"\n \n&dClick to manage your relation with this guild!")
							.glint(true)
							.durability((short) 1)
							.build();
				}
				else if(g.getRelation(g1) == Relation.ALLY) {
					guild = new ItemStackBuilder(Material.INK_SACK).name("&d" + g1.getName())
							.lore("\n  &5» &fGuild place: &d#" + g1.getTopPlace() + 
									"\n  &5» &fTotal members: &d" + g1.getMemberUuids().size() + "\n  &5» &fTotal online: &d" + g1.getOnlineMembers().size() + 
									"\n \n&dClick to manage your relation with this guild!")
							.glint(true)
							.durability((short) 13)
							.build();
				}
				inv.setItem(i, guild);
				guildSlot.put(i, g1);
			}
			else {
				break;
			}
		}
		if(totalPages > 1) {
			inv.setItem(44, new ItemStackBuilder(Material.ARROW).name("&dNext Page").build());
		}
	}
	
	private void changePage(int amount) {
		page = page + amount;
		checkPages();
		for(int i = 0; i < 36; i++) {
			if(guildsRelated.size() - 1 >= page * i) {
				Guild g1 = guildsRelated.get(page * i);
				ItemStack item = null;
				if(g.getRelation(g1) == Relation.ALLY) {
					item = new ItemStackBuilder(Material.DIAMOND_AXE).name("&d" + g1.getName() + " Guild")
							.lore("\n  &5» &fGuild's place: &d" + g1.getTopPlace() + 
									"\n  &5» &fTotal members: &d" + g1.getMemberUuids().size() + "\n  &5» &fOnline members: &d" + g1.getOnlineMembers().size() + 
									"\n \n&dClick here to request from them to become allies!")
							.glint(true)
							.build();
				}
				else if(g.getRelation(g1) == Relation.ENEMY) {
					item = new ItemStackBuilder(Material.DIAMOND_AXE).name("&d" + g1.getName() + " Guild")
							.lore("\n  &5» &fGuild's place: &d" + g1.getTopPlace() + 
									"\n  &5» &fTotal members: &d" + g1.getMemberUuids().size() + "\n  &5» &fOnline members: &d" + g1.getOnlineMembers().size() + 
									"\n \n&dClick here to become enemies with this guild!")
							.glint(true)
							.build();
				}
				inv.setItem(i, item);
				guildSlot.put(i, g1);
			}
		}
	}
	
	private void checkPages() {
		guildsRelated.clear();
		guildsRelated.addAll(g.getRelations().keySet());
		totalPages = (int) Math.ceil((double) guildsRelated.size() / 36);
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

	public GuildRelationsMenu() {
		
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
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof GuildRelationsMenu) {
			e.setCancelled(true);
			GuildRelationsMenu gm = (GuildRelationsMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 49) {
					new GuildMainMenu(gm.p, null);
				}
				else if(e.getSlot() == 39) {
					if(gm.getOnlineGuilds().size() > 0) {
						new GuildAddRelationMenu(gm.p, Relation.ALLY);
					}
					else {
						gm.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThere are no available guilds right now!"));
						gm.p.playSound(gm.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 41) {
					if(gm.getOnlineGuilds().size() > 0) {
						new GuildAddRelationMenu(gm.p, Relation.ENEMY);
					}
					else {
						gm.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThere are no available guilds right now!"));
						gm.p.playSound(gm.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(gm.totalPages > 1 && e.getSlot() == 44) {
					gm.changePage(1);
				}
				else if(gm.totalPages > 1 && gm.page > 0 && e.getSlot() == 36) {
					gm.changePage(-1);
				}
				else if(gm.guildSlot.containsKey(e.getSlot())) {
					if(gm.guildSlot.get(e.getSlot()).getOwner().getPlayer().isOnline()) {
						new GuildRelationMenu(gm.p, gm.guildSlot.get(e.getSlot()));
					}
					else {
						gm.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe owner of this guild is not currently online!"));
						gm.p.playSound(gm.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
			}
		}
	}

}
