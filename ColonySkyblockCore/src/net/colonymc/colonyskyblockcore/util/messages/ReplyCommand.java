package net.colonymc.colonyskyblockcore.util.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.colonymc.colonyskyblockcore.MainMessages;

public class ReplyCommand implements CommandExecutor {
	
	String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/reply <message>");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(args.length > 0) {
				if(Conversation.conversations.containsKey(p)) {
					List<String> text = new ArrayList<String>(Arrays.asList(args.clone()));
					String message = "";
					for(String s : text) {
						message = message + s + " ";
					}
					Conversation conv = Conversation.conversations.get(p);
					conv.resetTimer();
					conv.sendMessage(p, message);
				}
				else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou didn't open any conversations in the last 5 minutes!"));
				}
			}
			else {
				p.sendMessage(usage);
			}
		}
		else {
			sender.sendMessage(MainMessages.onlyPlayers);
		}
		return false;
	}

}
