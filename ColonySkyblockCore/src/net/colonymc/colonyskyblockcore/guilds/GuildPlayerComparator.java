package net.colonymc.colonyskyblockcore.guilds;

import java.util.Comparator;

public class GuildPlayerComparator implements Comparator<GuildPlayer> {

	@Override
	public int compare(GuildPlayer p1, GuildPlayer p2) {
		Role r1 = p1.getRole();
		Role r2 = p2.getRole();
		if(p1.getPlayer().isOnline() && p2.getPlayer().isOnline()) {
			if(r1.ordinal() > r2.ordinal()) {
				return -1;
			}
			else if(r1.ordinal() < r2.ordinal()) {
				return 1;
			}
			else {
				return 0;
			}
		}
		else if(p1.getPlayer().isOnline() && !p2.getPlayer().isOnline()) {
			return -1;
		}
		else if(!p1.getPlayer().isOnline() && p2.getPlayer().isOnline()){
			return 1;
		}
		else {
			if(r1.ordinal() > r2.ordinal()) {
				return -1;
			}
			else if(r1.ordinal() < r2.ordinal()) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}

}
