package net.colonymc.colonyskyblockcore.guilds.auction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyspigotapi.api.itemstack.ItemStackSerializer;
import net.colonymc.colonyspigotapi.api.itemstack.SkullItemBuilder;
import net.colonymc.colonyspigotapi.api.player.PlayerInventory;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.GuildPlayer;
import net.colonymc.colonyskyblockcore.npcs.NPCListener;

public class Auction {
	
	final GuildPlayer seller;
	final AuctionItem itemSold;
	Bidder topBidder;
	final int startingPrice;
	int currentPrice;
	final int id;
	long endsIn;
	boolean hasEnded = false;
	boolean sellerHasClaimed;
	BukkitRunnable expire;
	ArrayList<Bidder> bidders = new ArrayList<>();
	public static final ArrayList<Auction> activeAuctions = new ArrayList<>();
	
	public Auction(GuildPlayer seller, AuctionItem item, long duration, int startingPrice, boolean hasSellerClaimed, ArrayList<Bidder> bidders) {
		this.seller = seller;
		this.endsIn = System.currentTimeMillis() + duration;
		this.startingPrice = startingPrice;
		this.currentPrice = 0;
		this.id = getNewID();
		this.itemSold = item;
		this.topBidder = null;
		this.sellerHasClaimed = hasSellerClaimed;
		if(bidders != null) {
			this.bidders = bidders;
			for(Bidder p : bidders) {
				if(this.topBidder == null || p.getAmount() > topBidder.getAmount()) {
					this.topBidder = p;
				}
			}
		}
		expire = new BukkitRunnable() {
			@Override
			public void run() {
				if(System.currentTimeMillis() >= endsIn) {
					endAuction();
					cancel();
				}
			}
		};
		expire.runTaskTimerAsynchronously(Main.getInstance(), 0, 3L);
	}
	
	public Auction(int id, OfflinePlayer seller, AuctionItem item, long duration, int startingPrice, int currentPrice, boolean hasSellerClaimed, ArrayList<Bidder> bidders) {
		this.seller = Guild.getByPlayer(seller).getGuildPlayer(seller);
		this.endsIn = System.currentTimeMillis() + duration;
		this.startingPrice = startingPrice;
		this.currentPrice = currentPrice;
		this.id = id;
		this.itemSold = item;
		this.topBidder = null;
		this.sellerHasClaimed = hasSellerClaimed;
		if(bidders != null) {
			this.bidders = bidders;
			for(Bidder p : bidders) {
				if(this.topBidder == null || p.getAmount() > topBidder.getAmount()) {
					this.topBidder = p;
				}
			}
		}
		activeAuctions.add(this);
		if(duration > 0) {
			expire = new BukkitRunnable() {
				@Override
				public void run() {
					if(System.currentTimeMillis() >= endsIn) {
						endAuction();
						cancel();
					}
				}
			};
			expire.runTaskTimerAsynchronously(Main.getInstance(), 0, 3L);
		}
		else {
			hasEnded = true;
		}
	}
	
	public void startAuction() {
		activeAuctions.add(this);
		seller.getGuild().sendGuildMessage("&d&l&k:&d&lAUCTION STARTED&k:&r &fThe guild member &d" + seller.getPlayer().getName() + " &fhas just started an auction!"
					+ " &d" + itemSold.getItem().getAmount() + "x " + itemSold.getName() + " &ffor &d" + Guild.balance(startingPrice));
		NPCListener.sendRotatingHead(NPCListener.auctionMaster, ((Player) seller.getPlayer()), itemSold.getItem(), "&fYou just started an auction for &d" + Guild.balance(startingPrice), false, 0);
		Database.sendStatement("INSERT INTO ActiveAuctions (id, seller, topBidder, startingPrice, currentPrice, timeEnds, item, sellerClaimed) VALUES "
				+ "(" + id + ", '" + seller.getPlayer().getUniqueId().toString() + "', 'NONE', " + startingPrice + ", " + currentPrice + ", " + endsIn + ", '" + ItemStackSerializer.serializeItemStack(itemSold.getItem()) + "', 0)");
	}
	
	public void endAuction() {
		hasEnded = true;
		if(topBidder != null) {
			seller.getGuild().sendGuildMessage("&d&l&k:&d&lAUCTION ENDED&k:&r &fThe auction of the guild member &d" + seller.getPlayer().getName() + 
					" &fjust ended with the price of &d" + Guild.balance(currentPrice) + " &ffor the item &d[" + itemSold.getItem().getAmount() + "x " + itemSold.getName() + "&d]&f!");
			seller.getGuild().sendGuildSound(Sound.LEVEL_UP, 1);
			topBidder.getPlayer().getGuild().sendGuildMessage("&fThe guild member &d" + topBidder.getPlayer().getPlayer().getName() + " &fjust won the auction of &d" + seller.getGuild().getName() 
					+ " &ffor &d" + Guild.balance(currentPrice) + " &fand for the item &d[" + itemSold.getItem().getAmount() + "x " + itemSold.getName() + "&d]&f!");
			if(topBidder.getPlayer().getPlayer().isOnline()) {
				((Player) topBidder.getPlayer().getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fVisit the auction master on the spawn island to claim your items!"));
			}
		}
		else {
			seller.getGuild().sendGuildMessage("&c&l&k:&c&lAUCTION ENDED&k:&r &fThe auction of the guild member &d" + seller.getPlayer().getName() + 
					" &ffor the item &d[" + itemSold.getItem().getAmount() + "x " + itemSold.getName() + "&d] &fjust ended without any bidder!");
			seller.getGuild().sendGuildSound(Sound.VILLAGER_NO, 1);
		}
	}
	
	public void newBid(Bidder p, int amount) {
		Database.sendStatement("INSERT INTO AuctionBidders (id, bidder, amount, timeStamp, claimed) VALUES "
				+ "(" + id + ", '" + p.getPlayer().getPlayer().getUniqueId().toString() + "', " + p.getAmount() + ", " + p.getTimeBidded() + ", 0)");
		Database.sendStatement("UPDATE ActiveAuctions SET currentPrice=" + amount + " WHERE id=" + id);
		Database.sendStatement("UPDATE ActiveAuctions SET topBidder='" + p.getPlayer().getPlayer().getUniqueId().toString() + "' WHERE id=" + id);
		if(topBidder != null) {
			topBidder.getPlayer().getGuild().sendGuildMessage("&fYour guild has been outbidded by &d" + p.getPlayer().getGuild().getName() 
					+ " &fon the auction of &d" + this.seller.getGuild().getName() + " &ffor the new price of &d" + Guild.balance(amount) 
					+ " &fon the item &d[" + itemSold.getItem().getAmount() + "x " + itemSold.getName() + "&d]&f!");
		}
		if(endsIn - System.currentTimeMillis() <= 60000) {
			endsIn = endsIn + 90000;
		}
		topBidder = p;
		topBidder.getPlayer().removeBalance(amount);
		this.seller.getGuild().sendGuildMessage("&d&l&k:&d&lNEW BID&k:&r &fYou have received a new bid of &d" + Guild.balance(amount) + " &fon the item &d[" 
				+ itemSold.getItem().getAmount() + "x " + itemSold.getName() + "&d] &fwhich was put to the auction house by &d" + p.getPlayer().getGuild().getName());
		this.bidders.add(p);
		this.currentPrice = amount;
		p.getPlayer().getGuild().sendGuildMessage("&fThe guild member &d" + p.getPlayer().getPlayer().getName() + " &fjust bid on &d" 
		+ this.getSeller().getGuild().getName() + "'s &fauction for &d" + Guild.balance(currentPrice) + " &fon the item "
				+ "&d[" + itemSold.getItem().getAmount() + "x " + itemSold.getName() + "&d]&f!");
		NPCListener.sendRotatingHead(NPCListener.auctionMaster, ((Player) p.getPlayer().getPlayer()), new SkullItemBuilder().url("http://textures.minecraft.net/texture/e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852").build()
				, "&fYou just bidded &d" + Guild.balance(amount) + " &fon this auction!", false, 4);
	}
	
	public void claimSeller() {
		sellerHasClaimed = true;
		Database.sendStatement("UPDATE ActiveAuctions SET sellerClaimed=1;"); 
		shouldDelete();
		if(topBidder != null) {
			seller.addBalance(currentPrice);
			NPCListener.sendRotatingHead(NPCListener.auctionMaster, ((Player) seller.getPlayer()), new SkullItemBuilder().url("http://textures.minecraft.net/texture/e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852").build(),
					"&fYou just claimed &d" + Guild.balance(currentPrice) + " &ffrom this auction!", true, 4);
			((Player) seller.getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have claimed &d" + Guild.balance(currentPrice) 
			+ " &ffrom this auction and from the guild &d" + topBidder.getPlayer().getGuild().getName() + "&f!"));
		}
		else {
			NPCListener.sendRotatingHead(NPCListener.auctionMaster, ((Player) seller.getPlayer()), itemSold.getItem(), "&fYou just claimed your item back from this auction!", true, 0);
			PlayerInventory.addItem(itemSold.getItem(), (Player) seller.getPlayer(), itemSold.getItem().getAmount());
			((Player) seller.getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have claimed your item back from this auction!"));
		}
	}
	
	public void claimBuyer() {
		topBidder.hasClaimed = true;
		Database.sendStatement("UPDATE AuctionBidders SET claimed=1 WHERE id=" + id + " AND bidder='" + topBidder.getPlayer().getPlayer().getUniqueId().toString() + "';"); 
		shouldDelete();
		NPCListener.sendRotatingHead(NPCListener.auctionMaster, ((Player) topBidder.getPlayer().getPlayer()), itemSold.getItem(), "&fYou just claimed your item from this auction!", true, 0);
		PlayerInventory.addItem(itemSold.getItem(), (Player) topBidder.gp.getPlayer(), itemSold.getItem().getAmount());
		((Player) topBidder.getPlayer().getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have claimed the item "
				+ "&d[" + itemSold.getItem().getAmount() + "x " + itemSold.getName() + "&d] &ffrom this auction!"));
	}
	
	public void claimSilverLeft(Player p, int amount) {
		Bidder b = null;
		for(Bidder b1 : bidders) {
			if(b1.getPlayer().getPlayer().equals(p)) {
				b = b1;
				break;
			}
		}
		if(bidders.contains(b)) {
			Database.sendStatement("UPDATE AuctionBidders SET claimed=1 WHERE id=" + id + " AND bidder='" + p.getUniqueId().toString() + "';");
			bidders.get(bidders.indexOf(b)).hasClaimed = true;
			bidders.get(bidders.indexOf(b)).gp.addBalance(amount);
			shouldDelete();
			NPCListener.sendRotatingHead(NPCListener.auctionMaster, ((Player) topBidder.getPlayer()), itemSold.getItem(), "&fYou just claimed &d" + Guild.balance(amount) + " &fback!", true, 0);
			((Player) b.getPlayer().getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have claimed &d" + Guild.balance(amount) 
					+ " &fwhich you spent on this auction!"));
		}
	}
	
	public void removeBidder(Bidder b) {
		bidders.remove(b);
		Database.sendStatement("DELETE FROM AuctionBidders WHERE bidder='" + b.getPlayer().getPlayer().getUniqueId().toString() + "' AND id=" + id + ";");
		if(!bidders.isEmpty()) {
			topBidder = bidders.get(bidders.size() - 1);
			currentPrice = topBidder.getAmount();
			if(seller.getPlayer().isOnline()) {
				seller.getGuild().sendGuildMessage("&fThe guild &d[" + b.getPlayer().getGuild().getName() + "] &fwas disbanded so the top bid on your auction for a &d["
						+ itemSold.getItem().getAmount() + "x " + itemSold.getName() + "&d] &fwas changed to &d" + Guild.balance(currentPrice) + " &ffrom the guild &d[" + topBidder.getPlayer().getGuild().getName() + "]!");
			}
			Database.sendStatement("UPDATE ActiveAuctions SET topBidder='" + topBidder.getPlayer().getPlayer().getUniqueId().toString() + "' WHERE id=" + id + ";");
			Database.sendStatement("UPDATE ActiveAuctions SET currentPrice=" + currentPrice + " WHERE id=" + id + ";");
		}
		else {
			topBidder = null;
			currentPrice = 0;
			if(seller.getPlayer().isOnline()) {
				seller.getGuild().sendGuildMessage("&fThe guild &d[" + b.getPlayer().getGuild().getName() + "] &fwas disbanded and there are no bids left on your auction for a &d["
						+ itemSold.getItem().getAmount() + "x " + itemSold.getName() + "&d]!");
			}
			Database.sendStatement("UPDATE ActiveAuctions SET topBidder=null WHERE id=" + id + ";");
			Database.sendStatement("UPDATE ActiveAuctions SET currentPrice=" + currentPrice + " WHERE id=" + id + ";");
		}
	}
	
	public void shouldDelete() {
		boolean shouldDelete = false;
		if(topBidder != null) {
			if(sellerHasClaimed && topBidder.hasClaimed) {
				shouldDelete = true;
			}
			for(Bidder b1 : bidders) {
				if (!b1.hasClaimed) {
					shouldDelete = false;
					break;
				}
			}
		}
		else if(sellerHasClaimed) {
			shouldDelete = true;
		}
		if(shouldDelete) {
			activeAuctions.remove(this);
			Database.sendStatement("DELETE FROM AuctionBidders WHERE id=" + id + ";");
			Database.sendStatement("DELETE FROM ActiveAuctions WHERE id=" + id + ";");
		}
	}
	
	private int getNewID() {
		try {
			ResultSet rs = Database.getResultSet("SELECT * FROM ActiveAuctions ORDER BY id ASC;");
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
	
	public GuildPlayer getSeller() {
		return seller;
	}
	
	public AuctionItem getItemSold() {
		return itemSold;
	}
	
	public Bidder getTopBidder() {
		return topBidder;
	}
	
	public int getStartingBid() {
		return startingPrice;
	}
	
	public int getCurrentBid() {
		return currentPrice;
	}
	
	public long getEndTimeStamp() {
		return endsIn;
	}
	
	public boolean hasEnded() {
		return hasEnded;
	}
	
	public boolean hasSellerClaimed() {
		return !sellerHasClaimed;
	}
	
	public ArrayList<Bidder> getBidders(){
		return bidders;
	}
	
	public static Auction getAuctionById(int id) {
		for(Auction a : activeAuctions) {
			if(a.id == id) {
				return a;
			}
		}
		return null;
	}

}
