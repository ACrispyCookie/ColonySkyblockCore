package net.colonymc.colonyskyblockcore.minions.fuel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import net.colonymc.colonyspigotapi.api.itemstack.ItemStackBuilder;
import net.colonymc.colonyspigotapi.api.itemstack.ItemStackNBT;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagString;

public abstract class Fuel {
	
	final int percentage;
	final int duration;
	int timeLeft;
	final ItemStack i;
	String name;
	final FuelType t;
	BukkitTask expire;
	protected abstract ItemStack createItem();
	
	public Fuel(int percentage, int duration, FuelType t, ItemStack i, int durationLeft) {
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
			timeLeft = durationLeft;
		}
		else {
			timeLeft = duration;
		}
	}
	
	public void decreaseItem() {
		i.setAmount(i.getAmount() - 1);
	}
	
	public void setTimeLeft(int amount) {
		timeLeft = amount;
	}
	
	public ItemStack getItem() {
		return i;
	}
	
	public ArrayList<ItemStack> getItemsToDrop() {
		ArrayList<ItemStack> items = new ArrayList<>();
		ItemStack item = i.clone();
		if(item.getAmount() > 1) {
			ItemStack toAdd = item.clone();
			toAdd.setAmount(toAdd.getAmount() - 1);
			items.add(toAdd);
		}
		item.setAmount(1);
		item = ItemStackNBT.addTag(item, "fuelDurationLeft", new NBTTagInt(getTimeLeft()));
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
		return timeLeft;
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
	
	public static Fuel createNewFromType(FuelType type) {
		try {
			Class<?> cl = Class.forName("net.colonymc.colonyskyblockcore.minions.fuel.types." + type.className);
			Constructor<?> con = cl.getDeclaredConstructors()[0];
			return (Fuel) con.newInstance(null, -1);
		} catch (ClassNotFoundException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
