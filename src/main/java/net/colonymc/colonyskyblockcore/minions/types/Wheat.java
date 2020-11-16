package net.colonymc.colonyskyblockcore.minions.types;

import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyskyblockcore.minions.MaterialType;
import net.colonymc.colonyskyblockcore.minions.Minion;
import net.colonymc.colonyskyblockcore.minions.MinionType;
import net.colonymc.colonyskyblockcore.minions.RandomAmount;

public class Wheat extends Minion {

	public Wheat(int level, ItemStack i) {
		super(level, "9af328c87b068509aca9834eface197705fe5d4f0871731b7b21cd99b9fddc", MaterialType.WHEAT, MinionType.FARMER, Material.SOIL, Material.CROPS, Color.YELLOW, i);
	}

	@Override
	public ItemStack createItem() {
		return getNormalItemStack("Wheat Minion", "&fThis minion when placed\n&fon a 5x5 area of dirt,\n&fwill collect wheat and seeds every &d" + duration + "s");
	}
	
	@Override
	public HashMap<ItemStack, RandomAmount> lootToGet() {
		HashMap<ItemStack, RandomAmount> lootToGet = new HashMap<>();
		lootToGet.put(new ItemStack(Material.WHEAT), new RandomAmount(100, 1));
		lootToGet.put(new ItemStack(Material.SEEDS), new RandomAmount(40, 3));
		return lootToGet;
	}
}
