package net.colonymc.colonyskyblockcore.pouches;

public enum PouchType {
	MONEY("&6Money", '6'),
	DUST("&eDust", 'e');

	final String pouchType;
	final char color;
	
	PouchType(String type, char color){
		pouchType = type;
		this.color = color;
	}
	
	public static boolean contains(String name) {
		try {
			PouchType.valueOf(name.toUpperCase());
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
}
