package net.colonymc.colonyskyblockcore.guilds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mysql.jdbc.StringUtils;

import net.colonymc.api.messages.Message;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.MainMessages;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildLootMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildMainMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildRelationsMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildSettingsMenu;
import net.colonymc.colonyskyblockcore.guilds.war.ConfirmWarMenu;
import net.colonymc.colonyskyblockcore.guilds.war.EndedWar;
import net.colonymc.colonyskyblockcore.guilds.war.SelectModeWarMenu;
import net.colonymc.colonyskyblockcore.guilds.war.War;
import net.colonymc.colonyskyblockcore.guilds.war.WarLootMenu;

public class GuildCommand implements CommandExecutor {
	
	HashMap<Player, Long> confirmDelete = new HashMap<Player, Long>();
	HashMap<Player, Long> confirmLeave = new HashMap<Player, Long>();
	static ArrayList<Guild> creatingIsland = new ArrayList<Guild>();
	static ArrayList<Player> bypasses = new ArrayList<Player>();
	static ArrayList<GuildInvite> invitations = new ArrayList<GuildInvite>();
	static ArrayList<GuildAllyRequest> allyRequests = new ArrayList<GuildAllyRequest>();
	static ArrayList<Player> toggledOnGuildChat = new ArrayList<Player>();
	
	@SuppressWarnings("deprecation")
	public Guild searchGuild(String name) {
		if(Guild.getByName(name) != null) {
			return Guild.getByName(name);
		}
		else if(Guild.getByPlayer(Bukkit.getOfflinePlayer(name)) != null) {
			return Guild.getByPlayer(Bukkit.getOfflinePlayer(name));
		}
		else {
			return null;
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equals("guild")) {
				if(args.length == 0) {
					new GuildMainMenu(p, null);
				}
				else if(args.length >= 1) {
					if(!creatingIsland.contains(Guild.getByPlayer(p))) {
						if(args[0].equalsIgnoreCase("create")) {
							handleGuildCreate(p, args);
						}
						else if(args[0].equalsIgnoreCase("invite")) {
							handleGuildInvite(p, args);
						}
						else if(args[0].equalsIgnoreCase("join")) {
							handleGuildJoin(p, args);
						}
						else if(args[0].equalsIgnoreCase("go") || args[0].equalsIgnoreCase("visit")) {
							handleGuildIsland(p, args, cmd);
						}
						else if(args[0].equalsIgnoreCase("leave")) {
							handleGuildLeave(p, args);
						}
						else if(args[0].equalsIgnoreCase("chat")) {
							handleGuildChat(p, args);
						}
						else if(args[0].equalsIgnoreCase("list")) {
							handleGuildList(p, args);
						}
						else if(args[0].equalsIgnoreCase("who")) {
							handleGuildWho(p, args);
						}
						else if(args[0].equalsIgnoreCase("top")) {
							handleGuildTop(p, args);
						}
						else if(args[0].equalsIgnoreCase("war")) {
							handleGuildWar(p, args);
						}
						else if(args[0].equalsIgnoreCase("ally")) {
							if(args.length > 1) {
								if(Guild.getByName(args[1]) != null) {
									if(GuildAllyRequest.isAlliedBy(Guild.getByPlayer(p), Guild.getByName(args[1]))) {
										GuildAllyRequest.getAllyRequestByGuild(Guild.getByPlayer(p), Guild.getByName(args[1])).accept();
									}
									else if(GuildAllyRequest.isAlliedBy(Guild.getByName(args[1]), Guild.getByPlayer(p))){
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou have already requested from this guild to become allies!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
									else {
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou haven't been requested by this guild to become allies!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
								}
								else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis guild doesn't exist!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/g ally <name>"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else if(args[0].equalsIgnoreCase("balance")) {
							handleGuildBalance(p, args);
						}
						else if(args[0].equalsIgnoreCase("level")) {
							handleGuildLevel(p, args);
						}
						else if(args[0].equalsIgnoreCase("bypass")) {
							handleGuildBypass(p, args);
						}
						else if(args[0].equalsIgnoreCase("menu")) {
							new GuildMainMenu(p, null);
						}
						else if(args[0].equalsIgnoreCase("relations")) {
							new GuildRelationsMenu(p, false);
						}
						else if(args[0].equalsIgnoreCase("unclaimed")) {
							if(args.length == 2) {
								if(Guild.getByPlayer(p).getGuildPlayer(p).getRole().ordinal() > 0) {
									if(Guild.getByPlayer(p).getUnclaimedItems().containsKey(Database.getUuid(args[1]))) {
										new GuildLootMenu(p, Database.getUuid(args[1]));
									}
									else {
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou don't have any unclaimed items from this player!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
								}
								else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou must be at least an Officer to do this!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/" + label + " unclaimed <player>"));
								p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
							}
						}
						else if(args[0].equalsIgnoreCase("loot")) {
							EndedWar w = null;
							for(EndedWar e : Guild.getByPlayer(p).getEndedWars()) {
								if(w == null || e.getTimeEnded() > w.getTimeEnded()) {
									w = e;
								}
							}
							if(w != null) {
								if(!w.getLoot(Guild.getByPlayer(p)).isEmpty()) {
									new WarLootMenu(p, w);
								}
								else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour latest war has already been looted!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild hasn't participated in any wars yet!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else if(args[0].equalsIgnoreCase("settings")) {
							new GuildSettingsMenu(p, false);
						}
						else if(args[0].equalsIgnoreCase("help")) {
							handleGuildHelp(p, args, label);
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cInvalid command! Type /" + label + " help to see the available commands!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
					else {
						if(creatingIsland.contains(Guild.getByPlayer(p))) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease wait until your island creation is finished!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
				}
			}
		}
		else {
			sender.sendMessage(MainMessages.onlyPlayers);
		}
		return false;
	}

	private void handleGuildHelp(Player p, String[] args, String label) {
		Message header = new Message("&d&lGuild/Island Commands").centered(true).addRecipient(p);
		String commands = ChatColor.translateAlternateColorCodes('&', " &5&l» &d/" + label + " help &f- Displays this menu."
				+ "\n &5&l» &d/" + label + " create <name> &f- Creates a new guild with the specified name."
				+ "\n &5&l» &d/" + label + " invite <name> &f- Invites a player to your current guild. "
				+ "\n &5&l» &d/" + label + " join <name> &f- Command to join to the guild you specified."
				+ "\n &5&l» &d/" + label + " menu &f- Command to open the main guild menu."
				+ "\n &5&l» &d/" + label + " relations &f- Command to manage your relations with other guilds."
				+ "\n &5&l» &d/" + label + " settings &f- Command to access more advanced option about your guild."
				+ "\n &5&l» &d/" + label + " visit [name] &f- Command to visit another's guild island."
				+ "\n &5&l» &d/" + label + " leave &f- Command to leave your current guild."
				+ "\n &5&l» &d/" + label + " chat [message] &f- Command to send a message to the members of your guild."
				+ "\n &5&l» &d/" + label + " list &f- Displays a list of all the members in your current guild."
				+ "\n &5&l» &d/" + label + " who <name> &f- Displays information about the specified guild."
				+ "\n &5&l» &d/" + label + " top &f- Displays the top 15 guilds along with the place of your guild."
				+ "\n &5&l» &d/" + label + " balance &f- Displays the balance of your guild."
				+ "\n &5&l» &d/" + label + " level &f- Displays the current power level of your guild.");
		header.send();
		p.sendMessage("\n \n" + commands);
	}
	

	private void handleGuildBypass(Player p, String[] args) {
		if(p.hasPermission("guild.bypass")) {
			if(bypasses.contains(p)) {
				bypasses.remove(bypasses.indexOf(p));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have toggled your bypass mode to &cdisabled&f!"));
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
			}
			else {
				bypasses.add(p);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have toggled your bypass mode to &aenabled&f!"));
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
			}
		}
		else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot execute this command!"));
			p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
		}
	}

	private void handleGuildWar(Player p, String[] args) {
		if(Guild.getByPlayer(p).getOwner().getPlayer().getPlayer() != null && Guild.getByPlayer(p).getOwner().getPlayer().getPlayer().equals(p)) {
			if(args.length == 1) {
				if(War.isRequested(Guild.getByPlayer(p)) != null) {
					War w = War.isRequested(Guild.getByPlayer(p));
					if(w.getRequester().equals(Guild.getByPlayer(p))) {
						new ConfirmWarMenu(p, w);
					}
					else {
						new ConfirmWarMenu(p, w);
					}
				}
				else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild is not currently on a war! You can start one by typing /guild war <guild>!"));
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
			}
			else if(args.length == 2) {
				if(War.isRequested(Guild.getByPlayer(p)) == null) {
					if(Guild.getByName(args[1]) != null) {
						if(!Guild.getByName(args[1]).equals(Guild.getByPlayer(p))) {
							if(War.isRequested(Guild.getByName(args[1])) == null) {
								if(Guild.getByName(args[1]).getOwner().getPlayer().getPlayer() != null) {
									int recent = 0;
									for(EndedWar e : Guild.getByName(args[1]).getEndedWars()) {
										if((e.getOneGuild().equals(Guild.getByPlayer(p)) && e.getOneGuild().equals(Guild.getByName(args[1]))) || (e.getOneGuild().equals(Guild.getByName(args[1])) && e.getAnotherGuild().equals(Guild.getByPlayer(p)))) {
											if(System.currentTimeMillis() - 86400000 < e.getTimeStarted()) {
												recent++;
											}
										}
									}
									if(recent < 3) {
										new SelectModeWarMenu(p, Guild.getByName(args[1]));
									}
									else {
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou can only declare a war against the same guild 3 times a day!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
								}
								else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe owner of the guild is not online!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis guild is already in another war!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot declare a war against your own guild!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis guild doesn't exist!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else {
					War w = War.isRequested(Guild.getByPlayer(p));
					if(w.getRequester().equals(Guild.getByPlayer(p))) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild is already on a war with the guild " + w.getRequested().getName() + "! Type /guild war to agree/disagree!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild is already on a war with the guild " + w.getRequester().getName() + "! Type /guild war to agree/disagree!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
			}
			else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: /guild war [guild]"));
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
			}
		}
		else {
			if(War.isRequested(Guild.getByPlayer(p)) != null) {
				War w = War.isRequested(Guild.getByPlayer(p));
				if(w.getRequester().equals(Guild.getByPlayer(p))) {
					new ConfirmWarMenu(p, w);
				}
				else {
					new ConfirmWarMenu(p, w);
				}
			}
			else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild is not currently on a war! Only the owner can start a new one against another guild!"));
				p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
			}
		}
	}

	private void handleGuildIsland(Player p, String[] args, Command cmd) {
		if(cmd.getName().equals("guild")) {
			if(args.length == 1) {
				Guild.getByPlayer(p).getIsland().sendPlayer(p, false);
			}
			else if(args.length > 1) {
				Guild anotherGuild = searchGuild(args[1]);
				if(anotherGuild != null && anotherGuild.getIsland() != null) {
					anotherGuild.getIsland().sendPlayer(p, false);
				}
				else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis guild does not exist!"));
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
			}
		}
	}
	
	private void handleGuildTop(Player p, String[] args) {
		Message header = new Message("&d&lTop Guilds").centered(true).addRecipient(p);
		String topGuilds = null;
		for(int i = 0; i < Guild.getSortedTopGuild(15).size(); i++) {
			Guild g = Guild.getSortedTopGuild(15).get(i);
			if(i == 0) {
				topGuilds = "\n  &5&l» &f#" + (i + 1) + " &d" + g.getName() + " &f- &d" + g.getLevel() + " Power Level";
			}
			else {
				topGuilds = topGuilds + "\n  &5&l» &f#" + (i + 1) + " &d" + g.getName() + " &f- &d" + g.getLevel() + " Power Level";
			}
		}
		if(Guild.getSortedTopGuild(15).contains(Guild.getByPlayer(p))) {
			header.send();
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', topGuilds + "\n "));
		}
		else {
			String place = "  &5&l» &fYour place: &d#" + Guild.getByPlayer(p).getTopPlace();
			header.send();
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', topGuilds + "\n \n" + place +"\n "));
		}
	}

	private void handleGuildBalance(Player p, String[] args) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYour guild's current balance is &d" + Guild.balance(Guild.getByPlayer(p).getBalance())));
	}
	
	private void handleGuildLevel(Player p, String[] args) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYour guild's current power level is &d" + Guild.getByPlayer(p).getLevel() + " &fwhich is enough "
				+ "to get you to the &d#" + Guild.getByPlayer(p).getTopPlace() + " place &fon the leaderboards!"));
	}

	private void handleGuildChat(Player p, String[] args) {
		if(args.length == 1) {
			if(toggledOnGuildChat.contains(p)) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have toggled your &dguild chat &fto &coff&f!"));
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				toggledOnGuildChat.remove(toggledOnGuildChat.indexOf(p));
			}
			else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have toggled your &dguild chat &fto &aon&f!"));
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
				toggledOnGuildChat.add(p);
			}
		}
		else if(args.length >= 2) {
			String messageToSend = "";
			for(int i = 1; i < args.length; i++) {
				if(i + 1 == args.length) {
					messageToSend = messageToSend + args[i];
				}
				else {
					messageToSend = messageToSend + args[i] + " ";
				}
			}
			if(!StringUtils.isEmptyOrWhitespaceOnly(messageToSend)) {
				Guild.getByPlayer(p).sendGuildMessage("&d" + p.getName() + ": &f" + messageToSend);
			}
			else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/guild chat [message]"));
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
			}
		}
		else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/guild chat [message]"));
			p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
		}
	}

	private void handleGuildList(Player p, String[] args) {
		Guild g = Guild.getByPlayer(p);
		Message header = new Message("&f{ &a" + g.getName() + " &f}").centered(true).addRecipient(p);
		ArrayList<GuildPlayer> allPlayers = new ArrayList<GuildPlayer>();
		allPlayers.addAll(g.getMemberUuids().values());
		Collections.sort(allPlayers, new GuildPlayerComparator());
		ArrayList<OfflinePlayer> onlinePlayers = new ArrayList<OfflinePlayer>();
		for(GuildPlayer pl : allPlayers) {
			if(pl.getPlayer().isOnline()) {
				onlinePlayers.add(pl.getPlayer());
			}
		}
		String totalMembers = ChatColor.translateAlternateColorCodes('&', "\n      &fTotal Members: &d" + g.getMemberUuids().size());
		String totalOnline = ChatColor.translateAlternateColorCodes('&', "\n      &fTotal Online: &d" + onlinePlayers.size());
		String playerList = ChatColor.translateAlternateColorCodes('&', "\n      &fMembers: &d");
		for(int i = 0; i < allPlayers.size(); i++) {
			if((i + 1) % 4 == 0) {
				playerList = playerList + "\n      "; 
			}
			if(i == g.getMemberUuids().keySet().size() - 1) {
				OfflinePlayer toAdd = allPlayers.get(i).getPlayer();
				if(toAdd.isOnline()) {
					playerList = playerList + g.getGuildPlayer(toAdd).getRole().color + toAdd.getName() + " " + ChatColor.GREEN + "■";
				}
				else {
					playerList = playerList + g.getGuildPlayer(toAdd).getRole().color + toAdd.getName() + " " + ChatColor.RED + "■";
				}
			}
			else {
				OfflinePlayer toAdd = allPlayers.get(i).getPlayer();
				if(toAdd.isOnline()) {
					playerList = playerList + g.getGuildPlayer(toAdd).getRole().color + toAdd.getName() + " " + ChatColor.GREEN + "■" + g.getGuildPlayer(toAdd).getRole().color + ", ";
				}
				else {
					playerList = playerList + g.getGuildPlayer(toAdd).getRole().color + toAdd.getName() + " " + ChatColor.RED + "■" + g.getGuildPlayer(toAdd).getRole().color + ", ";
				}
			}
		}
		String allies = ChatColor.translateAlternateColorCodes('&', "\n      &fAllies: &7(None)");
		String enemies = ChatColor.translateAlternateColorCodes('&', "\n      &fEnemies: &7(None)");
		ArrayList<Guild> relations = new ArrayList<Guild>();
		relations.addAll(g.getRelations().keySet());
		for(int i = 0; i < relations.size(); i++) {
			if(i + 1 == relations.size()) {
				if(g.getRelation(relations.get(i)) == Relation.ALLY) {
					if(allies.equals(ChatColor.translateAlternateColorCodes('&', "\n      &fAllies: &7(None)"))) {
						allies = ChatColor.translateAlternateColorCodes('&', "\n      &fAllies: &d");
					}
					allies = allies + relations.get(i).getName();
				}
				else if(g.getRelation(relations.get(i)) == Relation.ENEMY) {
					if(enemies.equals(ChatColor.translateAlternateColorCodes('&', "\n      &fEnemies: &7(None)"))) {
						enemies = ChatColor.translateAlternateColorCodes('&', "\n      &fEnemies: &d");
					}
					enemies = enemies + relations.get(i).getName();
				}
			}
			else {
				if(g.getRelation(relations.get(i)) == Relation.ALLY) {
					if(allies.equals(ChatColor.translateAlternateColorCodes('&', "\n      &fAllies: &7(None)"))) {
						allies = ChatColor.translateAlternateColorCodes('&', "\n      &fAllies: &d");
					}
					allies = allies + relations.get(i).getName() + ", ";
				}
				else if(g.getRelation(relations.get(i)) == Relation.ENEMY) {
					if(enemies.equals(ChatColor.translateAlternateColorCodes('&', "\n      &fEnemies: &7(None)"))) {
						enemies = ChatColor.translateAlternateColorCodes('&', "\n      &fEnemies: &d");
					}
					enemies = enemies + relations.get(i).getName() + ", ";
				}
			}
		}
		header.send();
		String toSend = "\n " + totalMembers  + totalOnline + "\n " + playerList + "\n \n" + allies + enemies + "\n \n" + ChatColor.translateAlternateColorCodes('&', "      &4Dark Red &5&l➜ &fOwner\n      &6Gold &5&l➜ &fOfficers\n      &dLight Purple &5&l➜ &fMembers") + "\n ";
		p.sendMessage(toSend);
	}
	
	private void handleGuildWho(Player p, String[] args) {
		if(args.length == 2) {
			Guild g = searchGuild(args[1]);
			if(g != null) {
				if(!g.equals(Guild.getByPlayer(p))) {
					Message header = new Message("&f{ &d" + Guild.getByPlayer(p).getRelation(g).color + g.getName() + " &f}").centered(true).addRecipient(p);
					ArrayList<GuildPlayer> allPlayers = new ArrayList<GuildPlayer>();
					allPlayers.addAll(g.getMemberUuids().values());
					Collections.sort(allPlayers, new GuildPlayerComparator());
					ArrayList<GuildPlayer> onlinePlayers = new ArrayList<GuildPlayer>();
					for(GuildPlayer pl : allPlayers) {
						if(pl.getPlayer().isOnline()) {
							onlinePlayers.add(pl);
						}
					}
					String totalMembers = ChatColor.translateAlternateColorCodes('&', "\n      &fTotal Members: &d" + g.getMemberUuids().size());
					String totalOnline = ChatColor.translateAlternateColorCodes('&', "\n      &fTotal Online: &d" + onlinePlayers.size());
					String playerList = ChatColor.translateAlternateColorCodes('&', "\n      &fMembers: &d");
					for(int i = 0; i < allPlayers.size(); i++) {
						if((i + 1) % 4 == 0) {
							playerList = playerList + "\n      "; 
						}
						if(i == g.getMemberUuids().keySet().size() - 1) {
							OfflinePlayer toAdd = allPlayers.get(i).getPlayer();
							playerList = playerList + g.getGuildPlayer(toAdd).getRole().color + toAdd.getName();
						}
						else {
							OfflinePlayer toAdd = allPlayers.get(i).getPlayer();
							playerList = playerList + g.getGuildPlayer(toAdd).getRole().color + toAdd.getName() + ", ";
						}
					}
					String allies = ChatColor.translateAlternateColorCodes('&', "\n      &fAllies: &7(None)");
					String enemies = ChatColor.translateAlternateColorCodes('&', "\n      &fEnemies: &7(None)");
					ArrayList<Guild> relations = new ArrayList<Guild>();
					relations.addAll(g.getRelations().keySet());
					for(int i = 0; i < relations.size(); i++) {
						if(i + 1 == relations.size()) {
							if(g.getRelation(relations.get(i)) == Relation.ALLY) {
								if(allies.equals(ChatColor.translateAlternateColorCodes('&', "\n      &fAllies: &7(None)"))) {
									allies = ChatColor.translateAlternateColorCodes('&', "\n      &fAllies: &d");
								}
								allies = allies + relations.get(i).getName();
							}
							else if(g.getRelation(relations.get(i)) == Relation.ENEMY) {
								if(enemies.equals(ChatColor.translateAlternateColorCodes('&', "\n      &fEnemies: &7(None)"))) {
									enemies = ChatColor.translateAlternateColorCodes('&', "\n      &fEnemies: &d");
								}
								enemies = enemies + relations.get(i).getName();
							}
						}
						else {
							if(g.getRelation(relations.get(i)) == Relation.ALLY) {
								if(allies.equals(ChatColor.translateAlternateColorCodes('&', "\n      &fAllies: &7(None)"))) {
									allies = ChatColor.translateAlternateColorCodes('&', "\n      &fAllies: &d");
								}
								allies = allies + relations.get(i).getName() + ", ";
							}
							else if(g.getRelation(relations.get(i)) == Relation.ENEMY) {
								if(enemies.equals(ChatColor.translateAlternateColorCodes('&', "\n      &fEnemies: &7(None)"))) {
									enemies = ChatColor.translateAlternateColorCodes('&', "\n      &fEnemies: &d");
								}
								enemies = enemies + relations.get(i).getName() + ", ";
							}
						}
					}
					header.send();
					String toSend = "\n " + totalMembers  + totalOnline + "\n " + playerList + "\n \n" + allies + enemies + "\n \n" + ChatColor.translateAlternateColorCodes('&', "      &4Dark Red &5&l➜ &fOwner\n      &6Gold &5&l➜ &fOfficers\n      &dLight Purple &5&l➜ &fMembers") + "\n ";
					p.sendMessage(toSend);
				}
				else {
					handleGuildList(p, args);
				}
			}
			else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis guild does not exist!"));
				p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
			}
		}
		else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/guild who <name>"));
			p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
		}
	}

	@SuppressWarnings("deprecation")
	private void handleGuildCreate(Player p, String[] args) {
		if(args.length == 2) {
			if(Guild.hasGuild(p) == 0) {
				String guildName = args[1];
				if(Guild.isValidName(args[1])) {
					if(Guild.alreadyExists(guildName)) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis name is taken by another guild!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
					else {
						Guild g = Guild.loadNewGuild(guildName, p);
						creatingIsland.add(g);
						g.createIsland();
						creatingIsland.remove(g);
						GuildListeners.isForced.remove(GuildListeners.isForced.indexOf(p));
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &d&l&k:&d&lGUILD CREATED!&k:&r &fYou have created the guild &d" + guildName + "&f!"));
						p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&d&k:&dGUILD CREATED&k:"), ChatColor.translateAlternateColorCodes('&', "&fYou created the guild &d" + guildName));
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 2, 1);
					}
				}
				else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe name must be at least 4 characters long and must not contain special characters!"));
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
			}
			else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou are already in a guild! You must leave your current to create a new one!"));
				p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
			}
		}
		else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/guild create <name>"));
			p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
		}
	}

	private void handleGuildInvite(Player p, String[] args) {
		if(args.length == 2) {
			if(!Guild.getByPlayer(p).isOpen()) {
				if(Guild.getByPlayer(p).getMemberUuids().get(p.getUniqueId()).getRole() == Role.OWNER) {
					String playerName = args[1];
					if(Bukkit.getPlayerExact(playerName) != null) {
						Player invited = Bukkit.getPlayer(playerName);
						if(Guild.hasGuild(invited) == 0) {
							if(!GuildInvite.isInvitedBy(invited, Guild.getByPlayer(p))) {
								Guild.getByPlayer(p).sendGuildMessage(ChatColor.translateAlternateColorCodes('&', "&fThe owner of the guild &d" + p.getName() 
									+ " &fhas invited &d" + invited.getName() + "&f! They have &d60 seconds &fto accept!"));
								Guild.getByPlayer(p).sendGuildSound(Sound.ORB_PICKUP, 1);
								new GuildInvite(invited, Guild.getByPlayer(p));
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou have already invited " + invited.getName() + " to the guild!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else {
							if(Guild.getByPlayer(p).equals(Guild.getByPlayer(invited))) {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is already in your guild!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is already in another guild!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cOnly the owner of the guild can invite new members!"));
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
			}
			else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild is already open! There is no need for an invitation!"));
				p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
			}
		}
		else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/guild invite <name>"));
			p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
		}
	}

	@SuppressWarnings("deprecation")
	private void handleGuildJoin(Player p, String[] args) {
		if(args.length == 2) {
			if(Guild.hasGuild(p) == 0) {
				Guild g = searchGuild(args[1]);
				if(g != null) {
					if(g.isOpen()) {
						g.addMember(p, Role.MEMBER);
						GuildListeners.isForced.remove(GuildListeners.isForced.indexOf(p));
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &d&l&k:&d&lJOINED GUILD!&k:&r &fYou have joined the guild &d" + g.getName() + "&f!"));
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fType &d/guild go &fto teleport to your island!"));
						p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&d&k:&dJOINED GUILD&k:"), ChatColor.translateAlternateColorCodes('&', "&fYou joined the guild &d" + g.getName()));
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 2, 1);
					}
					else if(GuildInvite.isInvitedBy(p, g)) {
						GuildInvite invite = GuildInvite.getInviteByGuild(p, g);
						invite.accept();
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis guild requires an invitation!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
				else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis guild does not exist!"));
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
			}
			else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou are already in a guild! You must leave your current to join a new one!"));
				p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
			}
		}
		else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/guild join <name>"));
			p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
		}
	}
	
	private void handleGuildLeave(Player p, String[] args) {
		Guild g = Guild.getByPlayer(p);
		if(g.getGuildPlayer(p).getRole() != Role.OWNER) {
			if(args.length == 2) {
				if(args[1].equals("confirm")) {
					if(confirmLeave.containsKey(p) && confirmLeave.get(p) >= System.currentTimeMillis()) {
						g.getGuildPlayer(p).leave();
						confirmLeave.remove(p);
						if(!p.hasPermission("*")) {
							new Cooldown(p.getUniqueId().toString(), CooldownType.LEAVE);
						}
					}
					else if(confirmLeave.containsKey(p)){
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour request to leave your guild has expired!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
					else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThere is no request to leave your guild! Please type /guild leave in order to create a request!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
				}
			}
			else if(args.length == 1) {
				if(Cooldown.hasCooldown(p, CooldownType.LEAVE) == null) {
					confirmLeave.put(p, System.currentTimeMillis() + 10000);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &c&lLEAVE GUILD? &fWith this action you will completely leave your guild!\n "
							+ "&5&l» &fAll your items, balance will be given to your guild's members!\n &5&l» &fIf you still want to leave your guild, type &d/guild leave confirm&f."));
					p.playSound(p.getLocation(), Sound.NOTE_STICKS, 2, 1);
				}
				else {
					Cooldown c = Cooldown.hasCooldown(p, CooldownType.LEAVE);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot do that for another " + getDuration(c.getDuration() * 1000) + "!"));
				}
			}
			else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/guild leave"));
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
			}
		}
		else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cSince you are the owner, you must disband the guild through the guild settings!"));
			p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
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
