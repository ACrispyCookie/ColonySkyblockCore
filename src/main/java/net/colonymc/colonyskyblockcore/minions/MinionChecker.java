package net.colonymc.colonyskyblockcore.minions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyspigotlib.lib.itemstack.ItemStackNBT;

public class MinionChecker {
	
	public static boolean isMinion(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getType() != Material.AIR && ItemStackNBT.hasTag(item, "minionType");
	}
	
	public static Minion getMinion(ItemStack i) {
		try {
			Class<?> cl = Class.forName("net.colonymc.colonyskyblockcore.minions.types." + whatType(i).className);
			Constructor<?> con = cl.getDeclaredConstructors()[0];
			return (Minion) con.newInstance(getLevel(i), i);
		} catch (ClassNotFoundException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static int getLevel(ItemStack i) {
		if(isMinion(i)) {
			return ItemStackNBT.getInt(i, "minionLevel");
		}
		return -1;
	}
	
	public static MaterialType whatType(ItemStack item) {
		if(isMinion(item)) {
				return typeFromEncodedName(ItemStackNBT.getString(item, "minionType"));
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
