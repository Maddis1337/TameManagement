package com.etriacraft.tamemanagement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MobListener implements Listener {

	TameManagement plugin;

	public static HashMap<String, String> transfers = new HashMap<String, String>();
	public static HashMap<String, String> releases = new HashMap<String, String>();
	public static HashMap<String, Horse.Style> horsestyles = new HashMap<String, Horse.Style>();
	public static HashMap<String, Horse.Color> horsecolors = new HashMap<String, Horse.Color>(); 
	public static HashMap<String, Horse.Variant> horsevariants = new HashMap<String, Variant>();
	public static Set<String> horseclaims = new HashSet<String>();
	public static Set<String> getInfo = new HashSet<String>();

	public MobListener(TameManagement instance) {
		this.plugin = instance;
	}
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		if (e.getSpawnReason() == SpawnReason.BREEDING) {
			Entity entity = e.getEntity();
			if (entity instanceof Wolf) {
				if (!plugin.getConfig().getBoolean("Breeding.Wolf")) {
					e.setCancelled(true);
				}
			}
			if (entity instanceof Ocelot) {
				if (!plugin.getConfig().getBoolean("Breeding.Ocelot")) {
					e.setCancelled(true);
				}
			}
			if (entity instanceof Horse) {
				if (!plugin.getConfig().getBoolean("Breeding.Horse")) {
					e.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void EntityDamageEvent(EntityDamageByEntityEvent e) {
		Entity damager = e.getDamager();
		Entity damaged = e.getEntity();

		if (damager instanceof Player) {
			Player p = (Player) damager;
			if (damaged instanceof Tameable) {
				if (((Tameable) damaged).isTamed()) {
					AnimalTamer tameOwner = ((Tameable) damaged).getOwner();
					if (plugin.getConfig().getBoolean("ProtectTames") == true) {
						if (!p.getName().equals(tameOwner.getName())) {
							plugin.sendMessage(p,"listener.animalDoesNotBelongToYou");
							e.setCancelled(true);
						}
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
			if (entity instanceof Horse) {
				Horse horse = (Horse) entity;
				if (horseclaims.contains(p.getName())) {
					if(horse.getOwner() == null) {
						horse.setOwner(p);
						horseclaims.remove(p.getName());
						e.setCancelled(true);
					} else {
						plugin.sendMessage(p,"listener.horseAlreadyOwned");
						horseclaims.remove(p.getName());
						e.setCancelled(true);
					}
				}
				// Runs this code on the /tame horse setstyle command.
				if (plugin.getConfig().getBoolean("ProtectHorses")) {
					if (horse.isTamed()) {
						if (currentOwner == null) {
//							e.setCancelled(false);
						}
						if (!currentOwner.getName().equals(p.getName()) && !p.hasPermission("tamemanagement.protecthorses.override")) {
							plugin.sendMessage(p,"listener.cantInteractWithHorse");
							e.setCancelled(true);
						}
					}
				}
				
				if (horsestyles.containsKey(p.getName())) {
					if (horse.isTamed()) {
						if (!currentOwner.getName().equals(p.getName())) {
							plugin.sendMessage(p,"listener.cantChangeStyle");
							horsestyles.remove(p.getName());
							e.setCancelled(true);
						} else {
							horse.setStyle(horsestyles.get(p.getName()));
							plugin.sendMessage(p,"listener.styleChanged");
							e.setCancelled(true);
							horsestyles.remove(p.getName());
						}
					}
				}
				if (horsecolors.containsKey(p.getName())) {
					if (horse.isTamed()) {
						if (!currentOwner.getName().equals(p.getName())) {
							plugin.sendMessage(p,"listener.cantChangeColor");
							horsecolors.remove(p.getName());
							e.setCancelled(true);
						} else {
							horse.setColor(horsecolors.get(p.getName()));
							plugin.sendMessage(p,"listener.colorChanged");
							e.setCancelled(true);
							horsecolors.remove(p.getName());
						}
					}
				}
				if (horsevariants.containsKey(p.getName())) {
					if (horse.isTamed()) {
						if (!currentOwner.getName().equals(p.getName())) {
							plugin.sendMessage(p,"listener.cantChangeVariant");
							horsevariants.remove(p.getName());
							e.setCancelled(true);
						} else {
							horse.setVariant(horsevariants.get(p.getName()));
							plugin.sendMessage(p,"listener.changedVariant");
							e.setCancelled(true);
							horsevariants.remove(p.getName());
						}
					}
				}

			}		
			// This code runs on the /tame release command.
			if (releases.containsKey(p.getName())) {
				if (!p.getName().equals(currentOwner.getName())) {
					plugin.sendMessage(p,"listener.doesNotOwn");
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
				plugin.sendMessage(p,"listener.animalReleased");
				e.setCancelled(true);
				releases.remove(p.getName());
			}

			// This code runs on /tame transfer.
			if (transfers.containsKey(p.getName())) {
				String newOwner = transfers.get(p.getName());
				if (!p.getName().equals(currentOwner.getName())) {
					plugin.sendMessage(p,"listener.doesNotOwn");
					return;
				}
				Player p2 = Bukkit.getPlayer(newOwner);
				AnimalTamer newOwner2 = p2;
				((Tameable) entity).setOwner(newOwner2);
				plugin.sendMessage(p,"listener.transferedTo", p2.getName());
				transfers.remove(p.getName());		
				e.setCancelled(true);
			}
		}
	}

}
