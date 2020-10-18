package net.colonymc.colonyskyblockcore.pets;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

import net.colonymc.api.itemstacks.SkullItemBuilder;
import net.colonymc.api.player.Particle;
import net.colonymc.api.player.PlayerInventory;
import net.colonymc.colonyskyblockcore.Main;

public class Pet implements Listener {
	
	PetType type;
	Player p;
	ArmorStand stand;
	Particle particle;
	BukkitTask doTask;
	BukkitTask move;
	boolean spawned;
	public static HashMap<Player, Pet> pets = new HashMap<Player, Pet>();
	
	public Pet(PetType t, Player p) {
		this.p = p;
		this.type = t;
		pets.put(p, this);
		spawn();
	}
	
	public Pet() {
		
	}
	
	public void spawn() {
		stand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
		stand.setVisible(false);
		stand.setGravity(false);
		stand.setRightArmPose(new EulerAngle(0, 0, 0));
		stand.setCustomName(ChatColor.translateAlternateColorCodes('&', type.name));
		stand.setItemInHand(new SkullItemBuilder().url("http://textures.minecraft.net/texture/" + type.url).build());
		particle = new Particle(Effect.CLOUD, 0, stand.getLocation().add(0, 1, 0));
		particle.play(p);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have spawned a " + PetItem.whatType(p.getItemInHand()).name + " Pet&f!"));
		startTask();
	}
	
	public void despawn() {
		stop();
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have despawned your " + type.name + " Pet&f!"));
		PlayerInventory.addItem(new PetItem(type, p).getItem(), p, 1);
		pets.remove(p);
	}
	
	public void startTask() {
		move = new BukkitRunnable() {
			@Override
			public void run() {
				particle.setLocation(stand.getLocation().add(0, 1, 0));
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	
	public void stop() {
		if(stand != null) {
			stand.remove();
		}
		if(particle != null) {
			particle.stop();
		}
		if(doTask != null) {
			doTask.cancel();
		}
	}
	
	public static boolean hasActivePet(Player p) {
		return pets.containsKey(p);
	}
	
	public static Pet getPet(Player p) {
		if(pets.containsKey(p)) {
			return pets.get(p);
		}
		return null;
	}
	
	@EventHandler
	public void onClick(PlayerInteractAtEntityEvent e) {
		Player p = e.getPlayer();
		if(Pet.hasActivePet(p)) {
			if(e.getRightClicked().equals(Pet.getPet(p).stand)) {
				e.setCancelled(true);
				Pet.getPet(p).despawn();
			}
		}
	}
	
	@EventHandler
	public void onClick(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player && e.getEntity() instanceof ArmorStand) {
			Player p = (Player) e.getDamager();
			ArmorStand as = (ArmorStand) e.getEntity();
			if(Pet.hasActivePet(p) && Pet.getPet(p).stand.equals(as)) {
				e.setCancelled(true);
				Pet.getPet(p).despawn();
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		Entity entity = e.getEntity();
		for(Pet p : pets.values()) {
			if(p.stand.equals(entity)) {
				e.setCancelled(true);
				break;
			}
		}
	}
}
