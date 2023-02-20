package net.colonymc.colonyskyblockcore.spawners;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackBuilder;
import net.minecraft.server.v1_8_R3.NBTTagString;

public class SpawnerItem {
	
	ItemStack item;
	final EntityType type;
	
	public SpawnerItem(EntityType type) {
		this.type = type;
		createItem();
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	private void createItem() {
		ItemStackBuilder builder = new ItemStackBuilder(Material.MOB_SPAWNER);
		builder.name(getName(type));
		builder.addTag("skyblockSpawner", new NBTTagString(type.name()));
        this.item = builder.build();
	}
	
	private String getName(EntityType type) {
		return "&d" + getNameMap().get(type) + " &fSpawner";
	}
	
	private HashMap<EntityType, String> getNameMap() {
		HashMap<EntityType, String> map = new HashMap<>();
		map.put(EntityType.ARMOR_STAND, "Armor Stand");
		map.put(EntityType.ARROW, "Arrow");
		map.put(EntityType.BAT, "Bat");
		map.put(EntityType.BLAZE, "Blaze");
		map.put(EntityType.BOAT, "Boat");
		map.put(EntityType.CAVE_SPIDER, "Cave spider");
		map.put(EntityType.CHICKEN, "Chicken");
		map.put(EntityType.COW, "Cow");
		map.put(EntityType.CREEPER, "Creeper");
		map.put(EntityType.ENDER_DRAGON, "Ender dragon");
		map.put(EntityType.ENDERMAN, "Enderman");
		map.put(EntityType.ENDERMITE, "Endermite");
		map.put(EntityType.GHAST, "Ghast");
		map.put(EntityType.GIANT, "Giant");
		map.put(EntityType.GUARDIAN, "Guardian");
		map.put(EntityType.HORSE, "Horse");
		map.put(EntityType.IRON_GOLEM, "Iron Golem");
		map.put(EntityType.MAGMA_CUBE, "Magma Cube");
		map.put(EntityType.MUSHROOM_COW, "Mushroom Cow");
		map.put(EntityType.OCELOT, "Ocelot");
		map.put(EntityType.PIG, "Pig");
		map.put(EntityType.PIG_ZOMBIE, "Zombie Pigman");
		map.put(EntityType.RABBIT, "Rabbit");
		map.put(EntityType.SHEEP, "Sheep");
		map.put(EntityType.SILVERFISH, "Silverfish");
		map.put(EntityType.SKELETON, "Skeleton");
		map.put(EntityType.SLIME, "Slime");
		map.put(EntityType.SNOWMAN, "Snowman");
		map.put(EntityType.SPIDER, "Spider");
		map.put(EntityType.SQUID, "Squid");
		map.put(EntityType.VILLAGER, "Villager");
		map.put(EntityType.WITCH, "Witch");
		map.put(EntityType.WITHER, "Wither");
		map.put(EntityType.WOLF, "Wolf");
		map.put(EntityType.ZOMBIE, "Zombie");
		return map;
	}
	
}
