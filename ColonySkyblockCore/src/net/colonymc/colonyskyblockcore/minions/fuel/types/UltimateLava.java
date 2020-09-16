package net.colonymc.colonyskyblockcore.minions.fuel.types;

import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyskyblockcore.minions.fuel.Fuel;
import net.colonymc.colonyskyblockcore.minions.fuel.FuelType;

public class UltimateLava extends Fuel {

	public UltimateLava(ItemStack i, int durationLeft) {
		super(25, 21600, FuelType.ULTIMATE_LAVA, i, durationLeft);
	}

	@Override
	protected ItemStack createItem() {
		return getNormalItemStack("&dUltimate Lava", "&fUse this on a minion to boost\n&fits production by &d25% &ffor &d6 hours&f!");
	}

}
