package net.colonymc.colonyskyblockcore.minions.fuel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.colonymc.api.itemstacks.NBTItems;
import net.minecraft.server.v1_8_R3.NBTTagInt;

public class FuelChecker {
	
	public static boolean isFuel(ItemStack item) {
		if(item != null && item.hasItemMeta() && item.getType() != Material.AIR && NBTItems.hasTag(item, "minionFuel")) {
			return true;
		}
		return false;
	}
	
	public static Fuel getFuel(ItemStack i) {
		try {
			Class<?> cl = Class.forName("net.colonymc.colonyskyblockcore.minions.fuel.types." + whatType(i).className);
			Constructor<?> con = cl.getDeclaredConstructors()[0];
			Fuel item;
			if(NBTItems.hasTag(i, "fuelDurationLeft")) {
				int durationLeft = ((NBTTagInt) NBTItems.getTag(i, "fuelDurationLeft")).d();
				item = (Fuel) con.newInstance(null, i, durationLeft);
			}
			else {
				item = (Fuel) con.newInstance(null, i, -1);
			}
			return item;
		} catch (ClassNotFoundException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static FuelType whatType(ItemStack item) {
		if(item != null && item.getItemMeta() != null && item.getType() != Material.AIR && isFuel(item)) {
				return FuelType.valueOf(NBTItems.getString(item, "minionFuel"));
		}
		return null;
	}
	
	public static FuelType typeFromEncodedName(String s) {
		for(FuelType t : FuelType.values()) {
			if(t.className.equalsIgnoreCase(s)) {
				return t;
			}
		}
		return null;
	}

}
