package net.colonymc.colonyskyblockcore;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.colonymc.colonyskyblockcore.guilds.Guild;

public class Placeholders extends PlaceholderExpansion {
	
	Plugin plugin;
	
	public Placeholders(Plugin main) {
		this.plugin = main;
	}
	
	@Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if(p == null) {
            return "";
        }
        if(identifier.equals("guild")) {
        	if(Guild.getByPlayer(p) != null) {
                return Guild.getByPlayer(p).getName();
        	}
        	else {
        		return "No guild";
        	}
        }
        if(identifier.equals("guild_role")) {
        	if(Guild.getByPlayer(p) != null) {
                return Guild.getByPlayer(p).getGuildPlayer(p).getRole().name;
        	}
        	else {
        		return "";
        	}
        }
        if(identifier.equals("guild_role_color")) {
        	if(Guild.getByPlayer(p) != null) {
                return String.valueOf(Guild.getByPlayer(p).getGuildPlayer(p).getRole().color.getChar());
        	}
        	else {
        		return "";
        	}
        }
        if(identifier.equals("balance")) {
        	if(Guild.getByPlayer(p) != null) {
                return String.valueOf(Guild.getByPlayer(p).getGuildPlayer(p).getBalance());
        	}
        	else {
        		return "";
        	}
        }
        if(identifier.equals("dust")) {
        	if(Guild.getByPlayer(p) != null) {
                return String.valueOf(Guild.getByPlayer(p).getGuildPlayer(p).getDust());
        	}
        	else {
        		return "";
        	}
        }
        if(identifier.equals("rank_prefix")) {
        	if(PlaceholderAPI.setPlaceholders(p, "%vault_group%").equalsIgnoreCase("knight")) {
        		return "&7[Knight]";
        	}
        	else {
            	String s = PlaceholderAPI.setPlaceholders(p, "%vault_prefix%");
            	return s.substring(0, s.length() - 5);
        	}
        }
        return null;
    }
	
	@Override
    public boolean persist(){
        return true;
    }
	
	@Override
    public boolean canRegister(){
        return true;
    }

	@Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

	@Override
    public String getIdentifier(){
        return "colonymc";
    }

	@Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

}
