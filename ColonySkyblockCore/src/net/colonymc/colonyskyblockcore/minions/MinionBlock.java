package net.colonymc.colonyskyblockcore.minions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.api.Main;
import net.colonymc.api.itemstacks.Serializer;
import net.colonymc.api.itemstacks.SkullItemBuilder;
import net.colonymc.api.player.PlayerInventory;
import net.colonymc.api.primitive.RomanNumber;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.minions.fuel.Fuel;

public abstract class MinionBlock implements Listener {
	
	Minion m;
	Fuel f;
	int id;
	int locX;
	int locZ;
	int finalDuration;
	long lastProduced;
	boolean isLoaded;
	String playerUuid;
	Location loc;
	BukkitTask task;
	ArmorStand as;
	HashMap<ItemStack, Integer> items = new HashMap<ItemStack, Integer>();
	ArrayList<Block> validBlocks = new ArrayList<Block>();
	public static ArrayList<MinionBlock> activeMinions = new ArrayList<MinionBlock>();
	protected abstract boolean doTask(); //returns if should add items
	protected abstract void playAnimation();
	protected abstract ItemStack getItemInHand();
	protected abstract boolean isInRightArea();
	
	
	public MinionBlock(Minion m, String playerUuid, Location loc) {
		this.m = m;
		this.playerUuid = playerUuid;
		this.loc = loc;
		this.locX = loc.getBlockX();
		this.locZ = loc.getBlockZ();
		this.finalDuration = m.getDuration() * 20;
		activeMinions.add(this);
	}
	
	public MinionBlock(Minion m, String playerUuid, Location loc, HashMap<ItemStack, Integer> items, long lastProduce, int id) {
		this.m = m;
		this.playerUuid = playerUuid;
		this.loc = loc;
		this.locX = loc.getBlockX();
		this.locZ = loc.getBlockZ();
		this.lastProduced = lastProduce;
		this.items = items;
		this.finalDuration = m.getDuration() * 20;
		this.id = id;
		this.isLoaded = loc.getWorld().isChunkLoaded(locX/16, locZ/16);
		if(this.isLoaded) {
			load();
		}
		else {
			
		}
		activeMinions.add(this);
	}
	
	public MinionBlock() {
		
	}
	
	public void place() {
		id = getNewID();
		Database.sendStatement("INSERT INTO ActiveMinions (id, playerUuid, materialType, level, x, y, z, world, lastProduced) VALUES "
				+ "(" + id + ", '" + playerUuid + "', '" + m.getMaterial().name() + "', " + m.getLevel() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ", '" + loc.getWorld().getName() + "', 0)");
		load();
	}
	
	public void breakMinion(Player p) {
		if(isLoaded) {
			unload();
		}
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l� &fYou have broken a &d" + m.getMaterial().className + " Minion (" + RomanNumber.toRoman(m.getLevel()) + ")&f!"));
		loc.getWorld().dropItem(loc, m.getItemStack());
		for(ItemStack i : items.keySet()) {
			int stacksToGive = (int) Math.floor(this.items.get(i)/64);
			int leftToGive = this.items.get(i) - stacksToGive * 64;
			for(int c = 0; c < stacksToGive; c++) {
				ItemStack toGive = i.clone();
				toGive.setAmount(64);
				loc.getWorld().dropItem(loc, toGive);
			}
			if(leftToGive > 0) {
				ItemStack toGive = i.clone();
				toGive.setAmount(leftToGive);
				loc.getWorld().dropItem(loc, toGive);
			}
		}
		if(f != null && f.getTimeLeft() > 1) {
			for(ItemStack i : f.getItemsToDrop()) {
				loc.getWorld().dropItem(loc, i);
			}
		}
		activeMinions.remove(activeMinions.indexOf(this));
		Database.sendStatement("DELETE FROM MinionFuels WHERE id=" + id + ";");
		Database.sendStatement("DELETE FROM MinionInventories WHERE id=" + id + ";");
		Database.sendStatement("DELETE FROM ActiveMinions WHERE id=" + id + ";");
	}
	
	public void load() {
		as = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0.5, 1, 0.5), EntityType.ARMOR_STAND);
		as.setSmall(true);
		as.setBasePlate(false);
		as.setArms(true);
		as.setCustomNameVisible(false);
		as.setGravity(false);
		addEquipment();
		calculateItems();
		startTask();
		isLoaded = true;
		lastProduced = 0;
		Database.sendStatement("UPDATE ActiveMinions SET lastProduced=" + lastProduced + " WHERE id=" + id + ";");
	}
	
	public void unload() {
		as.remove();
		stopTask();
		isLoaded = false;
		lastProduced = System.currentTimeMillis();
		Database.sendStatement("UPDATE ActiveMinions SET lastProduced=" + lastProduced + " WHERE id=" + id + ";");
	}
	
	public void startTask() {
		task = new BukkitRunnable() {
			int lastDuration = -1;
			@Override
			public void run() {
				if(lastDuration == -1) {
					lastDuration = finalDuration;
				}
				else {
					if(lastDuration != finalDuration) {
						restartTask();
					}
					else {
						checkValidBlocks();
						if(doTask()) {
							addItems(1);
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), finalDuration, finalDuration);
	}
	
	public void stopTask() {
		task.cancel();
	}
	
	public void restartTask() {
		if(isLoaded) {
			stopTask();
			startTask();
		}
	}
	
	protected void addItems(int times) {
		ArrayList<ItemStack> newItems = new ArrayList<ItemStack>();
		HashMap<ItemStack, Integer> itemsToAdd = getRandomLoot(times);
		for(ItemStack i : itemsToAdd.keySet()) {
			int amount = 0;
			for(ItemStack itemCounter : this.items.keySet()) {
				amount = amount + this.items.get(itemCounter);
			}
			boolean containsIt = false;
			boolean fitsWholeStack = false;
			ItemStack similar = null;
			for(ItemStack item : this.items.keySet()) {
				if(item.isSimilar(i)) {
					similar = item;
					containsIt = true;
					break;
				}
			}
			if(amount + itemsToAdd.get(i) <= m.getSlots() * 64) {
				fitsWholeStack = true;
			}
			if(fitsWholeStack) {
				if(!containsIt){
					this.items.put(i, itemsToAdd.get(i));
					newItems.add(i);
				}
				else {
					this.items.put(i, this.items.get(similar) + itemsToAdd.get(i));
				}
			}
			else {
				this.items.put(i, m.getSlots() * 64 - amount);
			}
		}
		for(ItemStack i : this.items.keySet()) {
			if(newItems.contains(i)) {
				Database.sendStatement("INSERT INTO MinionInventories (id, item, amount) VALUES (" + id + ", '" + Serializer.serializeItemStack(i) + "', " + this.items.get(i) + ");");
			}
			else {
				Database.sendStatement("UPDATE MinionInventories SET amount=" + this.items.get(i) + " WHERE id=" + id + " AND item='" + Serializer.serializeItemStack(i) + "';");
			}
		}
	}
	
	public void removeItem(ItemStack i, int amount, Player p) {
		ItemStack toRemove = i.clone();
		toRemove.setAmount(1);
		ItemStack toRemoveAfter = null;
		for(ItemStack item : items.keySet()) {
			if(toRemove.isSimilar(item)) {
				if(amount > items.get(item)) {
					amount = items.get(item);
				}
				int stacksToGive = (int) Math.floor((double) amount/64);
				int leftToGive = amount - stacksToGive * 64;
				ArrayList<ItemStack> toDropList = new ArrayList<ItemStack>();
				for(int c = 0; c < stacksToGive; c++) {
					ItemStack toDrop = item.clone();
					toDrop.setAmount(64);
					toDropList.add(toDrop);
				}
				if(leftToGive > 0) {
					ItemStack toDrop = item.clone();
					toDrop.setAmount(leftToGive);
					toDropList.add(toDrop);
				}
				PlayerInventory.addItems(toDropList, p);
				if(amount == items.get(item)) {
					toRemoveAfter = item;
					Database.sendStatement("DELETE FROM MinionInventories WHERE id=" + id + " AND item='" + Serializer.serializeItemStack(item) + "';");
				}
				else {
					items.put(item, items.get(item) - amount);
					Database.sendStatement("UPDATE MinionInventories SET amount=" + (items.get(item) - amount) + " WHERE id=" + id + " AND item='" + Serializer.serializeItemStack(item) + "';");
				}
			}
		}
		if(toRemoveAfter != null) {
			items.remove(toRemoveAfter);
		}
	}
	
	protected void calculateItems() {
		long diff = System.currentTimeMillis() - lastProduced;
		int times = (int) ((diff/1000)/(finalDuration/20));
		addItems(times);
	}
	
	protected HashMap<ItemStack, Integer> getRandomLoot(int times) {
		HashMap<ItemStack, Integer> items = new HashMap<ItemStack, Integer>();
		for(int c = 0; c < times; c++) {
			for(ItemStack i : m.getLoot().keySet()) {
				int randomAmount = m.getLoot().get(i).getRandomAmount();
				if(randomAmount > 0) {
					items.put(i.clone(), randomAmount);
				}
			}
		}
		return items;
	}

	protected void addEquipment() {
		ItemStack i = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
		meta.setColor(m.getColor());
		i.setItemMeta(meta);
		as.setChestplate(i);
		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) i.getItemMeta();
		leggingsMeta.setColor(m.getColor());
		leggings.setItemMeta(leggingsMeta);
		as.setLeggings(leggings);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta bootsMeta = (LeatherArmorMeta) i.getItemMeta();
		bootsMeta.setColor(m.getColor());
		boots.setItemMeta(bootsMeta);
		as.setBoots(boots);
		as.setHelmet(new SkullItemBuilder().url("http://textures.minecraft.net/texture/" + m.getSkinUrl()).build());
		as.setItemInHand(getItemInHand());
	}
	
	protected int getNewID() {
		try {
			ResultSet rs = Database.getResultSet("SELECT * FROM ActiveMinions ORDER BY id ASC;");
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
	
	public void checkValidBlocks() {
		if(isInRightArea()) {
			validBlocks.clear();
			for(int z = 2; z > -3; z--) {
				for(int x = 2; x > -3; x--) {
					if(x == 0 && z == 0) {
						continue;
					}
					validBlocks.add(loc.clone().add(x, 0, z).getBlock());
				}
			}
		}
	}
	
	public void setFuel(Fuel f) {
		this.f = f;
		if(this.f != null) {
			double percentage = (double) f.getPercentage()/100;
			int durationInTicks = m.getDuration() * 20;
			finalDuration = durationInTicks - (int) (percentage * durationInTicks);
		}
		else {
			finalDuration = m.getDuration() * 20;
		}
	}
	
	public Minion getMinion() {
		return m;
	}
	
	public Location getLocation() {
		return loc.clone();
	}
	
	public int getId() {
		return id;
	}
	
	public Fuel getFuel() {
		return f;
	}
	 
	public HashMap<ItemStack, Integer> getItems() {
		return items;
	}
	
	public boolean isFull() {
		int amount = 0;
		for(ItemStack i : items.keySet()) {
			amount = items.get(i) + amount;
		}
		return amount == m.getSlots() * 64;
	}
	
	public long getLastProduced() {
		return lastProduced;
	}
	
	public static MinionBlock getByArmorStand(ArmorStand as) {
		for(MinionBlock b : activeMinions) {
			if(b.as.equals(as)) {
				return b;
			}
		}
		return null;
	}
	
	public static MinionBlock getByLocation(Location loc) {
		for(MinionBlock b : activeMinions) {
			if(b.loc.getX() == loc.getX() && b.loc.getY() == loc.getY() && b.loc.getZ() == loc.getZ()) {
				return b;
			}
		}
		return null;
	}
	
	public static ArrayList<MinionBlock> getByChunk(Chunk chunk) {
		ArrayList<MinionBlock> block = new ArrayList<MinionBlock>();
		for(MinionBlock b : activeMinions) {
			if(chunk.getWorld().equals(b.loc.clone().getWorld()) && chunk.getX() == b.locX/16 && chunk.getZ() == b.locZ/16) {
				block.add(b);
			}
		}
		return block;
	}
	
	public static ArrayList<MinionBlock> getByPlayer(Player p) {
		ArrayList<MinionBlock> block = new ArrayList<MinionBlock>();
		for(MinionBlock b : activeMinions) {
			if(b.playerUuid.equals(p.getUniqueId().toString())) {
				block.add(b);
			}
		}
		return block;
	}
	
	public static MinionBlock getByBlock(Block b) {
		for(MinionBlock bl : activeMinions) {
			if(bl.validBlocks.contains(b)) {
				return bl;
			}
		}
		return null;
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		if(e.getEntity() instanceof LivingEntity) {
			LivingEntity en = (LivingEntity) e.getEntity();
			EntityDamageEvent event = en.getLastDamageCause();
			if(event instanceof EntityDamageByEntityEvent) {
				if(((EntityDamageByEntityEvent) event).getDamager() instanceof ArmorStand && ((EntityDamageByEntityEvent) event).getFinalDamage() > en.getHealth()) {
					ArmorStand as = (ArmorStand) ((EntityDamageByEntityEvent) event).getDamager();
					MinionBlock b = MinionBlock.getByArmorStand(as);
					if(b != null) {
						b.addItems(1);
						e.getDrops().clear();
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player && e.getEntity() instanceof ArmorStand) {
			ArmorStand as = (ArmorStand) e.getEntity();
			if(MinionBlock.getByArmorStand(as) != null) {
				e.setCancelled(true);
				MinionBlock b = MinionBlock.getByArmorStand(as);
				new MinionMenu((Player) e.getDamager(), b);
			}
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractAtEntityEvent e) {
		if(e.getRightClicked() instanceof ArmorStand) {
			ArmorStand as = (ArmorStand) e.getRightClicked();
			if(MinionBlock.getByArmorStand(as) != null) {
				e.setCancelled(true);
				MinionBlock b = MinionBlock.getByArmorStand(as);
				new MinionMenu(e.getPlayer(), b);
			}
		}
	}
	
	@EventHandler
	public void onClick(EntityDamageEvent e) {
		if(e.getEntity() instanceof ArmorStand) {
			ArmorStand as = (ArmorStand) e.getEntity();
			if(MinionBlock.getByArmorStand(as) != null) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(MinionChecker.isMinion(e.getItemInHand())) {
			e.setCancelled(true);
			if(MinionBlock.getByLocation(e.getBlock().getLocation().subtract(0, 1, 0)) == null) {
				Minion m = MinionChecker.getMinion(e.getItemInHand());
				if(m.getType() == MinionType.MINER) {
					new MinerMinionBlock(m, e.getPlayer().getUniqueId().toString(), e.getBlock().getLocation().subtract(0, 1, 0));
				}
				else if(m.getType() == MinionType.FARMER) {
					new FarmerMinionBlock(m, e.getPlayer().getUniqueId().toString(), e.getBlock().getLocation().subtract(0, 1, 0));
				}
				else if(m.getType() == MinionType.SLAYER) {
					new SlayerMinionBlock(m, e.getPlayer().getUniqueId().toString(), e.getBlock().getLocation().subtract(0, 1, 0));
				}
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l� &fYou just placed a &d" + m.getName() + "&f!"));
				ItemStack i = p.getItemInHand();
				i.setAmount(i.getAmount() - 1);
				p.setItemInHand(i);
			}
			else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l� &cThere is already a minion on this block!"));
			}
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		if(!MinionBlock.getByChunk(e.getChunk()).isEmpty()) {
			for(MinionBlock b : MinionBlock.getByChunk(e.getChunk())) {
				b.load();
			}
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkUnloadEvent e) {
		if(!MinionBlock.getByChunk(e.getChunk()).isEmpty()) {
			for(MinionBlock b : MinionBlock.getByChunk(e.getChunk())) {
				b.unload();
			}
		}
	}
	
	@EventHandler
	public void onFade(BlockFadeEvent e) {
		if(MinionBlock.getByBlock(e.getBlock()) != null && MinionBlock.getByBlock(e.getBlock()).getMinion().getType() == MinionType.FARMER) {
			e.setCancelled(true);
		}
	}

}
