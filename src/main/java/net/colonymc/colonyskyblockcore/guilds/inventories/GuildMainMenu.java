package net.colonymc.colonyskyblockcore.guilds.inventories;

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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;
import net.colonymc.colonyspigotlib.lib.itemstack.SkullItemBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.GuildPlayer;
import net.colonymc.colonyskyblockcore.guilds.Role;

public class GuildMainMenu implements InventoryHolder, Listener {
	
	Inventory inv;
	BukkitRunnable update;
	Guild g;
	boolean admin;
	Player p;
	
	public GuildMainMenu(Player p, Guild g) {
		if(g == null) {
			this.g = Guild.getByPlayer(p);
			this.admin = false;
		}
		else {
			this.g = g;
			this.admin = true;
		}
		this.p = p;
		inv = Bukkit.createInventory(this, 27, "Your guild");
		fillInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
		startUpdating();
	}
	
	public GuildMainMenu() {
		
	}
	
	public void startUpdating() {
		update = new BukkitRunnable() {
			@Override
			public void run() {
				ItemStack guildStats = new ItemStackBuilder(Material.GOLD_HELMET)
						.name("&dYour guild stats")
						.lore("\n&fView your total stats, balance"
						+ "\n&fand place on the leaderboards!\n \n &5» &fBalance: &d" + Guild.balance(g.getBalance()) + (p.hasPermission("*") ? "\n &5» &fDwarf Dust Balance: &d" + Guild.balance(g.getDust()) : "") +
						"\n &5» &fPower Level: &d" + g.getLevel() + "\n \n &5» &fTotal Members: &d" + g.getMemberUuids().size() 
						+ "\n &5» &fTotal Online: &d" + g.getOnlineMembers().size() + "\n \n &5» &fLeaderboards place: &d#" + g.getTopPlace() + "\n ")
						.glint(true)
						.build();
				inv.setItem(10, guildStats);
			}
		};
		update.runTaskTimerAsynchronously(Main.getInstance(), 0, 3);
	}
	
	private void fillInventory() {
		ItemStack guildStats = new ItemStackBuilder(Material.GOLD_HELMET).name("&dYour guild stats")
				.lore("\n&fView your total stats, balance"
				+ "\n&fand place on the leaderboards!\n \n &5» &fBalance: &d" + Guild.balance(g.getBalance()) + (p.hasPermission("*") ? "\n &5» &fDwarf Dust Balance: &d" + Guild.balance(g.getDust()) : "") + 
				"\n &5» &fPower Level: &d" + g.getLevel() + "\n \n &5» &fTotal Members: &d" + g.getMemberUuids().size() 
				+ "\n &5» &fTotal Online: &d" + g.getOnlineMembers().size() + "\n \n &5» &fLeaderboards place: &d#" + g.getTopPlace() + "\n ")
				.glint(true)
				.build();
		inv.setItem(10, guildStats);
		ItemStack members = new SkullItemBuilder().playerUuid(p.getUniqueId())
				.name("&dManage guild members")
				.lore("\n&fManage the members of your guild!\n"
				+ "&fKick, promote and demote members\n&fin this menu!\n \n&dClick here to open this menu!")
				.build();
		inv.setItem(12, members);
		if(!Guild.getByPlayer(p).getUnclaimedItems().isEmpty()) {
			ItemStack unclaimed = new ItemStackBuilder(Material.CHAINMAIL_CHESTPLATE)
					.name("&dClaim items from members")
					.lore("\n&fClick here to claim the items\n"
							+ "&ffrom members who have either left or been kicked!\n \n&d&l" + Guild.getByPlayer(p).getUnclaimedItems().size() + " members left to claim!\n \n&dClick here to open this menu!")
					.glint(true)
					.build();
			inv.setItem(21, unclaimed);
		}
		ItemStack invite = new ItemStackBuilder(Material.GOLD_AXE)
				.name("&dManage guild relations")
				.lore("\n&fManage your guild's relations with other guilds!"
						+ "\n&fDeclare other guilds as &dallies, &4enemies &for\n&fdeclare a war on another guild!" + 
						"\n \n&dClick here to open this menu!")
				.glint(true)
				.addFlag(ItemFlag.HIDE_ATTRIBUTES)
				.build();
		inv.setItem(14, invite);
		ItemStack settings = new ItemStackBuilder(Material.REDSTONE_COMPARATOR)
				.name("&dGuild settings")
				.lore("\n&fView your guild's settings and edit"
						+ "\n&fthem to your preferings.\n \n&dClick here to open the settings menu!")
				.glint(true)
				.build();
		inv.setItem(16, settings);
		ItemStack isSettings = new ItemStackBuilder(Material.GRASS)
				.name("&dIsland settings")
				.lore("\n&fView your island's settings and edit"
				+ "\n&fthem to your preferings.\n \n&dClick here to open the settings menu!")
				.glint(true)
				.build();
		inv.setItem(25, isSettings);
		ItemStack wars = new ItemStackBuilder(Material.DIAMOND_SWORD).name("&dReview previous wars")
				.lore("\n&fCheck the statistics of any"
						+ "\n&fprevious war against other guilds!" + 
						"\n \n&dClick here to open this menu!")
				.glint(true)
				.addFlag(ItemFlag.HIDE_ATTRIBUTES)
				.build();
		inv.setItem(23, wars);
	}
	
	public GuildPlayer getLatestMember() {
		GuildPlayer latest = null;
		for(GuildPlayer gp : g.getMemberUuids().values()) {
			if(latest == null || latest.getJoinTimestamp() < gp.getJoinTimestamp()) {
				latest = gp;
			}
		}
		return latest;
	}

	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof GuildMainMenu) {
			e.setCancelled(true);
			Player p = ((GuildMainMenu) e.getInventory().getHolder()).p;
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 12) {
					new GuildMembersMenu(p, true);
				}
				else if(e.getSlot() == 14) {
					if(Guild.getByPlayer(p).getGuildPlayer(p).getRole() == Role.OWNER) {
						new GuildRelationsMenu(p, true);
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou must be the owner of the guild to change its relations!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 16) {
					new GuildSettingsMenu(p, true);
				}
				else if(e.getSlot() == 21) {
					if(!Guild.getByPlayer(p).getUnclaimedItems().isEmpty()) {
						if(Guild.getByPlayer(p).getGuildPlayer(p).getRole().ordinal() > 0) {
							new GuildSelectLootMenu(p);
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou must be at least an Officer to do this!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
				}
				else if(e.getSlot() == 23) {
					if(!Guild.getByPlayer(p).getEndedWars().isEmpty()) {
						new GuildWarsMenu(p);
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild hasn't participated in any wars yet!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 25) {
					new IslandSettingsMenu(p, true);
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof GuildMainMenu) {
			GuildMainMenu menu = (GuildMainMenu) e.getInventory().getHolder();
			menu.update.cancel();
		}
	}

}
