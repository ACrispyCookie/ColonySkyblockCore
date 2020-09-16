package net.colonymc.colonyskyblockcore.guilds.inventories;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.BorderColor;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.Role;

public class IslandSettingsMenu implements InventoryHolder, Listener {
	
	Inventory inv;
	Player p;
	Guild g;
	int borderColor = 22;
	int visitation = 20;
	
	public IslandSettingsMenu(Player p, boolean back) {
		g = Guild.getByPlayer(p);
		this.p = p;
		inv = Bukkit.createInventory(this, 45, "Your island's settings");
		fillInventory(back);
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}

	public IslandSettingsMenu() {
	}
	
	private void fillInventory(boolean back) {
		if(back) {
			inv.setItem(40, new ItemStackBuilder(Material.ARROW).name("&dBack").build());
		}
		if(g.getGuildPlayer(p).getRole() == Role.OWNER) {
			inv.setItem(borderColor, getBorderItem());
			inv.setItem(visitation, getVisitationItem());
		}
		else {
			inv.setItem(borderColor, new ItemStackBuilder(Material.BARRIER).name("&cYou cannot change the border color of your island!").build());
			inv.setItem(visitation, new ItemStackBuilder(Material.BARRIER).name("&cYou cannot change the visitation of your island!").build());
		}
	}

	private ItemStack getVisitationItem() {
		ItemStack item = new ItemStack(Material.INK_SACK);
		boolean visitation = this.g.getIsland().getVisitation();
		if(visitation) {
			item.setDurability((short) 10);
		}
		else {
			item.setDurability((short) 8);
		}
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dChange island visitation"));
		List<String> finalLore = new ArrayList<String>();
		finalLore.add(" ");
		finalLore.add(ChatColor.translateAlternateColorCodes('&', "&fChanges the visitation of"));
		finalLore.add(ChatColor.translateAlternateColorCodes('&', "&fyour island for other players!"));
		finalLore.add(" ");
		finalLore.add(ChatColor.translateAlternateColorCodes('&', "&fCurrent Visitation: " + getVisitation(visitation)));
		finalLore.add(" ");
		finalLore.add(ChatColor.translateAlternateColorCodes('&', "&dClick here to toggle your"));
		finalLore.add(ChatColor.translateAlternateColorCodes('&', "&dvisitation to " + getVisitation(!visitation) + "&d!"));
		meta.setLore(finalLore);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		item.setItemMeta(meta);
		return item;
	}
	
	public String getVisitation(boolean visitable) {
		if(visitable) {
			return ChatColor.translateAlternateColorCodes('&', "&aPublic");
		}
		else {
			return ChatColor.translateAlternateColorCodes('&', "&cPrivate");
		}
	}

	private ItemStack getBorderItem() {
		ItemStack item = new ItemStack(Material.WOOL);
		BorderColor c = this.g.getIsland().getBorderColor();
		if(c == BorderColor.GREEN) {
			item.setDurability((short) 5);
		}
		else if(c == BorderColor.RED) {
			item.setDurability((short) 14);
		}
		else {
			item.setDurability((short) 3);
		}
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dChange border color"));
		List<String> finalLore = new ArrayList<String>();
		finalLore.add(" ");
		finalLore.add(ChatColor.translateAlternateColorCodes('&', "&fChanges the color of the"));
		finalLore.add(ChatColor.translateAlternateColorCodes('&', "&fborder around your island!"));
		finalLore.add(" ");
		finalLore.add(ChatColor.translateAlternateColorCodes('&', "&fCurrent Color: " + g.getIsland().getBorderColor().c + g.getIsland().getBorderColor().name));
		if(BorderColor.values().length > c.ordinal() + 1) {
			finalLore.add(ChatColor.translateAlternateColorCodes('&', "&fNext Color: " + getNext(g).c + getNext(g).name));
		}
		else {
			finalLore.add(ChatColor.translateAlternateColorCodes('&', "&fNext Color: " + BorderColor.BLUE.c + BorderColor.BLUE.name));
		}
		finalLore.add(" ");
		finalLore.add(ChatColor.translateAlternateColorCodes('&', "&dClick here to cycle through the colors!"));
		meta.setLore(finalLore);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		item.setItemMeta(meta);
		return item;
	}
	
	public BorderColor getNext(Guild g) {
		if(g.getIsland().getBorderColor().ordinal() == 2) {
			return BorderColor.values()[0];
		}
		else {
			return BorderColor.values()[g.getIsland().getBorderColor().ordinal() + 1];
		}
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof IslandSettingsMenu) {
			e.setCancelled(true);
			IslandSettingsMenu is = (IslandSettingsMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == is.borderColor) {
					if(is.g.getGuildPlayer(is.p).getRole() == Role.OWNER) {
						is.g.getIsland().cycleBorder();
						is.getInventory().setItem(is.borderColor, is.getBorderItem());
					}
					else {
						is.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou must be the owner of the guild to change the border color of it!"));
						is.p.playSound(is.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == is.visitation) {
					if(is.g.getGuildPlayer(is.p).getRole() == Role.OWNER) {
						is.g.getIsland().toggleVisitation();
						is.inv.setItem(visitation, is.getVisitationItem());
					}
					else {
						is.p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou must be the owner of the guild to change the visitation of it!"));
						is.p.playSound(is.p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else if(e.getSlot() == 40) {
					new GuildMainMenu(is.p, null);
				}
			}
		}
	}
}
