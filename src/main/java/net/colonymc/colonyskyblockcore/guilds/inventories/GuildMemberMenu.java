package net.colonymc.colonyskyblockcore.guilds.inventories;

import java.text.SimpleDateFormat;
import java.util.Date;

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

import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;
import net.colonymc.colonyspigotlib.lib.itemstack.SkullItemBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.GuildPlayer;
import net.colonymc.colonyskyblockcore.guilds.Role;

public class GuildMemberMenu implements InventoryHolder, Listener {
	
	Inventory inv;
	Player p;
	GuildPlayer gp;
	Guild g;
	boolean back;
	
	public GuildMemberMenu(Player p, GuildPlayer gp, boolean back) {
		this.p = p;
		this.gp = gp;
		this.g = Guild.getByPlayer(p);
		this.back = back;
		inv = Bukkit.createInventory(this, 45, "Manage " + gp.getPlayer().getName());
		fillInventory(back);
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}
	
	private void fillInventory(boolean back) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
		Role afterDemotion = null;
		Role afterPromotion = null;
		if(gp.canPromote()) {
			if(Guild.getByPlayer(p).getGuildPlayer(p).getRole().ordinal() > gp.getRole().ordinal()) {
				afterPromotion = Role.values()[gp.getRole().ordinal() + 1];
			}
		}
		if(gp.canDemote()) {
			if(Guild.getByPlayer(p).getGuildPlayer(p).getRole().ordinal() > gp.getRole().ordinal()) {
				afterDemotion = Role.values()[gp.getRole().ordinal() - 1];
			}
		}
		inv.setItem(13, new SkullItemBuilder().playerUuid(gp.getPlayer().getUniqueId())
				.name("&d" + gp.getPlayer().getName())
				.lore("\n  &5» &fGuild Rank: " + gp.getRole().color + gp.getRole().name + 
				"\n  &5» &fJoin Timestamp: &d" + sdf.format(new Date(gp.getJoinTimestamp())) + 
				"\n ")
				.build());
		if(gp.canKick(g.getGuildPlayer(p).getRole())) {
			inv.setItem(21, new ItemStackBuilder(Material.ANVIL).name("&cKick player")
					.lore("\n&fClick here to kick this player!\n&fAfter you kick a player, they will need\n&fan invitation to join back!\n ")
					.glint(true)
					.build());
		}
		else {
			inv.setItem(21, new ItemStackBuilder(Material.BARRIER).name("&cYou cannot kick this player!").build());
		}
		if(afterDemotion != null) {
			inv.setItem(22, new ItemStackBuilder(Material.COAL)
					.name("&cDemote player")
					.lore("\n&fClick here to demote this player!\n \n  &5» &fCurrent Rank: " + gp.getRole().color + gp.getRole().name + "\n  &5» &fAfter demotion: " + 
							afterDemotion.color + afterDemotion.name + "\n ")
					.glint(true)
					.build());
		}
		else {
			inv.setItem(22, new ItemStackBuilder(Material.BARRIER).name("&cYou cannot demote this player!").build());
		}
		if(afterPromotion != null) {
			inv.setItem(23, new ItemStackBuilder(Material.DIAMOND)
					.name("&aPromote player")
					.lore("\n&fClick here to demote this player!\n \n  &5» &fCurrent Rank: " + 
							gp.getRole().color + gp.getRole().name + "\n  &5» &fAfter promotion: " + afterPromotion.color + afterPromotion.name + "\n ")
					.glint(true)
					.build());
		}
		else {
			inv.setItem(23, new ItemStackBuilder(Material.BARRIER).name("&cYou cannot promote this player!").build());
		}
		if(back) {
			inv.setItem(40, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
		}
	}

	public GuildMemberMenu() {
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof GuildMemberMenu) {
			GuildMemberMenu gm = (GuildMemberMenu) e.getInventory().getHolder();
			e.setCancelled(true);
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 21) {
					if(!gm.gp.getPlayer().equals(gm.p)) {
						if(gm.gp.canKick(gm.g.getGuildPlayer(gm.p).getRole())) {
							gm.gp.kick();
							gm.p.closeInventory();
						}
						else {
							gm.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot kick this player from the guild!"));
							gm.p.playSound(gm.p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
					else {
						gm.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot kick yourself from the guild!"));
						gm.p.playSound(gm.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 22) {
					if(gm.gp.canDemote()) {
						gm.gp.demote();
						gm.fillInventory(gm.back);
					}
					else {
						gm.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot demote this player!"));
						gm.p.playSound(gm.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 23) {
					if(gm.gp.canPromote()) {
						gm.gp.promote();
						gm.fillInventory(gm.back);
					}
					else {
						gm.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot promote this player!"));
						gm.p.playSound(gm.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 40) {
					new GuildMembersMenu(gm.p, true);
				}
			}
		}
	}
}
