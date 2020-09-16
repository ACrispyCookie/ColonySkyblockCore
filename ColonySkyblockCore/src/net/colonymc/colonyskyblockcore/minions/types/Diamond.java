package net.colonymc.colonyskyblockcore.minions.types;

import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyskyblockcore.minions.MaterialType;
import net.colonymc.colonyskyblockcore.minions.Minion;
import net.colonymc.colonyskyblockcore.minions.MinionType;
import net.colonymc.colonyskyblockcore.minions.RandomAmount;

public class Diamond extends Minion {

	public Diamond(int level, ItemStack i) {
		super(level, "11ed9abf51fe4ea84cfcb27297f1bc54cd382edf85e7bd6e75ecca2b806611", MaterialType.DIAMOND, MinionType.MINER, Material.DIAMOND_ORE, null, Color.TEAL, i);
	}

	@Override
	public ItemStack createItem() {
		return getNormalItemStack("Diamond Minion", "&fThis minion when placed\n&fon a 5x5 area of diamond ore,\n&fwill collect diamonds every &d" + duration + "s");
	}
	
	@Override
	public HashMap<ItemStack, RandomAmount> lootToGet() {
		HashMap<ItemStack, RandomAmount> lootToGet = new HashMap<ItemStack, RandomAmount>();
		lootToGet.put(new ItemStack(Material.DIAMOND), new RandomAmount(100, 1));
		return lootToGet;
	}
}
