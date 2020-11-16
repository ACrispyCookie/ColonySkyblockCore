package net.colonymc.colonyskyblockcore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.colonymc.colonyskyblockcore.kits.Kit;

public class SkyblockPlayer implements Listener {

	static final ArrayList<SkyblockPlayer> skyblockPlayers = new ArrayList<>();
	
	Player p;
	final HashMap<Kit, Long> kits = new HashMap<>();
	
	public SkyblockPlayer(Player p) {
		this.p = p;
		loadInfo();
		skyblockPlayers.add(this);
	}
	
	public SkyblockPlayer() {
		
	}
	
	public void update() {
		kits.clear();
		loadInfo();
	}
	
	private void loadInfo() {
		ResultSet rs = Database.getResultSet("SELECT * FROM PlayerKits WHERE playerUuid='" + p.getUniqueId().toString() + "'");
		try {
			while(rs.next()) {
				kits.put(Kit.getByName(rs.getString("kit")), rs.getLong("canBeClaimedAgainAt"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<Kit, Long> getKits() {
		return kits;
	}
	
	public static SkyblockPlayer getByPlayer(Player p) {
		for(SkyblockPlayer sbp : skyblockPlayers) {
			if(sbp.p.equals(p)) {
				return sbp;
			}
		}
		return null;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		new SkyblockPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		skyblockPlayers.remove(SkyblockPlayer.getByPlayer(e.getPlayer()));
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e) {
		skyblockPlayers.remove(SkyblockPlayer.getByPlayer(e.getPlayer()));
	}
	
}
