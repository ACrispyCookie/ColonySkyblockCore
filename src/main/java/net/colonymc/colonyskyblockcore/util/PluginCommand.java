package net.colonymc.colonyskyblockcore.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import net.colonymc.colonyspigotapi.api.player.visuals.ChatMessage;

public class PluginCommand implements CommandExecutor, Listener{

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!sender.hasPermission("*")) {
			ChatMessage header = new ChatMessage("&5&lCustom Plugins").centered(true).addRecipient(sender);
			header.send();
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &dColonySkyblockCore: &fEverything from the guild system to the aution house, the minions and the custom bosses is from this plugin. "
					+ " It was coded by the owner of the server &d&lACrispyCookie&f."));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &dColonyStaffSystem: &fThe basic plugin for moderating the network. It is designed to be as easy to use as possible!"
					+ " &fIt was also coded by the owner of the server &d&lACrispyCookie&f."));
		}
		else {
			ChatMessage header = new ChatMessage("&a&lAll Plugins (" + Bukkit.getPluginManager().getPlugins().length + ")").centered(true).addRecipient(sender);
			header.send();
			StringBuilder message = new StringBuilder();
			int i = 0;
			for(Plugin p : Bukkit.getPluginManager().getPlugins()) {
				i++;
				if(i != Bukkit.getPluginManager().getPlugins().length) {
					message.append(ChatColor.LIGHT_PURPLE).append(p.getName()).append(", ");
				}
				else {
					message.append(ChatColor.LIGHT_PURPLE).append(p.getName());
				}
			}
			sender.sendMessage(message.toString());
		}
		return false;
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if(e.getMessage().contains(":") && !e.getPlayer().hasPermission("*")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease do not use this syntax on your commands!"));
		}
		else if(e.getMessage().equals("/plugins") || e.getMessage().equals("/pl") || e.getMessage().equals("/ver") || e.getMessage().equals("/version")) {
			e.setMessage("/plugin");
		}
	}

}
