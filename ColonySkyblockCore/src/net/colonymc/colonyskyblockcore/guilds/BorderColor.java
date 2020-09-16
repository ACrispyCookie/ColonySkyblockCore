package net.colonymc.colonyskyblockcore.guilds;

import org.bukkit.ChatColor;

public enum BorderColor {
	BLUE(ChatColor.getByChar('b'), "Blue"),
	GREEN(ChatColor.getByChar('a'), "Green"),
	RED(ChatColor.getByChar('c'), "Red");
	
	public ChatColor c;
	public String name;
	
	BorderColor(ChatColor c, String name){
		this.c = c;
		this.name = name;
	}
}
