package net.colonymc.colonyskyblockcore.guilds.trade;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyspigotapi.api.itemstack.ItemStackBuilder;
import net.colonymc.colonyspigotapi.api.player.PlayerInventory;
import net.colonymc.colonyskyblockcore.Main;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.Relation;

public class TradeInventory implements InventoryHolder, Listener{
	
		Inventory inv;
		Trader p;
		Trader anotherP;
		boolean cancelled = false;
		boolean settingAmount = false;
		BukkitTask update;
		BukkitTask inactive;
		boolean shouldCancel = false;
		boolean isOpen = true;
		
		public TradeInventory(Trader p, Trader anotherP) {
			this.p = p;
			this.anotherP = anotherP;
			this.inv = Bukkit.createInventory(this, 54, "Trading with " + anotherP.getPlayer().getPlayer().getName() + "...");
			fillInventory();
			BukkitRunnable updateRunnable = startUpdating();
			new BukkitRunnable() {
				@Override
				public void run() {
					Player pl =  (Player) p.getPlayer().getPlayer();
					pl.openInventory(inv);
					update = updateRunnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
				}
			}.runTaskLater(Main.getInstance(), 1L);
		}
		
		public TradeInventory() {
		}
		
		public void openInventory() {
			new BukkitRunnable() {
				@Override
				public void run() {
					if(inactive != null) {
						inactive.cancel();
					}
					Player pl =  (Player) p.getPlayer().getPlayer();
					pl.openInventory(inv);
					BukkitRunnable updateRunnable = startUpdating();
					update = updateRunnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
				}
			}.runTaskLater(Main.getInstance(), 1L);
		}
		
		public void closeInventory() {
			new BukkitRunnable() {
				@Override
				public void run() {
					update.cancel();
					Player pl =  (Player) p.getPlayer().getPlayer();
					pl.closeInventory();
					startInactive();
				}
			}.runTaskLater(Main.getInstance(), 1L);
		}
		
		public void startInactive() {
			BukkitRunnable runnable = new BukkitRunnable() {
				final Player pl = (Player) p.getPlayer().getPlayer();
				final Player anotherPl = (Player) anotherP.getPlayer().getPlayer();
				@Override
				public void run() {
					p.t.cancel();
					pl.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe trading has been cancelled due to inactivity!"));
					pl.playSound(pl.getLocation(), Sound.NOTE_BASS, 2, 1);
					anotherPl.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe trading has been cancelled due to inactivity!"));
					anotherPl.playSound(pl.getLocation(), Sound.NOTE_BASS, 2, 1);
				}
			};
			inactive = runnable.runTaskLater(Main.getInstance(), 200);
		}

		private void fillInventory() {
			for(int i = 45; i < 54; i++) {
				inv.setItem(i, new ItemStackBuilder(Material.STAINED_GLASS_PANE).durability((short) 2).name(" ").build());
			}
			for(int i = 4; i < 50; i = i + 9) {
				inv.setItem(i, new ItemStackBuilder(Material.STAINED_GLASS_PANE).durability((short) 2).name(" ").build());
			}
			inv.setItem(49, new ItemStackBuilder(Material.BARRIER).name("&cCancel trade").build());
			//this player
			inv.setItem(45, new ItemStackBuilder(Material.IRON_INGOT)
					.name("&dMoney added - $0")
					.lore("\n&dLeft-Click &fto add money to the trade"
					+ "\n&dRight-Click &fto remove money from the trade")
					.build());
			if(p.getPlayer().getPlayer().getPlayer().hasPermission("*") && anotherP.getPlayer().getPlayer().getPlayer().hasPermission("*")) {
				inv.setItem(46, new ItemStackBuilder(Material.GLOWSTONE_DUST)
						.name("&dGrams of dwarf dust - 0g")
						.lore("\n&dLeft-Click &fto add dwarf dust to the trade"
						+ "\n&dRight-Click &fto remove dwarf dust from the trade")
						.build());
			}
			inv.setItem(47, new ItemStackBuilder(Material.STAINED_CLAY)
					.name("&cNot Ready").lore("\n&fClick here if you have added\n"
					+ "&feverything you want and you are\n&ftotally ready for the trade!").durability((short) 5).build());
			//other player
			inv.setItem(51, new ItemStackBuilder(Material.STAINED_CLAY).name("&cNot Ready").lore("\n&cThe other player is\n&cnot ready yet!").durability((short) 5).build());
			inv.setItem(53, new ItemStackBuilder(Material.IRON_INGOT).name("&dMoney added - $0").lore("\n&fThe other player is"
					+ "\n&foffering &d$0").build());
			if(p.getPlayer().getPlayer().getPlayer().hasPermission("*") && anotherP.getPlayer().getPlayer().getPlayer().hasPermission("*")) {
				inv.setItem(52, new ItemStackBuilder(Material.GLOWSTONE_DUST).name("&dGrams of dwarf dust - 0g").lore("\n&fThe other player is"
						+ "\n&foffering &d0g of dwarf dust").build());
			}
		}

		@Override
		public Inventory getInventory() {
			return inv;
		}
		
		public BukkitRunnable startUpdating() {
			return new BukkitRunnable() {
				@Override
				public void run() {
					for(int i = 0; i < 20; i++) {
						int line = (int) (Math.ceil((double) (i + 1)/ 4));
						int row = i - (4 * (line - 1));
						int slot = row + (line - 1) * 9;
						if(i + 1 > p.getItems().size()) {
							inv.setItem(slot, new ItemStack(Material.AIR));
						}
						else {
							inv.setItem(slot, p.getItems().get(i));
						}
					}
					for(int i = 0; i < 20; i++) {
						int line = (int) (Math.ceil((double) (i + 1)/ 4));
						int row = i - (4 * (line - 1)) + 5;
						int slot = row + (line - 1) * 9;
						if(i + 1 > anotherP.getItems().size()) {
							inv.setItem(slot, new ItemStack(Material.AIR));
						}
						else {
							inv.setItem(slot, anotherP.getItems().get(i));
						}
					}
					inv.setItem(45, new ItemStackBuilder(Material.IRON_INGOT).name("&dMoney added - " + Guild.balance(p.getSilver()))
							.lore("\n&dLeft-Click &fto add money to the trade\n"
							+ "&dRight-Click &fto remove money from the trade").build());
					if(p.getPlayer().getPlayer().getPlayer().hasPermission("*") && anotherP.getPlayer().getPlayer().getPlayer().hasPermission("*")) {
						inv.setItem(46, new ItemStackBuilder(Material.GLOWSTONE_DUST).name("&dGrams of dwarf dust - " + Guild.balance(p.getDust()))
								.lore("\n&dLeft-Click &fto add dwarf dust to the trade"
								+ "\n&dRight-Click &fto remove dwarf dust from the trade").build());
					}
					inv.setItem(53, new ItemStackBuilder(Material.IRON_INGOT).name("&dMoney added - " + Guild.balance(anotherP.getSilver()))
							.lore("\n&fThe other player is"
							+ "\n&foffering &d$" + anotherP.getSilver()).build());
					if(p.getPlayer().getPlayer().getPlayer().hasPermission("*") && anotherP.getPlayer().getPlayer().getPlayer().hasPermission("*")) {
						inv.setItem(52, new ItemStackBuilder(Material.GLOWSTONE_DUST).name("&dGrams of dwarf dust - " + Guild.balance(anotherP.getDust())).lore("\n&fThe other player is"
								+ "\n&foffering &d" + anotherP.getDust() + "g of dwarf dust").build());
					}
					if(p.isReady() && !anotherP.isReady()) {
						//this player
						inv.setItem(47, new ItemStackBuilder(Material.STAINED_CLAY).name("&aReady").lore("\n&fYou are currently waiting for \n&fthe other player to ready up!").durability((short) 5).build());
						//other player
						inv.setItem(51, new ItemStackBuilder(Material.STAINED_CLAY).name("&cNot Ready").lore("\n&cThe other player is\n&cnot ready yet!").durability((short) 14).build());
					}
					else if(anotherP.isReady() && !p.isReady()){
						//this player
						inv.setItem(47, new ItemStackBuilder(Material.STAINED_CLAY).name("&cNot Ready").lore("\n&fClick here if you have added\n"
								+ "&feverything you want and you are\n&ftotally ready for the trade!").durability((short) 14).build());
						//other player
						inv.setItem(51, new ItemStackBuilder(Material.STAINED_CLAY).name("&aReady").lore("\n&fThe other player is waiting\n&ffor you to ready up!").durability((short) 5).build());
					}
					else if(!p.isReady() && !anotherP.isReady()){
						//this player
						inv.setItem(47, new ItemStackBuilder(Material.STAINED_CLAY).name("&cNot Ready").lore("\n&fClick here if you have added\n"
								+ "&feverything you want and you are\n&ftotally ready for the trade!").durability((short) 14).build());
						//other player
						inv.setItem(51, new ItemStackBuilder(Material.STAINED_CLAY).name("&cNot Ready").lore("\n&cThe other player is\n&cnot ready yet!").durability((short) 14).build());
					}
				}
			};
		}
		
		@EventHandler
		public void onClick(InventoryClickEvent e) {
			if(e.getInventory().getHolder() instanceof TradeInventory) {
				e.setCancelled(true);
				TradeInventory tr = (TradeInventory) e.getInventory().getHolder();
				Player p = (Player) tr.p.getPlayer().getPlayer();
				Player anotherP = (Player) tr.anotherP.getPlayer().getPlayer();
				if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
					if(tr.p.getItems().contains(e.getCurrentItem())) {
						if(!tr.p.ready) {
							ItemStack i = tr.p.getItems().get(tr.p.getItems().indexOf(e.getCurrentItem()));
							PlayerInventory.addItem(i, p, i.getAmount());
							tr.p.getItems().remove(i);
						}
					}
					else if(e.getSlot() == 49) {
						tr.p.getTrade().cancel();
					}
					else if(e.getSlot() == 47) {
						if(tr.p.t.isHappening) {
							tr.p.t.stopCountdown();
							tr.p.setReady(!tr.p.isReady());
							tr.anotherP.setReady(!tr.anotherP.isReady());
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou stopped the process of the trade!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
							anotherP.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe other player stopped the process of the trade!"));
							anotherP.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
						else {
							tr.p.setReady(!tr.p.isReady());
							tr.p.t.startCountdown();
						}
					}
					else if(e.getSlot() == 45) {
						if(!tr.p.ready) {
							if(e.getClick() == ClickType.LEFT) {
								if(tr.p.getSilver() == 100000) {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou have already added the maximum amount of money to the trade!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
								else {
									tr.settingAmount = true;
									p.closeInventory();
									Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^^", "Enter an amount", "of money!"}, (player, lines) -> {
										tr.settingAmount = false;
										if(!tr.cancelled) {
											String msg = lines[0].replaceAll("\"", "");
											Trader t = tr.p;
											if(isInt(msg) && Integer.parseInt(msg) > 0) {
												int amount = Integer.parseInt(msg);
												if(t.p.getBalance() >= amount) {
													if(amount > 100000) {
														p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have added &d$" + (100000 - t.getSilver()) +
																" &fto the trade! &f(Total: &d$100,000&f)"));
														p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
														t.p.removeBalance(100000 - t.getSilver());
														t.addSilver(100000 - t.getSilver());
													}
													else if(t.getSilver() + amount > 100000) {
														p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have added &d$" + (100000 - t.getSilver()) +
																" &fto the trade! &f(Total: &d$100,000&f)"));
														p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
														t.p.removeBalance(100000 - t.getSilver());
														t.addSilver(100000 - t.getSilver());
													}
													else {
														t.p.removeBalance(amount);
														t.addSilver(amount);
														p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have added &d$" + amount +
																" &fto the trade! &f(Total: &d$" + t.getSilver() + "&f)"));
														p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
													}
												}
												else {
													p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford to trade this much money!"));
													p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
												}
											}
											else {
												p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid value bigger than 0!"));
												p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
											}
											tr.openInventory();
										}
									});
								}
							}
							else if(e.getClick() == ClickType.RIGHT) {
								if(tr.p.getSilver() == 0) {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou haven't added any money to the trade!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
								else {
									tr.settingAmount = true;
									p.closeInventory();
									Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^^", "Enter an amount", "of money!"}, (player, lines) -> {
										tr.settingAmount = false;
										if(!tr.cancelled) {
											String msg = lines[0].replaceAll("\"", "");
											Trader t = tr.p;
											if(isInt(msg) && Integer.parseInt(msg) > 0) {
												int amount = Integer.parseInt(msg);
												if(t.getSilver() - amount < 0) {
													p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have removed &d" + Guild.balance(t.getSilver()) +
															" &ffrom the trade! &f(Total: &d$0&f)"));
													p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
													t.p.addBalance(t.getSilver());
													t.addSilver(-t.getSilver());
												}
												else {
													t.p.addBalance(amount);
													t.addSilver(-amount);
													p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have removed &d" + Guild.balance(amount) +
															" &ffrom the trade! &f(Total: &d$" + t.getSilver() + "&f)"));
													p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
												}
											}
											else {
												p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid value bigger than 0!"));
												p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
											}
											tr.openInventory();
										}
									});
								}
							}
						}
					}
					else if(e.getSlot() == 46) {
						if(p.getPlayer().getPlayer().getPlayer().hasPermission("*") && anotherP.getPlayer().getPlayer().getPlayer().hasPermission("*")) {
							if(!tr.p.ready) {
								if(e.getClick() == ClickType.LEFT) {
									if(tr.p.getDust() == 10000) {
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou have already added the maximum amount of dust to the trade!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
									else {
										tr.settingAmount = true;
										p.closeInventory();
										Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^^", "Enter the amount", "of dust you want"}, (player, lines) -> {
											tr.settingAmount = false;
											if(!tr.cancelled) {
												String msg = lines[0].replaceAll("\"", "");
												Trader t = tr.p;
												if(isInt(msg) && Integer.parseInt(msg) > 0) {
													int amount = Integer.parseInt(msg);
													if(t.p.getDust() >= amount) {
														if(amount > 10000) {
															p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have added &d" + Guild.balance(10000 - t.getDust()) +
																	" of dwarf dust &fto the trade! &f(Total: &d10,000g&f)"));
															p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
															t.p.removeDust(10000 - t.getDust());
															t.addDust(10000 - t.getDust());
														}
														else if(t.getDust() + amount > 10000) {
															p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have added &d" + Guild.balance((10000 - t.getDust()))+
																	" of dwarf dust &fto the trade! &f(Total: &d10,000g&f)"));
															p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
															t.p.removeDust(10000 - t.getDust());
															t.addDust(10000 - t.getDust());
														}
														else {
															t.p.removeDust(amount);
															t.addDust(amount);
															p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have added &d" + Guild.balance(amount) +
																	" of dwarf dust &fto the trade! &f(Total: &d" + t.getDust() + "g&f)"));
															p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
														}
													}
													else {
														p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot afford to trade this much dust!"));
														p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
													}
												}
												else {
													p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid value bigger than 0!"));
													p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
												}
												tr.openInventory();
											}
										});
									}
								}
								else if(e.getClick() == ClickType.RIGHT) {
									if(tr.p.getDust() == 0) {
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou haven't added any dust to the trade!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
									else {
										tr.settingAmount = true;
										p.closeInventory();
										Main.getSignGui().open(p, new String[] {"", "^^^^^^^^^^^^^^^^", "Enter the amount", "of dust you want"}, (player, lines) -> {
											tr.settingAmount = false;
											if(!tr.cancelled) {
												String msg = lines[0].replaceAll("\"", "");
												Trader t = tr.p;
												if(isInt(msg) && Integer.parseInt(msg) > 0) {
													int amount = Integer.parseInt(msg);
													if(t.getDust() - amount < 0) {
														p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have removed &d" + Guild.balance(t.getDust()) +
																" of dwarf dust &ffrom the trade! &f(Total: &d0g&f)"));
														p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
														t.p.addDust(t.getDust());
														t.addDust(-t.getDust());
													}
													else {
														t.p.addDust(amount);
														t.addDust(-amount);
														p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &fYou have removed &d" + Guild.balance(amount) +
																" of dwarf dust &ffrom the trade! &f(Total: &d" + t.getDust() + "g&f)"));
														p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
													}
												}
												else {
													p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cPlease enter a valid value bigger than 0!"));
													p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
												}
												tr.openInventory();
											}
										});
									}
								}
							}
						}
					}
				}
				else {
					if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
						if(tr.p.getItems().size() == 20) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou cannot add any more items!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
						else {
							if(!tr.p.ready) {
								tr.p.addItem(e.getCurrentItem());
								e.setCurrentItem(new ItemStack(Material.AIR));
								p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2, 1);
							}
						}
					}
				}
			}
		}
		
		@EventHandler
		public void onLeave(PlayerQuitEvent e) {
			if(Trade.isInTrade(e.getPlayer()) != null) {
				Trade.isInTrade(e.getPlayer()).cancel();
				Player p = Trade.isInTrade(e.getPlayer()).requestedTrader.getPlayer().getPlayer().getPlayer().equals(e.getPlayer()) 
						? Trade.isInTrade(e.getPlayer()).requestedTrader.getPlayer().getPlayer().getPlayer() : Trade.isInTrade(e.getPlayer()).requesterTrader.getPlayer().getPlayer().getPlayer();
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe other player logged out!"));
			}
		}
		
		@EventHandler
		public void onLeave(PlayerKickEvent e) {
			if(Trade.isInTrade(e.getPlayer()) != null) {
				Trade.isInTrade(e.getPlayer()).cancel();
				Player p = Trade.isInTrade(e.getPlayer()).requestedTrader.getPlayer().getPlayer().getPlayer().equals(e.getPlayer()) 
						? Trade.isInTrade(e.getPlayer()).requestedTrader.getPlayer().getPlayer().getPlayer() : Trade.isInTrade(e.getPlayer()).requesterTrader.getPlayer().getPlayer().getPlayer();
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThe other player logged out!"));
			}
		}
		
		@EventHandler
		public void onLeave(PlayerDeathEvent e) {
			if(Trade.isInTrade(e.getEntity()) != null) {
				Trader t = Trade.isInTrade(e.getEntity()).requestedTrader.getPlayer().getPlayer().getPlayer().equals(e.getEntity()) ? Trade.isInTrade(e.getEntity()).requestedTrader : Trade.isInTrade(e.getEntity()).requesterTrader;
				e.getDrops().addAll(t.getItems());
				Trade.isInTrade(e.getEntity()).cancel();
			}
		}
		
		@EventHandler
		public void onClose(InventoryCloseEvent e) {
			if(e.getInventory().getHolder() instanceof TradeInventory) {
				TradeInventory tr = (TradeInventory) e.getInventory().getHolder();
				tr.update.cancel();
				if(!tr.cancelled && !tr.settingAmount) {
					tr.openInventory();
				}
			}
		}
		
		@EventHandler
		public void onRightClick(PlayerInteractEntityEvent e) {
			Player p = e.getPlayer();
			if(p.isSneaking()) {
				if(e.getRightClicked() instanceof Player) {
					Player anotherP = (Player) e.getRightClicked();
					if(Guild.getByPlayer(anotherP) != null) {
						if(Trade.isInTrade(anotherP) != null) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cThis player is already trading with someone else!"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
						}
						else {
							if(Trade.hasBeenRequested(Guild.getByPlayer(p).getGuildPlayer(p), Guild.getByPlayer(anotherP).getGuildPlayer(anotherP)) != null) {
								Trade t = Trade.hasBeenRequested(Guild.getByPlayer(p).getGuildPlayer(p), Guild.getByPlayer(anotherP).getGuildPlayer(anotherP));
								if(!t.accepted) {
									if(Guild.getByPlayer(p).getRelation(Guild.getByPlayer(anotherP)) != Relation.ENEMY) {
										t.accept();
									}
									else {
										t.cancel();
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou can't trade with a player from an enemy!"));
										p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
									}
								}
								else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou already have an active trade with this player!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
							}
							else if(Trade.hasBeenRequested(Guild.getByPlayer(anotherP).getGuildPlayer(anotherP), Guild.getByPlayer(p).getGuildPlayer(p)) == null) {
								if(Guild.getByPlayer(p).getRelation(Guild.getByPlayer(anotherP)) != Relation.ENEMY) {
									new Trade(Guild.getByPlayer(p).getGuildPlayer(p), Guild.getByPlayer(anotherP).getGuildPlayer(anotherP));
								}
								else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou can't trade with a player from an enemy!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
							}
							else {
								Trade t = Trade.hasBeenRequested(Guild.getByPlayer(anotherP).getGuildPlayer(anotherP), Guild.getByPlayer(p).getGuildPlayer(p));
								if(!t.accepted) {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou have already requested from this player to trade!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
								else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou already have an active trade with this player!"));
									p.playSound(p.getLocation(), Sound.NOTE_BASS, 2, 1);
								}
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
