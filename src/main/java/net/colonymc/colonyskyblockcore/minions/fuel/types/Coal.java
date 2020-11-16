package net.colonymc.colonyskyblockcore.minions.fuel.types;

import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyskyblockcore.minions.fuel.Fuel;
import net.colonymc.colonyskyblockcore.minions.fuel.FuelType;

public class Coal extends Fuel {

	public Coal(ItemStack i, int durationLeft) {
		super(10, 20, FuelType.COAL, i, durationLeft);
	}

	@Override
	protected ItemStack createItem() {
		return getNormalItemStack("&dCoal fuel", "&fUse this on a minion to boost\n&fits production by &d10% &ffor &d2 hours&f!");
	}

}
