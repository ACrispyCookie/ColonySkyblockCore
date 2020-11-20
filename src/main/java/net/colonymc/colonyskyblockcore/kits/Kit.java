package net.colonymc.colonyskyblockcore.kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyspigotapi.api.player.PlayerInventory;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.SkyblockPlayer;

public class Kit {
	
	static final ArrayList<Kit> loadedKits = new ArrayList<>();
	ArrayList<ItemStack> items = new ArrayList<>();
	HashMap<String, ItemStack> cmds = new HashMap<>();
	final String permissionRequired;
	final long cooldown;
	final String name;
	final Material displayItem;
	
	public Kit(ArrayList<ItemStack> items, HashMap<String, ItemStack> cmds, String perm, long coold, Material display, String name) {
		this.items = items;
		this.cmds = cmds;
		permissionRequired = perm;
		cooldown = coold;
		displayItem = display;
		this.name = name;
		loadedKits.add(this);
	}
	
	public Kit(List<ItemStack> items, HashMap<String, ItemStack> cmds, String perm, long coold, Material display, String name) {
		this.items.addAll(items);
		this.cmds = cmds;
		permissionRequired = perm;
		cooldown = coold;
		displayItem = display;
		this.name = name;
		loadedKits.add(this);
	}
	
	public void claim(Player p) {
		if(!p.hasPermission("kit.bypassCooldown") && cooldown > 0) {
			if(SkyblockPlayer.getByPlayer(p).getKits().containsKey(this)) {
				Database.sendStatement("UPDATE PlayerKits SET canBeClaimedAgainAt=" + (System.currentTimeMillis() + cooldown * 1000) + " WHERE "
						+ "playerUuid='" + p.getUniqueId().toString() + "' AND kit='" + name + "'");
			}
			else {
				Database.sendStatement("INSERT INTO PlayerKits (playerUuid, kit, canBeClaimedAgainAt) VALUES "
						+ "('" + p.getUniqueId().toString() + "', '" + name + "', " + (System.currentTimeMillis() + cooldown * 1000) + ")");
			}
			SkyblockPlayer.getByPlayer(p).update();
		}
		PlayerInventory.addItems(items, p);
		for(String s : cmds.keySet()) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", p.getName()));
		}
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 2, 1);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have claimed the kit &d" + name + "&f!"));
	}
	
	public void delete() {
		loadedKits.remove(this);
		FileConfiguration file = Main.getInstance().getKitsConf();
		file.set("kits." + name, null);
		Database.sendStatement("DELETE FROM PlayerKits WHERE kit='" + name + "'");
		Main.getInstance().saveKitsConf(file);
	}
	
	public ArrayList<ItemStack> getItems() {
		return items;
	}
	
	public HashMap<String, ItemStack> getCommands() {
		return cmds;
	}

	public String getPermission() {
		return permissionRequired;
	}
	
	public long getCooldown() {
		return cooldown;
	}
	
	public Material getDisplayMat() {
		return displayItem;
	}
	
	public String getName() {
		return name;
	}
	
	public static ArrayList<Kit> getKits() {
		return loadedKits;
	}
	
	public static Kit getByName(String name) {
		for(Kit kit : loadedKits) {
			if(kit.name.equalsIgnoreCase(name)) {
				return kit;
			}
		}
		return null;
	}
}
