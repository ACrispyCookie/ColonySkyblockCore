package net.colonymc.colonyskyblockcore.kits;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import net.colonymc.colonyskyblockcore.Main;

public class KitCreator implements Listener {
	
	Player p;
	String name;
	int stage = 0;
	Material displayItem;
	long cooldown;
	String permission;
	String tempCmd = "";
	ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	HashMap<String, ItemStack> cmds = new HashMap<String, ItemStack>();
	static ArrayList<KitCreator> kitCreators = new ArrayList<KitCreator>();

	public KitCreator(Player p, String name) {
		this.p = p;
		this.name = name;
		kitCreators.add(this);
		stage(false);
	}
	
	public KitCreator() {
		
	}
	
	public void stage(boolean next) {
		if(next) {
			stage++;
		}
		switch(stage) {
		case 0:
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fRight-click the item which you want it to be the display item of the kit!"));
			break;
		case 1:
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fPlease select the cooldown of the kit in the menu!"));
			new ChooseCooldownMenu(p);
			break;
		case 2:
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fPlease enter the permission string you want the kit to have!"));
			break;
		case 3:
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fMake your inventory a preview of the kit (Exact items) and type '&aready&f' when you are ready"));
			break;
		case 4:
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fPlease enter a command to be executed when claiming the kit! (Or type '&adone&f' to complete the kit)"));
			break;
		default:
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 2, 1);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fPlease enter another command or type '&adone&f' if you want to complete the kit!"));
			break;
		}
	}
	
	public void addItem(ItemStack item) {
		this.items.add(item);
	}
	
	public void addCmd(String cmd, ItemStack item) {
		this.cmds.put(cmd, item);
	}
	
	public void setCooldown(long seconds) {
		cooldown = seconds;
	}
	
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	public void setDisplay(Material display) {
		displayItem = display;
	}
	
	public void submit() {
		new Kit(items, cmds, permission, cooldown, displayItem, name);
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.addAll(cmds.keySet());
		ArrayList<ItemStack> display = new ArrayList<ItemStack>();
		display.addAll(cmds.values());
		FileConfiguration file = Main.getInstance().getKitsConf();
		file.set("kits." + name + ".display_item", displayItem.name());
		file.set("kits." + name + ".permission", permission);
		file.set("kits." + name + ".cooldown", cooldown);
		file.set("kits." + name + ".items", items);
		file.set("kits." + name + ".commands", cmd);
		file.set("kits." + name + ".command_displays", display);
		Main.getInstance().saveKitsConf(file);
	}
	
	public static KitCreator getByPlayer(Player p) {
		for(KitCreator k : kitCreators) {
			if(k.p.equals(p)) {
				return k;
			}
		}
		return null;
	}
	
	@EventHandler
	public void onKick(PlayerQuitEvent e) {
		if(KitCreator.getByPlayer(e.getPlayer()) != null) {
			KitCreator.kitCreators.remove(KitCreator.getByPlayer(e.getPlayer()));
		}
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e) {
		if(KitCreator.getByPlayer(e.getPlayer()) != null) {
			KitCreator.kitCreators.remove(KitCreator.getByPlayer(e.getPlayer()));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e) {
		if(KitCreator.getByPlayer(e.getPlayer()) != null) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(KitCreator.getByPlayer(e.getPlayer()).stage == 0) {
					e.setCancelled(true);
					Material mat = e.getPlayer().getItemInHand().getType();
					e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					KitCreator.getByPlayer(e.getPlayer()).setDisplay(mat);
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fYou set the display item of the kit to &d" + mat.name() + "&f!"));
					KitCreator.getByPlayer(e.getPlayer()).stage(true);
					
				}
				else if(!KitCreator.getByPlayer(e.getPlayer()).tempCmd.equals("") && KitCreator.getByPlayer(e.getPlayer()).stage >= 4) {
					KitCreator kit = KitCreator.getByPlayer(e.getPlayer());
					e.setCancelled(true);
					ItemStack item = e.getPlayer().getItemInHand().clone();
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ORB_PICKUP, 2, 1);
					kit.addCmd(kit.tempCmd, item);
					kit.tempCmd = "";
					e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fYou set the display item of the command &d" + kit.tempCmd + " &fto " + item.getItemMeta().getDisplayName() + "&f!"));
					KitCreator.getByPlayer(e.getPlayer()).stage(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(KitCreator.getByPlayer(e.getPlayer()) != null) {
			e.setCancelled(true);
			KitCreator kit = KitCreator.getByPlayer(e.getPlayer());
			if(kit.stage == 2) {
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fYou set the permission string of the kit to &d" + e.getMessage() + "&f!"));
				kit.setPermission(e.getMessage());
				kit.stage(true);
			}
			else if(kit.stage == 3) {
				if(e.getMessage().equals("ready")) {
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fYou set the items of the kit to the items of your inventory!"));
					for(ItemStack item : e.getPlayer().getInventory().getContents()) {
						if(item != null) {
							kit.addItem(item.clone());
						}
					}
					kit.stage(true);
				}
				else {
					kit.stage(false);
				}
			}
			else if(kit.stage >= 4) {
				if(e.getMessage().equals("done") && kit.tempCmd.equals("")) {
					kit.submit();
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fThe kit &d" + kit.name + " &fhas been successfully created!"));
				}
				else if(kit.tempCmd.equals("")) {
					kit.tempCmd = e.getMessage();
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fYou added the command &d'" + e.getMessage() + "' &fto the kit!"));
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ORB_PICKUP, 2, 1);
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fRight click an item to set it as the display item of the command!"));
				}
				else {
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ORB_PICKUP, 2, 1);
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&l[KIT CREATION] &fRight click an item to set it as the display item of the command!"));
				}
			}
			else {
				kit.stage(false);
			}
		}
	}
	
}
