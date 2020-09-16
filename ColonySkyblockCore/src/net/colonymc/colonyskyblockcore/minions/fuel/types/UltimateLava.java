package net.colonymc.colonyskyblockcore.minions.fuel.types;

import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyskyblockcore.minions.MinionBlock;
import net.colonymc.colonyskyblockcore.minions.fuel.Fuel;
import net.colonymc.colonyskyblockcore.minions.fuel.FuelType;

public class UltimateLava extends Fuel {

	public UltimateLava(MinionBlock b, ItemStack i, int durationLeft) {
		super(80, 21600, FuelType.ULTIMATE_LAVA, b, i, durationLeft);
	}

	@Override
	protected ItemStack createItem() {
		return getNormalItemStack("&dUltimate Lava", "&fUse this on a minion to boost\n&fits production by &d80% &ffor &d6 hours&f!");
	}

}
