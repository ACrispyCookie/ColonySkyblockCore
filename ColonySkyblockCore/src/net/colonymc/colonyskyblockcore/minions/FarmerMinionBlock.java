package net.colonymc.colonyskyblockcore.minions;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.minions.fuel.Fuel;


public class FarmerMinionBlock extends MinionBlock {
	
	public FarmerMinionBlock(Minion m, String playerUuid, Location loc) {
		super(m, playerUuid, loc);
	}
	
	public FarmerMinionBlock(Minion m, String playerUuid, Location loc, HashMap<ItemStack, Integer> items, Fuel f, long lastProduced, int id) {
		super(m, playerUuid, loc, items, f, lastProduced, id);
	}
	
	public FarmerMinionBlock() {
		
	}
	
	@Override
	protected boolean doTask() {
		if(isInRightArea()) {
			checkValidBlocks();
			if(isFull()) {
				as.setCustomNameVisible(true);
				as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&cMinion's inventory is full!"));
			}
			else {
				as.setCustomNameVisible(false);
				if(isAreaReady() && !isFarmingAreaReady()) {
					as.setCustomNameVisible(true);
					as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&cPlanting crops..."));
				}
				else if(!isAreaReady() && !isFarmingAreaReady()){
					as.setCustomNameVisible(true);
					as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&cGetting the area ready..."));
				}
				playAnimation();
				return true;
			}
		}
		else {
			as.setCustomNameVisible(true);
			as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&cThe place is not perfect for harvesting!"));
		}
		return false;
	}
	
	@Override
	protected void playAnimation() {
		Block b = getRandomBlock();
		boolean wasAreaReady = isAreaReady();
		boolean wasFarmingAreaReady = isFarmingAreaReady();
		Location loc = this.loc.clone().add(0.5, 1, 0.5);
		loc.setDirection(b.getLocation().clone().add(0.5, 0, 0.5).subtract(loc.clone()).toVector().normalize());
		as.teleport(loc);
		new BukkitRunnable() {
			int i = 0;
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if(wasAreaReady && wasFarmingAreaReady) {
					if(i == 0.75 * animationLengthT) {
						as.setRightArmPose(new EulerAngle(-0.26, 0, 0.17));
						as.setHeadPose(new EulerAngle(0, 0, 0));
						b.setType(Material.AIR);
					}
					else if(i == animationLengthT) {
						b.setType(getMinion().getFarmingFor());
						b.setData((byte) 7);
						as.teleport(getLocation().add(0.5, 1, 0.5));
						cancel();
					}
					else if(i < 0.5 * animationLengthT) {
						as.setCustomNameVisible(false);
						as.setHeadPose(new EulerAngle(Math.toRadians(25), 0, 0));
						if(as.getRightArmPose().getX() == -0.26 && as.getRightArmPose().getZ() == 0.17) {
							as.setRightArmPose(new EulerAngle(Math.toRadians(-160), as.getRightArmPose().getY(), as.getRightArmPose().getZ()));
						}
						else if(as.getRightArmPose().getX() >= Math.toRadians(-15)) {
							as.setRightArmPose(new EulerAngle(Math.toRadians(-160), as.getRightArmPose().getY(), as.getRightArmPose().getZ()));
						}
						else {
							as.setRightArmPose(new EulerAngle(Math.toRadians(Math.toDegrees(as.getRightArmPose().getX()) + 7), as.getRightArmPose().getY(), as.getRightArmPose().getZ()));
						}
					}
				}
				else if(wasAreaReady) {
					if(i < 0.5 * animationLengthT) {
						as.setCustomNameVisible(true);
						as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&cPlanting crops..."));
						as.setHeadPose(new EulerAngle(Math.toRadians(25), 0, 0));
						if(as.getRightArmPose().getX() == -0.26 && as.getRightArmPose().getZ() == 0.17) {
							as.setRightArmPose(new EulerAngle(Math.toRadians(-160), as.getRightArmPose().getY(), as.getRightArmPose().getZ()));
						}
						else if(as.getRightArmPose().getX() >= Math.toRadians(-15)) {
							as.setRightArmPose(new EulerAngle(Math.toRadians(-160), as.getRightArmPose().getY(), as.getRightArmPose().getZ()));
						}
						else {
							as.setRightArmPose(new EulerAngle(Math.toRadians(Math.toDegrees(as.getRightArmPose().getX()) + 7), as.getRightArmPose().getY(), as.getRightArmPose().getZ()));
						}
					}
					else if(i == 0.75 * animationLengthT) {
						as.setHeadPose(new EulerAngle(0, 0, 0));
						as.setRightArmPose(new EulerAngle(-0.26, 0, 0.17));
						b.setType(getMinion().getFarmingFor());
						b.setData((byte) 7); 
						as.teleport(getLocation().add(0.5, 1, 0.5));
						cancel();
					}
				}
				else {
					if(i < 0.5 * animationLengthT) {
						as.setCustomNameVisible(true);
						as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&cGetting the area ready..."));
						as.setHeadPose(new EulerAngle(Math.toRadians(25), 0, 0));
						if(as.getRightArmPose().getX() == -0.26 && as.getRightArmPose().getZ() == 0.17) {
							as.setRightArmPose(new EulerAngle(Math.toRadians(-160), as.getRightArmPose().getY(), as.getRightArmPose().getZ()));
						}
						else if(as.getRightArmPose().getX() >= Math.toRadians(-15)) {
							as.setRightArmPose(new EulerAngle(Math.toRadians(-160), as.getRightArmPose().getY(), as.getRightArmPose().getZ()));
						}
						else {
							as.setRightArmPose(new EulerAngle(Math.toRadians(Math.toDegrees(as.getRightArmPose().getX()) + 7), as.getRightArmPose().getY(), as.getRightArmPose().getZ()));
						}
					}
					else if(i == 0.75 * animationLengthT) {
						as.setHeadPose(new EulerAngle(0, 0, 0));
						as.setRightArmPose(new EulerAngle(-0.26, 0, 0.17));
						b.setType(getMinion().getBlocksNeeded());
						as.teleport(getLocation().add(0.5, 1, 0.5));
						cancel();
					}
				}
				i++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
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
