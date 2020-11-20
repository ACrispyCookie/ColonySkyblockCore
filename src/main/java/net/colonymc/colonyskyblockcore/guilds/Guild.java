package net.colonymc.colonyskyblockcore.guilds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.colonymc.colonyspigotapi.api.player.visuals.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyspigotapi.api.itemstack.ItemStackSerializer;
import net.colonymc.colonyspigotapi.api.itemstack.SkullItemBuilder;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.guilds.auction.Auction;
import net.colonymc.colonyskyblockcore.guilds.bank.BankTransaction;
import net.colonymc.colonyskyblockcore.guilds.bank.Currency;
import net.colonymc.colonyskyblockcore.guilds.war.EndedWar;
import net.colonymc.colonyskyblockcore.guilds.war.TeamDeathmatch;
import net.colonymc.colonyskyblockcore.guilds.war.WarType;
import net.colonymc.colonyskyblockcore.npcs.NPCListener;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class Guild {
	
	public final static String chatPrefix = "&d[Guild] ";
	String name;
	int id;
	int level;
	double balance;
	double dust;
	boolean open;
	int renamesRemaining;
	boolean islandCreated;
	Island island;
	final ArrayList<BankTransaction> transactions = new ArrayList<>();
	final ArrayList<EndedWar> endedWars = new ArrayList<>();
	HashMap<UUID, GuildPlayer> members = new HashMap<>();
	final HashMap<Guild, Relation> relations = new HashMap<>();
	final HashMap<String, ArrayList<ItemStack>> unclaimedItems = new HashMap<>();
	public static final ArrayList<Guild> loadedGuilds = new ArrayList<>();
	
	public Guild(String name, int level) {
		this.name = name;
		this.level = level;
	}
	
	public Guild(String name, int id, int level, double balance, double dust, boolean open, int renames, boolean islandCreated) {
		this.name = name;
		this.id = id;
		this.level = level;
		this.open = open;
		this.balance = balance;
		this.dust = dust;
		this.renamesRemaining = renames;
		this.islandCreated = islandCreated;
		if(islandCreated) {
			this.island = Island.getByGuild(this);
		}
		else {
			this.island = null;
		}
	}
	
	public void sendGuildTitle(String title, String subtitle, int fadeIn, int duration, int fadeOut) {
		for(UUID u : members.keySet()) {
			if(Bukkit.getPlayer(u) != null) {
				IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
				IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
				PacketPlayOutTitle titlep = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
				PacketPlayOutTitle subtitlep = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
				PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, duration, fadeOut);
				((CraftPlayer) Bukkit.getPlayer(u)).getHandle().playerConnection.sendPacket(titlep);
				((CraftPlayer) Bukkit.getPlayer(u)).getHandle().playerConnection.sendPacket(subtitlep);
				((CraftPlayer) Bukkit.getPlayer(u)).getHandle().playerConnection.sendPacket(length);
			}
		}
	}
	
	public void sendGuildMessage(String msg) {
		for(UUID u : members.keySet()) {
			if(Bukkit.getPlayer(u) != null) {
				Bukkit.getPlayer(u).sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + msg));
			}
		}
	}
	
	public void sendGuildMessage(TextComponent msg) {
		TextComponent finalMsg = new TextComponent(ChatColor.translateAlternateColorCodes('&', chatPrefix));
		finalMsg.addExtra(msg);
		for(UUID u : members.keySet()) {
			if(Bukkit.getPlayer(u) != null) {
				Bukkit.getPlayer(u).spigot().sendMessage(finalMsg);
			}
		}
	}
	
	public void sendGuildSound(Sound sound, float pitch) {
		for(UUID u : members.keySet()) {
			if(Bukkit.getPlayer(u) != null) {
				Bukkit.getPlayer(u).playSound(Bukkit.getPlayer(u).getLocation(), sound, 2, pitch);
			}
		}
	}
	
	public void addBalance(double amount, GuildPlayer p, boolean left) {
		if(!left) {
			this.sendGuildMessage("&fThe guild member &d" + p.getPlayer().getName() + " &fhas deposited &d" + balance(amount) + " &fto the guild bank!");
		}
		else {
			this.sendGuildMessage("&d" + balance(amount) + " &fhave been added to your guild bank because the player &d" + p.getPlayer().getName() + " &fleft the guild!");
		}
		setBalance(balance + amount);
	}
	
	public void removeBalance(double amount, GuildPlayer p) {
		this.sendGuildMessage("&fThe guild member &d" + p.getPlayer().getName() + " &fhas withdrawn &d" + balance(amount) + " &ffrom the guild bank!");
		setBalance(balance - amount);
	}
	
	public void addDust(double amount, GuildPlayer p, boolean left) {
		if(!left) {
			this.sendGuildMessage("&fThe guild member &d" + p.getPlayer().getName() + " &fhas deposited &d" + balance(amount) + " of dwarf dust &fto the guild bank!");
		}
		else {
			this.sendGuildMessage("&d" + balance(amount) + " of dwarf dust &fhave been added to your guild bank because the player &d" + p.getPlayer().getName() + " &fleft the guild!");
		}
		setDust(dust + amount);
	}
	
	public void removeDust(double amount, GuildPlayer p) {
		this.sendGuildMessage("&fThe guild member &d" + p.getPlayer().getName() + " &fhas withdrawn &d" + balance(amount) + " of dwarf dust &ffrom the guild bank!");
		setDust(dust - amount);
	}
	
	public void addLevels(int amount, TeamDeathmatch d) {
		Guild lost = d.getWinner().equals(d.getWar().getRequested()) ? d.getWar().getRequester() : d.getWar().getRequested();
		this.sendGuildMessage("&fYour guild received &d" + amount + " &fof power levels from the war against &d" + lost.getName() + "&f!");
		setLevel(level + amount);
	}
	
	public void addBalance(double amount, TeamDeathmatch d) {
		Guild lost = d.getWinner().equals(d.getWar().getRequested()) ? d.getWar().getRequester() : d.getWar().getRequested();
		this.sendGuildMessage("&fYour guild received &d" + balance(amount) + " &ffrom the war against &d" + lost.getName() + "&f!");
		setBalance(balance + amount);
	}
	
	public void removeBalance(double amount, TeamDeathmatch d) {
		this.sendGuildMessage("&fYour guild lost &d" + balance(amount) + " &ffrom the war against &d" + d.getWinner().getName() + "&f!");
		setBalance(balance - amount);
	}
	
	public void addTransaction(BankTransaction b) {
		this.transactions.add(b);
		Player p = (Player) b.getPlayer().getPlayer();
		String action = "DEPOSIT";
		int amount = b.getAmount();
		if(b.getAmount() < 0) {
			action = "WITHDRAW";
			amount = -amount;
			NPCListener.sendRotatingHead(NPCListener.banker, p, new SkullItemBuilder().url("http://textures.minecraft.net/texture/e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852").build(),
					"You withdrew &d" + Guild.balance(amount) + "&f!", true, 4);
		}
		else {
			NPCListener.sendRotatingHead(NPCListener.banker, p, new SkullItemBuilder().url("http://textures.minecraft.net/texture/e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852").build(),
					"You deposited &d" + Guild.balance(amount) + "&f!", false, 4);
		}
		Database.sendStatement("INSERT INTO GuildTransactions (id, action, amount, type, timestamp, playerUuid) VALUES "
				+ "(" + this.getId() + ", '" + action + "', " + amount + ", '" + b.getCurrency().name() + "', " + b.getTimestamp() + ", '" + b.getPlayer().getPlayer().getUniqueId().toString() + "')");
	}
	
	public void createIsland(){
		if(!islandCreated) {
			this.island = new Island(this, 0, null, null, false, null);
			this.island.create();
			this.islandCreated = true;
			Database.sendStatement("UPDATE GuildInfo SET islandCreated='1' WHERE id=" + this.getId() + ";");
		}
	}
	
	public void rename(String name) {
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThe guild &d" + this.name + " &fis now known as &d" + name + "!"));
		this.setRenamesRemaining(this.getRenamesRemaining() - 1);
		this.setName(name);
		this.sendGuildMessage("&fYour guild's name has been changed to &d" + this.getName() + "&f!");
		this.sendGuildSound(Sound.ANVIL_USE, 1);
	}
	
	public void toggleOpen() {
		this.setOpen(!open);
		if(open) {
			this.sendGuildMessage("&fThe guild is now open!");
			this.sendGuildSound(Sound.VILLAGER_YES, 1);
		}
		else {
			this.sendGuildMessage("&fThe guild is now closed!");
			this.sendGuildSound(Sound.VILLAGER_NO, 1);
		}
	}
	
	public void levelUp(int amount) {
		setLevel(level + amount);
	}
	
	@SuppressWarnings("deprecation")
	public void disband(){
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThe guild &d" + name + " &fhas been disbanded!"));
		ArrayList<OfflinePlayer> visitors = new ArrayList<>();
		for(OfflinePlayer p : Island.visitors.keySet()) {
			if(Island.visitors.get(p).equals(this.getIsland())) {
				visitors.add(p);
			}
		}
		for(OfflinePlayer p : visitors) {
			if(p.isOnline()) {
				if(this.getMemberUuids().containsKey(p.getUniqueId())) {
					p.getPlayer().chat("/spawn");
				}
				else {
					Guild.getByPlayer(p).getIsland().sendPlayer((Player) p, false);
					((Player) p).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fThe guild of the island you were in got disbanded, so you teleported to your island!"));
					((Player) p).playSound(((Player) p).getLocation(), Sound.NOTE_BASS, 2, 1);
				}
			}
		}
		//irreversible
		ArrayList<Guild> relations = new ArrayList<>(this.relations.keySet());
		for (Guild relation : relations) {
			removeRelation(relation, true, true);
		}
		for(Auction a : Auction.activeAuctions) {
			if(a.getTopBidder() != null && a.getTopBidder().getPlayer().getGuild().equals(this)) {
				a.removeBidder(a.getTopBidder());
			}
		}
		if(loadedGuilds.contains(this)) {
			loadedGuilds.remove(this);
		}
		this.island.delete();
		this.endedWars.clear();
		ArrayList<UUID> uuids = new ArrayList<>(members.keySet());
		this.sendGuildMessage("&c&l&k:&c&lGUILD DISBANDED!&k:&r &fYour guild has been &cDISBANDED!");
		this.sendGuildSound(Sound.ENDERDRAGON_GROWL, 1);
		for (UUID uuid : uuids) {
			removeMember(Bukkit.getOfflinePlayer(uuid));
			((Player) Bukkit.getOfflinePlayer(uuid)).sendTitle(ChatColor.translateAlternateColorCodes('&', "&cGUILD DISBAND"), ChatColor.translateAlternateColorCodes('&', "&fYour guild has been disbanded!"));
		}
		Database.sendStatement("DELETE FROM IslandInfo WHERE id=" + this.getId() + ";");
		Database.sendStatement("DELETE FROM GuildTransactions WHERE id=" + this.getId() + ";");
		Database.sendStatement("UPDATE GuildWars SET oneId=null WHERE oneId=" + this.getId() + ";");
		Database.sendStatement("UPDATE GuildWars SET anotherId=null WHERE anotherId=" + this.getId() + ";");
		Database.sendStatement("UPDATE GuildWars SET winnerId=null WHERE winnerId=" + this.getId() + ";");
		Database.sendStatement("DELETE FROM WarItems WHERE guildId=" + this.getId() + ";");
		Database.sendStatement("DELETE FROM GuildWars WHERE oneId=null AND anotherId=null;");
		Database.sendStatement("DELETE FROM GuildInfo WHERE id=" + this.getId() + ";");
	}
	
	public void addMember(OfflinePlayer p, Role r){
		this.members.put(p.getUniqueId(), new GuildPlayer(p.getUniqueId(), this, r, System.currentTimeMillis(), 0, 0));
		Database.sendStatement("UPDATE PlayerInfo SET guild=" + this.getId() +  ", guildRank='MEMBER',timeJoined=" + System.currentTimeMillis() + " WHERE playerUuid='" + p.getUniqueId().toString() + "';");
	}
	
	public void removeMember(OfflinePlayer p){	
		if(this.members.containsKey(p.getUniqueId())) {
			Database.sendStatement("UPDATE PlayerInfo SET guild=0, guildRank='MEMBER',timeJoined=0 WHERE playerUuid='" + p.getUniqueId().toString() + "';");
			if(p.isOnline()) {
				p.getPlayer().closeInventory();
				p.getPlayer().chat("/spawn");
				ScoreboardManager.getByPlayer(p.getPlayer()).setType("starting");
				GuildListeners.forceCreate((Player) p, false);
			}
			this.members.remove(p.getUniqueId());
		}
	}
	
	public void ally(Guild g, Player p) {
		if(GuildAllyRequest.isAlliedBy(g, this)){
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou have already requested from this guild to become allies!"));
			p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
		}
		else if(GuildAllyRequest.isAlliedBy(this, g)) {
			GuildAllyRequest.getAllyRequestByGuild(this, g).accept();
		}
		else {
			new GuildAllyRequest(this, g);
		}
	}
	
	public void setRelation(Guild g, Relation r, boolean addToDatabase){
		if(r == Relation.NEUTRAL) {
			removeRelation(g, false, true);
			return;
		}
		this.relations.put(g, r);
		this.sendGuildMessage("&fThe guild's relation with the guild &d" + g.getName() + " &fhas been changed to " + r.color + r.name + "&f!");
		if(addToDatabase) {
			try {
				ResultSet rs = Database.getResultSet("SELECT * FROM GuildRelations WHERE id=" + this.getId() + " AND relatedId=" + g.getId() + ";");
				if(!rs.next()) {
					Database.sendStatement("INSERT INTO GuildRelations (id, relatedId, relation) VALUES (" + this.getId() + ", " + g.getId() + ", '" + r.name() + "');");
				}
				else {
					Database.sendStatement("UPDATE GuildRelations SET relation='" + r.name() + "' WHERE id=" + this.getId() + " AND relatedId=" + g.getId() + ";");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			g.setRelation(this, r, !addToDatabase);
		}
	}
	
	public void addWar(TeamDeathmatch d, boolean addToDatabase) {
		Guild oneGuild = d.getWar().getRequested();
		Guild anotherGuild = d.getWar().getRequester();
		Guild winner = d.getWinner();
		WarType type = d.getWar().getType();
		EndedWar w = new EndedWar(d.getId(), oneGuild, anotherGuild, winner, winner == null, type, d.getTimeStarted(), d.getTimeEnded(), d.getMoneyCollected(), d.getTopDamager(), d.getItemMap());
		endedWars.add(w);
		if(addToDatabase) {
			Database.sendStatement("INSERT INTO GuildWars (warId, oneId, anotherId, type, winnerId, timeStarted, timeEnded, moneyCollected, topDamager) VALUES "
					+ "(" + d.getId() + ", " + oneGuild.getId() + ", " + anotherGuild.getId() + ", '" + type.name() + "', " + (winner == null ? String.valueOf(-1) : winner.getId()) + ", " + d.getTimeStarted() + ", " + d.getTimeEnded() + ", " 
					+ d.getMoneyCollected() + ", '" + d.getTopDamager() + "');");
			anotherGuild.addWar(d, !addToDatabase);
		}
		for(ItemStack i : d.getItems(this)) {
			Database.sendStatement("INSERT INTO WarItems (warId, guildId, item) VALUES "
					+ "(" + d.getId() + ", " + this.getId() + ", '" + ItemStackSerializer.serializeItemStack(i) + "');");
		}
	}
	
	public void addUnclaimedItems(String playerUuid, ArrayList<ItemStack> items) {
		if(unclaimedItems.containsKey(playerUuid)) {
			ArrayList<ItemStack> previousItems = unclaimedItems.get(playerUuid);
			previousItems.addAll(items);
			unclaimedItems.put(playerUuid, previousItems);
		}
		else {
			unclaimedItems.put(playerUuid, items);
		}
		for(ItemStack i : items) {
			Database.sendStatement("INSERT INTO GuildUnclaimed (guildId, playerUuid, item) VALUES "
					+ "(" + this.getId() + ", '" + playerUuid + "', '" + ItemStackSerializer.serializeItemStack(i) + "')");
		}
	}
	
	public void removeUnclaimedItem(String playerUuid, ItemStack item) {
		if(unclaimedItems.containsKey(playerUuid)) {
			unclaimedItems.get(playerUuid).remove(item);
			if(unclaimedItems.get(playerUuid).isEmpty()) {
				unclaimedItems.remove(playerUuid);
			}
			Database.sendStatement("DELETE FROM GuildUnclaimed WHERE guildId=" + this.getId() + " AND playerUuid='" + playerUuid + "' AND item='" + ItemStackSerializer.serializeItemStack(item) + "';");
		}
	}
	
	public HashMap<Player, GuildPlayer> getOnlineMembers(){
		HashMap<Player, GuildPlayer> online = new HashMap<>();
		for(UUID u : this.getMemberUuids().keySet()) {
			OfflinePlayer p = Bukkit.getOfflinePlayer(u);
			if(p.isOnline()) {
				online.put((Player) p, this.getMemberUuids().get(u));
			}
		}
		return online;
	}
	
	public GuildPlayer getOwner() {
		for(GuildPlayer p : this.members.values()) {
			if(p.getRole() == Role.OWNER) {
				return p;
			}
		}
		return null;
	}
	
	public GuildPlayer getGuildPlayer(OfflinePlayer p) {
		for(UUID u : this.members.keySet()) {
			if(p.getUniqueId().equals(u)) {
				return this.members.get(u);
			}
		}
		return null;
	}
	
	public int getTopPlace() {
		ArrayList<Guild> guilds = getSortedTopGuild(-1);
		for(int i = 0; i < guilds.size(); i++) {
			Guild g = guilds.get(i);
			if(g.equals(this)) {
				return i + 1;
			}
		}
		return -1;
	}
	
	public static ArrayList<Guild> getSortedTopGuild(int amount) {
		ResultSet rs = Database.getResultSet("SELECT * FROM GuildInfo ORDER BY guildLevel DESC;");
		ArrayList<Guild> sorted = new ArrayList<>();
		if(amount <= 0) {
			try {
				while(rs.next()) {
					sorted.add(Guild.getByName(rs.getString("guildName")));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else {
			for(int i = 0; i < amount; i++) {
				try {
					if(rs.next()) {
						sorted.add(Guild.getByName(rs.getString("guildName")));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return sorted;
	}
	
	public Relation getRelation(Guild g) {
		if(this.relations.containsKey(g)) {
			return this.relations.get(g);
		}
		return Relation.NEUTRAL;
	}
	
	public boolean hasIsland() {
		return islandCreated;
	}
	
	public Island getIsland() {
		return island;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public HashMap<Guild, Relation> getRelations(){
		return relations;
	}
	
	public HashMap<UUID, GuildPlayer> getMemberUuids(){
		return members;
	}
	
	public HashMap<String, ArrayList<ItemStack>> getUnclaimedItems(){
		return unclaimedItems;
	}
	
	public ArrayList<BankTransaction> getTransactions(){
		return transactions;
	}
	
	public ArrayList<EndedWar> getEndedWars(){
		return endedWars;
	}
	
	public int getLevel() {
		return level;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public double getDust() {
		return dust;
	}
	
	public int getRenamesRemaining() {
		return renamesRemaining;
	}
	
	private void removeRelation(Guild g, boolean disband, boolean removeFromDatabase){
		if(this.relations.containsKey(g)) {
			this.relations.remove(g);
			if(removeFromDatabase) {
				Database.sendStatement("DELETE FROM GuildRelations WHERE id=" + this.getId() + " AND relatedId=" + g.getId() + ";");
				Database.sendStatement("DELETE FROM GuildRelations WHERE relatedId=" + this.getId() + " AND id=" + g.getId() + ";");
				g.removeRelation(this, disband, !removeFromDatabase);
			}
			if(!disband) {
				this.sendGuildMessage("&fThe guild's relation with the guild &d" + g.getName() + " &fhas been changed to Neutral!");
			}
			this.sendGuildSound(Sound.ORB_PICKUP, 1);
		}
	}

	private void setName(String name) {
		Database.sendStatement("UPDATE GuildInfo SET guildName='" + name + "' WHERE id='" + this.getId() + "';");
		this.name = name;
	}
	
	private void setLevel(int level) {
		this.level = level;
		Database.sendStatement("UPDATE GuildInfo SET guildLevel=" + level + " WHERE id=" + this.getId() + ";");
	}
	
	private void setBalance(double amount) {
		this.balance = amount;
		Database.sendStatement("UPDATE GuildInfo SET bankSilver=" + amount + " WHERE id=" + this.getId() + ";");
	}
	
	private void setDust(double amount) {
		this.dust = amount;
		Database.sendStatement("UPDATE GuildInfo SET bankDwarfDust=" + amount + " WHERE id=" + this.getId() + ";");
	}
	
	private void setOpen(boolean open){
		this.open = open;
		Database.sendStatement("UPDATE GuildInfo SET open=" + open + " WHERE id=" + this.getId() + ";");
	}
	
	private void setRenamesRemaining(int remaining){
		this.renamesRemaining = remaining;
		Database.sendStatement("UPDATE GuildInfo SET renamesRemaining=" + remaining + " WHERE id=" + this.getId() + ";");
	}
	
	private void setMembers(HashMap<UUID, GuildPlayer> members){
		this.members = members;
	}
	
	public static boolean alreadyExists(String name) {
		return getByName(name) != null;
	}
	
	public static boolean isValidName(String name) {
		if(name.length() > 3 && name.length() < 17) {
			Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(name);
			return !m.find();
		}
		return false;
	}
	
	public static Guild loadNewGuild(String name, Player p) {
		int id = getNewID();
		Guild g = new Guild(name, id, 1, 0, 0, false, 3, false);
		g.addMember(p, Role.OWNER);
		loadedGuilds.add(g);
		Database.sendStatement("INSERT INTO GuildInfo (id, guildName, guildLevel, bankSilver, bankDwarfDust, open, renamesRemaining) VALUES (" + id + ", '" + name + "', 1, 0, 0, false, 3);");
		Database.sendStatement("UPDATE PlayerInfo SET guild=" + id + ", guildRank='OWNER' WHERE playerUuid='" + p.getUniqueId().toString() + "';");
		return g;
	}
	
	private static int getNewID() {
		try {
			ResultSet rs = Database.getResultSet("SELECT * FROM GuildInfo ORDER BY id ASC;");
			int x = 1;
			while(rs.next()) {
				if(rs.getInt("id") > x) {
					return x;
				}
				else {
					x = x + 1;
				}
			}
			return x;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static Guild getByPlayer(OfflinePlayer p) {
		for(Guild g : loadedGuilds) {
			if(g.getMemberUuids().containsKey(p.getUniqueId())) {
				return g;
			}
		}
		return loadGuild(p);
	}

	public static Guild getByName(String name) {
		for(Guild g : loadedGuilds) {
			if(g.getName().equalsIgnoreCase(name)) {
				return g;
			}
		}
		return loadGuild(name);
	}
	
	public static Guild getById(int id) {
		for(Guild g : loadedGuilds) {
			if(g.getId() == id) {
				return g;
			}
		}
		return loadGuild(id);
	}
	
	private static Guild loadGuild(OfflinePlayer p) {
		ResultSet rs = Database.getResultSet("SELECT guild FROM PlayerInfo WHERE playerUuid='" + p.getUniqueId().toString() + "';");
		try {
			if(rs.next()) {
				int id = rs.getInt("guild");
				if(id == 0) {
					return null;
				}
				ResultSet guildRs = Database.getResultSet("SELECT * FROM GuildInfo WHERE id=" + id + ";");
				if(guildRs.next()) {
					String name = guildRs.getString("guildName");
					int level = guildRs.getInt("guildLevel");
					int silver = guildRs.getInt("bankSilver");
					int dust = guildRs.getInt("bankDwarfDust");
					int renames = guildRs.getInt("renamesRemaining");
					HashMap<UUID, GuildPlayer> members = new HashMap<>();
					ResultSet membersRs = Database.getResultSet("SELECT * FROM PlayerInfo WHERE guild=" + id + ";");
					Guild guild = new Guild(name, id, level, silver, dust, guildRs.getBoolean("open"), renames, guildRs.getBoolean("islandCreated"));
					loadedGuilds.add(guild);
					while(membersRs.next()) {
						UUID u = UUID.fromString(membersRs.getString("playerUuid"));
						Role r = Role.valueOf(membersRs.getString("guildRank"));
						long timeStamp = membersRs.getLong("timeJoined");
						int pSilver = membersRs.getInt("silver");
						int pDust = membersRs.getInt("dwarfDust");
						members.put(u, new GuildPlayer(u, guild, r, timeStamp, pSilver, pDust));
					}
					guild.setMembers(members);
					guild.loadRelations();
					guild.loadTransactions();
					guild.loadUnclaimed();
					guild.loadWars();
					return guild;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static Guild loadGuild(String name) {
		if(name.equals("0")) {
			return null;
		}
		try {
			ResultSet guildRs = Database.getResultSet("SELECT * FROM GuildInfo WHERE guildName='" + name + "';");
			if(guildRs.next()) {
				int id = guildRs.getInt("id");
				int level = guildRs.getInt("guildLevel");
				double silver = guildRs.getDouble("bankSilver");
				double dust = guildRs.getDouble("bankDwarfDust");
				int renames = guildRs.getInt("renamesRemaining");
				HashMap<UUID, GuildPlayer> members = new HashMap<>();
				ResultSet membersRs = Database.getResultSet("SELECT * FROM PlayerInfo WHERE guild=" + id + ";");
				Guild guild = new Guild(name, id, level, silver, dust, guildRs.getBoolean("open"), renames, guildRs.getBoolean("islandCreated"));
				loadedGuilds.add(guild);
				while(membersRs.next()) {
					UUID u = UUID.fromString(membersRs.getString("playerUuid"));
					Role r = Role.valueOf(membersRs.getString("guildRank"));
					long timeStamp = membersRs.getLong("timeJoined");
					int pSilver = membersRs.getInt("silver");
					int pDust = membersRs.getInt("dwarfDust");
					members.put(u, new GuildPlayer(u, guild, r, timeStamp, pSilver, pDust));
				}
				guild.setMembers(members);
				guild.loadRelations();
				guild.loadTransactions();
				guild.loadUnclaimed();
				guild.loadWars();
				return guild;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static Guild loadGuild(int id) {
		try {
			ResultSet guildRs = Database.getResultSet("SELECT * FROM GuildInfo WHERE id=" + id + ";");
			if(guildRs.next()) {
				String name = guildRs.getString("guildName");
				int level = guildRs.getInt("guildLevel");
				double silver = guildRs.getDouble("bankSilver");
				double dust = guildRs.getDouble("bankDwarfDust");
				int renames = guildRs.getInt("renamesRemaining");
				HashMap<UUID, GuildPlayer> members = new HashMap<>();
				ResultSet membersRs = Database.getResultSet("SELECT * FROM PlayerInfo WHERE guild=" + id + ";");
				Guild guild = new Guild(name, id, level, silver, dust, guildRs.getBoolean("open"), renames, guildRs.getBoolean("islandCreated"));
				loadedGuilds.add(guild);
				while(membersRs.next()) {
					UUID u = UUID.fromString(membersRs.getString("playerUuid"));
					Role r = Role.valueOf(membersRs.getString("guildRank"));
					long timeStamp = membersRs.getLong("timeJoined");
					int pSilver = membersRs.getInt("silver");
					int pDust = membersRs.getInt("dwarfDust");
					members.put(u, new GuildPlayer(u, guild, r, timeStamp, pSilver, pDust));
				}
				guild.setMembers(members);
				guild.loadRelations();
				guild.loadTransactions();
				guild.loadUnclaimed();
				guild.loadWars();
				return guild;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void loadUnclaimed(){
		ResultSet rs = Database.getResultSet("SELECT * FROM GuildUnclaimed WHERE guildId=" + this.getId() + ";");
		try {
			while(rs.next()) {
				String uuid = rs.getString("playerUuid");
				ArrayList<ItemStack> items = new ArrayList<>();
				ResultSet rs1 = Database.getResultSet("SELECT * FROM GuildUnclaimed WHERE guildId=" + this.getId() + " AND playerUuid='" + uuid + "';");
				while(rs1.next()) {
					items.add(ItemStackSerializer.deserializeItemStack(rs1.getString("item")));
				}
				unclaimedItems.put(uuid, items);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void loadRelations(){
		ResultSet rs = Database.getResultSet("SELECT * FROM GuildRelations WHERE id=" + this.getId() + " OR relatedId=" + this.getId() + ";");
		try {
			while(rs.next()) {
				if(rs.getInt("id") == this.getId()) {
					relations.put(Guild.getById(rs.getInt("relatedId")), Relation.valueOf(rs.getString("relation")));
				}
				else {
					relations.put(Guild.getById(rs.getInt("id")), Relation.valueOf(rs.getString("relation")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void loadWars(){
		ResultSet rs = Database.getResultSet("SELECT * FROM GuildWars WHERE oneId=" + this.getId() + " OR anotherId=" + this.getId() + ";");
		try {
			endedWars.addAll(EndedWar.getByGuild(this));
			while(rs.next()) {
				if(!endedWars.contains(EndedWar.getById(rs.getInt("warId")))) {
					Guild oneGuild;
					Guild anotherGuild;
					Guild winner;
					if(this.getId() == rs.getInt("oneId")) {
						oneGuild = this;
						anotherGuild = Guild.getById(rs.getInt("anotherId"));
					}
					else {
						anotherGuild = this;
						oneGuild = Guild.getById(rs.getInt("oneId"));
					}
					if(rs.getInt("winnerId") != -1) {
						if(rs.getInt("oneId") == rs.getInt("winnerId")) {
							winner = oneGuild;
						}
						else {
							winner = anotherGuild;
						}
					}
					else {
						winner = null;
					}
					ArrayList<ItemStack> items = new ArrayList<>();
					ResultSet item = Database.getResultSet("SELECT * FROM WarItems WHERE warId=" + rs.getInt("warId") + " AND guildId=" +  rs.getInt("oneId") + ";");
					while(item.next()) {
						items.add(ItemStackSerializer.deserializeItemStack(item.getString("item")));
					}
					ArrayList<ItemStack> otherItems = new ArrayList<>();
					ResultSet otherItem = Database.getResultSet("SELECT * FROM WarItems WHERE warId=" + rs.getInt("warId") + " AND guildId=" + rs.getInt("anotherId") + ";");
					while(otherItem.next()) {
						otherItems.add(ItemStackSerializer.deserializeItemStack(otherItem.getString("item")));
					}
					HashMap<Guild, ArrayList<ItemStack>> loot = new HashMap<>();
					loot.put(oneGuild, items);
					loot.put(anotherGuild, otherItems);
					EndedWar e = new EndedWar(rs.getInt("warId"), oneGuild, anotherGuild, winner, rs.getInt("winnerId") == -1, WarType.valueOf(rs.getString("type")), rs.getLong("timeStarted"), rs.getLong("timeEnded"), rs.getInt("moneyCollected"), 
							rs.getString("topDamager"), loot);
					endedWars.add(e);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void loadTransactions(){
		ResultSet rs = Database.getResultSet("SELECT * FROM GuildTransactions WHERE id=" + this.getId() + " ORDER BY timestamp DESC;");
		try {
			while(rs.next()) {
				OfflinePlayer offP = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("playerUuid")));
				GuildPlayer p = Guild.getByPlayer(offP).getGuildPlayer(offP);
				BankTransaction b = new BankTransaction(p, rs.getInt("amount"), Currency.valueOf(rs.getString("type")), rs.getLong("timestamp"));
				transactions.add(b);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int hasGuild(Player p) {
		ResultSet rs = Database.getResultSet("SELECT * FROM PlayerInfo WHERE playerUuid='" + p.getUniqueId() + "';");
		try {
			if(rs.next()) {
				if(rs.getString("guild").equals("0")) {
					return 0;
				}
				else {
					return 1;
				}
			}
			else {
				return -1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static String balance(double silver) {
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		return "$" + format.format(silver);
	}
	
	public static String formattedBalance(double silver) {
		String string = "$";
		DecimalFormat d = new DecimalFormat("#.##");
		if(silver >= 1000000000) {
			string = string + (d.format(silver / 1000000000)) + "B";
		}
		else if(silver >= 1000000) {
			string = string + (d.format(silver / 1000000)) + "M";
		}
		else if(silver >= 1000) {
			string = string + (d.format(silver / 1000)) + "K";
		}
		else {
			string = string + silver;
		}
		return string;
	}

}
