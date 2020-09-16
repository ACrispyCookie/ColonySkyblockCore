package net.colonymc.colonyskyblockcore.minions;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import net.colonymc.colonyskyblockcore.Main;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;

public class Animations {

	public static void miningAnimation(MinerMinionBlock m, Block b, boolean ready) {
		ArmorStand as = m.as;
		m.stopCountdown();
		m.harvesting = new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				if(ready) {
					if(i == 70) {
						as.setRightArmPose(new EulerAngle(-0.26, 0, 0.17));
						as.setHeadPose(new EulerAngle(0, 0, 0));
						PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(as.getEntityId(), new BlockPosition(b.getX(), b.getY(), b.getZ()), -1);
						for(Entity e : as.getNearbyEntities(20, 50, 20)) {
							if(e instanceof Player) {
								Player p = (Player) e;
								((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
							}
						}
						b.setType(Material.AIR);
					}
					else if(i == 80) {
						b.setType(m.getMinion().getBlocksNeeded());
						m.addItems(1);
						as.teleport(m.getLocation().add(0.5, 1, 0.5));
						m.isHarvesting = false;
						m.startCountdown();
						cancel();
					}
					else if(i < 70) {
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
						PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(as.getEntityId(), new BlockPosition(b.getX(), b.getY(), b.getZ()), i/10);
						for(Entity e : as.getNearbyEntities(20, 50, 20)) {
							if(e instanceof Player) {
								Player p = (Player) e;
								((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
							}
						}
					}
				}
				else {
					if(i < 21) {
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
					else if(i == 30) {
						as.setHeadPose(new EulerAngle(0, 0, 0));
						as.setRightArmPose(new EulerAngle(-0.26, 0, 0.17));
						b.setType(m.getMinion().getBlocksNeeded());
						as.teleport(m.getLocation().add(0.5, 1, 0.5));
						m.isHarvesting = false;
						m.startCountdown();
						cancel();
					}
				}
				i++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	
	public static void slayingAnimation(SlayerMinionBlock b, LivingEntity e) {
		ArmorStand as = b.as;
		b.stopCountdown();
		b.harvesting = new BukkitRunnable() {
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
					if(b.getCloseEntities().size() < 6) {
						b.getLocation().getWorld().spawnEntity(b.getLocation().add(rand.nextInt(3), 1, rand.nextInt(3)), b.getMinion().getEntityType());
					}
					as.teleport(b.getLocation().add(0.5, 1, 0.5));
					b.isHarvesting = false;
					b.startCountdown();
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
	
	public static void farmingAnimation(FarmerMinionBlock m, Block b, boolean areaReady, boolean farmingAreaReady) {
		ArmorStand as = m.as;
		m.stopCountdown();
		m.harvesting = new BukkitRunnable() {
			int i = 0;
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if(areaReady && farmingAreaReady) {
					if(i == 30) {
						as.setRightArmPose(new EulerAngle(-0.26, 0, 0.17));
						as.setHeadPose(new EulerAngle(0, 0, 0));
						b.setType(Material.AIR);
					}
					else if(i == 40) {
						b.setType(m.getMinion().getFarmingFor());
						b.setData((byte) 7);
						m.addItems(1);
						as.teleport(m.getLocation().add(0.5, 1, 0.5));
						m.isHarvesting = false;
						m.startCountdown();
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
				}
				else if(areaReady) {
					if(i < 21) {
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
					else if(i == 30) {
						as.setHeadPose(new EulerAngle(0, 0, 0));
						as.setRightArmPose(new EulerAngle(-0.26, 0, 0.17));
						b.setType(m.getMinion().getFarmingFor());
						b.setData((byte) 7); 
						as.teleport(m.getLocation().add(0.5, 1, 0.5));
						m.isHarvesting = false;
						m.startCountdown();
						cancel();
					}
				}
				else {
					if(i < 21) {
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
					else if(i == 30) {
						as.setHeadPose(new EulerAngle(0, 0, 0));
						as.setRightArmPose(new EulerAngle(-0.26, 0, 0.17));
						b.setType(m.getMinion().getBlocksNeeded());
						as.teleport(m.getLocation().add(0.5, 1, 0.5));
						m.isHarvesting = false;
						m.startCountdown();
						cancel();
					}
				}
				i++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
}
