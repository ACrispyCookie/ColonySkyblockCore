package net.colonymc.colonyskyblockcore.guilds;

import org.bukkit.ChatColor;

public enum BorderColor {
	BLUE(ChatColor.getByChar('b'), "Blue"),
	GREEN(ChatColor.getByChar('a'), "Green"),
	RED(ChatColor.getByChar('c'), "Red");
	
	public final ChatColor c;
	public final String name;
	
	BorderColor(ChatColor c, String name){
		this.c = c;
		this.name = name;
	}
}
