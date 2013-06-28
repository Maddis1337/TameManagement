package com.etriacraft.tamemanagement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TameManagement extends JavaPlugin {
	
	protected static Logger log;
	public static TameManagement instance;
	
	File configFile;
	FileConfiguration config;
	
	Commands cmd;
	
	private final MobListener moblistener = new MobListener (this);
	
	@Override
	public void onEnable() {
		instance = this;
		this.log = this.getLogger();
		
		configFile = new File(getDataFolder(), "config.yml");
		
		try {
			firstRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		config = new YamlConfiguration();
		
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
	
	public void firstRun() throws Exception {
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
			log.info("Config not found. Generating.");
		}
	}
	
	private void loadYamls() {
		try {
			config.load(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void copy (InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf))>0) {
				out.write(buf,0,len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveYamls() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static TameManagement getInstance() {
		return instance;
	}
	
	public void configReload() {
		reloadConfig();
	}

}
