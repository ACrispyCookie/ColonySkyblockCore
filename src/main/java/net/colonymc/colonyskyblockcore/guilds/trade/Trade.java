package net.colonymc.colonyskyblockcore.guilds.trade;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;
import net.colonymc.colonyspigotlib.lib.player.PlayerInventory;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.GuildPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Trade {
	
	final Trader requesterTrader;
	final Trader requestedTrader;
	TradeInventory requesterInv;
	TradeInventory requestedInv;
	boolean accepted;
	boolean isHappening = false;
	final BukkitRunnable expire;
	BukkitTask countdown;
	public static final ArrayList<Trade> activeTrades = new ArrayList<>();
	
	public Trade(GuildPlayer oneTrader, GuildPlayer requestedTrader) {
		this.requesterTrader = new Trader(oneTrader, this);
		this.requestedTrader = new Trader(requestedTrader, this);
		this.accepted = false;
		activeTrades.add(this);
		expire = new BukkitRunnable() {
			@Override
			public void run() {
				activeTrades.remove(Trade.this);
				OfflinePlayer requester = requesterTrader.getPlayer().getPlayer();
				OfflinePlayer requested = requestedTrader.getPlayer().getPlayer();
				if(requesterTrader.getPlayer().getPlayer().isOnline()) {
					Player onRequester = (Player) requesterTrader.getPlayer().getPlayer();
					onRequester.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour trade request to the player " + requested.getName() + " has expired!"));
					onRequester.playSound(onRequester.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
				if(requestedTrader.getPlayer().getPlayer().isOnline()) {
					Player onRequested = requestedTrader.getPlayer().getPlayer();
					onRequested.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour trade request from the player " + requester.getName() + " has expired!"));
					onRequested.playSound(onRequested.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
			}
		};
		expire.runTaskLater(Main.getInstance(), 1200);
		request();
	}
	
	public void request() {
		Player requester = (Player) requesterTrader.getPlayer().getPlayer();
		Player requested = (Player) requestedTrader.getPlayer().getPlayer();
		requester.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have requested from &d" + requested.getName() + " &fto trade! "
				+ "They have &d60 seconds &fto accept!"));
		requester.playSound(requester.getLocation(), Sound.ORB_PICKUP, 2, 1);
		TextComponent toSendOnRequested = new TextComponent(ChatColor.translateAlternateColorCodes('&', 
				" &5&l» &fThe player &d" + requester.getName() + " &fhas requested from you to trade! "));
		toSendOnRequested.addExtra(getMessage("&d&l[CLICK HERE]", "/trade " + requester.getName()));
		toSendOnRequested.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', " &fto accept. You have &d60 seconds &funtil it expires!")));
		requested.spigot().sendMessage(toSendOnRequested);
		requested.playSound(requested.getLocation(), Sound.ORB_PICKUP, 2, 1);
	}
	
	public void accept() {
		if(requesterTrader.getPlayer().getPlayer().isOnline()) {
			if(Trade.isInTrade(requesterTrader.getPlayer().getPlayer().getPlayer()) != null) {
				Player requested = (Player) requestedTrader.getPlayer().getPlayer();
				requested.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is currently trading with someone else!"));
				requested.playSound(requested.getLocation(), Sound.NOTE_BASS, 2, 1);
			}
			else {
				this.accepted = true;
				expire.cancel();
				if(!requesterTrader.getPlayer().getPlayer().isOnline() || !requestedTrader.getPlayer().getPlayer().isOnline()) {
					cancel();
				}
				else {
					Player requester = (Player) requesterTrader.getPlayer().getPlayer();
					Player requested = (Player) requestedTrader.getPlayer().getPlayer();
					requester.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThe player &d" + requested.getName() + " &fhas accepted the request!"));
					requester.playSound(requester.getLocation(), Sound.ORB_PICKUP, 2, 1);
					requested.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou accepted the trade request from &d" + requester.getName() + "&f!"));
					requested.playSound(requested.getLocation(), Sound.ORB_PICKUP, 2, 1);
					PlayerInventory.addItem(requester.getItemOnCursor(), requester, requester.getItemOnCursor().getAmount());
					requester.setItemOnCursor(null);
					PlayerInventory.addItem(requested.getItemOnCursor(), requested, requested.getItemOnCursor().getAmount());
					requested.setItemOnCursor(null);
					requesterInv = new TradeInventory(requesterTrader, requestedTrader);
					requestedInv = new TradeInventory(requestedTrader, requesterTrader);
				}
			}
		}
		else {
			Player requested = (Player) requestedTrader.getPlayer().getPlayer();
			requested.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online!"));
			requested.playSound(requested.getLocation(), Sound.NOTE_BASS, 2, 1);
		}
	}
	
	public void cancel() {
		if(isHappening) {
			countdown.cancel();
		}
		activeTrades.remove(this);
		if(requesterTrader.getPlayer().getPlayer().isOnline()) {
			Player requester = (Player) requesterTrader.getPlayer().getPlayer();
			requesterInv.cancelled = true;
			if(requesterInv.inactive != null) {
				requesterInv.inactive.cancel();
			}
			requester.closeInventory();
			PlayerInventory.addItems(requesterTrader.getItems(), requester);
			requesterTrader.getPlayer().getGuild().getGuildPlayer(requester).addBalance(requesterTrader.getSilver());
			requesterTrader.getPlayer().getGuild().getGuildPlayer(requester).addDust(requesterTrader.getDust());
			requester.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe trade has been cancelled!"));
			requester.playSound(requester.getLocation(), Sound.NOTE_BASS, 2, 1);
		}
		if(requestedTrader.getPlayer().getPlayer().isOnline()) {
			Player requested = (Player) requestedTrader.getPlayer().getPlayer();
			requestedInv.cancelled = true;
			if(requestedInv.inactive != null) {
				requestedInv.inactive.cancel();
			}
			requested.closeInventory();
			PlayerInventory.addItems(requestedTrader.getItems(), requested);
			requestedTrader.getPlayer().getGuild().getGuildPlayer(requested).addBalance(requestedTrader.getSilver());
			requestedTrader.getPlayer().getGuild().getGuildPlayer(requested).addDust(requestedTrader.getDust());
			requested.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe trade has been cancelled!"));
			requested.playSound(requested.getLocation(), Sound.NOTE_BASS, 2, 1);
		}
	}
	
	public void startCountdown() {
		if(requestedTrader.isReady() && requesterTrader.isReady()) {
			isHappening = true;
			BukkitRunnable runnable = new BukkitRunnable() {
				int i = 0;
				@Override
				public void run() {
					if(i == 0) {
						ItemStack ready = new ItemStackBuilder(Material.STAINED_CLAY).name("&aTrade will complete in 3").lore("\n&fClick here if you want to stop"
								+ "\n&fthe trade from completing!").durability((short) 5).build();
						requesterInv.inv.setItem(47, ready);
						requesterInv.inv.setItem(51, ready);
						requestedInv.inv.setItem(47, ready);
						requestedInv.inv.setItem(51, ready);
					}
					else if(i == 1) {
						ItemStack ready = new ItemStackBuilder(Material.STAINED_CLAY).name("&6Trade will complete in 3").lore("\n&fClick here if you want to stop"
								+ "\n&fthe trade from completing!").durability((short) 4).build();
						requesterInv.inv.setItem(47, ready);
						requesterInv.inv.setItem(51, ready);
						requestedInv.inv.setItem(47, ready);
						requestedInv.inv.setItem(51, ready);
					}
					else if(i == 2) {
						ItemStack ready = new ItemStackBuilder(Material.STAINED_CLAY).name("&cTrade will complete in 3").lore("\n&fClick here if you want to stop"
								+ "\n&fthe trade from completing!").durability((short) 14).build();
						requesterInv.inv.setItem(47, ready);
						requesterInv.inv.setItem(51, ready);
						requestedInv.inv.setItem(47, ready);
						requestedInv.inv.setItem(51, ready);
					}
					else if(i == 3) {
						complete();
					}
					i++;
				}
			};
			countdown = runnable.runTaskTimer(Main.getInstance(), 0, 20);
		}
	}
	
	public void stopCountdown() {
		isHappening = false;
		countdown.cancel();
	}
	
	public void complete() {
		activeTrades.remove(this);
		if(isHappening) {
			Player requester = (Player) requesterTrader.getPlayer().getPlayer();
			Player requested = (Player) requestedTrader.getPlayer().getPlayer();
			requesterInv.cancelled = true;
			requestedInv.cancelled = true;
			requesterInv.update.cancel();
			requestedInv.update.cancel();
			requester.closeInventory();
			requested.closeInventory();
			PlayerInventory.addItems(requesterTrader.getItems(), requested);
			PlayerInventory.addItems(requestedTrader.getItems(), requester);
			requesterTrader.getPlayer().addBalance(requestedTrader.getSilver());
			requesterTrader.getPlayer().addDust(requestedTrader.getDust());
			requestedTrader.getPlayer().addBalance(requesterTrader.getSilver());
			requestedTrader.getPlayer().addDust(requesterTrader.getDust());
			requester.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &aThe trade has been completed!"));
			requester.playSound(requester.getLocation(), Sound.LEVEL_UP, 2, 1);
			requested.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &aThe trade has been completed!"));
			requested.playSound(requested.getLocation(), Sound.LEVEL_UP, 2, 1);
		}
	}
	
	public static Trade hasBeenRequested(GuildPlayer requested, GuildPlayer requester) {
		for(Trade t : activeTrades) {
			if(t.requestedTrader.getPlayer().equals(requested) && t.requesterTrader.getPlayer().equals(requester)) {
				return t;
			}
		}
		return null;
	}
	
	public static Trade isInTrade(Player p) {
		for(Trade t : activeTrades) {
			if(t.requestedTrader.getPlayer().getPlayer().getPlayer().equals(p) || t.requesterTrader.getPlayer().getPlayer().getPlayer().equals(p)) {
				if(t.accepted) {
					return t;
				}
			}
		}
		return null;
	}
	
	public static ArrayList<Trade> getByPlayer(Player p) {
		ArrayList<Trade> trades = new ArrayList<>();
		for(Trade t : activeTrades) {
			if(t.requestedTrader.getPlayer().getPlayer().getPlayer().equals(p) || t.requesterTrader.getPlayer().getPlayer().getPlayer().equals(p)) {
				trades.add(t);
			}
		}
		return trades;
	}
	
	public TextComponent getMessage(String message, String command) {
		TextComponent t = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return t;
	}

}
