package net.colonymc.colonyskyblockcore.guilds.bank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyspigotapi.itemstacks.ItemStackBuilder;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionMenuUtils;

public class BankInventory implements Listener, InventoryHolder {
	
	Player p;
	Inventory inv;
	BukkitTask update;
	
	public BankInventory(Player p) {
		if(Guild.getByPlayer(p) != null) {
			this.p = p;
			this.inv = Bukkit.createInventory(this, 27, "Bank");
			fillInventory();
			if(p.hasPermission("*")) {
				updateInventory();
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					Player pl = p.getPlayer().getPlayer();
					pl.openInventory(inv);
					p.updateInventory();
				}
			}.runTaskLater(Main.getInstance(), 1L);
		}
		else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the bank!"));
			p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
		}
	}
	
	private void closeInventory() {
		update.cancel();
		p.closeInventory();
	}
	
	private void openInventory() {
		p.openInventory(inv);
		updateInventory();
	}
	
	private void updateInventory() {
		update = new BukkitRunnable() {
			boolean silver = true;
			@Override
			public void run() {
				ItemStack i13 = inv.getItem(13);
				ItemStack i15 = inv.getItem(15);
				if(silver) {
					i13.setType(Material.IRON_INGOT);
					i15.setType(Material.IRON_INGOT);
				}
				else{
					i13.setType(Material.GLOWSTONE_DUST);
					i15.setType(Material.GLOWSTONE_DUST);
				}
				inv.setItem(13, i13);
				inv.setItem(15, i15);
				silver = !silver;
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 30);
	}
	
	private void fillInventory() {
		AuctionMenuUtils.fillGlasses(this);
		if(p.hasPermission("*")) {
			inv.setItem(11, new ItemStackBuilder(Material.PAPER)
					.name("&dTransaction history")
					.lore("\n&fClick here to see all the latest\n&ftransaction history of your guild!")
					.build());
			inv.setItem(13, new ItemStackBuilder(Material.IRON_INGOT)
					.name("&dWithdraw money/dwarf dust")
					.lore("\n&fWithdraw money/dwarf dust to your personal wallet.\n&fActions like this get saved on\n&fthe transaction history of the guild!\n \n"
							+ "&fAvailable guild money: &d" + Guild.balance(Guild.getByPlayer(p).getBalance()) + "\n&fAvailable guild dwarf dust: &d" + Guild.balance(Guild.getByPlayer(p).getDust()) + "\n \n"
							+ "&dLeft-Click to withdraw money\n&dRight-Click to withdraw dust")
					.build());
			inv.setItem(15, new ItemStackBuilder(Material.IRON_INGOT)
					.name("&dDesposit money/dwarf dust")
					.lore("\n&fDeposit money/dwarf dust from your personal wallet.\n&fActions like this get saved on\n&fthe transaction history of the guild!\n \n"
							+ "&fAvailable personal money: &d" + Guild.balance(Guild.getByPlayer(p).getGuildPlayer(p).getBalance()) + "\n&fAvailable personal dwarf dust: &d" + Guild.balance(Guild.getByPlayer(p).getGuildPlayer(p).getDust()) + "\n \n"
							+ "&dLeft-Click to deposit money\n&dRight-Click to deposit dust")
					.build());
		}
		else {
			inv.setItem(13, new ItemStackBuilder(Material.PAPER)
					.name("&dTransaction history")
					.lore("\n&fClick here to see all the latest\n&ftransaction history of your guild!")
					.build());
			inv.setItem(11, new ItemStackBuilder(Material.IRON_INGOT)
					.name("&dWithdraw money")
					.lore("\n&fWithdraw money to your personal wallet.\n&fActions like this get saved on\n&fthe transaction history of the guild!\n \n"
							+ "&fAvailable guild money: &d" + Guild.balance(Guild.getByPlayer(p).getBalance()) + "\n \n"
							+ "&dClick to withdraw money")
					.build());
			inv.setItem(15, new ItemStackBuilder(Material.IRON_INGOT)
					.name("&dDesposit money")
					.lore("\n&fDeposit money from your personal wallet.\n&fActions like this get saved on\n&fthe transaction history of the guild!\n \n"
							+ "&fAvailable personal money: &d" + Guild.balance(Guild.getByPlayer(p).getGuildPlayer(p).getBalance()) + "\n \n"
							+ "&dClick to deposit money")
					.build());
		}
	}

	public BankInventory() {
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof BankInventory) {
			e.setCancelled(true);
			BankInventory bank = (BankInventory) e.getInventory().getHolder();
			Player p = bank.p;
			Guild g = Guild.getByPlayer(p);
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
				if(p.hasPermission("*")) {
					if(e.getSlot() == 11) {
						new TransactionHistoryMenu(p);
					}
					else if(e.getSlot() == 13) {
						if(e.getClick() == ClickType.LEFT) {
							if(g.getBalance() > 0) {
								bank.closeInventory();
								Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^", "Enter an amount", "to withdraw"}, (player, lines) -> {
									if(Guild.getByPlayer(p) != null) {
										String msg = lines[0].replaceAll("\"", "");
										if(isInt(msg)) {
											int amount = Integer.parseInt(msg);
											if(g.getBalance() >= amount) {
												g.removeBalance(amount, g.getGuildPlayer(p));
												g.getGuildPlayer(p).addBalance(amount);
												g.addTransaction(new BankTransaction(g.getGuildPlayer(p), -amount, Currency.SILVER, System.currentTimeMillis()));
											}
											else {
												if(g.getBalance() == 0) {
													p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cWithdraw cancelled! The guild doesn't have any money!"));
													p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
													bank.openInventory();
												}
												else {
													p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild doesn't have this much money!"));
													p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
													bank.openInventory();
												}
											}
										}
										else {
											p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number!"));
											p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
											bank.openInventory();
										}
									}
									else {
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the bank!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
								});
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild doesn't have any money!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else if(e.getClick() == ClickType.RIGHT) {
							if(g.getDust() > 0) {
								bank.closeInventory();
								Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^", "Enter an amount", "to withdraw"}, (player, lines) -> {
									if(Guild.getByPlayer(p) != null) {
										String msg = lines[0].replaceAll("\"", "");
										if(isInt(msg)) {
											int amount = Integer.parseInt(msg);
											if(g.getDust() >= amount) {
												g.removeDust(amount, g.getGuildPlayer(p));
												g.getGuildPlayer(p).addDust(amount);
												g.addTransaction(new BankTransaction(g.getGuildPlayer(p), -amount, Currency.DUST, System.currentTimeMillis()));
											}
											else {
												if(g.getDust() == 0) {
													p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cWithdraw cancelled! The guild doesn't have any dwarf dust!"));
													p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
													bank.openInventory();
												}
												else {
													p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild doesn't have this much dwarf dust!"));
													p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
													bank.openInventory();
												}
											}
										}
										else {
											p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number!"));
											p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
											bank.openInventory();
										}
									}
									else {
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the bank!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
								});
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild doesn't have any dwarf dust!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
					}
					else if(e.getSlot() == 15) {
						if(e.getClick() == ClickType.LEFT) {
							if(g.getGuildPlayer(p).getBalance() > 0) {
								bank.closeInventory();
								Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^", "Enter an amount", "to deposit"}, (player, lines) -> {
									if(Guild.getByPlayer(p) != null) {
										String msg = lines[0].replaceAll("\"", "");
										if(isInt(msg)) {
											int amount = Integer.parseInt(msg);
											if(g.getGuildPlayer(p).getBalance() >= amount) {
												g.getGuildPlayer(p).removeBalance(amount);
												g.addBalance(amount, g.getGuildPlayer(p), false);
												g.addTransaction(new BankTransaction(g.getGuildPlayer(p), amount, Currency.SILVER, System.currentTimeMillis()));
											}
											else {
												p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford to deposit this much money!"));
												p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
												bank.openInventory();
											}
										}
										else {
											p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number!"));
											p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
											bank.openInventory();
										}
									}
									else {
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the bank!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
								});
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour personal wallet doesn't have any money!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
						else if(e.getClick() == ClickType.RIGHT) {
							if(g.getGuildPlayer(p).getDust() > 0) {
								bank.closeInventory();
								Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^", "Enter an amount", "to deposit"}, (player, lines) -> {
									if(Guild.getByPlayer(p) != null) {
										String msg = lines[0].replaceAll("\"", "");
										if(isInt(msg)) {
											int amount = Integer.parseInt(msg);
											if(g.getGuildPlayer(p).getDust() >= amount) {
												g.getGuildPlayer(p).removeDust(amount);
												g.addDust(amount, g.getGuildPlayer(p), false);
												g.addTransaction(new BankTransaction(g.getGuildPlayer(p), amount, Currency.DUST, System.currentTimeMillis()));
											}
											else {
												p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford to deposit this much dwarf dust!"));
												p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
												bank.openInventory();
											}
										}
										else {
											p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number!"));
											p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
											bank.openInventory();
										}
									}
									else {
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the bank!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
								});
							}
							else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour personal wallet doesn't have any dwarf dust!"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							}
						}
					}
				}
				else {
					if(e.getSlot() == 13) {
						new TransactionHistoryMenu(p);
					}
					else if(e.getSlot() == 11) {
						if(g.getBalance() > 0) {
							bank.closeInventory();
							Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^", "Enter an amount", "to withdraw"}, (player, lines) -> {
								if(Guild.getByPlayer(p) != null) {
									String msg = lines[0].replaceAll("\"", "");
									if(isInt(msg)) {
										int amount = Integer.parseInt(msg);
										if(g.getBalance() >= amount) {
											g.removeBalance(amount, g.getGuildPlayer(p));
											g.getGuildPlayer(p).addBalance(amount);
											g.addTransaction(new BankTransaction(g.getGuildPlayer(p), -amount, Currency.SILVER, System.currentTimeMillis()));
										}
										else {
											if(g.getBalance() == 0) {
												p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cWithdraw cancelled! The guild doesn't have any money!"));
												p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
												bank.openInventory();
											}
											else {
												p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild doesn't have this much money!"));
												p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
												bank.openInventory();
											}
										}
									}
									else {
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
										bank.openInventory();
									}
								}
								else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the bank!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
							});
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour guild doesn't have any money!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
					else if(e.getSlot() == 15) {
						if(g.getGuildPlayer(p).getBalance() > 0) {
							bank.closeInventory();
							Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^", "Enter an amount", "to deposit"}, (player, lines) -> {
								if(Guild.getByPlayer(p) != null) {
									String msg = lines[0].replaceAll("\"", "");
									if(isInt(msg)) {
										int amount = Integer.parseInt(msg);
										if(g.getGuildPlayer(p).getBalance() >= amount) {
											g.getGuildPlayer(p).removeBalance(amount);
											g.addBalance(amount, g.getGuildPlayer(p), false);
											g.addTransaction(new BankTransaction(g.getGuildPlayer(p), amount, Currency.SILVER, System.currentTimeMillis()));
										}
										else {
											p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford to deposit this much money!"));
											p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
											bank.openInventory();
										}
									}
									else {
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid number!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
										bank.openInventory();
									}
								}
								else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease create/join a guild to access the bank!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
							});
						}
						else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYour personal wallet doesn't have any money!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
					}
				}
			}
		}
	}
	
	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

}
