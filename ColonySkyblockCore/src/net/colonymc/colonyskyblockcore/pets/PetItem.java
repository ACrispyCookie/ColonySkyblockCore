package net.colonymc.colonyskyblockcore.pets;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.colonymc.api.itemstacks.NBTItems;
import net.colonymc.api.itemstacks.SkullItemBuilder;
import net.minecraft.server.v1_8_R3.NBTTagString;

public class PetItem implements Listener {
	
	PetType type;
	ItemStack item;
	Player p;
	
	public PetItem(PetType type, Player p) {
		this.type = type;
		this.p = p;
		this.item = getNormalItemStack();
	}
	
	public PetItem() {
		
	}

	private ItemStack getNormalItemStack() {
		return new SkullItemBuilder()
				.url("http://textures.minecraft.net/texture/" + type.url)
				.name(ChatColor.translateAlternateColorCodes('&', type.name + " Pet"))
				.lore(ChatColor.translateAlternateColorCodes('&', "\n" + type.description + "\n \n&dRight-click to activate it"))
				.addTag("skyblockPet", new NBTTagString(type.name()))
				.build();
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public PetType getType() {
		return type;
	}
	
	public ItemStack getItem() {
		return item.clone();
	}
	
	public static boolean isPet(ItemStack item) {
		if(item.hasItemMeta()) {
			if(NBTItems.hasTag(item, "skyblockPet")) {
				return true;
			}
		}
		return false;
	}
	
	public static PetType whatType(ItemStack item) {
		if(isPet(item)) {
			return PetType.valueOf(NBTItems.getString(item, "skyblockPet"));
		}
		return null;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction() == Action.PHYSICAL || e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(p.getItemInHand() != null && PetItem.isPet(p.getItemInHand())) {
				e.setCancelled(true);
				if(!Pet.hasActivePet(p)) {
					new Pet(PetItem.whatType(p.getItemInHand()), p);
					ItemStack item = p.getItemInHand().clone();
					item.setAmount(item.getAmount() - 1);
					p.setItemInHand(item);
				}
				else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &5&l» &cYou already have an active pet!"));
				}
			}
		}
	}

}
