package net.colonymc.colonyskyblockcore.guilds;

import org.bukkit.ChatColor;

public enum Role {
	MEMBER(ChatColor.getByChar('d'), "Member"),
	OFFICER(ChatColor.getByChar('6'), "Officer"),
	OWNER(ChatColor.getByChar('4'), "Owner");
	
	public final ChatColor color;
	public final String name;
	
	Role(ChatColor color, String name) {
		this.color = color;
		this.name = name;
	}
}
