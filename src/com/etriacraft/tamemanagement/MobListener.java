package com.etriacraft.tamemanagement;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class MobListener implements Listener {

	TameManagement plugin;

	public static HashMap<String, String> transfers = new HashMap();
	public static HashMap<String, String> releases = new HashMap();

	public MobListener(TameManagement instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageByEntityEvent e) {
		Entity damager = e.getDamager();
		Entity damaged = e.getEntity();

		if (damager instanceof Player) {
			if (((Tameable) damaged).isTamed()) {
				Player p = (Player) damager;
				AnimalTamer tameOwner = ((Tameable) damaged).getOwner();
				if (plugin.getConfig().getBoolean("ProtectTames") == true) {
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
			AnimalTamer currentOwner = ((Tameable) entity).getOwner();
			if (releases.containsKey(p.getName())) {
				if (!p.getName().equals(currentOwner.getName())) {
					p.sendMessage("§cYou do not own this animal.");
					return;
				}
				((Tameable) entity).setOwner(null);
				
				if (entity instanceof Wolf) {
					Wolf wolf = (Wolf) entity;
					if (wolf.isSitting()) {
						wolf.setSitting(false);
					}
				}
				if (entity instanceof Ocelot) {
					Ocelot ocelot = (Ocelot) entity;
					if (ocelot.isSitting()) {
						ocelot.setSitting(false);
					}
					ocelot.setCatType(Type.WILD_OCELOT);
				}
				p.sendMessage("§aYou have released this animal to the wild.");
				releases.remove(p.getName());
			}
			if (transfers.containsKey(p.getName())) {
				String newOwner = transfers.get(p.getName());
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
