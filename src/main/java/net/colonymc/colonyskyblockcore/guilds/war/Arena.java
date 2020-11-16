package net.colonymc.colonyskyblockcore.guilds.war;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.colonymc.colonyskyblockcore.Main;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Arena {
	
	final Location firstLocation;
	final Location secondLocation;
	final Location pos1;
	final Location pos2;
	final Location fw1;
	final Location fw2;
	final TeamDeathmatch tdm;
	BukkitTask fireworks;
	public static final World w = Bukkit.getWorld("arenas");
	static final ArrayList<Arena> arenas = new ArrayList<>();
	final ArrayList<Block> blocksPlaced = new ArrayList<>();
	
	public Arena(TeamDeathmatch tdm) {
		this.tdm = tdm;
		if(arenas.size() == 0) {
			firstLocation = new Location(Bukkit.getWorld("arenas"), 102, 100, 24, 180, 0);
			secondLocation = new Location(Bukkit.getWorld("arenas"), 0, 100, 0, 0, 0);
		}
		else {
			Arena a = arenas.get(arenas.size() - 1);
			firstLocation = a.getFirstLocation().add(1500, 0, 0);
			secondLocation = a.getSecondLocation().add(1500, 0, 0);
		}
		fw1 = secondLocation.clone().add(76, -1, -2);
		fw2 = secondLocation.clone().add(26, -1, 26);
		pos1 = secondLocation.clone().add(-8, -6, -31);
		pos2 = firstLocation.clone().add(8, 33, 29);
		paste();
	}
	
	private void paste() {
		File file = new File("plugins/ColonySkyblockCore/arena.schematic");
		try {
			EditSession editSession = ClipboardFormats.findByFile(file).load(file).paste(FaweAPI.getWorld(w.getName()), new Vector(pos1.getX(), pos1.getY(), pos1.getZ()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void clear() {
		CuboidRegion region = new CuboidRegion(new Vector(pos1.getX(), pos1.getY(), pos1.getZ()),
				new Vector(pos2.getX(), pos2.getY(), pos2.getZ()));
		EditSession session = FaweAPI.getEditSessionBuilder(FaweAPI.getWorld(w.getName())).fastmode(true).build();
		session.setBlocks((Region) region, new BaseBlock(0));
		session.flushQueue();
		blocksPlaced.clear();
	}
	
	public void addBars() {
		int y = 2;
		int x = 0;
		for(int i = 0; i < 12; i++) {
			if(i % 4 == 0) {
				y--;
				x = 0;
			}
			Block block = secondLocation.clone().add(-x, y, 2).getBlock();
			Block block1 = firstLocation.clone().add(x, y, -2).getBlock();
			block.setType(Material.IRON_FENCE);
			block1.setType(Material.IRON_FENCE);
			x++;
		}
	}
	
	public void removeBars() {
		int y = 2;
		int x = 0;
		for(int i = 0; i < 12; i++) {
			if(i % 4 == 0) {
				y--;
				x = 0;
			}
			Block block = secondLocation.clone().add(-x, y, 2).getBlock();
			Block block1 = firstLocation.clone().add(x, y, -2).getBlock();
			block.setType(Material.AIR);
			block1.setType(Material.AIR);
			x++;
		}
	}
	
	public void sendSpectator(Player p) {
		p.teleport(firstLocation.clone().subtract(51, 0, 12).add(0, 6, 0));
	}
	
	public void startFw() {
		fireworks = new BukkitRunnable() {
			@Override
			public void run() {
				Firework fw = Arena.w.spawn(getRandomLocation(), Firework.class);
				FireworkMeta fwm = fw.getFireworkMeta();
				Builder builder = FireworkEffect.builder();
				fwm.addEffect(builder.flicker(true).withColor(Color.FUCHSIA).build());
				fwm.addEffect(builder.trail(true).build());
				fwm.addEffect(builder.withFade(Color.PURPLE).build());
				fwm.setPower(2);
				fw.setFireworkMeta(fwm);
			}
		}.runTaskTimer(Main.getInstance(), 0L, 10L);
	}
	
	public boolean isInArena(Player p) {
		return (p.getLocation().getX() <= Math.max(pos1.getX(), pos2.getX())
				&& p.getLocation().getX() >= Math.min(pos1.getX(), pos2.getX()))
				&& (p.getLocation().getY() <= Math.max(pos1.getY(), pos2.getY())
				&& p.getLocation().getY() >= Math.min(pos1.getY(), pos2.getY()))
				&& (p.getLocation().getZ() <= Math.max(pos1.getZ(), pos2.getZ())
				&& p.getLocation().getZ() >= Math.min(pos1.getZ(), pos2.getZ()));
	}
	
	public boolean isInArena(Location loc) {
		if(loc.getWorld().equals(w)) {
			return (loc.getX() <= Math.max(pos1.getX(), pos2.getX())
					&& loc.getX() >= Math.min(pos1.getX(), pos2.getX()))
					&& (loc.getY() <= Math.max(pos1.getY(), pos2.getY())
					&& loc.getY() >= Math.min(pos1.getY(), pos2.getY()))
					&& (loc.getZ() <= Math.max(pos1.getZ(), pos2.getZ())
					&& loc.getZ() >= Math.min(pos1.getZ(), pos2.getZ()));
		}
		return false;
	}
	
	public static Arena getByLocation(Location loc) {
		for(Arena a : arenas) {
			System.out.println(a.isInArena(loc));
			if(a.isInArena(loc)) {
				return a;
			}
		}
		return null;
	}
	
	public Location getRandomLocation() {
		Location loc = new Location(w, 0, 100, 0);
		Random r = new Random();
		loc.setX(r.nextInt((fw1.getBlockX() - fw2.getBlockX()) + 1) + fw2.getBlockX());
		loc.setZ(r.nextInt((fw2.getBlockZ() - fw1.getBlockZ()) + 1) + fw1.getBlockZ());
		return loc;
	}
	
	public TeamDeathmatch getTDM() {
		return tdm;
	}
	
	public Location getFirstLocation() {
		return firstLocation.clone();
	}
	
	public Location getSecondLocation() {
		return secondLocation.clone();
	}
	
	public static Arena getByTDM(TeamDeathmatch tdm) {
		for(Arena a : arenas) {
			if(a.tdm.equals(tdm)) {
				return a;
			}
		}
		return null;
	}

}
