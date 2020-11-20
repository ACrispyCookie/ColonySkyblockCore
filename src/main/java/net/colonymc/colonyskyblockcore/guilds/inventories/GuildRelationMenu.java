package net.colonymc.colonyskyblockcore.guilds.inventories;

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
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyspigotapi.api.itemstack.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.GuildAllyRequest;
import net.colonymc.colonyskyblockcore.guilds.Relation;

public class GuildRelationMenu implements InventoryHolder, Listener {
	
	Player p;
	Guild editing;
	Inventory inv;
	
	public GuildRelationMenu(Player p, Guild toEdit) {
		this.p = p;
		this.editing = toEdit;
		this.inv = Bukkit.createInventory(this, 27, "Editing " + toEdit.getName() + "...");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}
	
	public GuildRelationMenu() {
	}
	
	public void fillInventory() {
		if(Guild.getByPlayer(p).getRelation(editing) == Relation.ALLY) {
			inv.setItem(12, new ItemStackBuilder(Material.INK_SACK).name("&cDeclare as enemy").lore("\n&fClick here to declare &d" + editing.getName() + " &fas\n&fan &cenemy.").glint(true).durability((short) 1).build());
			inv.setItem(13, new ItemStackBuilder(Material.BARRIER).name("&cYou are already allies with this guild!").build());
		}
		else if(Guild.getByPlayer(p).getRelation(editing) == Relation.ENEMY) {
			inv.setItem(12, new ItemStackBuilder(Material.BARRIER).name("&cYou are already enemies with this guild!").build());
			if(GuildAllyRequest.isAlliedBy(Guild.getByPlayer(p), editing)) {
				inv.setItem(13, new ItemStackBuilder(Material.INK_SACK).name("&dDeclare as ally")
						.lore("\n&fClick here to accept the request \n&ffrom &d" + editing.getName() + " &fand become &dallies.")
						.durability((short) 13)
						.glint(true)
						.build());
			}
			else {
				inv.setItem(13, new ItemStackBuilder(Material.INK_SACK).name("&dDeclare as ally")
						.lore("\n&fClick here to request from &d" + editing.getName() + " &fto\n&fbecome &dallies.")
						.durability((short) 13)
						.glint(true)
						.build());
			}
		}
		inv.setItem(14, new ItemStackBuilder(Material.INK_SACK).name("&fDeclare as neutral").lore("\n&fClick here to declare your relationship\n&fwith &d" + editing.getName() + " &fas neutral.").durability((short) 7).glint(true).build());
		inv.setItem(22, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
	}
	
	public Inventory getInventory() {
		return inv;
	}

	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof GuildRelationMenu) {
			e.setCancelled(true);
			GuildRelationMenu gm = (GuildRelationMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 22) {
					new GuildRelationsMenu(gm.p, true);
				}
				else {
					Player p = gm.p;
					if(Guild.getByPlayer(gm.p).getRelation(gm.editing) == Relation.ENEMY) {
						if(e.getSlot() == 12) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou are already enemies with this guild!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
						else if(e.getSlot() == 13) {
							Guild.getByPlayer(p).ally(gm.editing, p);
							p.closeInventory();
						}
						else if(e.getSlot() == 14) {
							Guild.getByPlayer(p).setRelation(gm.editing, Relation.NEUTRAL, true);
							p.closeInventory();
						}
					}
					else if(Guild.getByPlayer(gm.p).getRelation(gm.editing) == Relation.ALLY) {
						if(e.getSlot() == 12) {
							Guild.getByPlayer(p).setRelation(gm.editing, Relation.ENEMY, true);
							p.closeInventory();
						}
						else if(e.getSlot() == 13) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou are already allies with this guild!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
						else if(e.getSlot() == 14) {
							Guild.getByPlayer(p).setRelation(gm.editing, Relation.NEUTRAL, true);
							p.closeInventory();
						}
					}
				}
			}
		}
	}
}
