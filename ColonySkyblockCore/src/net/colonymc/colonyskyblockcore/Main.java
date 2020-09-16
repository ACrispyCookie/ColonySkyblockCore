package net.colonymc.colonyskyblockcore;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.api.itemstacks.Serializer;
import net.colonymc.colonyskyblockcore.crates.Crate;
import net.colonymc.colonyskyblockcore.crates.CrateMenu;
import net.colonymc.colonyskyblockcore.guilds.AdminGuildCommand;
import net.colonymc.colonyskyblockcore.guilds.Cooldown;
import net.colonymc.colonyskyblockcore.guilds.CooldownType;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.colonymc.colonyskyblockcore.guilds.GuildCommand;
import net.colonymc.colonyskyblockcore.guilds.GuildListeners;
import net.colonymc.colonyskyblockcore.guilds.SpawnCommand;
import net.colonymc.colonyskyblockcore.guilds.TeleportCommand;
import net.colonymc.colonyskyblockcore.guilds.auction.Auction;
import net.colonymc.colonyskyblockcore.guilds.auction.AuctionItem;
import net.colonymc.colonyskyblockcore.guilds.auction.Bidder;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionBidHistoryMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionBidMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionCreateMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionHouseMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionHouseSelectMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionInspectBidMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionInspectMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionManageBoughtMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionManageOwnMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionManagePendingMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionSearchMenu;
import net.colonymc.colonyskyblockcore.guilds.auction.inventories.AuctionSetDurationMenu;
import net.colonymc.colonyskyblockcore.guilds.bank.BankInventory;
import net.colonymc.colonyskyblockcore.guilds.bank.TransactionHistoryMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildAddRelationMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildLootMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildMainMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildMemberMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildMembersMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildRelationMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildRelationsMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildSelectLootMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildSettingsMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.GuildWarsMenu;
import net.colonymc.colonyskyblockcore.guilds.inventories.IslandSettingsMenu;
import net.colonymc.colonyskyblockcore.guilds.leaderboards.Leaderboard;
import net.colonymc.colonyskyblockcore.guilds.leaderboards.LeaderboardSort;
import net.colonymc.colonyskyblockcore.guilds.test.AuctionInventory;
import net.colonymc.colonyskyblockcore.guilds.trade.TradeCommand;
import net.colonymc.colonyskyblockcore.guilds.trade.TradeInventory;
import net.colonymc.colonyskyblockcore.guilds.war.ConfirmWarMenu;
import net.colonymc.colonyskyblockcore.guilds.war.SelectModeWarMenu;
import net.colonymc.colonyskyblockcore.guilds.war.TeamDeathmatch;
import net.colonymc.colonyskyblockcore.guilds.war.WarLootMenu;
import net.colonymc.colonyskyblockcore.kits.ChooseCooldownMenu;
import net.colonymc.colonyskyblockcore.kits.CreateKitCommand;
import net.colonymc.colonyskyblockcore.kits.DeleteKitCommand;
import net.colonymc.colonyskyblockcore.kits.Kit;
import net.colonymc.colonyskyblockcore.kits.KitCommand;
import net.colonymc.colonyskyblockcore.kits.KitCreator;
import net.colonymc.colonyskyblockcore.kits.KitsMenu;
import net.colonymc.colonyskyblockcore.kits.PreviewMenu;
import net.colonymc.colonyskyblockcore.minions.FarmerMinionBlock;
import net.colonymc.colonyskyblockcore.minions.MaterialType;
import net.colonymc.colonyskyblockcore.minions.MinerMinionBlock;
import net.colonymc.colonyskyblockcore.minions.Minion;
import net.colonymc.colonyskyblockcore.minions.MinionBlock;
import net.colonymc.colonyskyblockcore.minions.MinionCommand;
import net.colonymc.colonyskyblockcore.minions.MinionMenu;
import net.colonymc.colonyskyblockcore.minions.MinionType;
import net.colonymc.colonyskyblockcore.minions.SlayerMinionBlock;
import net.colonymc.colonyskyblockcore.minions.fuel.Fuel;
import net.colonymc.colonyskyblockcore.minions.fuel.FuelCommand;
import net.colonymc.colonyskyblockcore.minions.fuel.FuelType;
import net.colonymc.colonyskyblockcore.npcs.NPCListener;
import net.colonymc.colonyskyblockcore.pets.Pet;
import net.colonymc.colonyskyblockcore.pets.PetCommand;
import net.colonymc.colonyskyblockcore.pets.PetItem;
import net.colonymc.colonyskyblockcore.pouches.PouchCommand;
import net.colonymc.colonyskyblockcore.pouches.PouchItem;
import net.colonymc.colonyskyblockcore.shop.CommandProduct;
import net.colonymc.colonyskyblockcore.shop.Product;
import net.colonymc.colonyskyblockcore.shop.ShopCategoriesMenu;
import net.colonymc.colonyskyblockcore.shop.ShopCategory;
import net.colonymc.colonyskyblockcore.shop.ShopCategoryMenu;
import net.colonymc.colonyskyblockcore.shop.ShopCommand;
import net.colonymc.colonyskyblockcore.shop.ShopProductBuyMenu;
import net.colonymc.colonyskyblockcore.shop.ShopProductSellMenu;
import net.colonymc.colonyskyblockcore.spawners.SpawnerCommand;
import net.colonymc.colonyskyblockcore.spawners.SpawnerListener;
import net.colonymc.colonyskyblockcore.util.PluginCommand;
import net.colonymc.colonyskyblockcore.util.currencies.DwarfDustCommand;
import net.colonymc.colonyskyblockcore.util.currencies.SilverCommand;
import net.colonymc.colonyskyblockcore.util.messages.MessageCommand;
import net.colonymc.colonyskyblockcore.util.messages.MessageListeners;
import net.colonymc.colonyskyblockcore.util.messages.ReplyCommand;
import net.colonymc.colonyskyblockcore.util.scoreboard.ScoreboardUtils;
import net.colonymc.colonyskyblockcore.util.signs.SignGUI;

public class Main extends JavaPlugin{
	
	private static Connection connection;
    private static String host, database, username, password;
    private static int port;
    private static String url;
    private static Crate crateInstance;
    private static SignGUI signGui;
	static Main instance;
	File names = new File(this.getDataFolder(), "names.yml");
	File shop = new File(this.getDataFolder(), "shop.yml");
	File kits = new File(this.getDataFolder(), "kits.yml");
	static FileConfiguration namesConfig, shopConfig, kitsConfig;
	
	public void onEnable() {
		setInstance(this);
		signGui = new SignGUI(this);
		crateInstance = new Crate();
		if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			new Placeholders(this).register();
        }
		this.saveDefaultConfig();
		setupConfigFiles();
		loadGuildSystem();
		initializeCommands();
		initializeUtilListeners();
		setupConnections();
		loadLeaderboards();
		loadShop();
		loadAuctions();
		loadCooldowns();
		loadMinions();
		loadKits();
		loadCrate();
		loadOnlinePlayers();
		setupRunnables();
		System.out.println(" » ColonySkyblockCore has been enabled successfully!");
	}

	public void onDisable() {
		unloadMinions();
		unloadCrate();
		unloadLeaderboards();
		NPCListener.despawnNPCs();
		System.out.println(" » ColonySkyblockCore has been disabled successfully!");
	}

	private void loadGuildSystem() {
		if(Bukkit.getWorld("islands") == null) {
			WorldCreator c = new WorldCreator("islands");
			c.type(WorldType.NORMAL);
			c.environment(Environment.NORMAL);
			c.generator(new ChunkGenerator() {
	            @Override
	            public ChunkData generateChunkData(World world, Random random, int cx, int cz, ChunkGenerator.BiomeGrid biome) {
	                ChunkData chunkData = createChunkData(world);
	                for (int x = 0; x <= 15; x++) {
	                    for (int z = 0; z <= 15; z++) {
	                        biome.setBiome(x, z, Biome.PLAINS);
	                    }
	                }
	                return chunkData;
	            }
			});
			c.createWorld();
		}
		if(Bukkit.getWorld("arenas") == null) {
			WorldCreator c = new WorldCreator("arenas");
			c.type(WorldType.NORMAL);
			c.environment(Environment.NORMAL);
			c.generator(new ChunkGenerator() {
	            @Override
	            public ChunkData generateChunkData(World world, Random random, int cx, int cz, ChunkGenerator.BiomeGrid biome) {
	                ChunkData chunkData = createChunkData(world);
	                for (int x = 0; x <= 15; x++) {
	                    for (int z = 0; z <= 15; z++) {
	                        biome.setBiome(x, z, Biome.PLAINS);
	                    }
	                }
	                return chunkData;
	            }
			});
			c.createWorld();
		}
		NPCListener.setupNPCs();
		Bukkit.getPluginManager().registerEvents(crateInstance, this);
		Bukkit.getPluginManager().registerEvents(new SkyblockPlayer(), this);
		Bukkit.getPluginManager().registerEvents(new SpawnerListener(), this);
		Bukkit.getPluginManager().registerEvents(new PouchItem(), this);
		Bukkit.getPluginManager().registerEvents(new PetItem(), this);
		Bukkit.getPluginManager().registerEvents(new Pet(), this);
		Bukkit.getPluginManager().registerEvents(new IslandSettingsMenu(), this);
		Bukkit.getPluginManager().registerEvents(new GuildListeners(), this);
		Bukkit.getPluginManager().registerEvents(new GuildMainMenu(), this);
		Bukkit.getPluginManager().registerEvents(new GuildMembersMenu(), this);
		Bukkit.getPluginManager().registerEvents(new GuildMemberMenu(), this);
		Bukkit.getPluginManager().registerEvents(new GuildSettingsMenu(), this);
		Bukkit.getPluginManager().registerEvents(new GuildRelationsMenu(), this);
		Bukkit.getPluginManager().registerEvents(new GuildRelationMenu(), this);
		Bukkit.getPluginManager().registerEvents(new GuildAddRelationMenu(), this);
		Bukkit.getPluginManager().registerEvents(new GuildWarsMenu(), this);
		Bukkit.getPluginManager().registerEvents(new GuildSelectLootMenu(), this);
		Bukkit.getPluginManager().registerEvents(new GuildLootMenu(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionHouseMenu(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionHouseSelectMenu(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionSearchMenu(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionCreateMenu(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionBidMenu(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionInspectMenu(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionManageBoughtMenu(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionManageOwnMenu(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionManagePendingMenu(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionBidHistoryMenu(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionInspectBidMenu(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionSetDurationMenu(), this);
		Bukkit.getPluginManager().registerEvents(new ShopCategoriesMenu(), this);
		Bukkit.getPluginManager().registerEvents(new ShopCategoryMenu(), this);
		Bukkit.getPluginManager().registerEvents(new ShopProductBuyMenu(), this);
		Bukkit.getPluginManager().registerEvents(new ShopProductSellMenu(), this);
		Bukkit.getPluginManager().registerEvents(new TeamDeathmatch(), this);
		Bukkit.getPluginManager().registerEvents(new TradeInventory(), this);
		Bukkit.getPluginManager().registerEvents(new BankInventory(), this);
		Bukkit.getPluginManager().registerEvents(new TransactionHistoryMenu(), this);
		Bukkit.getPluginManager().registerEvents(new SelectModeWarMenu(), this);
		Bukkit.getPluginManager().registerEvents(new ConfirmWarMenu(), this);
		Bukkit.getPluginManager().registerEvents(new WarLootMenu(), this);
		Bukkit.getPluginManager().registerEvents(new MinionMenu(), this);
		Bukkit.getPluginManager().registerEvents(new MinionBlock() {
			@Override public void place() {} @Override protected void playAnimation() {} @Override protected ItemStack getItemInHand() {return null;}
			@Override protected boolean isInRightArea() {return false;} @Override protected boolean doTask() { return false; }
		}, this);
		Bukkit.getPluginManager().registerEvents(new CrateMenu(), this);
		Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
		Bukkit.getPluginManager().registerEvents(new AuctionInventory() {@Override public void changePage(int amount) {} @Override public void addButtons() {} @Override public void onClose() {}}, this);
		this.getCommand("spawner").setExecutor(new SpawnerCommand());
		this.getCommand("shop").setExecutor(new ShopCommand());
		this.getCommand("minion").setExecutor(new MinionCommand());
		this.getCommand("pet").setExecutor(new PetCommand());
		this.getCommand("pouch").setExecutor(new PouchCommand());
		this.getCommand("fuel").setExecutor(new FuelCommand());
		this.getCommand("trade").setExecutor(new TradeCommand());
		this.getCommand("guild").setExecutor(new GuildCommand());
		this.getCommand("adminguild").setExecutor(new AdminGuildCommand());
		this.getCommand("spawn").setExecutor(new SpawnCommand());
		DwarfDustCommand dust = new DwarfDustCommand();
		this.getCommand("dust").setExecutor(dust);
		this.getCommand("dwarfdust").setExecutor(dust);
		SilverCommand silver = new SilverCommand();
		this.getCommand("balance").setExecutor(silver);
	}
	
	public void loadCrate() {
		crateInstance.spawn();
	}
	
	public void unloadCrate() {
		crateInstance.despawn();
	}

	public void loadKits() {
		this.getCommand("deletekit").setExecutor(new DeleteKitCommand());
		this.getCommand("createkit").setExecutor(new CreateKitCommand());
		this.getCommand("kit").setExecutor(new KitCommand());
		Bukkit.getPluginManager().registerEvents(new KitCreator(), this);
		Bukkit.getPluginManager().registerEvents(new KitsMenu(), this);
		Bukkit.getPluginManager().registerEvents(new PreviewMenu(), this);
		Bukkit.getPluginManager().registerEvents(new ChooseCooldownMenu(), this);
		ConfigurationSection sec = kitsConfig.getConfigurationSection("kits");
		for(String s : sec.getKeys(false)) {
			List<?> items = sec.getList(s + ".items");
			List<?> cmdDisplay = sec.getList(s + ".command_displays");
			ArrayList<ItemStack> itemStacks = new ArrayList<ItemStack>();
			ArrayList<String> cmds = new ArrayList<String>();
			ArrayList<ItemStack> displayCmd = new ArrayList<ItemStack>();
			Material display = Material.getMaterial(sec.getString(s + ".display_item"));
			String perm = sec.getString(s + ".permission");
			long cooldown = sec.getLong(s + ".cooldown");
			for(String st : sec.getStringList(s + ".commands")) {
				cmds.add(st);
			}
			for(Object obj : items) {
				itemStacks.add((ItemStack) obj);
			}
			for(Object obj : cmdDisplay) {
				displayCmd.add((ItemStack) obj);
			}
			HashMap<String, ItemStack> finalCmds = new HashMap<String, ItemStack>();
			for(int i = 0; i < cmds.size(); i++) {
				finalCmds.put(cmds.get(i), displayCmd.get(i));
			}
			new Kit(itemStacks, finalCmds, perm, cooldown, display, s);
		}
	}
	
	public void loadAuctions() {
		ResultSet rs = Database.getResultSet("SELECT * FROM ActiveAuctions");
		try {
			while(rs.next()) {
				OfflinePlayer seller = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("seller")));
				AuctionItem item = new AuctionItem(Serializer.deserializeItemStack(rs.getString("item")));
				int id = rs.getInt("id");
				long duration = rs.getLong("timeEnds") - System.currentTimeMillis();
				int startingPrice = rs.getInt("startingPrice");
				int currentPrice = rs.getInt("currentPrice");
				boolean sellerClaimed = rs.getBoolean("sellerClaimed");
				ResultSet rs1 = Database.getResultSet("SELECT * FROM AuctionBidders WHERE id=" + id + ";");
				ArrayList<Bidder> bidders = new ArrayList<Bidder>();
				if(rs1.next()) {
					OfflinePlayer bidder = Bukkit.getOfflinePlayer(UUID.fromString(rs1.getString("bidder")));
					bidders.add(new Bidder(bidder, rs1.getInt("amount"), rs1.getLong("timestamp"), rs1.getBoolean("claimed")));
				}
				Auction a = new Auction(id, seller, item, duration, startingPrice, currentPrice, sellerClaimed, bidders);
				a.shouldDelete();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadCooldowns() {
		ResultSet rs = Database.getResultSet("SELECT * FROM PlayerCooldowns");
		try {
			while(rs.next()) {
				String uuid = rs.getString("playerUuid");
				CooldownType type = CooldownType.valueOf(rs.getString("type"));
				long duration = (rs.getLong("shouldEnd") - System.currentTimeMillis()) / 50;
				new Cooldown(uuid, type, duration);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadOnlinePlayers() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			new SkyblockPlayer(p);
			if(Guild.getByPlayer(p) == null) {
				GuildListeners.forceCreate(p, false);
			}
		}
	}
	
	public void loadMinions() {
		ResultSet rs = Database.getResultSet("SELECT * FROM ActiveMinions");
		try {
			while(rs.next()) {
				int id = rs.getInt("id");
				String type = rs.getString("materialType");
				int level = rs.getInt("level");
				String worldName = rs.getString("world");
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				int z = rs.getInt("z");
				Minion m = Minion.createNewFromType(MaterialType.valueOf(type), level);
				ResultSet item = Database.getResultSet("SELECT * FROM MinionInventories WHERE id=" + id);
				HashMap<ItemStack, Integer> items = new HashMap<ItemStack, Integer>();
				while(item.next()) {
					int amount = item.getInt("amount");
					if(amount > 0) {
						ItemStack i = Serializer.deserializeItemStack(item.getString("item"));
						items.put(i, amount);
					}
				}
				ResultSet fuel = Database.getResultSet("SELECT * FROM MinionFuels WHERE id=" + id);
				Fuel f = null;
				if(fuel.next()) {
					int amount = fuel.getInt("amount");
					FuelType fuelType = FuelType.valueOf(fuel.getString("fuelType"));
					int timeLeft = (int) ((fuel.getLong("shouldNextEnd") - System.currentTimeMillis())/1000);
					if(timeLeft > 0) {
						f = Fuel.createNewFromType(fuelType);
						f.getItem().setAmount(amount);
						f.setTimeLeft(timeLeft);
					}
				}
				if(m.getType() == MinionType.MINER) {
					new MinerMinionBlock(m, rs.getString("playerUuid"), new Location(Bukkit.getWorld(worldName), x, y, z), items, f, rs.getLong("lastProduced"), id);
				}
				else if(m.getType() == MinionType.FARMER) {
					new FarmerMinionBlock(m, rs.getString("playerUuid"), new Location(Bukkit.getWorld(worldName), x, y, z), items, f, rs.getLong("lastProduced"), id);
				}
				else {
					new SlayerMinionBlock(m, rs.getString("playerUuid"), new Location(Bukkit.getWorld(worldName), x, y, z), items, f, rs.getLong("lastProduced"), id);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void unloadMinions() {
		for(MinionBlock b : MinionBlock.activeMinions) {
			b.unload();
		}
	}
	
	public void loadLeaderboards() {
		new Leaderboard(new Location(Bukkit.getWorld("hub"), -21, 81, -3), LeaderboardSort.POWER_LEVEL);
		new Leaderboard(new Location(Bukkit.getWorld("hub"), -26, 81, -3), LeaderboardSort.GUILD_BALANCE);
		new Leaderboard(new Location(Bukkit.getWorld("hub"), -32, 81, -3), LeaderboardSort.WARS_WON);
	}
	
	public void unloadLeaderboards() {
		for(Leaderboard l : Leaderboard.leaderboards) {
			l.destroy();
		}
	}
	
	public void loadShop() {
		ConfigurationSection sec = shopConfig.getConfigurationSection("categories");
		for(String s : sec.getKeys(false)) {
			Material mat = Material.valueOf(sec.getString(s + ".material"));
			String name = s;
			String displayName = sec.getString(s + ".name");
			String lore = sec.getString(s + ".lore");
			ShopCategory cat = new ShopCategory(mat, name, displayName, lore);
			ArrayList<Product> products = new ArrayList<Product>();
			ArrayList<String> stringProd = new ArrayList<String>();
			stringProd.addAll(shopConfig.getStringList("shop." + s + ".materials"));
			for(String prod : stringProd) {
				Material prodMat = Material.valueOf(prod.substring(0, StringUtils.ordinalIndexOf(prod, ";", 1)));
				short data = Short.parseShort(prod.substring(StringUtils.ordinalIndexOf(prod, ";", 1) + 1, StringUtils.ordinalIndexOf(prod, ";", 2)));
				double buyPrice = Double.parseDouble(prod.substring(StringUtils.ordinalIndexOf(prod, ";", 2) + 1, StringUtils.ordinalIndexOf(prod, ";", 3)));
				double sellPrice = Double.parseDouble(prod.substring(StringUtils.ordinalIndexOf(prod, ";", 3) + 1, StringUtils.ordinalIndexOf(prod, ";", 4)));
				int defaultAmount = Integer.parseInt(prod.substring(StringUtils.ordinalIndexOf(prod, ";", 4) + 1)) <= prodMat.getMaxStackSize() ? Integer.parseInt(prod.substring(StringUtils.ordinalIndexOf(prod, ";", 4) + 1)) : prodMat.getMaxStackSize();
				Product p = new Product(prodMat, data, buyPrice, sellPrice, defaultAmount, cat);
				products.add(p);
			}
			ArrayList<CommandProduct> cmdProducts = new ArrayList<CommandProduct>();
			ArrayList<String> stringCmdProd = new ArrayList<String>();
			stringCmdProd.addAll(shopConfig.getStringList("shop." + s + ".commands"));
			for(String prod : stringCmdProd) {
				Material prodMat = Material.valueOf(prod.substring(0, StringUtils.ordinalIndexOf(prod, ";", 1)));
				short data = Short.parseShort(prod.substring(StringUtils.ordinalIndexOf(prod, ";", 1) + 1, StringUtils.ordinalIndexOf(prod, ";", 2)));
				String prodName = prod.substring(StringUtils.ordinalIndexOf(prod, ";", 2) + 1, StringUtils.ordinalIndexOf(prod, ";", 3));
				double buyPrice = Double.parseDouble(prod.substring(StringUtils.ordinalIndexOf(prod, ";", 3) + 1, StringUtils.ordinalIndexOf(prod, ";", 4)));
				int defaultAmount = Integer.parseInt(prod.substring(StringUtils.ordinalIndexOf(prod, ";", 4) + 1, StringUtils.ordinalIndexOf(prod, ";", 5))) <= prodMat.getMaxStackSize() 
						? Integer.parseInt(prod.substring(StringUtils.ordinalIndexOf(prod, ";", 4) + 1, StringUtils.ordinalIndexOf(prod, ";", 5))) : prodMat.getMaxStackSize();
				String command = prod.substring(StringUtils.ordinalIndexOf(prod, ";", 5) + 1);
				CommandProduct p = new CommandProduct(new ItemStackBuilder(prodMat).name(prodName).durability(data).build(), command, buyPrice, defaultAmount, cat);
				cmdProducts.add(p);
			}
		}
	}
	
	public void setupRunnables() {
		BukkitRunnable linesUpdate = new BukkitRunnable() {
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()) {
					ScoreboardUtils.linesUpdate(p);
				}
			}
		};
		linesUpdate.runTaskTimer(this, 0, 10);
	}
	
	public void initializeCommands() {
		this.getCommand("tp").setExecutor(new TeleportCommand());
		this.getCommand("plugins").setExecutor(new PluginCommand());
		this.getCommand("message").setExecutor(new MessageCommand());
		this.getCommand("reply").setExecutor(new ReplyCommand());
	}
	
	public void initializeUtilListeners() {
		Bukkit.getPluginManager().registerEvents(new PluginCommand(), this);
		Bukkit.getPluginManager().registerEvents(new MessageListeners(), this);
	}
	
	private void setupConfigFiles() {
		if(!names.exists()) {
			names.getParentFile().mkdirs();
			saveResource("names.yml", false);
		}
		if(!shop.exists()) {
			shop.getParentFile().mkdirs();
			saveResource("shop.yml", false);
		}
		if(!kits.exists()) {
			kits.getParentFile().mkdirs();
			saveResource("kits.yml", false);
		}
		namesConfig = new YamlConfiguration();
		shopConfig = new YamlConfiguration();
		kitsConfig = new YamlConfiguration();
		try {
			namesConfig.load(names);
			shopConfig.load(shop);
			kitsConfig.load(kits);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private void setupConnections() {
		host = this.getConfig().getString("mysql.host");
		port = Integer.parseInt(this.getConfig().getString("mysql.port"));
		database = this.getConfig().getString("mysql.database");
		username = this.getConfig().getString("mysql.username");
		password = this.getConfig().getString("mysql.password");
		url = "jdbc:mysql://" + host + ":" + port + "/" + database;
		//open connections
		openConnection();
		setupTablesOnDatabase(Main.getConnection());
		keepConnectionAlive();
	}
	
	@SuppressWarnings("deprecation")
	public void keepConnectionAlive() {
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT 1");
			BukkitRunnable keepAlive = new BukkitRunnable() {
				@Override
				public void run() {
					try {
						ps.execute();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			};
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, keepAlive, 0L, 6000L);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void setupTablesOnDatabase(Connection conn) {
	    DatabaseMetaData meta;
		try {
			ArrayList<String> tables = new ArrayList<String>();
			if(conn.equals(Main.getConnection())) {
				tables.add("GuildInfo");
				tables.add("PlayerKits");
				tables.add("PlayerInfo");
				tables.add("PlayerCooldowns");
				tables.add("IslandInfo");
				tables.add("GuildTransactions");
				tables.add("GuildWars");
				tables.add("FriendlyWarItems");
				tables.add("WarItems");
				tables.add("GuildRelations");
				tables.add("GuildUnclaimed");
				tables.add("ActiveAuctions");
				tables.add("AuctionBidders");
				tables.add("ActiveMinions");
				tables.add("MinionInventories");
				tables.add("MinionFuels");
			}
			for(String s : tables) {
				meta = getConnection().getMetaData();
			    ResultSet result = meta.getTables(null, null, s, new String[] {"TABLE"});
			    if(!result.next()) {
			    	switch(s) {
			    	case "GuildInfo":
				    	PreparedStatement guildInfo = conn.prepareStatement("CREATE TABLE GuildInfo (id int(255), guildName varchar(255), guildLevel int(255), bankSilver double(255, 2), bankDwarfDust double(255, 2), open boolean, renamesRemaining int(255), islandCreated bool, PRIMARY KEY (id));");
				    	guildInfo.execute();
				    	break;
			    	case "PlayerKits":
				    	PreparedStatement playerKits = conn.prepareStatement("CREATE TABLE PlayerKits (playerUuid varchar(255), kit varchar(255), canBeClaimedAgainAt bigint(255), FOREIGN KEY (playerUuid) REFERENCES PlayerInfo(playerUuid));");
				    	playerKits.execute();
				    	break;
			    	case "PlayerInfo":
				    	PreparedStatement playerInfo = conn.prepareStatement("CREATE TABLE PlayerInfo (playerName varchar(255), playerUuid varchar(255) UNIQUE, silver double(255, 2), dwarfDust double(255, 2), guild int(255) DEFAULT '0', guildRank varchar(255), timeJoined bigint(255), messages bool);");
				    	playerInfo.execute();
				    	break;
			    	case "PlayerCooldowns":
				    	PreparedStatement cooldowns = conn.prepareStatement("CREATE TABLE PlayerCooldowns (playerUuid varchar(255), type varchar(255), shouldEnd bigint(255));");
				    	cooldowns.execute();
				    	break;
			    	case "IslandInfo":
				    	PreparedStatement islandInfo = conn.prepareStatement("CREATE TABLE IslandInfo (id int(225) UNIQUE, borderSize int(255), islandX int(255), islandY int(255), islandZ int(255), homeX double(255, 2), homeY double(255, 2), homeZ double(255, 2), visitation bool, borderColor varchar(225), FOREIGN KEY (id) REFERENCES GuildInfo(id));");
				    	islandInfo.execute();
				    	break;
			    	case "GuildRelations":
				    	PreparedStatement guildRelations = conn.prepareStatement("CREATE TABLE GuildRelations (id int(255), relatedId int(255), relation varchar(255), FOREIGN KEY (id) REFERENCES GuildInfo(id), FOREIGN KEY (relatedId) REFERENCES GuildInfo(id));");
				    	guildRelations.execute();
				    	break;
			    	case "GuildWars":
				    	PreparedStatement guildWars = conn.prepareStatement("CREATE TABLE GuildWars (warId int(255), oneId int(255), anotherId int(255), type varchar(255), winnerId int(255), timeStarted bigint(255), timeEnded bigint(255), moneyCollected int(255), topDamager varchar(255), PRIMARY KEY (warId), FOREIGN KEY (oneId) REFERENCES GuildInfo(id), FOREIGN KEY (anotherId) REFERENCES GuildInfo(id));");
				    	guildWars.execute();
				    	break;
			    	case "GuildUnclaimed":
				    	PreparedStatement unclaimed = conn.prepareStatement("CREATE TABLE GuildUnclaimed (guildId int(255), playerUuid varchar(255), item text(65535), FOREIGN KEY (guildId) REFERENCES GuildInfo(id));");
				    	unclaimed.execute();
				    	break;
			    	case "WarItems":
				    	PreparedStatement warItems = conn.prepareStatement("CREATE TABLE WarItems (warId int(255), guildId int(255), item text(65535), FOREIGN KEY (warId) REFERENCES GuildWars(warId), FOREIGN KEY (guildId) REFERENCES GuildInfo(id));");
				    	warItems.execute();
				    	break;
			    	case "FriendlyWarItems":
				    	PreparedStatement friendlyWarItems = conn.prepareStatement("CREATE TABLE FriendlyWarItems (playerUuid varchar(255), item text(65535));");
				    	friendlyWarItems.execute();
				    	break;
			    	case "GuildTransactions":
				    	PreparedStatement guildTransactions = conn.prepareStatement("CREATE TABLE GuildTransactions (id int(255), action varchar(255), amount int(255), type varchar(255), playerUuid varchar(255), timestamp bigint(255), FOREIGN KEY (id) REFERENCES GuildInfo(id));");
				    	guildTransactions.execute();
				    	break;
			    	case "ActiveAuctions":
				    	PreparedStatement auctionHouse = conn.prepareStatement("CREATE TABLE ActiveAuctions (id int(255), seller varchar(255), topBidder varchar(255), startingPrice int(255), currentPrice int(255), timeEnds bigint(255), item text(65535), sellerClaimed boolean, PRIMARY KEY (id));");
				    	auctionHouse.execute();
				    	break;
			    	case "AuctionBidders":
				    	PreparedStatement auctionBidders = conn.prepareStatement("CREATE TABLE AuctionBidders (id int(255), bidder varchar(255), amount int(255), timestamp bigint(255), claimed boolean, FOREIGN KEY (id) REFERENCES ActiveAuctions(id));");
				    	auctionBidders.execute();
				    	break;
			    	case "ActiveMinions":
				    	PreparedStatement activeMinions = conn.prepareStatement("CREATE TABLE ActiveMinions (id int(255), playerUuid varchar(255), materialType varchar(255), level int(255), x int(255), y int(255), z int(255), world varchar(255), lastProduced bigint(255), PRIMARY KEY (id));");
				    	activeMinions.execute();
				    	break;
			    	case "MinionInventories":
				    	PreparedStatement minionInventories = conn.prepareStatement("CREATE TABLE MinionInventories (id int(255), item text(65535), amount int(255), FOREIGN KEY (id) REFERENCES ActiveMinions(id));");
				    	minionInventories.execute();
				    	break;
			    	case "MinionFuels":
				    	PreparedStatement minionFuels = conn.prepareStatement("CREATE TABLE MinionFuels (id int(255), fuelType varchar(255), amount int(255), shouldNextEnd bigint(255), FOREIGN KEY (id) REFERENCES ActiveMinions(id));");
				    	minionFuels.execute();
				    	break;
			    	}
			    }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		    System.out.println("[ColonySkyblockCore] Couldn't setup the tables on the databases!");
		}
	}
	
	private void openConnection(){
		try {
		    try {
				if (connection != null && !connection.isClosed()) {
					return;
				}
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(url,username,password);
			    System.out.println("[ColonySkyblockCore] » Successfully connected to the database!");
			} catch (SQLException e) {
			    System.out.println("[ColonySkyblockCore] » Couldn't connect to the database!");
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		return connection;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public static SignGUI getSignGui() {
		return signGui;
	}
	
	private void setInstance(Main instance) {
		Main.instance = instance;
	}
	
	public FileConfiguration getNamesFileConf() {
		return namesConfig;
	}
	
	public FileConfiguration getKitsConf() {
		return kitsConfig;
	}
	
	public void saveKitsConf(FileConfiguration file) {
		try {
			kitsConfig = file;
			kitsConfig.save(kits);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
