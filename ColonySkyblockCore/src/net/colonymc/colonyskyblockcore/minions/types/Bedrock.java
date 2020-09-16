package net.colonymc.colonyskyblockcore.minions.types;

import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyskyblockcore.minions.MaterialType;
import net.colonymc.colonyskyblockcore.minions.Minion;
import net.colonymc.colonyskyblockcore.minions.MinionType;
import net.colonymc.colonyskyblockcore.minions.RandomAmount;

public class Bedrock extends Minion {
	
	public Bedrock(int level, ItemStack i) {
		super(level, "36d1fabdf3e342671bd9f95f687fe263f439ddc2f1c9ea8ff15b13f1e7e48b9", MaterialType.BEDROCK, MinionType.MINER, Material.BEDROCK, null, Color.BLACK, i);
	}

	@Override
	public ItemStack createItem() {
		return getNormalItemStack("Bedrock Minion", "&fThis minion when placed\n&fon a 5x5 area of bedrock,\n&fwill collect a bedrock block every &d" + duration + "s");
	}

	@Override
	public HashMap<ItemStack, RandomAmount> lootToGet() {
		HashMap<ItemStack, RandomAmount> lootToGet = new HashMap<ItemStack, RandomAmount>();
		lootToGet.put(new ItemStack(Material.BEDROCK), new RandomAmount(100, 1));
		return lootToGet;
	}
	
}
