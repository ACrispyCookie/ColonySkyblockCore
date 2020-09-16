package net.colonymc.colonyskyblockcore.guilds.test;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionCreateMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionHouseMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionManagePendingMenu;

public class AuctionHouseSelectMenu extends AuctionInventory {

	public AuctionHouseSelectMenu(Player p) {
		super(p, 27, "Auction Master");
	}

	@Override
	public void changePage(int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addButtons() {
		addButton(new AuctionButton(11, new ItemStackBuilder(Material.PAPER).name("&dCheck pending auctions").glint(true).build()) {
			@Override
			public void action(ItemStack i) {
				new AuctionManagePendingMenu(viewer);
			}
		});
		addButton(new AuctionButton(13, new ItemStackBuilder(Material.GOLD_BLOCK).name("&dBrowse the auction house").glint(true).build()) {
			@Override
			public void action(ItemStack i) {
				new AuctionHouseMenu(viewer);
			}
		});
		addButton(new AuctionButton(15, new ItemStackBuilder(Material.REDSTONE_TORCH_ON).name("&dCreate new auction").glint(true).build()) {
			@Override
			public void action(ItemStack i) {
				new AuctionCreateMenu(viewer);
			}
		});
	}

}
