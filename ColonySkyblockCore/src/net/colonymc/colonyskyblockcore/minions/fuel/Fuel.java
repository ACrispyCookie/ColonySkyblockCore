package net.colonymc.colonyskyblockcore.minions.fuel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.api.Main;
import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.api.itemstacks.NBTItems;
import net.colonymc.colonyskyblockcore.Database;
import net.colonymc.colonyskyblockcore.minions.MinionBlock;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagString;

public abstract class Fuel {
	
	int percentage;
	int duration;
	int timeLeft;
	ItemStack i;
	String name;
	MinionBlock block;
	FuelType t;
	BukkitTask expire;
	protected abstract ItemStack createItem();
	
	public Fuel(int percentage, int duration, FuelType t, MinionBlock b, ItemStack i, int durationLeft) {
		this.percentage = percentage;
		this.duration = duration;
		this.t = t;
		if(i == null) {
			this.i = createItem();
		}
		else {
			this.i = i;
			this.name = i.getItemMeta().getDisplayName();
		}
		if(durationLeft != -1) {
			timeLeft = durationLeft * 20;
		}
		else {
			timeLeft = duration * 20;
		}
		this.block = b;
		if(block != null) {
			b.setFuel(this);
			expire = new BukkitRunnable() {
				@Override
				public void run() {
					if(timeLeft == 0) {
						expire();
					}
					timeLeft--;
				}
			}.runTaskTimer(Main.getInstance(), 0, 1);
		}
	}
	
	public void expire() {
		i.setAmount(i.getAmount() - 1);
		if(i.getAmount() == 0) {
			block.setFuel(null);
			expire.cancel();
		}
		else {
			timeLeft = duration * 20;
			Database.sendStatement("UPDATE MinionFuels SET amount=" + i.getAmount() + ",shouldNextEnd=" + (System.currentTimeMillis() + duration * 1000) + " WHERE id=" + block.getId() + ";");
		}
	}
	
	public void setBlock(MinionBlock b) {
		if(b != null) {
			block = b;
			block.setFuel(this);
			expire = new BukkitRunnable() {
				@Override
				public void run() {
					timeLeft--;
					if(timeLeft == 0) {
						expire();
					}
				}
			}.runTaskTimer(Main.getInstance(), 0, 1);
			Database.sendStatement("INSERT INTO MinionFuels (id, fuelType, amount, shouldNextEnd) VALUES (" + block.getId() + ", '" + t.name() + "', " + i.getAmount() + ", " + (System.currentTimeMillis() + duration * 1000) + ")");
		}
		else {
			block.setFuel(null);
			expire.cancel();
			Database.sendStatement("DELETE FROM MinionFuels WHERE id=" + block.getId() + ";");
			
		}
	}
	
	public void setTimeLeft(int amount) {
		timeLeft = amount;
	}
	
	public ItemStack getItem() {
		return i;
	}
	
	public ArrayList<ItemStack> getItemsToDrop() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		ItemStack item = i.clone();
		if(item.getAmount() > 1) {
			ItemStack toAdd = item.clone();
			toAdd.setAmount(toAdd.getAmount() - 1);
			items.add(toAdd);
		}
		item.setAmount(1);
		item = NBTItems.addTag(item, "fuelDurationLeft", new NBTTagInt(getTimeLeft()));
		items.add(item);
		return items;
	}
	
	public String getName() {
		return name;
	}
	
	public FuelType getType() {
		return t;
	}
	
	public int getPercentage() {
		return percentage;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public int getTimeLeft() {
		return timeLeft/20;
	}
	
	public MinionBlock getBlock() {
		return block;
	}
	
	public ItemStack getNormalItemStack(String name, String description) {
		ItemStack item = new ItemStackBuilder(t.mat).name(name).lore("\n" + description).glint(true).addTag("minionFuel", new NBTTagString(t.name())).build();
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(org.bukkit.enchantments.Enchantment.ARROW_DAMAGE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		this.name = item.getItemMeta().getDisplayName();
		return item;
	}
	
	public static Fuel createNewFromType(FuelType type, MinionBlock b) {
		try {
			Class<?> cl = Class.forName("net.colonymc.colonyskyblockcore.minions.fuel.types." + type.className);
			Constructor<?> con = cl.getDeclaredConstructors()[0];
			Fuel item = (Fuel) con.newInstance(b, null, -1);
			return item;
		} catch (ClassNotFoundException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
