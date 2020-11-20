package net.colonymc.colonyskyblockcore.spawners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import net.colonymc.colonyspigotapi.api.itemstack.ItemStackNBT;

public class SpawnerListener implements Listener {

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if(e.getBlock().getType() == Material.MOB_SPAWNER) {
			if(ItemStackNBT.hasTag(e.getItemInHand(), "skyblockSpawner")) {
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have placed a " + e.getItemInHand().getItemMeta().getDisplayName() + "&f!"));
				Block block = e.getBlock();
				BlockState bs = block.getState();
				CreatureSpawner cs = (CreatureSpawner) bs;
				cs.setSpawnedType(EntityType.valueOf(ItemStackNBT.getString(e.getItemInHand(), "skyblockSpawner")));
				bs.update();
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if(e.getBlock().getType() == Material.MOB_SPAWNER) {
			Block block = e.getBlock();
			BlockState bs = block.getState();
			CreatureSpawner cs = (CreatureSpawner) bs;
			SpawnerItem item = new SpawnerItem(cs.getSpawnedType());
			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), item.getItem());
			e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have broken a " + item.getItem().getItemMeta().getDisplayName() + "&f!"));
			
		}
	}
	
}
