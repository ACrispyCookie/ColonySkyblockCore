package net.colonymc.colonyskyblockcore.guilds.bank;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionMenuUtils;

public class TransactionHistoryMenu implements Listener, InventoryHolder {

	Inventory inv;
	Player p;
	Guild g;
	BukkitTask task;
	int page = 0;
	int totalPages;
	
	public TransactionHistoryMenu(Player p) {
		this.p = p;
		this.g = Guild.getByPlayer(p);
		this.inv = Bukkit.createInventory(this, 54, "Transaction history");
		fillInventory();
		updateInventory();
		new BukkitRunnable() {
			@Override
			public void run() {
				Player pl =  (Player) p.getPlayer().getPlayer();
				pl.openInventory(inv);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}
	
	public TransactionHistoryMenu() {
		
	}

	private void fillInventory() {
		AuctionMenuUtils.fillGlasses(this);
		inv.setItem(49, new ItemStackBuilder(Material.ARROW).name("&dGo back").build());
	}

	private void updateInventory() {
		task = new BukkitRunnable() {
			@Override
			public void run() {
				totalPages = (int) Math.ceil((double) g.getTransactions().size()/45);
				for(int i = 0; i < 45; i++) {
					int index = i + (page * 45);
					if(index <= g.getTransactions().size() - 1) {
						BankTransaction b = g.getTransactions().get(index);
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						String timestamp = sdf.format(new Date(b.getTimestamp()));
						String action = "Deposit";
						int amount = b.getAmount();
						if(amount < 0) {
							action = "Withdraw";
							amount = -amount;
						}
						ItemStack item = new ItemStackBuilder(Material.PAPER)
								.name("&f" + action + " by &d" + b.getPlayer().getPlayer().getName())
								.lore("\n&fTransaction amount: &d" + Guild.balance(amount) + "\n&fTimestamp: &d" + timestamp)
								.build();
						inv.setItem(i, item);
					}
					else {
						inv.setItem(i, new ItemStack(Material.AIR));
					}
				}
				if(page > 0) {
					inv.setItem(45, new ItemStackBuilder(Material.ARROW).name("&dPrevious Page").build());
				}
				else {
					inv.setItem(45, new ItemStackBuilder(Material.STAINED_GLASS_PANE).durability((short) 2).name(" ").build());
				}
				if(totalPages > 1 && page < totalPages - 1) {
					inv.setItem(53, new ItemStackBuilder(Material.ARROW).name("&dNext Page").build());
				}
				else {
					inv.setItem(53, new ItemStackBuilder(Material.STAINED_GLASS_PANE).durability((short) 2).name(" ").build());
				}
				p.updateInventory();
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}
	
	public void changePage(int amount) {
		page = page + amount;
	}
	
	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof TransactionHistoryMenu) {
			e.setCancelled(true);
			TransactionHistoryMenu tr = (TransactionHistoryMenu) e.getInventory().getHolder();
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(e.getSlot() == 49) {
					new BankInventory(tr.p);
				}
				else if(e.getSlot() == 53) {
					if(tr.page < tr.totalPages - 1) {
						tr.changePage(1);
					}
				}
				else if(e.getSlot() == 45) {
					if(tr.page > 0) {
						tr.changePage(-1);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof TransactionHistoryMenu) {
			TransactionHistoryMenu tr = (TransactionHistoryMenu) e.getInventory().getHolder();
			tr.task.cancel();
		}
	}

}
