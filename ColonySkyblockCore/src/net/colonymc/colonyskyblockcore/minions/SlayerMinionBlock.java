package net.colonymc.colonyskyblockcore.minions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import net.colonymc.colonyskyblockcore.Main;

public class SlayerMinionBlock extends MinionBlock {
	
	public SlayerMinionBlock(Minion m, String playerUuid, Location loc) {
		super(m, playerUuid, loc);
	}
	
	public SlayerMinionBlock(Minion m, String playerUuid, Location loc, HashMap<ItemStack, Integer> items, long lastProduced, int id) {
		super(m, playerUuid, loc, items, lastProduced, id);
	}
	
	public SlayerMinionBlock() {
		
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
		if(getCloseEntities().size() < 4) {
			if(as.isCustomNameVisible()) {
				as.setCustomNameVisible(false);
			}
			Random rand = new Random();
			loc.getWorld().spawnEntity(loc.clone().add(rand.nextInt(3), 1, rand.nextInt(3)), m.getEntityType());
		}
		else {
			if(as.isCustomNameVisible()) {
				as.setCustomNameVisible(false);
			}
			LivingEntity e = getRandomEntity();
			if(e != null) {
				Location loc = this.loc.clone().add(0.5, 1, 0.5);
				loc.setDirection(e.getLocation().clone().add(0.5, 0, 0.5).subtract(loc.clone()).toVector().normalize());
				as.teleport(loc);
				new BukkitRunnable() {
					int i = 0;
					@Override
					public void run() {
						if(i == 30) {
							as.setRightArmPose(new EulerAngle(-0.26, 0, 0.17));
							as.setHeadPose(new EulerAngle(0, 0, 0));
							e.damage(e.getHealth(), as);
						}
						else if(i == 40) {
							Random rand = new Random();
							if(getCloseEntities().size() < 6) {
								getLocation().getWorld().spawnEntity(getLocation().add(rand.nextInt(3), 1, rand.nextInt(3)), getMinion().getEntityType());
							}
							as.teleport(getLocation().add(0.5, 1, 0.5));
							cancel();
						}
						else if(i < 21) {
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
						i++;
					}
				}.runTaskTimer(Main.getInstance(), 0, 1);
			}
		}
	}
	
	@Override
	protected ItemStack getItemInHand() {
		if(m.getLevel() < 3) {
			return new ItemStack(Material.WOOD_SWORD);
		}
		else if(m.getLevel() < 5) {
			return new ItemStack(Material.STONE_SWORD);
		}
		else if(m.getLevel() <= 8) {
			return new ItemStack(Material.IRON_SWORD);
		}
		else {
			return new ItemStack(Material.DIAMOND_SWORD);
		}
	}
	
	@Override
	protected boolean isInRightArea() {
		for(int z = 2; z > -3; z--) {
			for(int x = 2; x > -3; x--) {
				if(x == 0 && z == 0) {
					continue;
				}
				if(loc.clone().add(x, 0 ,z).getBlock().getType() == Material.AIR || loc.clone().add(x, 1, z).getBlock().getType() != Material.AIR) {
					return false;
				}
			}
		}
		return true;
	}
	
	public ArrayList<LivingEntity> getCloseEntities() {
		ArrayList<LivingEntity> entities = new ArrayList<LivingEntity>();
		for(Entity e : as.getNearbyEntities(2.5, 5, 2.5)) {
			if(e.getType() == m.getEntityType() && e instanceof LivingEntity) {
				entities.add((LivingEntity) e);
			}
		}
		return entities;
	}
	
	private LivingEntity getRandomEntity() {
		ArrayList<LivingEntity> entities = new ArrayList<LivingEntity>();
		for(Entity e : as.getNearbyEntities(2.5, 5, 2.5)) {
			if(e.getType() == m.getEntityType() && e instanceof LivingEntity) {
				entities.add((LivingEntity) e);
			}
		}
		Random rand = new Random();
		if(entities.isEmpty()) {
			return null;
		}
		else {
			return entities.get(rand.nextInt(entities.size()));
		}
	}
}
