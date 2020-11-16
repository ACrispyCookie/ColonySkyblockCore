package net.colonymc.colonyskyblockcore.guilds.trade;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.Relation;

public class TradeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(args.length == 1) {
				if(Bukkit.getPlayerExact(args[0]) != null) {
					Player anotherP = Bukkit.getPlayerExact(args[0]);
					if(Trade.isInTrade(anotherP) != null) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is already trading with someone else!"));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
					}
					else {
						if(Trade.hasBeenRequested(Guild.getByPlayer(p).getGuildPlayer(p), Guild.getByPlayer(anotherP).getGuildPlayer(anotherP)) != null) {
							Trade t = Trade.hasBeenRequested(Guild.getByPlayer(p).getGuildPlayer(p), Guild.getByPlayer(anotherP).getGuildPlayer(anotherP));
							if(!t.accepted) {
								if(Guild.getByPlayer(p).getRelation(Guild.getByPlayer(anotherP)) != Relation.ENEMY) {
									t.accept();
								}
								else {
									t.cancel();
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou can't trade with a player from an enemy!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou already have an active trade with this player!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else if(Trade.hasBeenRequested(Guild.getByPlayer(anotherP).getGuildPlayer(anotherP), Guild.getByPlayer(p).getGuildPlayer(p)) == null) {
							if(Guild.getByPlayer(p).getRelation(Guild.getByPlayer(anotherP)) != Relation.ENEMY) {
								new Trade(Guild.getByPlayer(p).getGuildPlayer(p), Guild.getByPlayer(anotherP).getGuildPlayer(anotherP));
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou can't trade with a player from an enemy!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else {
							Trade t = Trade.hasBeenRequested(Guild.getByPlayer(anotherP).getGuildPlayer(anotherP), Guild.getByPlayer(p).getGuildPlayer(p));
							if(!t.accepted) {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou have already requested from this player to trade!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou already have an active trade with this player!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
					}
				}
				else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is not online"));
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
			}
			else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/trade <player>"));
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
			}
		}
		else {
			sender.sendMessage("Only players can trade with other players!");
		}
		return false;
	}

}
