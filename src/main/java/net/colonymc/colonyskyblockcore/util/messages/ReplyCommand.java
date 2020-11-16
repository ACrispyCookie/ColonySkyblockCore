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
	
	final String usage = ChatColor.translateAlternateColorCodes('&', " &5&l» &fUsage: &d/reply <message>");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(args.length > 0) {
				if(Conversation.conversations.containsKey(p)) {
					List<String> text = new ArrayList<>(Arrays.asList(args.clone()));
					StringBuilder message = new StringBuilder();
					for(String s : text) {
						message.append(s).append(" ");
					}
					Conversation conv = Conversation.conversations.get(p);
					conv.resetTimer();
					conv.sendMessage(p, message.toString());
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
