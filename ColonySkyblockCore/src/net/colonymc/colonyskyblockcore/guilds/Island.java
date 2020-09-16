package net.colonymc.colonyskyblockcore.guilds;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.CuboidRegion;

import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.Main;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.WorldBorder;

public class Island {
	
	Guild g;
	int borderSize;
	boolean visitation;
	BorderColor borderColor;
	int[] spawnLocation;
	double[] homeLocation;
	public static World w = Bukkit.getWorld("islands");
	static HashMap<OfflinePlayer, Island> visitors = new HashMap<OfflinePlayer, Island>();
	static ArrayList<Island> loadedIslands = new ArrayList<Island>();
	
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
			try {
				File file = new File("plugins/ColonySkyblockCore/island.schematic");
				com.sk89q.worldedit.world.World world = BukkitUtil.getLocalWorld(w);
				com.sk89q.worldedit.Vector v = BukkitUtil.toVector(new Location(w, this.spawnLocation[0], 100, 0));
				EditSession session = ClipboardFormat.SCHEMATIC.load(file).paste(world, v);
				session.flushQueue();
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
	
	@SuppressWarnings("deprecation")
	public void delete() {
		if(loadedIslands.contains(this)) {
			loadedIslands.remove(loadedIslands.indexOf(this));
			com.sk89q.worldedit.world.World world = FaweAPI.getWorld("islands");
			com.sk89q.worldedit.Vector v1 = BukkitUtil.toVector(new Location(w, this.spawnLocation[0] - this.borderSize/2, 256, -this.borderSize/2));
			com.sk89q.worldedit.Vector v2 = BukkitUtil.toVector(new Location(w, this.spawnLocation[0] + this.borderSize/2, 0, this.borderSize/2));
			CuboidRegion r = new CuboidRegion(world, v1, v2);
			EditSession session = new EditSessionBuilder(world).fastmode(true).build();
			session.setBlocks(r, new BaseBlock(0));
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
