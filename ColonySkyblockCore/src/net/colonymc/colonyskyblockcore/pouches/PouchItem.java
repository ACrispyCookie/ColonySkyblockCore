package net.colonymc.colonyskyblockcore.pouches;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.colonymc.api.itemstacks.ItemStackBuilder;
import net.colonymc.api.itemstacks.NBTItems;
import net.colonymc.api.primitive.RomanNumber;
import net.colonymc.colonyskyblockcore.guilds.Guild;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagString;

public class PouchItem implements Listener {	
	
	PouchType type;
	int level;
	ItemStack item;
	Player p;
	
	public PouchItem(PouchType type, Player p, int level) {
		this.type = type;
		this.p = p;
		this.level = level;
		this.item = getNormalItemStack();
	}
	
	public PouchItem() {
		
	}

	private ItemStack getNormalItemStack() {
		return new ItemStackBuilder(Material.ENDER_CHEST)
				.name(ChatColor.translateAlternateColorCodes('&', type.pouchType + " Pouch &7(&d" + RomanNumber.toRoman(level) + "&7)"))
				.lore(ChatColor.translateAlternateColorCodes('&', "\n" + ("&fWin an amount between " + ChatColor.getByChar(type.color) + Guild.formattedBalance(Pouch.getLevelMap(type).get(level).getMinimum()) 
				+ " - " + Guild.formattedBalance(Pouch.getLevelMap(type).get(level).getMaximum())) 
				+ "\n \n&dRight-click to activate it"))
				.addTag("skyblockPouch", new NBTTagString(type.name()))
				.addTag("pouchLevel", new NBTTagInt(level))
				.build();
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public PouchType getType() {
		return type;
	}
	
	public ItemStack getItem() {
		return item.clone();
	}
	
	public static boolean isPouch(ItemStack item) {
		if(item.hasItemMeta()) {
			if(NBTItems.hasTag(item, "skyblockPouch")) {
				return true;
			}
		}
		return false;
	}
	
	public static PouchType whatType(ItemStack item) {
		if(isPouch(item)) {
			return PouchType.valueOf(NBTItems.getString(item, "skyblockPouch"));
		}
		return null;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction() == Action.PHYSICAL || e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(p.getItemInHand() != null && PouchItem.isPouch(p.getItemInHand())) {
				e.setCancelled(true);
				if(Pouch.getByPlayer(p) == null) {
					Pouch pouch = new Pouch(p, PouchItem.whatType(p.getItemInHand()), NBTItems.getInt(p.getItemInHand(), "pouchLevel"));
					ItemStack item = p.getItemInHand().clone();
					item.setAmount(item.getAmount() - 1);
					p.setItemInHand(item);
					pouch.activate();
				}
			}
		}
	}
	
}
