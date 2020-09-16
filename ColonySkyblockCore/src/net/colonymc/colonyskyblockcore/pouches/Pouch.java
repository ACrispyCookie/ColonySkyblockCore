package net.colonymc.colonyskyblockcore.pouches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.Range;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.api.Main;
import net.colonymc.api.player.Title;
import net.colonymc.api.player.TitleAction;
import net.colonymc.colonyskyblockcore.guilds.Guild;

public class Pouch {
	
	Player p;
	PouchType type;
	int level;
	int won;
	static ArrayList<Pouch> activePouches = new ArrayList<Pouch>(); 
	
	public Pouch(Player p, PouchType type, int level) {
		this.p = p;
		this.type = type;
		this.level = level;
		activePouches.add(this);
	}
	
	public void activate() {
		won = ThreadLocalRandom.current().nextInt(getLevelMap(type).get(level).getMinimum(), getLevelMap(type).get(level).getMaximum() + 1);
		playAnimation();
	}
	
	public void playAnimation() {
		String wonString = Guild.balance(this.won);
		new BukkitRunnable() {
			int i = 0;
			int endOfString = 1;
			@Override
			public void run() {
				if(i == 0) {
					Title t = new Title(TitleAction.TITLE).duration(1000).fadeIn(0).fadeOut(0).text(ChatColor.getByChar(type.color) + "" + wonString.substring(0, endOfString) + ChatColor.MAGIC + wonString.substring(endOfString));
					t.send(p);
					Title sub = new Title(TitleAction.SUBTITLE).duration(1000).fadeIn(0).fadeOut(0).text(ChatColor.getByChar(type.color) + "Rolling a " + type.pouchType.substring(2) + " pouch...");
					sub.send(p);
					p.playSound(p.getLocation(), Sound.ANVIL_LAND, 2, 1);
					endOfString++;
				}
				else if(i % 10 == 0) {
					if(endOfString < wonString.length()) {
						Title t = new Title(TitleAction.TITLE).duration(1000).fadeIn(0).fadeOut(0).text(ChatColor.getByChar(type.color) + "" + wonString.substring(0, endOfString) + ChatColor.MAGIC + wonString.substring(endOfString));
						t.send(p);
						Title sub = new Title(TitleAction.SUBTITLE).duration(1000).fadeIn(0).fadeOut(0).text(ChatColor.getByChar(type.color) + "Rolling a " + type.pouchType.substring(2) + " pouch...");
						sub.send(p);
						p.playSound(p.getLocation(), Sound.ANVIL_LAND, 2, 1);
						endOfString++;
					}
					else {
						Title t = new Title(TitleAction.TITLE).duration(8).fadeIn(0).fadeOut(2).text(ChatColor.getByChar(type.color) + "" + wonString.substring(0, endOfString) + ChatColor.MAGIC + wonString.substring(endOfString));
						t.send(p);
						Title sub = new Title(TitleAction.SUBTITLE).duration(8).fadeIn(0).fadeOut(2).text(ChatColor.getByChar(type.color) + "Rolling a " + type.pouchType.substring(2) + " pouch...");
						sub.send(p);
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 2, 1);
						if(type == PouchType.MONEY) {
							Guild.getByPlayer(p).getGuildPlayer(p).addBalance(won);
						}
						else if(type == PouchType.DUST) {
							Guild.getByPlayer(p).getGuildPlayer(p).addDust(won);
						}
						cancel();
						activePouches.remove(Pouch.this);
					}
				}
				i++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	
	public static HashMap<Integer, Range<Integer>> getLevelMap(PouchType type){
		HashMap<Integer, Range<Integer>> map = new HashMap<Integer, Range<Integer>>();
		if(type == PouchType.MONEY) {
			map.put(1, Range.between(1, 10000));
			map.put(2, Range.between(10001, 100000));
			map.put(3, Range.between(100001, 250000));
			map.put(4, Range.between(250001, 500000));
			map.put(5, Range.between(500001, 1000000));
		}
		else if(type == PouchType.DUST) {
			map.put(1, Range.between(1, 1000));
			map.put(2, Range.between(1001, 2000));
			map.put(3, Range.between(2001, 5000));
			map.put(4, Range.between(5001, 10000));
			map.put(5, Range.between(10001, 20000));
		}
		else {
			return null;
		}
		return map;
	}
	
	public static Pouch getByPlayer(Player p) {
		for(Pouch po : activePouches) {
			if(po.p.equals(p)) {
				return po;
			}
		}
		return null;
	}

}
