package net.colonymc.colonyskyblockcore.guilds.inventories;

import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.*;
import net.colonymc.colonyspigotapi.itemstacks.ItemStackBuilder;
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

import java.util.concurrent.TimeUnit;

public class GuildSettingsMenu implements InventoryHolder, Listener{

	Player p;
	GuildPlayer gp;
	Guild g;
	Inventory inv;
	boolean back;
	
	public GuildSettingsMenu(Player p, boolean back) {
		this.p = p;
		this.gp = Guild.getByPlayer(p).getGuildPlayer(p);
		this.g = gp.getGuild();
		this.back = back;
		inv = Bukkit.createInventory(this, 45, "Your guild's settings");
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
			inv.setItem(40, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
		}
		if(gp.getRole() == Role.OWNER) {
			if(gp.getGuild().isOpen()) {
				inv.setItem(20, new ItemStackBuilder(Material.WOOD_DOOR).name("&dClose your guild")
						.lore("\n&fClosing your guild will ensure that\n&fthat all players have an invitation\n&ffrom the guild before they join!\n \n&dClick here to close your guild!")
						.build());
			}
			else {
				inv.setItem(20, new ItemStackBuilder(Material.IRON_DOOR).name("&dOpen your guild")
						.lore("\n&fOpening your guild will allow any player\n&fwithout an invitation to join!\n \n&dClick here to open your guild!")
						.build());
			}
			if(gp.getGuild().getRenamesRemaining() > 0) {
				inv.setItem(22, new ItemStackBuilder(Material.BOOK_AND_QUILL).name("&dRename guild")
						.lore("\n&fRename your guild to anything you want!\n&4WARNING: &fMake sure that the new name is appopriate\n \n&dClick here to rename your guild!")
						.build());
			}
			else {
				inv.setItem(22, new ItemStackBuilder(Material.BARRIER).name("&cNo renames available")
						.lore("\n&fYou have used all 3 available renames\n&fper season and therefore you cannot rename\n&fyour guild again!\n ")
						.build());
			}
			inv.setItem(24, new ItemStackBuilder(Material.REDSTONE_BLOCK).name("&cDISBAND GUILD")
					.lore("\n&fDisband your current guild. &cBE CAREFUL,"
							+ "\n&fthis will delete everything from your\n&fcurrent guild along with your whole island!\n \n&cClick here to DISBAND your guild!")
					.glint(true)
					.build());
		}
		else {
			inv.setItem(20, new ItemStackBuilder(Material.BOOK_AND_QUILL).name("&cYou cannot open/close the guild!")
					.build());
			inv.setItem(22, new ItemStackBuilder(Material.BARRIER).name("&cYou cannot rename the guild!")
					.build());
			inv.setItem(24, new ItemStackBuilder(Material.REDSTONE_BLOCK).name("&cLeave guild")
					.lore("\n&fLeave your current guild. Be careful,"
							+ "\n&fif your current guild is closed you will need\n&fan invitation to join back again!\n \n&dClick here to leave your guild!")
					.glint(true)
					.build());
		}
	}

	public GuildSettingsMenu() {
	}
	
	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof GuildSettingsMenu) {
			e.setCancelled(true);
			GuildSettingsMenu gm = (GuildSettingsMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 20) {
					if(gm.gp.getRole() == Role.OWNER) {
						gm.gp.getGuild().toggleOpen();
						gm.fillInventory(gm.back);
					}
					else {
						gm.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou must be the owner of the guild to open/close it!"));
						gm.p.playSound(gm.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 22) {
					if(gm.gp.getRole() == Role.OWNER) {
						if(gm.g.getRenamesRemaining() > 0) {
							if(Cooldown.hasCooldown(gm.p, CooldownType.RENAME) == null) {
								gm.p.closeInventory();
								Main.getSignGui().open(gm.p, new String[] {"", "^^^^^^^^^^^^^^^", "Enter a new name", "for your guild!"}, (player, lines) -> {
									String msg = lines[0].replaceAll("\"", "");
									if(Guild.isValidName(msg)) {
										if(!Guild.alreadyExists(msg)) {
											if(Guild.getByPlayer(player).getRenamesRemaining() > 1 && !player.hasPermission("*")) {
												new Cooldown(player.getUniqueId().toString(), CooldownType.RENAME);
											}
											Guild.getByPlayer(player).rename(msg);
										}
										else {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis guild already exists!"));
											player.playSound(player.getLocation(), Sound.NOTE_BASS, 2, 1);
										}
									}
									else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe name must be at least 4 characters long, smaller than 16 characters and must only contain alphanumeric characters!"));
										player.playSound(player.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
								});
							}
							else {
								Cooldown c = Cooldown.hasCooldown(gm.p, CooldownType.RENAME);
								gm.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot do that for another " + getDuration(c.getDuration() * 1000) + "!"));
							}
						}
						else {
							gm.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou don't have any renames remaining!"));
							gm.p.playSound(gm.p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
					else {
						gm.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou must be the owner of the guild to rename it!"));
						gm.p.playSound(gm.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 24) {
					if(gm.gp.getRole() == Role.OWNER) {
						if(Cooldown.hasCooldown(gm.p, CooldownType.DISBAND) == null) {
							if(!gm.p.hasPermission("*")) {
								new Cooldown(gm.p.getUniqueId().toString(), CooldownType.DISBAND);
							}
							gm.gp.getGuild().disband();
							gm.p.closeInventory();
						}
						else {
							Cooldown c = Cooldown.hasCooldown(gm.p, CooldownType.DISBAND);
							gm.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot do that for another " + getDuration(c.getDuration() * 1000) + "!"));
						}
					}
					else {
						gm.gp.leave();
						gm.p.closeInventory();
					}
				}
				else if(e.getSlot() == 40) {
					new GuildMainMenu(gm.p, null);
				}
			}
		}
	}
	
	private String getDuration(long duration) {
		String durationString = null;
		if(duration == -1) {
			durationString = "Never";
			return durationString;
		}
		if(TimeUnit.MILLISECONDS.toDays(duration) > 0) {
			durationString = String.format("%dd %dh %dm %ds",
					TimeUnit.MILLISECONDS.toDays(duration),
					TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)),
					TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
					TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
					);
			
		}
		if(TimeUnit.MILLISECONDS.toDays(duration) == 0) {
			durationString = String.format("%dh %dm %ds", 
					TimeUnit.MILLISECONDS.toHours(duration),
					TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
					TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
					);
		}
		if(TimeUnit.MILLISECONDS.toHours(duration) == 0) {
			durationString = String.format("%dm %ds", 
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
