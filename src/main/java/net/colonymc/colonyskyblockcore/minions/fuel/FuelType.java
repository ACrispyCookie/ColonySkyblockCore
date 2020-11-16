package net.colonymc.colonyskyblockcore.minions.fuel;

import org.bukkit.Material;

public enum FuelType {
	COAL(Material.COAL, "Coal"),
	ULTIMATE_LAVA(Material.LAVA_BUCKET, "UltimateLava");
	
	public final Material mat;
	public final String className;
	
	FuelType(Material mat, String name) {
		this.mat = mat;
		this.className = name;
	}

}
