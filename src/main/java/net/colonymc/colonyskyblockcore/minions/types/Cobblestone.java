package net.colonymc.colonyskyblockcore.minions.types;

import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyskyblockcore.minions.MaterialType;
import net.colonymc.colonyskyblockcore.minions.Minion;
import net.colonymc.colonyskyblockcore.minions.MinionType;
import net.colonymc.colonyskyblockcore.minions.RandomAmount;

public class Cobblestone extends Minion {

	public Cobblestone(int level, ItemStack i) {
		super(level, "dc1754851e367e8beba2a6d8f7c2fede87ae793ac546b0f299d673215b293", MaterialType.COBBLESTONE, MinionType.MINER, Material.COBBLESTONE, null, Color.fromRGB(128, 128, 128), i);
	}

	@Override
	public ItemStack createItem() {
		return getNormalItemStack("Cobblestone Minion", "&fThis minion when placed\n&fon a 5x5 area of cobblestone,\n&fwill collect cobblestone every &d" + duration + "s");
	}

	@Override
	public HashMap<ItemStack, RandomAmount> lootToGet() {
		HashMap<ItemStack, RandomAmount> lootToGet = new HashMap<>();
		lootToGet.put(new ItemStack(Material.COBBLESTONE), new RandomAmount(100, 1));
		return lootToGet;
	}

}
