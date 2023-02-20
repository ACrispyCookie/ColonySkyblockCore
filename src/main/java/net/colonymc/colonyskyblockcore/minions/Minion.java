package net.colonymc.colonyskyblockcore.minions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyspigotlib.lib.itemstack.SkullItemBuilder;
import net.colonymc.colonyspigotlib.lib.primitive.RomanNumber;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagString;

public abstract class Minion {
	
	protected final int level;
	protected String name;
	protected final int duration;
	protected final int invSlots;
	protected final String skinUrl;
	protected final MaterialType material;
	protected final MinionType type;
	protected Material blocksNeeded;
	protected Material farmingFor;
	protected EntityType entityType;
	protected ItemStack item;
	protected final Color armorColor;
	protected HashMap<ItemStack, RandomAmount> lootToGet = new HashMap<>();
	public abstract ItemStack createItem();
	public abstract HashMap<ItemStack, RandomAmount> lootToGet();
	
	public Minion(int level, String skinUrl, MaterialType material, MinionType type, Material blocksNeeded, Material farmingFor, Color armorColor, ItemStack i) {
		this.level = level;
		this.skinUrl = skinUrl;
		this.blocksNeeded = blocksNeeded;
		this.farmingFor = farmingFor;
		this.duration = selectDuration();
		this.material = material;
		this.armorColor = armorColor;
		this.type = type;
		this.lootToGet = lootToGet();
		this.invSlots = selectSlots();
		this.item = createItem();
		if(i == null) {
			this.item = createItem();
		}
		else {
			this.item = i;
			this.name = item.getItemMeta().getDisplayName();
		}
	}
	
	public Minion(int level, String skinUrl, MaterialType material, MinionType type, EntityType entity, Color armorColor, ItemStack i) {
		this.level = level;
		this.skinUrl = skinUrl;
		this.entityType = entity;
		this.duration = selectDuration();
		this.material = material;
		this.armorColor = armorColor;
		this.type = type;
		this.lootToGet = lootToGet();
		this.invSlots = selectSlots();
		this.item = createItem();
		if(i == null) {
			this.item = createItem();
		}
		else {
			this.item = i;
			this.name = item.getItemMeta().getDisplayName();
		}
	}
	
	public static Minion createNewFromType(MaterialType type, int level) {
		try {
			Class<?> cl = Class.forName("net.colonymc.colonyskyblockcore.minions.types." + type.className);
			Constructor<?> con = cl.getDeclaredConstructors()[0];
			return (Minion) con.newInstance(level, null);
		} catch (ClassNotFoundException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ItemStack getNormalItemStack(String name, String description) {
		ItemStack skull = new SkullItemBuilder()
				.url("http://textures.minecraft.net/texture/" + skinUrl)
				.name(ChatColor.LIGHT_PURPLE + name + " (" + RomanNumber.toRoman(level) + ")")
				.lore("\n" + description + "\n \n&dClick here to place it")
				.addFlag(ItemFlag.HIDE_ATTRIBUTES)
				.addTag("minionType", new NBTTagString(this.material.encodedName))
				.addTag("minionLevel", new NBTTagInt(level))
				.build();
		this.name = skull.getItemMeta().getDisplayName();
		return skull;
	}
	
	private int selectDuration() {
		switch(level) {
		case 1:
			return 20;
		case 2:
			return 17;
		case 3:
			return 16;
		case 4:
			return 15;
		case 5: 
			return 14;
		case 6:
			return 12;
		case 7:
			return 11;
		case 8:
			return 10;
		case 9:
			return 8;
		case 10:
			return 7;
		}
		return -1;
	}
	
	private int selectSlots() {
		switch(level) {
		case 1:
			return 4;
		case 2:
			return 6;
		case 3:
			return 8;
		case 4:
			return 10;
		case 5: 
			return 11;
		case 6:
			return 12;
		case 7:
			return 14;
		case 8:
			return 16;
		case 9:
			return 18;
		case 10:
			return 20;
		}
		return -1;
	}
	
	public Material getBlocksNeeded() {
		return blocksNeeded;
	}
	
	public EntityType getEntityType() {
		return entityType;
	}
	
	public MaterialType getMaterial() {
		return material;
	}
	
	public MinionType getType() {
		return type;
	}
	
	public Material getFarmingFor() {
		return farmingFor;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public int getSlots() {
		return invSlots;
	}
	
	public int getLevel() {
		return level;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSkinUrl() {
		return skinUrl;
	}
	
	public Color getColor() {
		return armorColor;
	}
	
	public ItemStack getItemStack() {
		return item;
	}
	
	public HashMap<ItemStack, RandomAmount> getLoot() {
		return lootToGet;
	}

}
