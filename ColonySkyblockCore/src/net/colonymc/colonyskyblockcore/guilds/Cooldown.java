package net.colonymc.colonyskyblockcore.guilds;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.Main;

public class Cooldown implements Listener {
	
	String uuid;
	CooldownType type;
	long duration;
	long ticksLeft;
	static ArrayList<Cooldown> cooldowns = new ArrayList<Cooldown>();
	
	public Cooldown(String uuid, CooldownType type) {
		this.uuid = uuid;
		this.type = type;
		this.duration = decideDuration();
		ticksLeft = duration;
		cooldowns.add(this);
		Database.sendStatement("INSERT INTO PlayerCooldowns (playerUuid, type, shouldEnd) VALUES "
				+ "('" + uuid + "', '" + type.name() + "', " + (System.currentTimeMillis() + duration * 50) + ");");
		new BukkitRunnable() {
			@Override
			public void run() {
				if(ticksLeft == 0) {
					remove();
					cancel();
				}
				ticksLeft--;
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}
	
	public Cooldown(String uuid, CooldownType type, long duration) {
		this.uuid = uuid;
		this.type = type;
		this.duration = duration;
		ticksLeft = duration;
		cooldowns.add(this);
		new BukkitRunnable() {
			@Override
			public void run() {
				if(ticksLeft == 0) {
					remove();
					cancel();
				}
				ticksLeft--;
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}
	
	public int decideDuration() {
		int dur = 0;
		switch(type) {
		case DISBAND:
			dur = 432000;
			break;
		case LEAVE:
			dur = 216000;
			break;
		case RENAME:
			dur = 432000;
			break;
		case WAR:
			dur = 72000;
			break;
		default:
			break;
		}
		return dur;
	}
	
	public void remove() {
		Database.sendStatement("DELETE FROM PlayerCooldowns WHERE playerUuid='" + uuid + "' AND type='" + type.name() + "';");
		cooldowns.remove(cooldowns.indexOf(this));
	}
	
	public CooldownType getType() {
		return type;
	}
	
	public long getDuration() {
		return ticksLeft/20;
	}
	
	public static Cooldown hasCooldown(Player p, CooldownType t) {
		for(Cooldown c : cooldowns) {
			if(c.type == t && c.uuid.equals(p.getUniqueId().toString())) {
				return c;
			}
		}
		return null;
	}

	
}
