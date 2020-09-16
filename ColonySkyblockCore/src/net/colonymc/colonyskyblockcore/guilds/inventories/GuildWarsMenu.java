package net.colonymc.colonyskyblockcore.guilds.inventories;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.war.EndedWar;
import net.colonymc.colonyskyblockcore.guilds.war.WarLootMenu;


public class GuildWarsMenu implements InventoryHolder, Listener {
	
	Inventory inv;
	Player p;
	int totalPages;
	int page = 0;
	BukkitTask update;
	HashMap<Integer, EndedWar> items = new HashMap<Integer, EndedWar>();
	
	public GuildWarsMenu(Player p) {
		this.p = p;
		this.inv = Bukkit.createInventory(this, 54, "Previous wars");
		fillInventory();
		startUpdating();
		openInventory();
	}
	
	public GuildWarsMenu() {
		
	}
	
	public void changePage(int amount) {
		page = page + amount;
		totalPages = (int) Math.ceil((double) Guild.getByPlayer(p).getEndedWars().size() / 45);
		items.clear();
		for(int i = 0; i < 45; i++) {
			int index = page * 45 + i;
			if(index < Guild.getByPlayer(p).getEndedWars().size()) {
				EndedWar e = Guild.getByPlayer(p).getEndedWars().get(index);
				if(!e.getLoot(Guild.getByPlayer(p)).isEmpty()) {
					items.put(i, e);
				}
				if(!e.wasDraw()) {
					Guild opponent = Guild.getByPlayer(p).equals(e.getOneGuild()) ? e.getAnotherGuild() : e.getOneGuild();
					if(e.getWinner() != null && e.getWinner().equals(Guild.getByPlayer(p))) {
						if(opponent != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							inv.setItem(i, new ItemStackBuilder(Material.STAINED_CLAY)
									.name("&aWar against " + opponent.getName())
									.lore("\n&fResult: &a&lVICTORY!\n \n&fWar type: &d" + e.getType().name + "\n&fStarted at: &d" + sdf.format(new Date(e.getTimeStarted()))
							 + "\n&fTime ended: &d" + sdf.format(new Date(e.getTimeEnded())) + "\n \n&fMoney Collected: &d" + Guild.balance(e.getSilverCollected(Guild.getByPlayer(p))) + "\n&fItems collected: &d" + e.getItemsCollected(Guild.getByPlayer(p)) + "\n&fTop Damager: &d" + 
									(e.getTopDamager().equals("None") ? "None" : Database.getName(e.getTopDamager()) + (e.getLoot(Guild.getByPlayer(p)).isEmpty() ? "\n \n&aFully Looted" : "\n \n&dClick here to loot it!")))
									.durability((short) 5)
									.build());
						}
						else {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							inv.setItem(i, new ItemStackBuilder(Material.STAINED_CLAY).name("&aWar against &c[GUILD DISBANDED]")
									.lore("\n&fResult: &a&lVICTORY!\n \n&fWar type: &d" + e.getType().name + "\n&fStarted at: &d" + sdf.format(new Date(e.getTimeStarted()))
							 + "\n&fTime ended: &d" + sdf.format(new Date(e.getTimeEnded())) + "\n \n&fMoney Collected: &d" + Guild.balance(e.getSilverCollected(Guild.getByPlayer(p))) + "\n&fItems collected: &d" + e.getItemsCollected(Guild.getByPlayer(p)) + "\n&fTop Damager: &d" + 
									(e.getTopDamager().equals("None") ? "None" : Database.getName(e.getTopDamager()) + (e.getLoot(Guild.getByPlayer(p)).isEmpty() ? "\n \n&aFully Looted" : "\n \n&dClick here to loot it!")))
									.durability((short) 5)
									.build());
						}
					}
					else {
						if(opponent != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							inv.setItem(i, new ItemStackBuilder(Material.STAINED_CLAY)
									.name("&cWar against " + opponent.getName())
									.lore("\n&fResult: &c&lDEFEAT\n \n&fWar type: &d" + e.getType().name + "\n&fStarted at: &d" + sdf.format(new Date(e.getTimeStarted()))
							 + "\n&fTime ended: &d" + sdf.format(new Date(e.getTimeEnded())) + "\n \n&fMoney Collected: &d0g\n&fItems collected: &d" + e.getItemsCollected(Guild.getByPlayer(p)) 
							 + (e.getLoot(Guild.getByPlayer(p)).isEmpty() ? "\n \n&aFully Looted" : "\n \n&dClick here to loot it!"))
									.durability((short) 14)
									.build());
						}
						else {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							inv.setItem(i, new ItemStackBuilder(Material.STAINED_CLAY)
									.name("&cWar against [GUILD DISBANDED]")
									.lore("\n&fResult: &c&lDEFEAT\n \n&fWar type: &d" + e.getType().name + "\n&fStarted at: &d" + sdf.format(new Date(e.getTimeStarted()))
							 + "\n&fTime ended: &d" + sdf.format(new Date(e.getTimeEnded())) + "\n \n&fMoney Collected: &d0g\n&fItems collected: &d" + e.getItemsCollected(Guild.getByPlayer(p)) 
							 + (e.getLoot(Guild.getByPlayer(p)).isEmpty() ? "\n \n&aFully Looted" : "\n \n&dClick here to loot it!"))
									.durability((short) 14)
									.build());
						}
					}
				}
				else {
					Guild opponent = Guild.getByPlayer(p).equals(e.getOneGuild()) ? e.getAnotherGuild() : e.getOneGuild();
					if(opponent != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						inv.setItem(i, new ItemStackBuilder(Material.STAINED_CLAY).name("&6War against " + opponent.getName())
								.lore("\n&fResult: &6&lDRAW\n \n&fWar type: &d" + e.getType().name + "\n&fStarted at: &d" + sdf.format(new Date(e.getTimeStarted()))
						 + "\n&fTime ended: &d" + sdf.format(new Date(e.getTimeEnded())) + "\n \n&fMoney Collected: &d0g\n&fItems collected: &d" + e.getItemsCollected(Guild.getByPlayer(p)) 
						 + (e.getLoot(Guild.getByPlayer(p)).isEmpty() ? "\n \n&aFully Looted" : "\n \n&dClick here to loot it!"))
								.durability((short) 4)
								.build());
					}
					else {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						inv.setItem(i, new ItemStackBuilder(Material.STAINED_CLAY).name("&6War against [GUILD DISBANDED]")
								.lore("\n&fResult: &6&lDRAW\n \n&fWar type: &d" + e.getType().name + "\n&fStarted at: &d" + sdf.format(new Date(e.getTimeStarted()))
						 + "\n&fTime ended: &d" + sdf.format(new Date(e.getTimeEnded())) + "\n \n&fMoney Collected: &d0g\n&fItems collected: &d" + e.getItemsCollected(Guild.getByPlayer(p)) 
						 + (e.getLoot(Guild.getByPlayer(p)).isEmpty() ? "\n \n&aFully Looted" : "\n \n&dClick here to loot it!"))
								.durability((short) 4)
								.build());
					}
				}
			}
			else {
				inv.setItem(i, new ItemStack(Material.AIR));
			}
		}
		if(page > 0) {
			inv.setItem(45, new ItemStackBuilder(Material.ARROW).name("&dPrevious Page").build());
		}
		else {
			inv.setItem(45, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
		if(totalPages > 1 && page < totalPages - 1) {
			inv.setItem(53, new ItemStackBuilder(Material.ARROW).name("&dNext Page").build());
		}
		else {
			inv.setItem(53, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
		inv.setItem(49, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
		p.updateInventory();
	}
	
	private void fillInventory() {
		for(int i = 45; i < 54; i++) {
			inv.setItem(i, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
	}
	
	private void startUpdating() {
		update = new BukkitRunnable() {
			@Override
			public void run() {
				changePage(0);
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}
	
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
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof GuildWarsMenu) {
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				GuildWarsMenu menu = (GuildWarsMenu) e.getInventory().getHolder();
				e.setCancelled(true);
				if(e.getSlot() == 45) {
					if(menu.page > 0) {
						menu.changePage(-1);
					}
				}
				else if(e.getSlot() == 53) {
					if(menu.page < menu.totalPages - 1) {
						menu.changePage(1);
					}
				}
				else if(e.getSlot() == 49) {
					menu.p.closeInventory();
					new GuildMainMenu(menu.p, null);
				}
				else if(menu.items.containsKey(e.getSlot())) {
					if(Guild.getByPlayer(menu.p).getGuildPlayer(menu.p).getRole().ordinal() > 0) {
						menu.p.closeInventory();
						new WarLootMenu(menu.p, menu.items.get(e.getSlot()));
					}
					else {
						menu.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou must be at least an Officer to do this!"));
						menu.p.playSound(menu.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof GuildWarsMenu) {
			GuildWarsMenu menu = (GuildWarsMenu) e.getInventory().getHolder();
			menu.update.cancel();
		}
	}

}
