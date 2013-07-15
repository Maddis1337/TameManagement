package com.etriacraft.tamemanagement;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TameManagement extends JavaPlugin {

	protected static Logger log;
	public static TameManagement instance;

	Commands cmd;

	private final MobListener moblistener = new MobListener (this);

	@Override
	public void onEnable() {
		instance = this;
		TameManagement.log = this.getLogger();

		configCheck(); // this does everything automatically


		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(moblistener, this);

		cmd = new Commands(this);

		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to Submit Stats
		}
	}

	public static TameManagement getInstance() {
		return instance;
	}

	public void configReload() {
		reloadConfig();
	}
	
	public void sendMessage(Player p, String messageKey){
		sendMessage(p, messageKey, null);
	}
	
	public void sendMessage(Player p, String messageKey, String data){
		
		String message = getConfig().getString("messages."+messageKey);
		if(message == null){
			getLogger().warning("Missing text in config! Unknown: "+messageKey);
			return;
		}
		if(data != null) message = String.format(message, data);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public void configCheck() {
		getConfig().addDefault("ProtectTames", true);
		getConfig().addDefault("AllowTransfers", true);
		getConfig().addDefault("AllowReleases", true);
		getConfig().addDefault("ProtectHorses", true);
		getConfig().addDefault("Breeding.Horse", true);
		getConfig().addDefault("Breeding.Wolf", true);
		getConfig().addDefault("Breeding.Ocelot", true);
		
		getConfig().addDefault("messages.listener.animalDoesNotBelongToYou", "&cYou can't damage an animal that doesn't belong to you.");
		getConfig().addDefault("messages.listener.horseAlreadyOwned", "This horse is already owned.");
		getConfig().addDefault("messages.listener.cantInteractWithHorse", "&cYou can't interact with a horse you do not own.");
		getConfig().addDefault("messages.listener.cantChangeStyle", "You can't change the style on a horse that you do not own.");
		getConfig().addDefault("messages.listener.styleChanged", "&aHorse style changed.");
		getConfig().addDefault("messages.listener.cantChangeColor", "&cYou can't change the color of a horse you don't own.");
		getConfig().addDefault("messages.listener.colorChanged", "&aHorse color changed.");
		getConfig().addDefault("messages.listener.cantChangeVariant", "&cYou can't change the variant of a horse you don't own.");
		getConfig().addDefault("messages.listener.changedVariant", "&aHorse variation changed.");
		getConfig().addDefault("messages.listener.doesNotOwn", "&cYou do not own this animal.");
		getConfig().addDefault("messages.listener.animalReleased", "&aYou have released this animal to the wild.");
		getConfig().addDefault("messages.listener.animalTransfered", "&aYou have transferred this animal to %s");
		
//		getConfig().set("ConfigVersion", 120); no longer needed
		getConfig().options().copyDefaults(true);
		
		saveConfig();
	}

}
