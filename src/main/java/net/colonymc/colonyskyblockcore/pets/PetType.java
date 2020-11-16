package net.colonymc.colonyskyblockcore.pets;

public enum PetType {
	RABBIT("&6Rabbit", "7d1169b2694a6aba826360992365bcda5a10c89a3aa2b48c438531dd8685c3a7"),
	COW("&6Cow", "7dfa0ac37baba2aa290e4faee419a613cd6117fa568e709d90374753c032dcb0");
	
	final String name;
	final String description;
	final String url;
	
	PetType(String name, String url){
		this.name = name;
		this.description = "&fThis pet will give you jump boost and\n&fhelp you while farming!";
		this.url = url;
	}
	
	public static boolean contains(String name) {
		try {
			PetType.valueOf(name.toUpperCase());
			return true;
		} catch(Exception e) {
			return false;
		}
	}

}
