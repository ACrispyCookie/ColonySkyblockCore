package net.colonymc.colonyskyblockcore.guilds;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.Main;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.WorldBorder;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Island {
	
	final Guild g;
	int borderSize;
	boolean visitation;
	BorderColor borderColor;
	int[] spawnLocation;
	double[] homeLocation;
	public static final World w = Bukkit.getWorld("islands");
	static final HashMap<OfflinePlayer, Island> visitors = new HashMap<>();
	static final ArrayList<Island> loadedIslands = new ArrayList<>();
	
	public Island(Guild g, int borderSize, int[] spawnLocation, double[] homeLocation, boolean visitation, BorderColor color) {
		this.g = g;
		this.borderSize = borderSize;
		this.spawnLocation = spawnLocation;
		this.homeLocation = homeLocation;
		this.visitation = visitation;
		this.borderColor = color;
	}
	
	public void sendBorder(Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(p.getWorld().equals(Island.getWorld())) {
					WorldBorder worldBorder = new WorldBorder();
					worldBorder.setSize(borderSize);
					worldBorder.setCenter(spawnLocation[0], spawnLocation[2]);
					worldBorder.world = ((CraftWorld) p.getWorld()).getHandle();
					if(borderColor == BorderColor.GREEN) {
						worldBorder.transitionSizeBetween(borderSize, borderSize + 0.1, 10000000);
					}
					else if(borderColor == BorderColor.RED) {
						worldBorder.transitionSizeBetween(borderSize, borderSize - 0.1, 10000000);
					}
					PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE);
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
				}
			}
		}.runTaskLaterAsynchronously(Main.getInstance(), 1L);
	}
	
	public void create() {
		if(!g.hasIsland()) {
			this.borderSize = 100;
			this.spawnLocation = new int[] {getNewX(), 100, 0};
			this.homeLocation = new double[] {spawnLocation[0], spawnLocation[1], spawnLocation[2]};
			this.borderColor = BorderColor.BLUE;
			File file = new File("plugins/ColonySkyblockCore/arena.schematic");
			try {
				EditSession editSession = ClipboardFormats.findByFile(file).load(file).paste(FaweAPI.getWorld(w.getName()), new Vector(this.spawnLocation[0], 100, 0));
			} catch (IOException e) {
				e.printStackTrace();
			}
			loadedIslands.add(this);
			g.island = this;
			Database.sendStatement("INSERT INTO IslandInfo (id, borderSize, islandX, islandY, islandZ, homeX, homeY, homeZ, borderColor) VALUES"
					+ " (" + g.getId() + ", 100, " + spawnLocation[0] + ", 100, 0, " + homeLocation[0] + ", 100, 0, 'BLUE');");
			for(GuildPlayer p : g.getMemberUuids().values()) {
				if(p.getPlayer().isOnline()) {
					sendPlayer((Player) p.getPlayer(), false);
				}
			}
		}
	}

	public void sendPlayer(Player p, boolean ignorePrivate) {
		if(this.g.equals(Guild.getByPlayer(p))) {
			visitors.put(p, this);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fTeleporting you to your island..."));
			p.teleport(new Location(w, this.homeLocation[0], this.homeLocation[1], this.homeLocation[2]));
			p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 2, 1);
			sendBorder(p);
		}
		else if(this.visitation || GuildCommand.bypasses.contains(p) || ignorePrivate) {
			visitors.put(p, this);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fTeleporting you to &d" + g.getName() + "'s &fisland..."));
			p.teleport(new Location(w, this.homeLocation[0], this.homeLocation[1], this.homeLocation[2]));
			p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 2, 1);
			sendBorder(p);
		}
		else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis island is currently private!"));
			p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
		}
	}
	
	public void delete() {
		if(loadedIslands.contains(this)) {
			loadedIslands.remove(this);
			CuboidRegion region = new CuboidRegion(new Vector(this.spawnLocation[0] - this.borderSize/2, 256, -this.borderSize/2),
					new Vector(this.spawnLocation[0] + this.borderSize/2, 0, this.borderSize/2));
			EditSession session = FaweAPI.getEditSessionBuilder(FaweAPI.getWorld(w.getName())).fastmode(true).build();
			session.setBlocks((Region) region, new BaseBlock(0));
			session.flushQueue();
		}
	}
	
	public void toggleVisitation() {
		this.setVisitation(!visitation);
		this.g.sendGuildMessage("&fIsland's access has been changed to " + getVisitationString() + "&f!");
		this.g.sendGuildSound(Sound.ENDERDRAGON_WINGS, 1);
	}
	
	public void cycleBorder() {
		if(g.getIsland().getBorderColor().ordinal() == 2) {
			g.getIsland().setBorderColor(BorderColor.values()[0]);
		}
		else {
			g.getIsland().setBorderColor(BorderColor.values()[g.getIsland().getBorderColor().ordinal() + 1]);
		}
		g.sendGuildMessage("&fIsland's border color had been changed to " + borderColor.c + borderColor.name + "&f!");
		g.sendGuildSound(Sound.ENDERDRAGON_WINGS, 1);
	}
	
	public Guild getGuild() {
		return g;
	}
	
	public int[] getSpawnLocation() {
		return spawnLocation;
	}
	
	public double[] getHomeLocation() {
		return homeLocation;
	}
	
	public int getBorderSize() {
		return borderSize;
	}
	
	public boolean getVisitation() {
		return visitation;
	}
	
	public BorderColor getBorderColor() {
		return borderColor;
	}

	public static World getWorld() {
		return w;
	}
	
	public static HashMap<OfflinePlayer, Island> getVisitorMap() {
		return visitors;
	}
	
	private String getVisitationString() {
		if(visitation) {
			return ChatColor.translateAlternateColorCodes('&', "&aPublic");
		}
		else {
			return ChatColor.translateAlternateColorCodes('&', "&cPrivate");
		}
	}
	
	private int getNewX() {
		try {
			ResultSet rs = Database.getResultSet("SELECT * FROM IslandInfo ORDER BY islandX ASC;");
			int x = 0;
			while(rs.next()) {
				if(rs.getInt("islandX") > x) {
					return x;
				}
				else {
					x = x + 1500;
				}
			}
			return x;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private void setVisitation(boolean visitation) {
		this.visitation = visitation;
		Database.sendStatement("UPDATE IslandInfo SET visitation=" + visitation + " WHERE id=" + g.getId() + ";");
	}
	
	private void setBorderColor(BorderColor c) {
		borderColor = c;
		Database.sendStatement("UPDATE IslandInfo SET borderColor='" + c.name() + "' WHERE id=" + g.getId() + ";");
		for(OfflinePlayer p : visitors.keySet()) {
			if(visitors.get(p).equals(this)) {
				sendBorder((Player) p);
			}
		}
	}
	
	public static Island getByPlayer(Player p) {
		Guild guild = Guild.getByPlayer(p);
		return getByGuild(guild);
	}
	
	public static Island getByPlayer(OfflinePlayer p) {
		Guild guild = Guild.getByPlayer(p);
		return getByGuild(guild);
	}
	
	public static Island getByGuild(Guild g) {
		for(Island i : loadedIslands) {
			if(i.getGuild().equals(g)) {
				return i;
			}
		}
		return loadIsland(g);
	}

	private static Island loadIsland(Guild g) {
		ResultSet rs = Database.getResultSet("SELECT * FROM IslandInfo WHERE id=" + g.getId() + ";");
		Island i = null;
		try {
			if(rs.next()) {
				int borderSize = rs.getInt("borderSize");
				BorderColor borderColor = BorderColor.valueOf(rs.getString("borderColor"));
				int[] spawnLocation = new int[] {rs.getInt("islandX"), rs.getInt("islandY"), rs.getInt("islandZ")};
				double[] homeLocation = new double[] {rs.getDouble("homeX"), rs.getDouble("homeY"), rs.getDouble("homeZ")};
				i = new Island(g, borderSize, spawnLocation, homeLocation, rs.getBoolean("visitation"), borderColor);
				loadedIslands.add(i);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

}
