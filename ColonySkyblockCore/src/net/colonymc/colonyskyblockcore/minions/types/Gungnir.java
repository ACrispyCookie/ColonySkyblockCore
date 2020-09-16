package net.colonymc.colonyskyblockcore.minions.types;

import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyskyblockcore.minions.MaterialType;
import net.colonymc.colonyskyblockcore.minions.Minion;
import net.colonymc.colonyskyblockcore.minions.MinionType;
import net.colonymc.colonyskyblockcore.minions.RandomAmount;
import net.colonymc.vikingcore.items.ItemType;
import net.colonymc.vikingcore.items.SpecialItem;

public class Gungnir extends Minion {

	public Gungnir(int level, ItemStack i) {
		super(level, "b78ef2e4cf2c41a2d14bfde9caff10219f5b1bf5b35a49eb51c6467882cb5f0", MaterialType.GUNGNIR, MinionType.SLAYER, EntityType.BLAZE, Color.FUCHSIA, i);
	}

	@Override
	public ItemStack createItem() {
		return getNormalItemStack("Gungnir Minion", "&fThis minion when placed\n&fon a 5x5 open area, will kill blazes\n&fand collect &cGungirs &7(&aI&7) &fevery &d" + duration + "s");
	}

	@Override
	public HashMap<ItemStack, RandomAmount> lootToGet() {
		HashMap<ItemStack, RandomAmount> loot = new HashMap<ItemStack, RandomAmount>();
		loot.put(SpecialItem.getByType(ItemType.GUNGNIR, 250, 250, 1, null).getItemStack(), new RandomAmount(30, 3));
		return loot;
	}

}
