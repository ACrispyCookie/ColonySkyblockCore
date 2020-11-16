package net.colonymc.colonyskyblockcore.guilds.war;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyspigotapi.itemstacks.Serializer;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.guilds.Guild;

public class EndedWar {
	
	Guild oneGuild;
	Guild anotherGuild;
	final Guild winner;
	final WarType type;
	final long timeStarted;
	final long timeEnded;
	final boolean draw;
	final int warId;
	final String topDamagerUuid;
	HashMap<Guild, ArrayList<ItemStack>> loot = new HashMap<>();
	final HashMap<Guild, Integer> lootCollected = new HashMap<>();
	final HashMap<Guild, Integer> moneyCollected = new HashMap<>();
	static final ArrayList<EndedWar> wars = new ArrayList<>();
	
	public EndedWar(int warId, Guild oneGuild, Guild anotherGuild, Guild winner, boolean draw, WarType type, long timeStarted, long timeEnded, int moneyCollected, String topDamagerUuid, HashMap<Guild, ArrayList<ItemStack>> loot) {
		this.warId = warId;
		this.oneGuild = oneGuild;
		this.anotherGuild = anotherGuild;
		this.winner = winner;
		this.type = type;
		this.draw = draw;
		this.timeStarted = timeStarted;
		this.timeEnded = timeEnded;
		this.topDamagerUuid = topDamagerUuid;
		this.loot = loot;
		setupLootCollected();
		wars.add(this);
		this.moneyCollected.put(winner, moneyCollected);
		this.moneyCollected.put((winner != null && winner.equals(oneGuild)) || (winner == null && oneGuild == null) ? anotherGuild : oneGuild, 0);
	}
	
	private void setupLootCollected() {
		for(Guild g : loot.keySet()) {
			int coll = 0;
			for(ItemStack i : loot.get(g)) {
				coll = coll + i.getAmount();
			}
			lootCollected.put(g, coll);
		}
	}

	public int getWarId() {
		return warId;
	}
	
	public Guild getOneGuild() {
		return oneGuild;
	}
	
	public Guild getAnotherGuild() {
		return anotherGuild;
	}
	
	public Guild getWinner() {
		return winner;
	}
	
	public Guild getLoser() {
		return oneGuild.equals(winner) ? anotherGuild : oneGuild;
	}
	
	public WarType getType() {
		return type;
	}
	
	public long getTimeStarted() {
		return timeStarted;
	}
	
	public long getTimeEnded() {
		return timeEnded;
	}
	
	public int getSilverCollected(Guild g) {
		return moneyCollected.get(g);
	}
	
	public String getTopDamager() {
		return topDamagerUuid;
	}
	
	public int getItemsCollected(Guild g) {
		return lootCollected.get(g);
	}
	
	public boolean wasDraw() {
		return draw;
	}
	
	public void setOneGuild(Guild guild) {
		oneGuild = guild;
	}
	
	public void setAnotherGuild(Guild guild) {
		anotherGuild = guild;
	}
	
	public void removeItem(ItemStack i, Guild g) {
		if(loot.get(g) != null) {
			loot.get(g).remove(i);
			Database.sendStatement("DELETE FROM WarItems WHERE warId=" + this.getWarId() + " AND guildId=" + g.getId() + " AND item='" + Serializer.serializeItemStack(i) + "';");
		}
	}
	
	public ArrayList<ItemStack> getLoot(Guild g) {
		if(loot.get(g) != null) {
			return loot.get(g);
		}
		return new ArrayList<>();
	}
	
	public static EndedWar getById(int id) {
		for(EndedWar war : wars) {
			if(war.getWarId() == id) {
				return war;
			}
		}
		return null;
	}
	
	public static ArrayList<EndedWar> getByGuild(Guild g) {
		ArrayList<EndedWar> guildWars = new ArrayList<>();
		for(EndedWar war : wars) {
			if((war.getAnotherGuild() != null && war.getAnotherGuild().equals(g)) || (war.getOneGuild() != null && war.getOneGuild().equals(g))) {
				guildWars.add(war);
			}
		}
		return guildWars;
	}
}
