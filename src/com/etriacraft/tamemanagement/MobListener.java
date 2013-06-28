package com.etriacraft.tamemanagement;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MobListener implements Listener {

	TameManagement plugin;

	public static HashMap<String, String> transfers = new HashMap();

	public MobListener(TameManagement instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageByEntityEvent e) {
		Entity damager = e.getDamager();
		Entity damaged = e.getEntity();

		if (damager instanceof Player) {
			if (((Tameable) damaged).isTamed()) {
				if (plugin.getConfig().getBoolean("ProtectTames") == true) {
					Player p = (Player) damager;
					AnimalTamer tameOwner = ((Tameable) damaged).getOwner();
					if (!p.getName().equals(tameOwner.getName())) {
						p.sendMessage("§cYou can't damage an animal that doesn't belong to you.");
						e.setCancelled(true);
					}
				}
			}
		}
	}
	@EventHandler
	public void PlayerInteract(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		Entity entity = e.getRightClicked();

		if (entity instanceof Tameable) {
			if (transfers.containsKey(p.getName())) {
				String newOwner = transfers.get(p.getName());
				AnimalTamer currentOwner = ((Tameable) entity).getOwner();
				if (!p.getName().equals(currentOwner.getName())) {
					p.sendMessage("§cYou do not own this animal.");
					return;
				}
				Player p2 = Bukkit.getPlayer(newOwner);
				AnimalTamer newOwner2 = p2;
				((Tameable) entity).setOwner(newOwner2);
				p.sendMessage("§aYou have transferred this animal to §3" + p2.getName() + "§a.");
				transfers.remove(p.getName());		
			}
		}
	}

}
