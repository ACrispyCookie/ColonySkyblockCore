package net.colonymc.colonyskyblockcore.guilds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.colonymc.colonyspigotapi.api.player.visuals.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.clip.placeholderapi.PlaceholderAPI;
import net.colonymc.colonyspigotapi.api.player.visuals.ChatMessage;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class GuildListeners implements Listener {
	
	static final ArrayList<Player> isForced = new ArrayList<>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		new ScoreboardManager(p, "main");
		try {
			if(Guild.hasGuild(p) == -1) {
				ScoreboardManager.getByPlayer(p).setType("starting");
				ResultSet rs = Database.getResultSet("SELECT * FROM PlayerInfo");
				int unique = 1;
				while(rs.next()) {
					unique++;
				}
				e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &d" + p.getName() + " &fjoined &dColonyMC's Skyblock &ffor the first time! (&d&l#" + unique + "&f)"));
				Database.sendStatement("INSERT INTO PlayerInfo (playerName, playerUuid, silver, dwarfDust, guild, guildRank, messages) VALUES ('" + p.getName() + "', '" + p.getUniqueId().toString() + "',"
						+ "0, 0, 0, 'MEMBER', 1);");
				p.chat("/spawn");
				forceCreate(p, true);
			}
			else if(Guild.hasGuild(p) == 0){
				ScoreboardManager.getByPlayer(p).setType("starting");
				e.setJoinMessage(null);
				ResultSet rs = Database.getResultSet("SELECT * FROM PlayerInfo WHERE playerUuid='" + p.getUniqueId().toString() + "';");
				if(rs.next()) {
					String oldName = rs.getString("playerName");
					if(!p.getName().equals(oldName)) {
						Database.sendStatement("UPDATE PlayerInfo SET playerName='" + p.getName() + "' WHERE playerUuid='" + p.getUniqueId().toString() + "';");
					}
				}
				p.chat("/spawn");
				forceCreate(p, false);
			}
			else {
				ScoreboardManager.getByPlayer(p).setType("main");
				e.setJoinMessage(null);
				Guild g = Guild.getByPlayer(p);
				if(p.getWorld().equals(Island.getWorld())) {
					if(g.getIsland().getSpawnLocation()[0] + g.getIsland().getBorderSize()/2 < p.getLocation().getX() || 
						g.getIsland().getSpawnLocation()[0] - g.getIsland().getBorderSize()/2 > p.getLocation().getX()) {
						g.getIsland().sendPlayer(p, false);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have been teleported to your island!"));
					}
					else {
						Island.visitors.put(p, g.getIsland());
						g.getIsland().sendBorder(p);
					}
				}
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d&m*-*&f&m-*-*-*-&d&m*-*-*-*-*-*-&f&m*-*-*&d&m-*-*-*-*-*-*&f&m-*-*-*-&d&m*-*"));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
				new ChatMessage("&fWelcome, &6&l" + p.getName() + " &fto &d&lColony&f&lMC").addRecipient(p).centered(true).send();
				new ChatMessage("&7&l&o{{ &7Skyblock Dimension  &7&l&o}}").addRecipient(p).centered(true).send();
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f(&d-&f)         &d&l* &f&lWEBSITE &dhttps://colonymc.net"));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f(&d-&f)         &d&l* &f&lSTORE   &dhttps://store.colonymc.net"));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f(&d-&f)         &d&l* &f&lDISCORD &dhttps://colonymc.net/discord"));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d&m*-*&f&m-*-*-*-&d&m*-*-*-*-*-*-&f&m*-*-*&d&m-*-*-*-*-*-*&f&m-*-*-*-&d&m*-*"));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		if(isForced.contains(e.getPlayer())) {
			isForced.remove(e.getPlayer());
		}
		if(GuildCommand.toggledOnGuildChat.contains(e.getPlayer())) {
			GuildCommand.toggledOnGuildChat.remove(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onLeave(PlayerKickEvent e) {
		if(isForced.contains(e.getPlayer())) {
			isForced.remove(e.getPlayer());
		}
		if(GuildCommand.toggledOnGuildChat.contains(e.getPlayer())) {
			GuildCommand.toggledOnGuildChat.remove(e.getPlayer());
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(isForced.contains(p)) {
			e.setCancelled(true);
			p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
			p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&cPlease create a guild!"), ChatColor.translateAlternateColorCodes('&', "&cType /guild create/join <name>!"));
		}
		else if(GuildCommand.toggledOnGuildChat.contains(p)) {
			e.setCancelled(true);
			Guild.getByPlayer(p).sendGuildMessage("&d" + p.getName() + ": &f" + e.getMessage());
		}
		else if(!e.isCancelled()) {
			ArrayList<Player> players = new ArrayList<>(e.getRecipients());
			e.getRecipients().clear();
			TextComponent guildName = null;
			TextComponent guildPlace = null;
			if(Guild.getByPlayer(p) != null) {
				guildName = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&d[" + Guild.getByPlayer(p).getName() + "] "));
				guildName.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/g who " + Guild.getByPlayer(p).getName()));
				guildName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fClick to see more about &dthis guild!"))}));
				if(Guild.getByPlayer(p).getTopPlace() == 1) {
					guildPlace = new TextComponent(ChatColor.translateAlternateColorCodes('&', " &b&l&k:&b&l#" + Guild.getByPlayer(p).getTopPlace() + "&k:&r"));
				}
				else if(Guild.getByPlayer(p).getTopPlace() == 2) {
					guildPlace = new TextComponent(ChatColor.translateAlternateColorCodes('&', " &f&l&k:&f&l#" + Guild.getByPlayer(p).getTopPlace() + "&k:&r"));
				}
				else if(Guild.getByPlayer(p).getTopPlace() == 3) {
					guildPlace = new TextComponent(ChatColor.translateAlternateColorCodes('&', " &6&l&k:&6&l#" + Guild.getByPlayer(p).getTopPlace() + "&k:&r"));
				}
				else {
					guildPlace = new TextComponent(ChatColor.translateAlternateColorCodes('&', " &d[#" + Guild.getByPlayer(p).getTopPlace() + "]"));
				}
				guildPlace.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/g top"));
				guildPlace.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fClick to check the &dguild leaderboards!"))}));
			}
			TextComponent c = new TextComponent(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(p, "%vault_prefix%")));
			c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/store"));
			c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fClick here to get a link to our &dstore!"))}));
			TextComponent name = new TextComponent(p.hasPermission("prince.store") ? ChatColor.translateAlternateColorCodes('&', "&r" + p.getName()) : ChatColor.translateAlternateColorCodes('&', "&7" + p.getName()));
			name.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + p.getName() + " "));
			name.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fRank: " + 
					PlaceholderAPI.setPlaceholders(p, p.hasPermission("prince.store") ? "%vault_prefix%" : "&7Knight") + "\n&fGuild: &d" + Guild.getByPlayer(p).getName() + "\n&fGuild rank: " + Guild.getByPlayer(p).getGuildPlayer(p).getRole().color + Guild.getByPlayer(p).getGuildPlayer(p).getRole().name
					+ "\n&fGuild top: &d#" + Guild.getByPlayer(p).getTopPlace() + "\n&fGuild power level: &d" + Guild.getByPlayer(p).getLevel() + " level\n \n&fClick here to message &d" + p.getName()))}));
			ArrayList<TextComponent> msg = new ArrayList<>();
			String[] words = e.getMessage().split(" ");
			for(String s : words) {
				if((s.contains("https://") || s.contains("http://")) && s.contains(".")) {
					TextComponent t = new TextComponent(s + " ");
					t.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, s));
					msg.add(t);
				}
				else {
					msg.add(new TextComponent(s + " "));
				}
			}
			TextComponent finalmsg = new TextComponent("");
			if(guildName != null) {
				finalmsg.addExtra(guildName);
			}
			finalmsg.addExtra(c);
			finalmsg.addExtra(name);
			if(guildPlace != null) {
				finalmsg.addExtra(guildPlace);
			}
			finalmsg.addExtra(ChatColor.translateAlternateColorCodes('&', " &5» " + (p.hasPermission("prince.store") ? "&f" : "&7")));
			for(TextComponent t : msg) {
				finalmsg.addExtra(t);
			}
			for(Player pl : players) {
				pl.spigot().sendMessage(finalmsg);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onChat(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if(isForced.contains(p)  && !e.getMessage().startsWith("/g create") && !e.getMessage().startsWith("/g join") && !e.getMessage().startsWith("/guild create") && !e.getMessage().startsWith("/guild join")) {
			e.setCancelled(true);
			p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
			p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&cPlease create a guild!"), ChatColor.translateAlternateColorCodes('&', "&cType /guild create/join <name>!"));
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Guild g = Guild.getByPlayer(p);
		if(p.getWorld().equals(Island.getWorld())) {
			if(Island.getVisitorMap().containsKey(p) && !Island.getVisitorMap().get(p).equals(g.getIsland()) && !GuildCommand.bypasses.contains(p)) {
				e.setCancelled(true);
			}
		}
		else if(p.getWorld().equals(Bukkit.getWorld("hub")) && !p.hasPermission("*")){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Guild g = Guild.getByPlayer(p);
		if(p.getWorld().equals(Island.getWorld())) {
			if(Island.getVisitorMap().containsKey(p) && !Island.getVisitorMap().get(p).equals(g.getIsland()) && !GuildCommand.bypasses.contains(p)) {
				e.setCancelled(true);
			}
		}
		else if(p.getWorld().equals(Bukkit.getWorld("hub")) && !p.hasPermission("*")){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
			Guild g = Guild.getByPlayer(p);
			if(p.getWorld().equals(Island.getWorld())) {
				if(Island.getVisitorMap().containsKey(p) && !Island.getVisitorMap().get(p).equals(g.getIsland()) && !GuildCommand.bypasses.contains(p)) {
					e.setCancelled(true);
				}
			}
			else if(p.getWorld().equals(Bukkit.getWorld("hub")) && !p.hasPermission("*")){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		Guild g = Guild.getByPlayer(p);
		if(p.getWorld().equals(Island.getWorld())) {
			if(Island.getVisitorMap().containsKey(p) && !Island.getVisitorMap().get(p).equals(g.getIsland()) && !GuildCommand.bypasses.contains(p)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		Guild g = Guild.getByPlayer(p);
		if(p.getWorld().equals(Island.getWorld())) {
			if(Island.getVisitorMap().containsKey(p) && !Island.getVisitorMap().get(p).equals(g.getIsland()) && !GuildCommand.bypasses.contains(p)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if(Island.getVisitorMap().containsKey(p) && Island.getVisitorMap().get(p).equals(Guild.getByPlayer(p).getIsland())) {
				if(e.getEntity() instanceof Player && !GuildCommand.bypasses.contains(p)) {
					e.setCancelled(true);
				}
			}
			else if(Island.getVisitorMap().containsKey(p) && !Island.getVisitorMap().get(p).equals(Guild.getByPlayer(p).getIsland()) && !GuildCommand.bypasses.contains(p)) {
				e.setCancelled(true);
			}
			else if(p.getWorld().equals(Bukkit.getWorld("hub")) && !p.hasPermission("*")){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent e) {
		if(e.getTarget() instanceof Player) {
			Player p = (Player) e.getTarget();
			if(Island.getVisitorMap().containsKey(p) && !Island.getVisitorMap().get(p).equals(Guild.getByPlayer(p).getIsland()) && !GuildCommand.bypasses.contains(p)) {
				e.setCancelled(true);
			}
			else if(p.getWorld().equals(Bukkit.getWorld("hub")) && !p.hasPermission("*")){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onLaunchProj(ProjectileLaunchEvent e) {
		if(e.getEntity().getShooter() instanceof Player) {
			Player p = (Player) e.getEntity().getShooter();
			Guild g = Guild.getByPlayer(p);
			if(Island.getVisitorMap().containsKey(p) && !Island.getVisitorMap().get(p).equals(g.getIsland()) && !GuildCommand.bypasses.contains(p)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(e.getCause() == DamageCause.VOID) {
				e.setCancelled(true);
				p.setHealth(0);
			}
			else if(Island.getVisitorMap().containsKey(p) && !Island.getVisitorMap().get(p).equals(Guild.getByPlayer(p).getIsland()) && e.getCause() != DamageCause.ENTITY_ATTACK) {
				e.setCancelled(true);
			}
			else if(p.getWorld().equals(Bukkit.getWorld("hub"))){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPotion(PotionSplashEvent e) {
		if(e.getPotion().getShooter() instanceof Player){
			Player pl = (Player) e.getPotion().getShooter();
			if(pl.getWorld().equals(Bukkit.getWorld("hub")) && !pl.hasPermission("*")){
				e.setCancelled(true);
			}
		}
		for(Entity en : e.getAffectedEntities()) {
			if(en instanceof Player) {
				Player p = (Player) en;
				if(p.getWorld().equals(Island.getWorld()) && !Island.getVisitorMap().get(p).equals(Guild.getByPlayer(p).getIsland()) && !GuildCommand.bypasses.contains(p)) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		if(!e.getPlayer().getWorld().equals(Island.getWorld())) {
			Island.getVisitorMap().remove(e.getPlayer());
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		boolean isOnIsland = Island.getWorld().equals(p.getWorld());
		if(isOnIsland) {
			e.setDeathMessage(null);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if(isOnIsland) {
					p.spigot().respawn();
					Guild g = Guild.getByPlayer(p);
					g.getIsland().sendPlayer(p, false);
					p.playSound(p.getLocation(), Sound.GHAST_SCREAM, 2, 1.5f);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &c&l&k:&c&lDEATH!&k:&r &fYou just died!"));
				}
				else if(p.getWorld().equals(Bukkit.getWorld("hub"))) {
					p.spigot().respawn();
					p.teleport(new Location(Bukkit.getWorld("hub"), 0, 70, 0));
					p.playSound(p.getLocation(), Sound.GHAST_SCREAM, 2, 1.5f);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &c&l&k:&c&lDEATH!&k:&r &fYou just died!"));
				}
			}
		}.runTaskLater(Main.getInstance(), 1);
	}

	@SuppressWarnings("deprecation")
	public static void forceCreate(Player p, boolean firstTime) {
		BukkitRunnable inform = new BukkitRunnable() {
			int counter = 0;
			@Override
			public void run() {
				if(counter == 0) {
					isForced.add(p);
				}
				else if(counter == 10) {
					if(firstTime) {
						p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&fWelcome &d" + p.getName()), ChatColor.translateAlternateColorCodes('&', "&fto &dColonyMC's Skyblock&f!"));
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
					}
					else {
						p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&fCreate a guild to get started!"), ChatColor.translateAlternateColorCodes('&', "&fType &d/guild create/join <name>!"));
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
					}
				}
				else if(counter == 90) {
					if(firstTime) {
						p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&fCreate a guild to get started!"), ChatColor.translateAlternateColorCodes('&', "&fType &d/guild create/join <name>!"));
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
					}
				}
				else if(!isForced.contains(p)) {
					cancel();
				}
				counter++;
			}
		};
		inform.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}

}
