package net.colonymc.colonyskyblockcore.guilds.auction.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.colonyspigotapi.api.itemstack.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;

public class AuctionHouseSelectMenu implements InventoryHolder, Listener{

	Inventory inv;
	Player p;
	
	public AuctionHouseSelectMenu(Player p) {
		if(Guild.getByPlayer(p) != null) {
			this.p = p;
			this.inv = Bukkit.createInventory(this, 27, "Auction Master");
			fillInventory();
			new BukkitRunnable() {
				@Override
				public void run() {
					p.openInventory(inv);
				}
			}.runTaskLater(Main.getInstance(), 1L);
		}
		else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the auction house!"));
			p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
		}
	}
	
	public AuctionHouseSelectMenu() {
	}
	
	public void fillInventory() {
		inv.setItem(11, new ItemStackBuilder(Material.PAPER).name("&dCheck pending auctions").glint(true).build());
		inv.setItem(13, new ItemStackBuilder(Material.GOLD_BLOCK).name("&dBrowse the auction house").glint(true).build());
		inv.setItem(15, new ItemStackBuilder(Material.REDSTONE_TORCH_ON).name("&dCreate new auction").glint(true).build());
	}

	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof AuctionHouseSelectMenu) {
			e.setCancelled(true);
			AuctionHouseSelectMenu auc = (AuctionHouseSelectMenu) e.getInventory().getHolder();
			Player p = auc.p;
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 13) {
					new AuctionHouseMenu(p);
				}
				else if(e.getSlot() == 15) {
					new AuctionCreateMenu(p);
				}
				else if(e.getSlot() == 11) {
					new AuctionManagePendingMenu(p);
				}
			}
		}
	}

}
