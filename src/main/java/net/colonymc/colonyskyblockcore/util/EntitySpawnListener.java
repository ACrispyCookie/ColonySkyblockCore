package net.colonymc.colonyskyblockcore.util;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EntitySpawnListener implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSpawn(EntityDeathEvent e) {
		if(e.getEntity().getName() != null) {
			if(e.getEntity().getName().startsWith(ChatColor.translateAlternateColorCodes('&', "&d&lx"))) {
				int stackAmount = Integer.parseInt(e.getEntity().getName().substring(e.getEntity().getName().indexOf(ChatColor.translateAlternateColorCodes('&', "&d&lx")) + 5, 
								e.getEntity().getName().indexOf(' ')));
				if(stackAmount == 2) {
					e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), e.getEntityType());
				}
				else {
					Entity en = e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), e.getEntityType());
					en.setCustomName(ChatColor.translateAlternateColorCodes('&', "&d&lx" + (stackAmount - 1) + " &f" + e.getEntityType().getName()));
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSpawn(EntitySpawnEvent e) {
		Entity en = e.getEntity();
		if(en instanceof LivingEntity && isValidForStack(en) && !(en instanceof ArmorStand)) {
			List<Entity> entities = en.getNearbyEntities(8, 30, 8);
			for(Entity en1 : entities) {
				if(en1.getType() == e.getEntityType()) {
					int amount = 1;
					if(!en1.isDead() && isValidForStack(en1)) {
						if(en1.getName() == en.getName()) {
							en1.remove();
							amount++;
							en.setCustomName(ChatColor.translateAlternateColorCodes('&', "&d&lx" + amount + " &f" + en.getType().getName()));
						}
						else if(en1.getName().startsWith(ChatColor.translateAlternateColorCodes('&', "&d&lx"))) {
							int stackAmount = Integer.parseInt(en1.getName().substring(en1.getName().indexOf(ChatColor.translateAlternateColorCodes('&', "&d&lx")) + 5, 
									en1.getName().indexOf(' ')));
							en1.setCustomName(ChatColor.translateAlternateColorCodes('&', "&d&lx" + (stackAmount + 1) + " &f" + en1.getType().getName()));
							en.remove();
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onTame(EntityTameEvent e) {
		if(e.getEntity().getName().startsWith(ChatColor.translateAlternateColorCodes('&', "&d&lx"))) {
			e.setCancelled(true);
			if(e.getOwner() instanceof Player) {
				Player p = (Player) e.getOwner();
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot tame a stacked mob!"));
				ItemStack i = p.getItemInHand();
				i.setAmount(i.getAmount() + 1);
				p.setItemInHand(i);
				p.updateInventory();
			}
		}
	}
	
	@EventHandler
	public void onTame(PlayerInteractEntityEvent e) {
		if(e.getRightClicked().getName().startsWith(ChatColor.translateAlternateColorCodes('&', "&d&lx"))) {
			Player p = e.getPlayer();
			if(p.getItemInHand().getType() == Material.NAME_TAG &&  p.getItemInHand().hasItemMeta() && !p.getItemInHand().getItemMeta().getDisplayName().equals("Name Tag")) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot rename a stacked mob!"));
			}
		}
	}


	private boolean isValidForStack(Entity en) {
		if(en instanceof Tameable) {
			Tameable tameable = (Tameable) en;
			return !tameable.isTamed();
		}
		return true;
	}

}
