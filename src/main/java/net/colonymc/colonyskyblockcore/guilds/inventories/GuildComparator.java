package net.colonymc.colonyskyblockcore.guilds.inventories;

import java.util.Comparator;

import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.Relation;

public class GuildComparator implements Comparator<Guild> {

	@Override
	public int compare(Guild g, Guild g1) {
		if(g.getRelation(g1) == Relation.ALLY) {
			return 1;
		}
		else if(g.getRelation(g1) == Relation.ENEMY) {
			return -1;
		}
		return 0;
	}

}
