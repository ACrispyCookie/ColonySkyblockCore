package net.colonymc.colonyskyblockcore.npcs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.colonymc.api.player.Particle;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionHouseSelectMenu;
import net.colonymc.colonyskyblockcore.guilds.bank.BankInventory;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;

public class NPCListener implements Listener {
	
	public static NPC banker;
	public static NPC auctionMaster;
	
	public static void setupNPCs() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(CitizensAPI.getNPCRegistry().getById(58) != null) {
					banker = CitizensAPI.getNPCRegistry().getById(58);
					banker.spawn(new Location(Bukkit.getWorld("hub"), -3, 70, 6));
					banker.setName(ChatColor.translateAlternateColorCodes('&', "&dBanker"));
					banker.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, "");
					banker.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, "eyJ0aW1lc3RhbXAiOjE1ODc5MjI3NzUyMDQsInByb2ZpbGVJZCI6Ijc3MjdkMzU2NjlmOTQxNTE4MDIzZDYyYzY4MTc1OTE4IiwicHJvZmlsZU5hbWUiOiJsaWJyYXJ5ZnJlYWsiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2IzYjNhMzM2YTc3Y2FiOGI3NDQ2ODY4Y2VhMjkyMTkwZTc3Mjk4ZjJiNjBlODM1YjkwNTY4NjI1MDJkNjU0NjAifX19");
					banker.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, "pUHJWkgDVtJOGfZBP+oAihOVVkmfzrTQIy8/s3B0Xw/rIUGERNyv75YfHlosiFk7fff1eWWu40YtzmYd/uxeLy+uEKLU6Y2aU55iFBhX/2XJkviht5GluD/Bf9ySwvNt//fWg5+ISCRiWTD6RtZ/4BfQy70eNu++VkeR5re9+NZsDWsCG6EdoQz9dhrRlawcjjAN6uYMsOeN4VdiUSvWkP//dFxItUxQJ4KJfpaXry3h/4RdPaQPoERWLV644MaNKynqV8ogofwpXUExiIu8BjaXH7UfVqL7SyZExjx4ibSLWSq3oKkNI5Xr1XbCnmQFhrq8aiZimijvMsiSAaca/QG7+L9QmC2IB6pQwbuJ8LEzQ5YTpO/78lm/MCBLFKBfnwMoNq/F7U/MTrIPiM4s+D8zmSiHbUohIBJE9U3UY6vNddzcMD+IA4/YoLXKkG1BeNVtStEOkeG+8FsHSgqdwy5Ymof/7dLME5CsT+zSKaQj8442qPCX+HZpUb3iJMckL9mHosvNT1YcNuP5IcfsFI9CHQSTJf9PSQAKbTerD9K3ppaSoHJeGic3p9K1+p/okrwV/aWvLeAFgLP/TaLclGhKXqddmEnYAQN6ySSdVMW1DdmK92pRqKz3NaXLKTnZ7zpQM/4cD6mIOd7EgMMmjushR34w+Yk+t7doZndLb3M=");
					auctionMaster = CitizensAPI.getNPCRegistry().getById(53);
					auctionMaster.spawn(new Location(Bukkit.getWorld("hub"), -10.0, 80, -60.0));
					auctionMaster.setName(ChatColor.translateAlternateColorCodes('&', "&dAuction Master"));
					auctionMaster.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, "");
					auctionMaster.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, "ewogICJ0aW1lc3RhbXAiIDogMTU5MTA0MzA5MjA1NCwKICAicHJvZmlsZUlkIiA6ICJiOGFkZWQxYjk3MTA0NjA3YjI1YjFlN2MwMWZhNTZiNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJCcmFzdG8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNkMDAyYmVjZTBjMzZmMmY0YWI5ZDMwZjE4NTNhODQwMjI5YWVjNjc5MjA1NTBiYmNiZDczNjJhNDFjMDI4NCIKICAgIH0KICB9Cn0=");
					auctionMaster.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, "SyFKfWIyAeE/K0VipBoHfOFTAcGfMuh3eRiYG1S2v2k/BFzIdEorODTYLSD097964KOAO1IXIOZC1bLe/vISA8DcZ/kV+4k2BGG21cFGg8c/aK2P8rpVkW3l9rpCf17907Wlz/0B1Oj/fHKVPzh9fxte1w7HKv97jn+k3ljDzf/r5q3BbYAJD+HOaEOw/Rts2U2ssaq0LU5S2h1H1tq8ihBT0GXwsS6vbjLT2k8AzN4oA/CYWM6bkctWaPZY9Fv3e07mWibwNpgqUXBv6Zgu0fztX5T54SB7qP15lkH6pzkeSfxhdos9wLuEb4ygMwxnCcKwQNWrNOZr7XdIpHlPW1kjmcRkqgUUJp7GgcZcQInhWCF2GPsyVUf0kiqeEFLKI+hjsWLFogWWwgLZJG3Tw6TFL0VU12Debc/Dt+sGIpqqEVSNlTk4WO8MwyRAH8mspvCwQifxGjoaFUlRUBwFbsW8b9ocZte7IofKkDC3MPc5SLUPZS/hxXcuvF9nT/gMzxgyEpL9zDLB4clXetxbVuIGE0fsTUMrL5+waBQSHM3349gKXt2o6LNsKjR3A9akd97mGqnUzGnbrWoHxf7nYy6iLHF5CyPiy43LBuMxJyUWHUZM1iL47zpBKZ2Al0xCs7TB/aniQ+P8OIZFSIcblwIPA67N+3ZqJbwdWv5F8mg=");
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 5L);
	}
	
	public static void despawnNPCs() {
		if(auctionMaster != null && auctionMaster.isSpawned()) {
			auctionMaster.despawn();
		}
		if(banker != null && banker.isSpawned()) {
			banker.despawn();
		}
	}
	
	@EventHandler
	public void onClick(NPCRightClickEvent e) {
		if(e.getNPC().equals(banker)) {
			new BankInventory(e.getClicker());
		}
		else if(e.getNPC().equals(auctionMaster)) {
			new AuctionHouseSelectMenu(e.getClicker());
		}
	}
	
	@EventHandler
	public void onClick(NPCLeftClickEvent e) {
		if(e.getNPC().equals(banker)) {
			new BankInventory(e.getClicker());
		}
		else if(e.getNPC().equals(auctionMaster)) {
			new AuctionHouseSelectMenu(e.getClicker());
		}
	}
	
	public static void sendRotatingHead(NPC npc, Player p, ItemStack item, String name, boolean out, int bodyPart) {
		new BukkitRunnable() {
			int i = 0;
	        WorldServer s = ((CraftWorld)npc.getEntity().getWorld()).getHandle();
	        EntityArmorStand eas = new EntityArmorStand(s);
			Particle par = new Particle(EnumParticle.CLOUD, 0, new Location(npc.getEntity().getWorld(), eas.locX, eas.locY, eas.locZ).add(0, 1.5, 0));
			@Override
			public void run() {
				if(out) {
					if(i == 0) {
						eas.noclip = true;
						eas.setInvisible(true);
						eas.setAbsorptionHearts(1000000);
						eas.setCustomNameVisible(false);
						eas.setLocation(npc.getEntity().getLocation().getX(), npc.getEntity().getLocation().getY() + 1, npc.getEntity().getLocation().getZ(), 
								npc.getEntity().getLocation().getPitch(), npc.getEntity().getLocation().getYaw());
						PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(eas);
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
						PacketPlayOutEntityEquipment packet1 = new PacketPlayOutEntityEquipment(eas.getId(), bodyPart, CraftItemStack.asNMSCopy(item));
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet1);
						par.play(p, 1);
					}
					else if(eas.locY < npc.getEntity().getLocation().getY() + 4) {
						par.stop();
						par.play(p, 1);
						eas.setLocation(eas.locX, eas.locY + 0.1, eas.locZ, eas.pitch + 15, eas.yaw);
						PacketPlayOutEntityTeleport packet1 = new PacketPlayOutEntityTeleport(eas);
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet1);
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
					}
					else {
						par.stop();
						p.playSound(p.getLocation(), Sound.EXPLODE, 2, 2);
						par.setEffect(EnumParticle.EXPLOSION_HUGE, 0);
						par.play(p, 1);
						eas.setLocation(npc.getEntity().getLocation().getX(), npc.getEntity().getLocation().getY() + 0.35, npc.getEntity().getLocation().getZ(), 
								npc.getEntity().getLocation().getPitch(), npc.getEntity().getLocation().getYaw());
						eas.setGravity(false);
						eas.setCustomNameVisible(true);
						eas.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
						PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(eas);
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
						PacketPlayOutEntityEquipment packet1 = new PacketPlayOutEntityEquipment(eas.getId(), bodyPart, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet1);
						PacketPlayOutEntityMetadata packet2 = new PacketPlayOutEntityMetadata(eas.getId(), eas.getDataWatcher(), true);
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet2);
						afterReached(p, eas);
						cancel();
					}
				}
				else {
					if(i == 0) {
						eas.noclip = true;
						eas.setInvisible(true);
						eas.setAbsorptionHearts(1000000);
						eas.setCustomNameVisible(false);
						eas.setLocation(npc.getEntity().getLocation().getX(), npc.getEntity().getLocation().getY() + 3, npc.getEntity().getLocation().getZ(), 
								npc.getEntity().getLocation().getPitch(), npc.getEntity().getLocation().getYaw());
						PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(eas);
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
						PacketPlayOutEntityEquipment packet1 = new PacketPlayOutEntityEquipment(eas.getId(), bodyPart, CraftItemStack.asNMSCopy(item));
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet1);
						par.play(p, 1);
					}
					else if(eas.locY > npc.getEntity().getLocation().getY()) {
						par.stop();
						par.play(p, 1);
						eas.setLocation(eas.locX, eas.locY - 0.1, eas.locZ, eas.pitch + 15, eas.yaw);
						PacketPlayOutEntityTeleport packet1 = new PacketPlayOutEntityTeleport(eas);
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet1);
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
					}
					else {
						par.stop();
						p.playSound(p.getLocation(), Sound.EXPLODE, 2, 2);
						par.setEffect(EnumParticle.EXPLOSION_HUGE, 0);
						par.play(p, 1);
						eas.setLocation(npc.getEntity().getLocation().getX(), npc.getEntity().getLocation().getY() + 0.35, npc.getEntity().getLocation().getZ(), 
								npc.getEntity().getLocation().getPitch(), npc.getEntity().getLocation().getYaw());
						eas.setGravity(false);
						eas.setCustomNameVisible(true);
						eas.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
						PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(eas);
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
						PacketPlayOutEntityEquipment packet1 = new PacketPlayOutEntityEquipment(eas.getId(), bodyPart, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet1);
						PacketPlayOutEntityMetadata packet2 = new PacketPlayOutEntityMetadata(eas.getId(), eas.getDataWatcher(), true);
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet2);
						afterReached(p, eas);
						cancel();
					}
				}
				i++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	
	private static void afterReached(Player p, EntityArmorStand eas) {
		new BukkitRunnable() {
			int i = 0;
			Particle par = new Particle(EnumParticle.SPELL_WITCH, 41, new Location(eas.getWorld().getWorld(), eas.locX, eas.locY, eas.locZ).add(0, 2, 0));
			@Override
			public void run() {
				if(i == 0) {
					par.play(p, 1);
				}
				else if(i == 3) {
					PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(eas.getId());
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
					cancel();
				}
				i++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 20);
	}
}
