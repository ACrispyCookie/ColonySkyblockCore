package net.colonymc.colonyskyblockcore.minions.types;

import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyskyblockcore.minions.MaterialType;
import net.colonymc.colonyskyblockcore.minions.Minion;
import net.colonymc.colonyskyblockcore.minions.MinionType;
import net.colonymc.colonyskyblockcore.minions.RandomAmount;

public class Pig extends Minion {

	public Pig(int level, ItemStack i) {
		super(level, "621668ef7cb79dd9c22ce3d1f3f4cb6e2559893b6df4a469514e667c16aa4", MaterialType.PIG, MinionType.SLAYER, EntityType.PIG, Color.FUCHSIA, i);
	}

	@Override
	public ItemStack createItem() {
		return getNormalItemStack("Pig Minion", "&fThis minion when placed\n&fon a 5x5 open area,\n&fwill spawn and kill pigs every &d" + duration + "s");
	}
	
	@Override
	public HashMap<ItemStack, RandomAmount> lootToGet() {
		HashMap<ItemStack, RandomAmount> lootToGet = new HashMap<>();
		lootToGet.put(new ItemStack(Material.PORK), new RandomAmount(60, 3));
		return lootToGet;
	}

}
