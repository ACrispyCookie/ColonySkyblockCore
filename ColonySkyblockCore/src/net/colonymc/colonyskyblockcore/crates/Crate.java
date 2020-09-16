package net.colonymc.colonyskyblockcore.crates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import net.colonymc.api.player.PublicHologram;
import net.colonymc.colonyskyblockcore.Main;

public class Crate implements Listener {
	
	Location loc;
	EnderCrystal en;
	PublicHologram holo;
	
	public Crate() {
		loc = new Location(Bukkit.getWorld(Main.getInstance().getConfig().getString("crates.world")), 
				Main.getInstance().getConfig().getDouble("crates.x"),
				Main.getInstance().getConfig().getDouble("crates.y"),
				Main.getInstance().getConfig().getDouble("crates.z"));
		holo = new PublicHologram("&7&o{ &d&lCRATES &7&o}\n&7&oRight-click to open the menu", loc.clone().add(0, 1.5, 0));
	}
	
	private void resetEntity() {
		boolean contains = false;
		for(Entity en : loc.getChunk().getEntities()) {
			if(en.getType() == EntityType.ENDER_CRYSTAL) {
				contains = true;
				this.en = (EnderCrystal) en;
				break;
			}
		}
		if(!contains) {
			en = (EnderCrystal) loc.getWorld().spawnEntity(loc.clone(), EntityType.ENDER_CRYSTAL);
		}
	}
	
	public void spawn() {
		boolean contains = false;
		loc.getChunk().load();
		for(Entity en : loc.getChunk().getEntities()) {
			if(en.getType() == EntityType.ENDER_CRYSTAL) {
				contains = true;
				this.en = (EnderCrystal) en;
				break;
			}
		}
		if(!contains) {
			en = (EnderCrystal) loc.getWorld().spawnEntity(loc.clone(), EntityType.ENDER_CRYSTAL);
		}
		holo.show();
	}
	
	public void despawn() {
		holo.destroy();
	}
	
	public Location getLocation() {
		return loc.clone();
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if(e.getEntity().equals(en)) {
				e.setCancelled(true);
				p.chat("/crate");
			}
		}
	}
	
	@EventHandler
	public void onHit(PlayerInteractAtEntityEvent e) {
		Player p = e.getPlayer();
		if(e.getRightClicked().equals(en)) {
			e.setCancelled(true);
			p.chat("/crate");
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		if(e.getChunk().getX() == loc.clone().getBlockX()/16 && e.getChunk().getZ() == loc.clone().getBlockZ()/16) {
			resetEntity();
		}
	}
}
