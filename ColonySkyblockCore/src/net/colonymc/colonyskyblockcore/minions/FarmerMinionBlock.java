package net.colonymc.colonyskyblockcore.minions;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class FarmerMinionBlock extends MinionBlock {
	
	public FarmerMinionBlock(Minion m, String playerUuid, Location loc) {
		super(m, playerUuid, loc);
	}
	
	public FarmerMinionBlock(Minion m, String playerUuid, Location loc, HashMap<ItemStack, Integer> items, long lastProduced, int id) {
		super(m, playerUuid, loc, items, lastProduced, id);
	}
	
	public FarmerMinionBlock() {
		
	}
	
	@Override
	protected void harvest() {
		if(isInRightArea()) {
			checkValidBlocks();
			if(isFull()) {
				as.setCustomNameVisible(true);
				as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&cMinion's inventory is full!"));
			}
			else {
				as.setCustomNameVisible(false);
				playAnimation();
			}
		}
		else {
			as.setCustomNameVisible(true);
			as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&cThe place is not perfect for harvesting!"));
		}
	}
	
	@Override
	protected void playAnimation() {
		isHarvesting = true;
		Block b = getRandomBlock();
		Location loc = this.loc.clone().add(0.5, 1, 0.5);
		loc.setDirection(b.getLocation().clone().add(0.5, 0, 0.5).subtract(loc.clone()).toVector().normalize());
		as.teleport(loc);
		if(isFarmingAreaReady()) {
			as.setCustomNameVisible(false);
			Animations.farmingAnimation(this, b, true, true);
		}
		else if(isAreaReady()) {
			as.setCustomNameVisible(true);
			as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&cPlanting crops..."));
			Animations.farmingAnimation(this, b, true, false);
		}
		else {
			as.setCustomNameVisible(true);
			as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&cGetting the area ready..."));
			Animations.farmingAnimation(this, b, false, false);
		}
	}
	
	@Override
	protected ItemStack getItemInHand() {
		if(m.getLevel() < 3) {
			return new ItemStack(Material.WOOD_HOE);
		}
		else if(m.getLevel() < 5) {
			return new ItemStack(Material.STONE_HOE);
		}
		else if(m.getLevel() <= 8) {
			return new ItemStack(Material.IRON_HOE);
		}
		else {
			return new ItemStack(Material.DIAMOND_HOE);
		}
	}
	
	@Override
	protected boolean isInRightArea() {
		for(int x = 2; x > -3; x--) {
			if(loc.clone().add(x, 0, 2).getBlock().getType() != m.getBlocksNeeded() && loc.clone().add(x, 0, 2).getBlock().getType() != Material.AIR && 
					(m.getBlocksNeeded() == Material.SOIL ? loc.clone().add(x, 0, 2).getBlock().getType() != Material.DIRT : true) && 
					(m.getBlocksNeeded() == Material.SOIL ? loc.clone().add(x, 0, 2).getBlock().getType() != Material.GRASS : true)) {
				return false;
			}
		}
		for(int z = 2; z > -3; z--) {
			if(loc.clone().add(-2, 0, z).getBlock().getType() != m.getBlocksNeeded() && loc.clone().add(-2, 0, z).getBlock().getType() != Material.AIR && 
					(m.getBlocksNeeded() == Material.SOIL ? loc.clone().add(-2, 0, z).getBlock().getType() != Material.DIRT : true) && 
					(m.getBlocksNeeded() == Material.SOIL ? loc.clone().add(-2, 0, z).getBlock().getType() != Material.GRASS : true)) {
				return false;
			}
		}
		for(int x = -2; x > 3; x++) {
			if(loc.clone().add(x, 0, -2).getBlock().getType() != m.getBlocksNeeded() && loc.clone().add(x, 0, -2).getBlock().getType() != Material.AIR && 
					(m.getBlocksNeeded() == Material.SOIL ? loc.clone().add(x, 0, -2).getBlock().getType() != Material.DIRT : true) && 
					(m.getBlocksNeeded() == Material.SOIL ? loc.clone().add(x, 0, -2).getBlock().getType() != Material.GRASS : true)) {
				return false;
			}
		}
		for(int z = -2; z > 3; z++) {
			if(loc.clone().add(2, 0, z).getBlock().getType() != m.getBlocksNeeded() && loc.clone().add(2, 0, z).getBlock().getType() != Material.AIR && 
					(m.getBlocksNeeded() == Material.SOIL ? loc.clone().add(2, 0, z).getBlock().getType() != Material.DIRT : true) && 
					(m.getBlocksNeeded() == Material.SOIL ? loc.clone().add(2, 0, z).getBlock().getType() != Material.GRASS : true)) {
				return false;
			}
		}
		for(int z = 2; z > -3; z--) {
			for(int x = 2; x > -3; x--) {
				if(x == 0 && z == 0) {
					continue;
				}
				if(loc.clone().add(x, 1 ,z).getBlock().getType() != Material.AIR && loc.clone().add(x, 1, z).getBlock().getType() != m.getFarmingFor()) {
					return false;
				}
			}
		}
		return true;
	}
	
	private Block getRandomBlock() {
		if(!isAreaReady()) {
			Random rand = new Random();
			Random sign = new Random();
			int x = rand.nextInt(3) * (sign.nextInt(2) == 0 ? (-1) : (1));
			int z = rand.nextInt(3) * (sign.nextInt(2) == 0 ? (-1) : (1));
			while((x == 0 && z == 0) || loc.clone().add(x, 0, z).getBlock().getType() == m.getBlocksNeeded()) {
				x = rand.nextInt(3) * (sign.nextInt(2) == 0 ? (-1) : (1));
				z = rand.nextInt(3) * (sign.nextInt(2) == 0 ? (-1) : (1));
			}
			return loc.clone().add(x, 0, z).getBlock();
		}
		else if(!isFarmingAreaReady()) {
			Random rand = new Random();
			Random sign = new Random();
			int x = rand.nextInt(3) * (sign.nextInt(2) == 0 ? (-1) : (1));
			int z = rand.nextInt(3) * (sign.nextInt(2) == 0 ? (-1) : (1));
			while((x == 0 && z == 0) || loc.clone().add(x, 1, z).getBlock().getType() == m.getFarmingFor()) {
				x = rand.nextInt(3) * (sign.nextInt(2) == 0 ? (-1) : (1));
				z = rand.nextInt(3) * (sign.nextInt(2) == 0 ? (-1) : (1));
			}
			return loc.clone().add(x, 1, z).getBlock();
		}
		else {
			Random rand = new Random();
			Random sign = new Random();
			int x = rand.nextInt(3) * (sign.nextInt(2) == 0 ? (-1) : (1));
			int z = rand.nextInt(3) * (sign.nextInt(2) == 0 ? (-1) : (1));
			while(x == 0 && z == 0) {
				x = rand.nextInt(3) * (sign.nextInt(2) == 0 ? (-1) : (1));
				z = rand.nextInt(3) * (sign.nextInt(2) == 0 ? (-1) : (1));
			}
			return loc.clone().add(x, 1, z).getBlock();
		}
	}
	
	private boolean isFarmingAreaReady() {
		for(int z = 2; z > -3; z--) {
			for(int x = 2; x > -3; x--) {
				if(x == 0 && z == 0) {
					continue;
				}
				if(loc.clone().add(x, 1 ,z).getBlock().getType() != m.getFarmingFor()) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isAreaReady() {
		for(int z = 2; z > -3; z--) {
			for(int x = 2; x > -3; x--) {
				if(x == 0 && z == 0) {
					continue;
				}
				if(loc.clone().add(x, 0, z).getBlock().getType() != m.getBlocksNeeded()) {
					return false;
				}
			}
		}
		return true;
	}
}
