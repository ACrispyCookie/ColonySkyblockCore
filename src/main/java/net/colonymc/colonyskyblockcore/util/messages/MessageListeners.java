package net.colonymc.colonyskyblockcore.util.messages;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MessageListeners implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(Conversation.conversations.containsKey(p)) {
			Conversation conv = Conversation.conversations.get(p);
			if(conv.sender == p) {
				conv.recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &c" + p.getName() + " left the server so your conversation closed!"));
				conv.close();
			}
			else if(conv.recipient == p) {
				conv.sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &c" + p.getName() + " left the server so your conversation closed!"));
				conv.close();
			}
		}
	}
	
}
