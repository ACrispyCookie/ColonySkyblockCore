package net.colonymc.colonyskyblockcore.guilds.war;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.colonymc.colonyspigotlib.lib.player.visuals.ScoreboardManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackSerializer;
import net.colonymc.colonyspigotlib.lib.player.visuals.ChatMessage;
import net.colonymc.colonyspigotlib.lib.player.PlayerInventory;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.GuildPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class TeamDeathmatch implements Listener {
	
	War war;
	Arena a;
	boolean isStarted = false;
	boolean hasEnded = false;
	int ticksLeft;
	int id;
	int moneyCollected;
	long timeStarted;
	long timeEnded;
	Guild winner;
	BukkitTask draw;
	BukkitTask start;
	Player topDamager;
	String topDamagerUuid;
	final HashMap<Player, Double> damages = new HashMap<>();
	final ArrayList<Player> spectators = new ArrayList<>();
	final HashMap<Guild, ArrayList<ItemStack>> loot = new HashMap<>();
	final ArrayList<OfflinePlayer> left = new ArrayList<>();
	static final ArrayList<TeamDeathmatch> matches = new ArrayList<>();
	
	public TeamDeathmatch(War w) {
		this.war = w;
		a = new Arena(this);
		matches.add(this);
		id = getNewID();
		if(war.getType() == WarType.FRIENDLY) {
			ticksLeft = 12000;
		}
		else {
			ticksLeft = 24000;
		}
		loot.put(war.getRequested(), new ArrayList<>());
		loot.put(war.getRequester(), new ArrayList<>());
		sendPlayers();
		start();
	}
	
	public TeamDeathmatch() {
		
	}

	private void sendPlayers() {
		ArrayList<GuildPlayer> requester = new ArrayList<>(war.getRequester().getMemberUuids().values());
		int z = 0;
		for(int i = 0; i < war.getRequester().getMemberUuids().values().size(); i++) {
			if((i + 1) % 4 == 0) {
				z++;
			}
			((Player) requester.get(i).getPlayer()).teleport(a.getFirstLocation().add(i, 0, z));
			ScoreboardManager.getByPlayer(((Player) requester.get(i).getPlayer())).setType("war");
		}
		ArrayList<GuildPlayer> requested = new ArrayList<>(war.getRequested().getMemberUuids().values());
		z = 0;
		for(int i = 0; i < war.getRequested().getMemberUuids().values().size(); i++) {
			if((i + 1) % 4 == 0) {
				z--;
			}
			((Player) requested.get(i).getPlayer()).teleport(a.getSecondLocation().add(-i, 0, z));
			ScoreboardManager.getByPlayer(((Player) requested.get(i).getPlayer())).setType("war");
		}
	}
	
	public void kill(Player p, Player killer, boolean left) {
		if(war.getRequested().equals(Guild.getByPlayer(p)) || war.getRequester().equals(Guild.getByPlayer(p))) {
			if(!left) {
				if(killer != null) {
					TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).sendMessage("&c&l[DEATH] &c" + p.getName() + " was killed by " + killer.getName() + "!");
				}
			}
			else {
				TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).sendMessage("&c&l[DEATH] &c" + p.getName() + " just left!");
			}
			sendTitle(p, "&fYou are now &da spectator!", "", 2, 40, 2);
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
			if(left) {
				this.left.add(p);
			}
			else {
				addSpectator(p);
			}
			Guild won = declareWinner();
			if(won != null) {
				end(won);
			}
		}
	}

	public void start() {
		start = new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				if(i == 0) {
					a.addBars();
				}
				if(i == 200) {
					isStarted = true;
					timeStarted = System.currentTimeMillis();
					a.removeBars();
					sendMessage(" &5&l» &fThe war has just started!");
					sendSound(Sound.ENDERDRAGON_GROWL);
					cancel();
					draw = new BukkitRunnable() {
						@Override
						public void run() {
							if(ticksLeft == 0) {
								end(null);
								cancel();
							}
							ticksLeft--;
						}
					}.runTaskTimer(Main.getInstance(), 0, 1);
				}
				else if(i % 20 == 0) {
					sendMessage(" &5&l» &fThe war is starting in &d" + (200 - i)/20 + " seconds&f!");
					sendSound(Sound.CLICK);
				}
				i++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	
	public void end(Guild end) {
		if(!isStarted) {
			start.cancel();
		}
		if(draw != null) {
			draw.cancel();
		}
		hasEnded = true;
		timeEnded = System.currentTimeMillis();
		winner = end;
		topDamagerUuid = declareTopDamager();
		if(end != null) {
			Guild lost = end.equals(war.getRequested()) ? war.getRequester() : war.getRequested();
			winner.sendGuildTitle("&a&lVICTORY!", "&fYou have won the war!", 5, 300, 5);
			lost.sendGuildTitle("&c&lDEFEAT!", "&fYou have lost the war!", 5, 300, 5);
			winner.sendGuildSound(Sound.LEVEL_UP, 1);
			lost.sendGuildSound(Sound.ENDERMAN_SCREAM, 1);
		}
		else {
			war.getRequested().sendGuildTitle("&6&lDRAW!", "&fYou have ran out of time!", 5, 300, 5);
			war.getRequester().sendGuildTitle("&6&lDRAW!", "&fYou have ran out of time!", 5, 300, 5);
			war.getRequested().sendGuildSound(Sound.BAT_DEATH, 1);
			war.getRequester().sendGuildSound(Sound.BAT_DEATH, 1);
		}
		if(war.getType() != WarType.FRIENDLY && end != null) {
			giveRewards();
		}
		sendPersonalised();
		a.startFw();
		new BukkitRunnable() {
			@Override
			public void run() {
				a.fireworks.cancel();
				left.clear();
				a.clear();
				matches.remove(TeamDeathmatch.this);
				War.activeWars.remove(war);
				war.getRequested().addWar(TeamDeathmatch.this, true);
				for (Player spectator : spectators) {
					removeSpectator(spectator);
				}
				spectators.clear();
				for(GuildPlayer pl : war.getRequested().getMemberUuids().values()) {
					if(pl.getPlayer().isOnline()) {
						if(war.getType() == WarType.FRIENDLY) {
							try {
								ResultSet rs = Database.getResultSet("SELECT * FROM FriendlyWarItems WHERE playerUuid='" + pl.getPlayer().getUniqueId().toString() + "';");
								ArrayList<ItemStack> items = new ArrayList<>();
								while(rs.next()) {
									items.add(ItemStackSerializer.deserializeItemStack(rs.getString("item")));
								}
								PlayerInventory.addItems(items, pl.getPlayer().getPlayer());
								Database.sendStatement("DELETE FROM FriendlyWarItems WHERE playerUuid='" + pl.getPlayer().getUniqueId().toString() + "';");
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						ScoreboardManager.getByPlayer(((Player) pl.getPlayer())).setType("main");
						war.getRequested().getIsland().sendPlayer(((Player) pl.getPlayer()), false);
					}
				}
				for(GuildPlayer pl : war.getRequester().getMemberUuids().values()) {
					if(pl.getPlayer().isOnline()) {
						if(war.getType() == WarType.FRIENDLY) {
							try {
								ResultSet rs = Database.getResultSet("SELECT * FROM FriendlyWarItems WHERE playerUuid='" + pl.getPlayer().getUniqueId().toString() +  "';");
								ArrayList<ItemStack> items = new ArrayList<>();
								while(rs.next()) {
									items.add(ItemStackSerializer.deserializeItemStack(rs.getString("item")));
								}
								PlayerInventory.addItems(items, pl.getPlayer().getPlayer());
								Database.sendStatement("DELETE FROM FriendlyWarItems WHERE playerUuid='" + pl.getPlayer().getUniqueId().toString() + "';");
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						ScoreboardManager.getByPlayer(((Player) pl.getPlayer())).setType("main");
						war.getRequester().getIsland().sendPlayer(((Player) pl.getPlayer()), false);
					}
				}
			}
		}.runTaskLater(Main.getInstance(), 300);
	}
	
	public void addSpectator(Player p) {
		ScoreboardManager.getByPlayer(p).setType("war");
		spectators.add(p);
		p.setHealth(20);
		p.setSaturation(20);
		p.setFoodLevel(20);
		p.setAllowFlight(true);
		p.setFlying(true);
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 255, false, false));
		for(GuildPlayer pl : war.getRequested().getMemberUuids().values()) {
			if(pl.getPlayer().isOnline()) {
				((Player) pl.getPlayer()).hidePlayer(p);
			}
		}
		for(GuildPlayer pl : war.getRequester().getMemberUuids().values()) {
			if(pl.getPlayer().isOnline()) {
				((Player) pl.getPlayer()).hidePlayer(p);
			}
		}
		a.sendSpectator(p);
	}
	
	public void removeSpectator(Player p) {
		if(spectators.contains(p)) {
			p.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
			p.getPlayer().getPlayer().setFlying(false);
			p.getPlayer().getPlayer().setAllowFlight(false);
			for(GuildPlayer pl : war.getRequested().getMemberUuids().values()) {
				if(pl.getPlayer().isOnline()) {
					((Player) pl.getPlayer()).showPlayer(p.getPlayer());
				}
			}
			for(GuildPlayer pl : war.getRequester().getMemberUuids().values()) {
				if(pl.getPlayer().isOnline()) {
					((Player) pl.getPlayer()).showPlayer(p.getPlayer());
				}
			}
		}
	}
	
	public boolean isSpectator(Player p) {
		return spectators.contains(p);
	}
	
	private Guild declareWinner() {
		Guild winner = war.getRequested();
		for(GuildPlayer p : war.getRequester().getMemberUuids().values()) {
			if(p.getPlayer().isOnline()) {
				if(!spectators.contains(p.getPlayer().getPlayer()) && !left.contains(p.getPlayer().getPlayer())) {
					winner = war.getRequester();
					break;
				}
			}
		}
		for(GuildPlayer p : war.getRequested().getMemberUuids().values()) {
			if(p.getPlayer().isOnline()) {
				if(!winner.equals(war.getRequested()) && !spectators.contains(p.getPlayer().getPlayer()) && !left.contains(p.getPlayer().getPlayer())) {
					winner = null;
					break;
				}
			}
		}
		return winner;
	}
	
	private String declareTopDamager() {
		Player top = null;
		for(Player p : damages.keySet()) {
			if(top == null || damages.get(p) > damages.get(top)) {
				top = p;
			}
		}
		topDamager = top;
		return top == null ? "None" : top.getUniqueId().toString();
	}
	
	private void sendPersonalised() {
		if(winner != null) {
			Guild lost = winner.equals(war.getRequested()) ? war.getRequester() : war.getRequested();
			if(war.getType() == WarType.NORMAL) {
				for(GuildPlayer p : lost.getMemberUuids().values()) {
					if(p.getPlayer().isOnline()) {
						String message = ChatColor.translateAlternateColorCodes('&', "&c&m                                                                                &r");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&c&lDEFEAT!").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n" + ChatColor.translateAlternateColorCodes('&', "                    &fYou have lost the war!");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&fWar Duration: &d" + new SimpleDateFormat("mm:ss").format(new Date(timeEnded - timeStarted))).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fYour damage: &c" + (damages.get(p.getPlayer().getPlayer()) == null ? "0" : damages.get(p.getPlayer().getPlayer())) + "❤").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fTop Damager: " + (topDamager == null ? "&7None" : "&d" + topDamager.getName() + " &f(&c" + damages.get(topDamager) + "❤&f)")).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fLevels Gained: &d0 power levels").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fItems Collected: &d" + getItemsCollected(p.getGuild())).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fMoney Collected: &d$0").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n \n" + ChatColor.translateAlternateColorCodes('&', "&c&m                                                                                ");
						p.getPlayer().getPlayer().sendMessage(message);
					}
				}
				for(GuildPlayer p : winner.getMemberUuids().values()) {
					if(p.getPlayer().isOnline()) {
						String message = ChatColor.translateAlternateColorCodes('&', "&a&m                                                                                &r");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&a&lVICTORY!").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n" + ChatColor.translateAlternateColorCodes('&', "                    &fYou have won the war!");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&fWar Duration: &d" + new SimpleDateFormat("mm:ss").format(new Date(timeEnded - timeStarted))).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fYour damage: &c" + (damages.get(p.getPlayer().getPlayer()) == null ? "0" : damages.get(p.getPlayer().getPlayer())) + "❤").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fTop Damager: " + (topDamager == null ? "&7None" : "&d" + topDamager.getName() + " &f(&c" + damages.get(topDamager) + "❤&f)")).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fLevels Gained: &d10 power levels").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fItems Collected: &d" + getItemsCollected(p.getGuild())).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fMoney Collected: &d" + Guild.balance(moneyCollected)).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n \n" + ChatColor.translateAlternateColorCodes('&', "&a&m                                                                                ");
						p.getPlayer().getPlayer().sendMessage(message);
					}
				}
			}
			else {
				for(GuildPlayer p : lost.getMemberUuids().values()) {
					if(p.getPlayer().isOnline()) {
						String message = ChatColor.translateAlternateColorCodes('&', "&c&m                                                                                &r");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&c&lDEFEAT!").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n" + ChatColor.translateAlternateColorCodes('&', "                    &fYou have lost the war!");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&fWar Duration: &d" + new SimpleDateFormat("mm:ss").format(new Date(timeEnded - timeStarted))).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fYour damage: &c" + (damages.get(p.getPlayer().getPlayer()) == null ? "0" : damages.get(p.getPlayer().getPlayer())) + "❤").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fTop Damager: " + (topDamager == null ? "&7None" : "&d" + topDamager.getName() + " &f(&c" + damages.get(topDamager) + "❤&f)")).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fLevels Gained: &d0 power levels").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fItems Collected: &d0").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fMoney Collected: &d$0").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n \n" + ChatColor.translateAlternateColorCodes('&', "&c&m                                                                                ");
						p.getPlayer().getPlayer().sendMessage(message);
					}
				}
				for(GuildPlayer p : winner.getMemberUuids().values()) {
					if(p.getPlayer().isOnline()) {
						String message = ChatColor.translateAlternateColorCodes('&', "&a&m                                                                                &r");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&a&lVICTORY!").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n" + ChatColor.translateAlternateColorCodes('&', "                    &fYou have won the war!");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&fWar Duration: &d" + new SimpleDateFormat("mm:ss").format(new Date(timeEnded - timeStarted))).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fYour damage: &c" + (damages.get(p.getPlayer().getPlayer()) == null ? "0" : damages.get(p.getPlayer().getPlayer())) + "❤").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fTop Damager: " + (topDamager == null ? "&7None" : "&d" + topDamager.getName() + " &f(&c" + damages.get(topDamager) + "❤&f)")).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fLevels Gained: &d0 power levels").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fItems Collected: &d0").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fMoney Collected: &d$0").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n \n" + ChatColor.translateAlternateColorCodes('&', "&a&m                                                                                ");
						p.getPlayer().getPlayer().sendMessage(message);
					}
				}
			}
		}
		else {
			if(war.getType() == WarType.NORMAL) {
				for(GuildPlayer p : war.getRequested().getMemberUuids().values()) {
					if(p.getPlayer().isOnline()) {
						String message = ChatColor.translateAlternateColorCodes('&', "&6&m                                                                                &r");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&6&lDRAW!").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n" + ChatColor.translateAlternateColorCodes('&', "                    &fYou have ran out of time!");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&fWar Duration: &d" + new SimpleDateFormat("mm:ss").format(new Date(timeEnded - timeStarted))).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fYour damage: &c" + (damages.get(p.getPlayer().getPlayer()) == null ? "0" : damages.get(p.getPlayer().getPlayer())) + "❤").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fTop Damager: " + (topDamager == null ? "&7None" : "&d" + topDamager.getName() + " &f(&c" + damages.get(topDamager) + "❤&f)")).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fLevels Gained: &d0 power levels").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fItems Collected: &d" + getItemsCollected(p.getGuild())).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fMoney Collected: &d$0").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n \n" + ChatColor.translateAlternateColorCodes('&', "&6&m                                                                                ");
						p.getPlayer().getPlayer().sendMessage(message);
					}
				}
				for(GuildPlayer p : war.getRequester().getMemberUuids().values()) {
					if(p.getPlayer().isOnline()) {
						String message = ChatColor.translateAlternateColorCodes('&', "&6&m                                                                                &r");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&6&lDRAW!").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n" + ChatColor.translateAlternateColorCodes('&', "                    &fYou have ran out of time!");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&fWar Duration: &d" + new SimpleDateFormat("mm:ss").format(new Date(timeEnded - timeStarted))).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fYour damage: &c" + (damages.get(p.getPlayer().getPlayer()) == null ? "0" : damages.get(p.getPlayer().getPlayer())) + "❤").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fTop Damager: " + (topDamager == null ? "&7None" : "&d" + topDamager.getName() + " &f(&c" + damages.get(topDamager) + "❤&f)")).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fLevels Gained: &d0 power levels").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fItems Collected: &d" + getItemsCollected(p.getGuild())).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fMoney Collected: &d$0").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n \n" + ChatColor.translateAlternateColorCodes('&', "&6&m                                                                                ");
						p.getPlayer().getPlayer().sendMessage(message);
					}
				}
			}
			else {
				for(GuildPlayer p : war.getRequested().getMemberUuids().values()) {
					if(p.getPlayer().isOnline()) {
						String message = ChatColor.translateAlternateColorCodes('&', "&6&m                                                                                &r");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&6&lDRAW!").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n" + ChatColor.translateAlternateColorCodes('&', "                    &fYou have ran out of time!");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&fWar Duration: &d" + new SimpleDateFormat("mm:ss").format(new Date(timeEnded - timeStarted))).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fYour damage: &c" + (damages.get(p.getPlayer().getPlayer()) == null ? "0" : damages.get(p.getPlayer().getPlayer())) + "❤").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fTop Damager: " + (topDamager == null ? "&7None" : "&d" + topDamager.getName() + " &f(&c" + damages.get(topDamager) + "❤&f)")).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fLevels Gained: &d0 power levels").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fItems Collected: &d0").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fMoney Collected: &d$0").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n \n" + ChatColor.translateAlternateColorCodes('&', "&6&m                                                                                ");
						p.getPlayer().getPlayer().sendMessage(message);
					}
				}
				for(GuildPlayer p : war.getRequester().getMemberUuids().values()) {
					if(p.getPlayer().isOnline()) {
						String message = ChatColor.translateAlternateColorCodes('&', "&6&m                                                                                &r");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&6&lDRAW!").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n" + ChatColor.translateAlternateColorCodes('&', "                    &fYou have ran out of time!");
						p.getPlayer().getPlayer().sendMessage(message);
						new ChatMessage("\n \n&fWar Duration: &d" + new SimpleDateFormat("mm:ss").format(new Date(timeEnded - timeStarted))).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fYour damage: &c" + (damages.get(p.getPlayer().getPlayer()) == null ? "0" : damages.get(p.getPlayer().getPlayer())) + "❤").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fTop Damager: " + (topDamager == null ? "&7None" : "&d" + topDamager.getName() + " &f(&c" + damages.get(topDamager) + "❤&f)")).centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n \n&fLevels Gained: &d0 power levels").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fItems Collected: &d0").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						new ChatMessage("\n&fMoney Collected: &d$0").centered(true).addRecipient(p.getPlayer().getPlayer()).send();
						message = "\n \n" + ChatColor.translateAlternateColorCodes('&', "&6&m                                                                                ");
						p.getPlayer().getPlayer().sendMessage(message);
					}
				}
			}
		}
	}
	
	private void giveRewards() {
		double totalSilver = 0;
		Guild loser = winner.equals(war.getRequested()) ? war.getRequester() : war.getRequested();
		totalSilver = loser.getBalance();
		for(GuildPlayer p : loser.getMemberUuids().values()) {
			totalSilver = totalSilver + p.getBalance();
		}
		int toGive = (int) (totalSilver * 0.05);
		moneyCollected = toGive;
		winner.addBalance(toGive, this);
		double left = 0;
		if(loser.getBalance() >= toGive) {
			loser.removeBalance(toGive, this);
		}
		else {
			left = toGive - loser.getBalance();
			loser.removeBalance(loser.getBalance(), this);
			for(GuildPlayer p : loser.getMemberUuids().values()) {
				if(left == 0) {
					break;
				}
				if(p.getBalance() >= left) {
					p.removeBalance(left);
					break;
				}
				else {
					left = left - p.getBalance();
					p.removeBalance(p.getBalance());
				}
			}
		}
		winner.addLevels(10, this);
	}
	
	private static int getNewID() {
		try {
			ResultSet rs = Database.getResultSet("SELECT * FROM GuildWars ORDER BY warId ASC;");
			int x = 1;
			while(rs.next()) {
				if(rs.getInt("warId") > x) {
					return x;
				}
				else {
					x = x + 1;
				}
			}
			return x;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public War getWar() {
		return war;
	}
	
	public Arena getArena() {
		return a;
	}
	
	public int getId() {
		return id;
	}
	
	public Guild getWinner() {
		return winner;
	}
	
	public boolean hasStarted() {
		return isStarted;
	}
	
	public boolean hasEnded() {
		return hasEnded;
	}
	
	public int getTicksLeft() {
		return ticksLeft;
	}
	
	public int getItemsCollected(Guild g) {
		int collected = 0;
		for(ItemStack i : getItems(g)) {
			collected = collected + i.getAmount();
		}
		return collected;
	}
	
	public ArrayList<ItemStack> getItems(Guild g) {
		return loot.get(g);
	}
	
	public HashMap<Guild, ArrayList<ItemStack>> getItemMap(){
		return loot;
	}
	
	public String getTopDamager() {
		return topDamagerUuid;
	}
	
	public int getMoneyCollected() {
		return moneyCollected;
	}
	
	public long getTimeStarted() {
		return timeStarted;
	}
	
	public long getTimeEnded() {
		return timeEnded;
	}
	
	public String getTimeLeft() {
		if(ticksLeft == 0 || hasEnded) {
			return "ENDED";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		return sdf.format(new Date(ticksLeft * 50));
	}
	
	public int playerLeft(Guild g) {
		int left = 0;
		if(war.getRequested().equals(g) || war.getRequester().equals(g)) {
			for(GuildPlayer gp : g.getMemberUuids().values()) {
				if(gp.getPlayer().isOnline()) {
					if(!spectators.contains(gp.getPlayer().getPlayer())) {
						left++;
					}
				}
			}
		}
		return left;
	}
	
	public static TeamDeathmatch getByGuild(Guild g) {
		for(TeamDeathmatch t : matches) {
			if(t.war.getRequested().equals(g) || t.war.getRequester().equals(g)) {
				return t;
			}
		}
		return null;
	}
	
	public void sendTitle(Player p,String title, String subtitle, int fadeIn, int duration, int fadeOut) {
		IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
		IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
		PacketPlayOutTitle titlep = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
		PacketPlayOutTitle subtitlep = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
		PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, duration, fadeOut);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(titlep);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(subtitlep);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
	}
	
	private void sendMessage(String s) {
		for(GuildPlayer p : war.getRequested().getMemberUuids().values()) {
			if(p.getPlayer().isOnline()) {
				((Player) p.getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', s));
			}
		}
		for(GuildPlayer p : war.getRequester().getMemberUuids().values()) {
			if(p.getPlayer().isOnline()) {
				((Player) p.getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', s));
			}
		}
	}
	
	private void sendTeamMessage(Player p, String s) {
		for(GuildPlayer pl : Guild.getByPlayer(p).getMemberUuids().values()) {
			if(pl.getPlayer().isOnline()) {
				((Player) pl.getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[Team] " + p.getName() + ": &f" + s));
			}
		}
	}
	
	private void sendSpectatorMessage(Player p, String s) {
		for(OfflinePlayer pl : spectators) {
			if(pl.isOnline()) {
				((Player) pl).sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[Spectator] " + p.getName() + ": &f" + s));
			}
		}
	}
	
	private void sendSound(Sound sound) {
		war.getRequester().sendGuildSound(sound, (float) 1);
		war.getRequested().sendGuildSound(sound, (float) 1);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
			TeamDeathmatch tdm = TeamDeathmatch.getByGuild(Guild.getByPlayer(p));
			if(tdm.isSpectator(p)) {
				if(!tdm.a.isInArena(p)) {
					tdm.getArena().sendSpectator(p);
				}
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
			TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).addSpectator(p);
		}
		else if(p.getWorld().equals(Arena.w)) {
			if(p.isDead()) {
				new BukkitRunnable() {
					@Override
					public void run() {
						p.spigot().respawn();
						Guild g = Guild.getByPlayer(p);
						g.getIsland().sendPlayer(p, false);
						p.playSound(p.getLocation(), Sound.GHAST_SCREAM, 2, 1.5f);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &c&l&k:&c&lDEATH!&k:&r &fYou just died!"));
						try {
							ResultSet rs = Database.getResultSet("SELECT * FROM FriendlyWarItems WHERE playerUuid='" + p.getUniqueId().toString() + "';");
							ArrayList<ItemStack> items = new ArrayList<>();
							while(rs.next()) {
								items.add(ItemStackSerializer.deserializeItemStack(rs.getString("item")));
								Database.sendStatement("DELETE FROM FriendlyWarItems WHERE playerUuid='" + p.getUniqueId().toString() + "' AND item='" + rs.getString("item") + "';");
							}
							PlayerInventory.addItems(items, p);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}.runTaskLater(Main.getInstance(), 1);
			}
			else {
				ScoreboardManager.getByPlayer(p).setType("main");
				Guild g = Guild.getByPlayer(p);
				g.getIsland().sendPlayer(p, false);
				try {
					ResultSet rs = Database.getResultSet("SELECT * FROM FriendlyWarItems WHERE playerUuid='" + p.getUniqueId().toString() + "';");
					ArrayList<ItemStack> items = new ArrayList<>();
					while(rs.next()) {
						items.add(ItemStackSerializer.deserializeItemStack(rs.getString("item")));
						Database.sendStatement("DELETE FROM FriendlyWarItems WHERE playerUuid='" + p.getUniqueId().toString() + "' AND item='" + rs.getString("item") + "';");
					}
					PlayerInventory.addItems(items, p);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
			TeamDeathmatch t = TeamDeathmatch.getByGuild(Guild.getByPlayer(p));
			if(!t.spectators.contains(p)) {
				t.kill(p, null, true);
				if(t.getWar().getType() == WarType.NORMAL) {
					Guild opponent = Guild.getByPlayer(p).equals(t.getWar().getRequested()) ? t.getWar().getRequester() : t.getWar().getRequested();
					for(ItemStack i : p.getInventory().getContents()) {
						if(i != null && i.getType() != Material.AIR) {
							t.loot.get(opponent).add(i);
						}
					}
					for(ItemStack i : p.getInventory().getArmorContents()) {
						if(i != null && i.getType() != Material.AIR) {
							t.loot.get(opponent).add(i);
						}
					}
				}
				else {
					for(ItemStack i : p.getInventory().getContents()) {
						if(i != null && i.getType() != Material.AIR) {
							Database.sendStatement("INSERT INTO FriendlyWarItems (playerUuid, item) VALUES "
									+ "('" + p.getUniqueId().toString() + "', '" + ItemStackSerializer.serializeItemStack(i) + "')");
						}
					}
					for(ItemStack i : p.getInventory().getArmorContents()) {
						if(i != null && i.getType() != Material.AIR) {
							Database.sendStatement("INSERT INTO FriendlyWarItems (playerUuid, item) VALUES "
									+ "('" + p.getUniqueId().toString() + "', '" + ItemStackSerializer.serializeItemStack(i) + "')");
						}
					}
				}
				p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
				p.getInventory().clear();
				p.damage(999999);
				t.sendMessage(" &5&l» &fThe player &d" + p.getName() + " &fhas just left and died!");
			}
			else {
				t.removeSpectator(p);
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerKickEvent e) {
		Player p = e.getPlayer();
		if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
			TeamDeathmatch t = TeamDeathmatch.getByGuild(Guild.getByPlayer(p));
			if(!t.spectators.contains(p)) {
				t.kill(p, null, true);
				if(t.getWar().getType() == WarType.NORMAL) {
					Guild opponent = Guild.getByPlayer(p).equals(t.getWar().getRequested()) ? t.getWar().getRequester() : t.getWar().getRequested();
					for(ItemStack i : p.getInventory().getContents()) {
						if(i != null && i.getType() != Material.AIR) {
							t.loot.get(opponent).add(i);
						}
					}
					for(ItemStack i : p.getInventory().getArmorContents()) {
						if(i != null && i.getType() != Material.AIR) {
							t.loot.get(opponent).add(i);
						}
					}
				}
				else {
					for(ItemStack i : p.getInventory().getContents()) {
						if(i != null && i.getType() != Material.AIR) {
							Database.sendStatement("INSERT INTO FriendlyWarItems (playerUuid, item) VALUES "
									+ "('" + p.getUniqueId().toString() + "', '" + ItemStackSerializer.serializeItemStack(i) + "')");
						}
					}
					for(ItemStack i : p.getInventory().getArmorContents()) {
						if(i != null && i.getType() != Material.AIR) {
							Database.sendStatement("INSERT INTO FriendlyWarItems (playerUuid, item) VALUES "
									+ "('" + p.getUniqueId().toString() + "', '" + ItemStackSerializer.serializeItemStack(i) + "')");
						}
					}
				}
				p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
				p.getInventory().clear();
				p.damage(999999);
				t.sendMessage(" &5&l» &fThe player &d" + p.getName() + " &fhas just left and died!");
			}
			else {
				t.removeSpectator(p);
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null && (TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).spectators.contains(p) || !TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).isStarted 
				|| e.getPlayer().getItemInHand().getType() == Material.BOAT || e.getPlayer().getItemInHand().getType() == Material.MINECART || e.getPlayer().getItemInHand().getType() == Material.EXPLOSIVE_MINECART 
				|| e.getPlayer().getItemInHand().getType() == Material.HOPPER_MINECART || e.getPlayer().getItemInHand().getType() == Material.POWERED_MINECART || e.getPlayer().getItemInHand().getType() == Material.STORAGE_MINECART)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
    public void onFlow(BlockExplodeEvent e) {
		if(Arena.getByLocation(e.getBlock().getLocation()) != null) {
			e.setCancelled(true);
		}
    }
	
	@EventHandler
    public void onFlow(BlockFromToEvent e) {
		if(Arena.getByLocation(e.getBlock().getLocation()) != null) {
	        if(e.getToBlock().getType() == Material.REDSTONE_WIRE) {
	            e.setCancelled(true);
	        }
		}
    }
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
			if(e.getBlock().getLocation().getY() > 101) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot build this high!"));
			}
			else if(!TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).isStarted
					|| e.getBlock().getType() == Material.REDSTONE_WIRE || e.getBlock().getType() == Material.REDSTONE_BLOCK || e.getBlock().getType() == Material.STONE_BUTTON || e.getBlock().getType() == Material.WOOD_BUTTON 
					|| e.getBlock().getType() == Material.LEVER || e.getBlock().getType() == Material.STONE_PLATE || e.getBlock().getType() == Material.WOOD_PLATE || e.getBlock().getType() == Material.ITEM_FRAME 
					|| e.getBlock().getType() == Material.TRIPWIRE_HOOK || e.getBlock().getType() == Material.REDSTONE_TORCH_ON || e.getBlock().getType() == Material.REDSTONE_TORCH_OFF || e.getBlock().getType() == Material.GOLD_PLATE 
					|| e.getBlock().getType() == Material.IRON_PLATE || e.getBlock().getType() == Material.DAYLIGHT_DETECTOR || e.getBlock().getType() == Material.DAYLIGHT_DETECTOR_INVERTED) {
				e.setCancelled(true);
			}
			else {
				TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getArena().blocksPlaced.add(e.getBlock());
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
			if(!TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getArena().blocksPlaced.contains(e.getBlock())) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot break this block!"));
			}
			else if(!TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).isStarted) {
				e.setCancelled(true);
			}
			else {
				TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).getArena().blocksPlaced.remove(e.getBlock());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
			TeamDeathmatch t = TeamDeathmatch.getByGuild(Guild.getByPlayer(p));
			e.setDeathMessage(null);
			if(t.getWar().getType() == WarType.FRIENDLY) {
				e.setKeepLevel(true);
				for(ItemStack i : e.getDrops()) {
					if(i != null && i.getType() != Material.AIR) {
						Database.sendStatement("INSERT INTO FriendlyWarItems (playerUuid, item) VALUES "
								+ "('" + e.getEntity().getUniqueId().toString() + "', '" + ItemStackSerializer.serializeItemStack(i) + "')");
					}
				}
				e.getDrops().clear();
			}
			else {
				if(Guild.getByPlayer(p).equals(t.getWar().getRequested())) {
					for(ItemStack i : e.getDrops()) {
						if(i != null && i.getType() != Material.AIR) {
							t.loot.get(t.getWar().getRequester()).add(i);
						}
					}
					e.getDrops().clear();
					p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
					p.getInventory().clear();
				}
				else {
					for(ItemStack i : e.getDrops()) {
						if(i != null && i.getType() != Material.AIR) {
							t.loot.get(t.getWar().getRequested()).add(i);
						}
					}
					e.getDrops().clear();
					p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
					p.getInventory().clear();
				}
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if(p.isDead()) {
					if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
						if(!TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).spectators.contains(p) && !TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).left.contains(p)) {
							p.playSound(p.getLocation(), Sound.GHAST_SCREAM, 2, 1.5f);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &c&l&k:&c&lDEATH!&k:&r &fYou just died!"));
							TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).kill(p, p.getKiller(), false);
							p.spigot().respawn();
						}
					}
				}
			}
		}.runTaskLater(Main.getInstance(), 1);
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
				if(!TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).hasEnded() && TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).hasStarted()) {
					if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).spectators.contains(p)) {
						e.setCancelled(true);
					}
					else if(e.getEntity() instanceof Player) {
						Player attacked = (Player) e.getEntity();
						if(Guild.getByPlayer(attacked).equals(Guild.getByPlayer(p))) {
							e.setCancelled(true);
						}
						else {
							if(!TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).damages.containsKey(p)) {
								TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).damages.put(p, e.getDamage());
							}
							else {
								double damage = TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).damages.get(p);
								TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).damages.put(p, e.getDamage() + damage);
							}
						}
					}
					else {
						if(!TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).damages.containsKey(p)) {
							TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).damages.put(p, e.getDamage());
						}
						else {
							double damage = TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).damages.get(p);
							TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).damages.put(p, e.getDamage() + damage);
						}
					}
				}
				else {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
			if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).spectators.contains(p)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
			if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).spectators.contains(p)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
			e.setCancelled(true);
			if(!TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).spectators.contains(p)) {
				TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).sendTeamMessage(p, e.getMessage());
			}
			else {
				TeamDeathmatch.getByGuild(Guild.getByPlayer(p)).sendSpectatorMessage(p, e.getMessage());
			}
		}
	}
	
	@EventHandler
	public void onCmd(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if(TeamDeathmatch.getByGuild(Guild.getByPlayer(p)) != null) {
			e.setCancelled(true);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot use commands while in a war!"));
		}
	}

}
