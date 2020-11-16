package net.colonymc.colonyskyblockcore.guilds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTItem;
import net.colonymc.colonyskyblockcore.Database;

public class GuildPlayer {
	
	final UUID u;
	final Guild g;
	double balance;
	double dust;
	Role r;
	final long joinTimeStamp;
	
	public GuildPlayer(UUID u, Guild g, Role r, long timeStamp, int silver, int dust) {
		this.u = u;
		this.g = g;
		this.r = r;
		this.joinTimeStamp = timeStamp;
		this.balance = silver;
		this.dust = dust;
	}
	
	public void addBalance(double amount) {
		if(amount > 0) {
			if(getPlayer().isOnline()) {
				Player p = (Player) getPlayer();
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have received &d" + Guild.balance(amount) + "&f!"));
			}
			setBalance(balance + amount);
		}
	}
	
	public void removeBalance(double amount) {
		if(amount > 0) {
			if(getPlayer().isOnline()) {
				Player p = (Player) getPlayer();
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have been taken &d" + Guild.balance(amount) + "&f!"));
			}
			setBalance(balance - amount);
		}
	}
	
	public void addDust(double amount) {
		if(amount > 0) {
			if(getPlayer().isOnline()) {
				Player p = (Player) getPlayer();
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have received &d" + Guild.balance(amount) + " of dwarf dust&f!"));
			}
			setDust(dust + amount);
		}
	}
	
	public void removeDust(double amount) {
		if(amount > 0) {
			if(getPlayer().isOnline()) {
				Player p = (Player) getPlayer();
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have been taken &d" + Guild.balance(amount) + " of dwarf dust&f!"));
			}
			setDust(dust - amount);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void kick() {
		if(this.getPlayer().isOnline()) {
			Player p = (Player) this.getPlayer();
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have been kicked from the guild &d" + g.getName() + "&f!"));
			p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 2, 1);
			p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&cKICKED FROM GUILD!"), "");
		}
		g.sendGuildMessage("&fThe player &d" + this.getPlayer().getName() + " &fhas been kicked from the guild!");
		g.sendGuildSound(Sound.ENDERDRAGON_GROWL, 1);
		g.removeMember(this.getPlayer());
		if(balance > 0) {
			g.addBalance(balance, this, true);
			setBalance(0);
		}
		if(dust > 0) {
			g.addDust(dust, this, true);
			setDust(0);
		}
		addItems();
	}
	
	public void leave() {
		if(this.getPlayer().isOnline()) {
			Player p = (Player) this.getPlayer();
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have left from the guild &d" + g.getName() + "&f!"));
			p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 2, 1);
		}
		g.sendGuildMessage("&fThe player &d" + this.getPlayer().getName() + " &fjust left from the guild!");
		g.sendGuildSound(Sound.ENDERDRAGON_GROWL, 1);
		g.removeMember(this.getPlayer());
		if(balance > 0) {
			g.addBalance(balance, this, true);
			setBalance(0);
		}
		if(dust > 0) {
			g.addDust(dust, this, true);
			setDust(0);
		}
		addItems();
	}
	
	public void demote() {
		if(this.getRole().ordinal() >= Role.values().length - 2) {
			this.setRole(Role.values()[this.getRole().ordinal() - 1]);
		}
		g.sendGuildMessage("&fThe player &d" + this.getPlayer().getName() + " &fhas been demoted to " + this.r.color + getRole().name + "&f!");
		g.sendGuildSound(Sound.VILLAGER_NO, 1);
	}
	
	public void promote() {
		if(this.getRole().ordinal() < Role.values().length - 2) {
			this.setRole(Role.values()[this.getRole().ordinal() + 1]);
		}
		g.sendGuildMessage("&fThe player &d" + this.getPlayer().getName() + " &fhas been promoted to " + this.r.color + getRole().name + "&f!");
		g.sendGuildSound(Sound.ORB_PICKUP, 1);
	}
	
	private void setRole(Role r) {
		Database.sendStatement("UPDATE PlayerInfo SET guildRank='" + r.name() + "' WHERE playerUuid='" + this.getPlayer().getUniqueId().toString() + "';");
		this.r = r;
	}
	
	public void setBalance(double amount) {
		this.balance = amount;
		Database.sendStatement("UPDATE PlayerInfo SET silver=" + amount + " WHERE playerUuid='" + this.getPlayer().getUniqueId().toString() + "';");
	}
	
	public void setDust(double amount) {
		this.dust = amount;
		Database.sendStatement("UPDATE PlayerInfo SET dwarfDust=" + amount + " WHERE playerUuid='" + this.getPlayer().getUniqueId().toString() + "';");
	}
	
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(u);
	}
	
	public Guild getGuild() {
		return g;
	}
	
	public UUID getUUID() {
		return u;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public double getDust() {
		return dust;
	}
	
	public Role getRole() {
		return r;
	}
	
	public long getJoinTimestamp() {
		return joinTimeStamp;
	}
	
	private void addItems() {
		ArrayList<ItemStack> items = new ArrayList<>();
		if(getPlayer().isOnline()) {
			Player p = getPlayer().getPlayer();
			for(ItemStack i : p.getInventory().getContents()) {
				if(i != null && i.getType() != Material.AIR) {
					items.add(i);
				}
			}
			for(ItemStack i : p.getInventory().getArmorContents()) {
				if(i != null && i.getType() != Material.AIR) {
					items.add(i);
				}
			}
			for(ItemStack i : p.getEnderChest().getContents()) {
				if(i != null && i.getType() != Material.AIR) {
					items.add(i);
				}
			}
			p.getEnderChest().clear();
			p.getInventory().clear();
			p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
		}
		else {
			try {
				NBTFile file = new NBTFile(new File(Bukkit.getWorld("hub").getWorldFolder() + "/playerdata", getPlayer().getUniqueId().toString() + ".dat"));
				for(int i = 0; i < file.getCompoundList("Inventory").size(); i++) {
					ItemStack item = NBTItem.convertNBTtoItem(file.getCompoundList("Inventory").get(i));
					items.add(item);
				}
				for(int i = 0; i < file.getCompoundList("EnderItems").size(); i++) {
					ItemStack item = NBTItem.convertNBTtoItem(file.getCompoundList("EnderItems").get(i));
					items.add(item);
				}
				file.setObject("Inventory", null);
				file.setObject("EnderItems", null);
				file.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!items.isEmpty()) {
			g.addUnclaimedItems(getPlayer().getUniqueId().toString(), items);
			int amount = 0;
			for(ItemStack i : items) {
				amount = amount + i.getAmount();
			}
			for(ItemStack i : g.getUnclaimedItems().get(getPlayer().getUniqueId().toString())) {
				amount = amount + i.getAmount();
			}
			g.sendGuildMessage("&fThere are now &d" + amount + " items &funclaimed from the inventory of &d" + getPlayer().getName() + "&f! Type &d/g unclaimed " + getPlayer().getName() + " &fto claim them!");
		}
	}
	
	public boolean canKick(Role r) {
		if(this.getRole() == Role.OWNER) return false;
		else return this.getRole().ordinal() < r.ordinal();
	}
	
	public boolean canPromote() {
		return this.getRole().ordinal() < Role.values().length - 2;
	}
	
	public boolean canDemote() {
		return this.getRole().ordinal() >= Role.values().length - 2 && this.getRole() != Role.OWNER;
	}
	

}
