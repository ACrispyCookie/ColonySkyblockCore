package net.colonymc.colonyskyblockcore.util.messages;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyskyblockcore.Main;

public class Conversation {
	
	static HashMap<Player, Conversation> conversations = new HashMap<Player, Conversation>();
	Player sender;
	Player recipient;
	boolean open;
	int counter = 0;
	
	public Conversation(Player sender, Player recipient) {
		this.sender = sender;
		this.recipient = recipient;
	}
	
	public void open() {
		conversations.put(sender, this);
		conversations.put(recipient, this);
		open = true;
		autoClose.runTaskTimerAsynchronously(Main.getInstance(), 0L, 1L);
	}
	
	public void close() {
		conversations.remove(sender);
		conversations.remove(recipient);
		open = false;
	}
	
	public void resetTimer() {
		counter = 0;
	}
	
	public void sendMessage(Player player, String message) {
		if(player == sender) {
			recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f(From &d" + sender.getName() + "&f) &7") + message);
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f(To &d" + recipient.getName() + "&f) &7") + message);
		}
		else if(player == recipient) {
			recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f(To &d" + sender.getName() + "&f) &7") + message);
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f(From &d" + recipient.getName() + "&f) &7") + message);
		}
		sender.playSound(sender.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
		recipient.playSound(recipient.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
	}
	
	BukkitRunnable autoClose = new BukkitRunnable() {
		@Override
		public void run() {
			if(counter == 6000) {
				close();
				cancel();
			}
			counter++;
		}
	};
	

}
