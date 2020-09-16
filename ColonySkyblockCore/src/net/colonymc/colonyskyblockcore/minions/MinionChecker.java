package net.colonymc.colonyskyblockcore.minions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.colonymc.api.itemstacks.NBTItems;

public class MinionChecker {
	
	public static boolean isMinion(ItemStack item) {
		if(item != null && item.hasItemMeta() && item.getType() != Material.AIR && NBTItems.hasTag(item, "minionType")) {
			return true;
		}
		return false;
	}
	
	public static Minion getMinion(ItemStack i) {
		try {
			Class<?> cl = Class.forName("net.colonymc.colonyskyblockcore.minions.types." + whatType(i).className);
			Constructor<?> con = cl.getDeclaredConstructors()[0];
			Minion item = (Minion) con.newInstance(getLevel(i), i);
			return item;
		} catch (ClassNotFoundException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static int getLevel(ItemStack i) {
		if(i != null && isMinion(i)) {
			return NBTItems.getInt(i, "minionLevel");
		}
		return -1;
	}
	
	public static MaterialType whatType(ItemStack item) {
		if(item != null && isMinion(item)) {
				return typeFromEncodedName(NBTItems.getString(item, "minionType"));
		}
		return null;
	}
	
	public static MaterialType typeFromEncodedName(String s) {
		for(MaterialType t : MaterialType.values()) {
			if(t.encodedName.equalsIgnoreCase(s)) {
				return t;
			}
		}
		return null;
	}

}
