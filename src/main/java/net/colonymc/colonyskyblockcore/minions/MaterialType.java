package net.colonymc.colonyskyblockcore.minions;

public enum MaterialType {
	COBBLESTONE("Cobblestone", "cobblestone"),
	PIG("Pig", "pig"),
	WHEAT("Wheat", "wheat"),
	DIAMOND("Diamond", "diamond"),
	GUNGNIR("Gungnir", "gungnirMinion"),
	BEDROCK("Bedrock", "bedrock");
	
	public final String className;
	public final String encodedName;
	
	MaterialType(String clName, String encodedName) {
		className = clName;
		this.encodedName = encodedName;
	}
}
