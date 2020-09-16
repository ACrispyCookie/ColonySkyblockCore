package net.colonymc.colonyskyblockcore.guilds.auction.inventories;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.api.itemstacks.NBTItems;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.Relation;
import net.colonymc.colonyskyblockcore.guilds.auction.Auction;
import net.colonymc.colonyskyblockcore.guilds.auction.Bidder;
import net.colonymc.colonyskyblockcore.guilds.inventories.InventoryUtils;

public class AuctionMenuUtils {
	
	public static ItemStack getAuctionItem(Auction a, Player viewer, boolean lastLine) {
		Guild g = Guild.getByPlayer(viewer);
		String topBidder = "";
		String currentBid = (a.getCurrentBid() == 0) ? "&7None" : Guild.balance(a.getCurrentBid());
		String endsIn = InventoryUtils.getDurationString(a.getEndTimeStamp() - System.currentTimeMillis());
		if(a.getTopBidder() == null) {
			topBidder = "&7None";
		}
		else {
			topBidder = "&d" + a.getTopBidder().getPlayer().getGuild().getName();
		}
		if(a.getTopBidder() == null || !a.getTopBidder().getPlayer().getGuild().equals(g)) {
			if(!a.getSeller().equals(g.getGuildPlayer(viewer))) {
				if(!a.getSeller().getGuild().equals(g)) {
					int minimumBid = (a.getCurrentBid() == 0) ? a.getStartingBid() : a.getCurrentBid() + 100;
					if(minimumBid > g.getGuildPlayer(viewer).getBalance()) {
						String line = (lastLine) ? 
								"\n&fCurrent bid: &d" + currentBid +
								"\n&fTop bidder: " + topBidder +  
								"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
								"\n&fSeller: &d" + a.getSeller().getGuild().getName() +
								"\n \n&cYou cannot afford to bid on this auction!" 
								:
								"\n&fCurrent bid: &d" + currentBid +
								"\n&fTop bidder: " + topBidder + 
								"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
								"\n&fSeller: &d" + a.getSeller().getGuild().getName() + "\n ";
						ItemStack item = 
								new ItemStackBuilder(a.getItemSold().getItem().getType())
								.name("&d[" + endsIn + "] &f" + a.getItemSold().getName())
								.lore(a.getItemSold().getLore() + line)
								.glint(NBTItems.hasTag(a.getItemSold().getItem(), "ench"))
								.build();
						item.setAmount(a.getItemSold().getItem().getAmount());
						item.setDurability(a.getItemSold().getItem().getDurability());
						return item;
					}
					else {
						if(a.getSeller().getGuild().getRelation(g) == Relation.ENEMY) {
							String line = (lastLine) ? 
									"\n&fCurrent bid: &d" + currentBid +
									"\n&fTop bidder: " + topBidder + 
									"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) + 
									"\n&fSeller: &d" + a.getSeller().getGuild().getName() +
									"\n \n&cYou cannot bid on an enemy guild!" 
									:
									"\n&fCurrent bid: &d" + currentBid +
									"\n&fTop bidder: " + topBidder +  
									"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
									"\n&fSeller: &d" + a.getSeller().getGuild().getName() + "\n ";
							ItemStack item =
									new ItemStackBuilder(a.getItemSold().getItem().getType())
									.name("&d[" + endsIn + "] &f" + a.getItemSold().getName())
									.lore(a.getItemSold().getLore() + line)
									.glint(NBTItems.hasTag(a.getItemSold().getItem(), "ench"))
									.build();
							item.setAmount(a.getItemSold().getItem().getAmount());
							item.setDurability(a.getItemSold().getItem().getDurability());
							return item;
						}
						else {
							String line = (lastLine) ? 
									"\n&fCurrent bid: &d" + currentBid +
									"\n&fTop bidder: " + topBidder + 
									"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
									"\n&fSeller: &d" + a.getSeller().getGuild().getName() +
									"\n \n&dClick to bid on this auction!" 
									: 
									"\n&fCurrent bid: &d" + currentBid +
									"\n&fTop bidder: " + topBidder + 
									"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
									"\n&fSeller: &d" + a.getSeller().getGuild().getName() + "\n ";
							ItemStack item = 
									new ItemStackBuilder(a.getItemSold().getItem().getType())
									.name("&d[" + endsIn + "] &f" + a.getItemSold().getName())
									.lore(a.getItemSold().getLore() + line)
									.glint(NBTItems.hasTag(a.getItemSold().getItem(), "ench"))
									.build();
							item.setAmount(a.getItemSold().getItem().getAmount());
							item.setDurability(a.getItemSold().getItem().getDurability());
							return item;
						}
					}
				}
				else {
					String line = (lastLine) ? 
							"\n&fCurrent bid: &d" + currentBid +
							"\n&fTop bidder: " + topBidder + 
							"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
							"\n&fSeller: &d" + a.getSeller().getGuild().getName() +
							"\n \n&dThis auction was create by a member of your guild!"
							: 
							"\n&fCurrent bid: &d" + currentBid +
							"\n&fTop bidder: " + topBidder + 
							"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
							"\n&fSeller: &d" + a.getSeller().getGuild().getName() + "\n ";
					ItemStack item = 
							new ItemStackBuilder(a.getItemSold().getItem().getType())
							.name("&d[" + endsIn + "] &f" + a.getItemSold().getName())
							.lore(a.getItemSold().getLore() + line)
							.glint(NBTItems.hasTag(a.getItemSold().getItem(), "ench"))
							.build();
					item.setAmount(a.getItemSold().getItem().getAmount());
					item.setDurability(a.getItemSold().getItem().getDurability());
					return item;
				}
			}
			else {
				String line = (lastLine) ? 
						"\n&fCurrent bid: &d" + currentBid +
						"\n&fTop bidder: " + topBidder + 
						"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
						"\n&fSeller: &d" + a.getSeller().getGuild().getName() +
						"\n \n&dClick to inspect this auction!"
						: 
						"\n&fCurrent bid: &d" + currentBid +
						"\n&fTop bidder: " + topBidder + 
						"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
						"\n&fSeller: &d" + a.getSeller().getGuild().getName() + "\n ";
				ItemStack item = 
						new ItemStackBuilder(a.getItemSold().getItem().getType())
						.name("&d[" + endsIn + "] &f" + a.getItemSold().getName())
						.lore(a.getItemSold().getLore() + line)
						.glint(NBTItems.hasTag(a.getItemSold().getItem(), "ench"))
						.build();
				item.setAmount(a.getItemSold().getItem().getAmount());
				item.setDurability(a.getItemSold().getItem().getDurability());
				return item;
			}
		}
		else {
			String line = (lastLine) ? 
					"\n&fCurrent bid: &d" + currentBid +
					"\n&fTop bidder: " + topBidder + 
					"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
					"\n&fSeller: &d" + a.getSeller().getGuild().getName() +
					"\n \n&dYour guild is already the top bidder!"
					: 
					"\n&fCurrent bid: &d" + currentBid +
					"\n&fTop bidder: " + topBidder + 
					"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
					"\n&fSeller: &d" + a.getSeller().getGuild().getName() + "\n ";
			ItemStack item = 
					new ItemStackBuilder(a.getItemSold().getItem().getType())
					.name("&d[" + endsIn + "] &f" + a.getItemSold().getName())
					.lore(a.getItemSold().getLore() + line)
					.glint(NBTItems.hasTag(a.getItemSold().getItem(), "ench"))
					.build();
			item.setAmount(a.getItemSold().getItem().getAmount());
			item.setDurability(a.getItemSold().getItem().getDurability());
			return item;
		}
	}
	
	public static ItemStack getAuctionItemFromInspect(Auction a, boolean lastLine) {
		String topBidder = "";
		String currentBid = (a.getCurrentBid() == 0) ? "&7None" : String.valueOf(a.getCurrentBid()) + "g";
		if(a.getTopBidder() == null) {
			topBidder = "&7None";
		}
		else {
			topBidder = "&d" + a.getTopBidder().getPlayer().getGuild().getName();
		}
		String line = (lastLine) ? 
				"\n&fCurrent bid: &d" + currentBid +
				"\n&fTop bidder: " + topBidder +  
				"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
				"\n \n&dClick to inspect this auction!"
				:

				"\n&fCurrent bid: &d" + currentBid +
				"\n&fTop bidder: " + topBidder +
				"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) + "\n ";
		if(a.hasEnded()) {
			ItemStack item = 
					new ItemStackBuilder(a.getItemSold().getItem().getType())
					.name("&d[ENDED!] &f" + a.getItemSold().getName())
					.lore(a.getItemSold().getLore() + line)
					.glint(NBTItems.hasTag(a.getItemSold().getItem(), "ench"))
					.build();
			item.setAmount(a.getItemSold().getItem().getAmount());
			item.setDurability(a.getItemSold().getItem().getDurability());
			return item;
		}
		else {
			ItemStack item =
					new ItemStackBuilder(a.getItemSold().getItem().getType())
					.name("&d[" + InventoryUtils.getDurationString(a.getEndTimeStamp() - System.currentTimeMillis()) + "] &f" + a.getItemSold().getName())
					.lore(a.getItemSold().getLore() + line)
					.glint(NBTItems.hasTag(a.getItemSold().getItem(), "ench"))
					.build();
			item.setAmount(a.getItemSold().getItem().getAmount());
			item.setDurability(a.getItemSold().getItem().getDurability());
			return item;
		}
	}
	
	public static ItemStack getBoughtItemFromInspect(Auction a, Player viewer, boolean lastLine) {
		String topBidder = "";
		String currentBid = (a.getCurrentBid() == 0) ? "&7None" : String.valueOf(a.getCurrentBid()) + "g";
		if(a.getTopBidder() == null) {
			topBidder = "&7None";
		}
		else {
			topBidder = "&d" + a.getTopBidder().getPlayer().getGuild().getName();
		}
		String line = "";
		if(a.getTopBidder().getPlayer().getGuild().equals(Guild.getByPlayer(viewer))) {
			line = (lastLine) ? 
					"\n&fCurrent bid: &d" + currentBid +
					"\n&fTop bidder: " + topBidder +  
					"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
					"\n \n&d&kO&d You currently have the top bid! &kO&r" +
					"\n \n&dClick to inspect this auction!"
					:
					"\n&fCurrent bid: &d" + currentBid +
					"\n&fTop bidder: " + topBidder + 
					"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
					"\n \n&d&kO&d You currently have the top bid! &kO&r";
		}
		else {
			line = (lastLine) ? 
					"\n&fCurrent bid: &d" + currentBid +
					"\n&fTop bidder: " + topBidder + 
					"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
					"\n \n&c&kO&c You have lost the top bid! &kO&r" +
					"\n \n&dClick to inspect this auction!"
					:
					"\n&fCurrent bid: &d" + currentBid +
					"\n&fTop bidder: " + topBidder + 
					"\n&fStarting bid: &d" + Guild.balance(a.getStartingBid()) +
					"\n \n&c&kO&c You have lost the top bid! &kO&r";
		}
		if(a.hasEnded()) {
			ItemStack item = 
					new ItemStackBuilder(a.getItemSold().getItem().getType())
					.name("&d[ENDED!] &f" + a.getItemSold().getName())
					.lore(a.getItemSold().getLore() + line)
					.glint(NBTItems.hasTag(a.getItemSold().getItem(), "ench"))
					.build();
			item.setAmount(a.getItemSold().getItem().getAmount());
			item.setDurability(a.getItemSold().getItem().getDurability());
			return item;
		}
		else {
			ItemStack item =
					new ItemStackBuilder(a.getItemSold().getItem().getType())
					.name("&d[" + InventoryUtils.getDurationString(a.getEndTimeStamp() - System.currentTimeMillis()) + "] &f" + a.getItemSold().getName())
					.lore(a.getItemSold().getLore() + line)
					.glint(NBTItems.hasTag(a.getItemSold().getItem(), "ench"))
					.build();
			item.setAmount(a.getItemSold().getItem().getAmount());
			item.setDurability(a.getItemSold().getItem().getDurability());
			return item;
		}
	}
	
	public static ItemStack getSpentSilver(Auction a, int silverSpent, Player viewer) {
		ItemStack item = 
				new ItemStackBuilder(Material.IRON_INGOT)
				.name("&dClaim spent coins")
				.lore("\n&fClick here to claim your\n&fyour money from previous bids which\n&fwere outbidden! Money spent: &d" + Guild.balance(silverSpent))
				.glint(true)
				.build();
		return item;
	}
	
	public static ItemStack getBidHistory(Auction a) {
		ItemStack item = new ItemStackBuilder(Material.BOOK_AND_QUILL)
				.name("&dBid history")
				.lore("\n&fClick here to view the\n&fhistory of all the biddings\n&fof this auction!")
				.glint(true)
				.build();
		return item;
	}
	
	public static ItemStack getBidHistoryItem(Bidder b) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		ItemStack item = new ItemStackBuilder(Material.PAPER)
				.name("&d" + b.getPlayer().getGuild().getName())
				.lore("\n  &5» &fBid amount: &d" + Guild.balance(b.getAmount()) + "\n  &5» &fBid timestamp: &d" + sdf.format(new Date(b.getTimeBidded())) + "\n ")
				.glint(true)
				.build();
		return item;
	}
	
	public static void fillGlasses(InventoryHolder h) {
		for(int i = 0; i < h.getInventory().getSize(); i++) {
			h.getInventory().setItem(i, new ItemStackBuilder(Material.STAINED_GLASS_PANE).name(" ").durability((short) 2).build());
		}
	}

}
