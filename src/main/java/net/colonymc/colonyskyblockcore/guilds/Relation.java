package net.colonymc.colonyskyblockcore.guilds;

import org.bukkit.ChatColor;

public enum Relation {
	NEUTRAL(ChatColor.getByChar('f'), "Neutral"),
	ALLY(ChatColor.getByChar('d'), "Ally"),
	ENEMY(ChatColor.getByChar('4'), "Enemy");
	
	public final ChatColor color;
	public final String name;
	
	Relation(ChatColor color, String name) {
		this.color = color;
		this.name = name;
	}
}
