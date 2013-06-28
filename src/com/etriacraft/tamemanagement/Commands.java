package com.etriacraft.tamemanagement;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class Commands {

	TameManagement plugin;

	public Commands(TameManagement instance) {
		this.plugin = instance;
		init();
	}

	private void init() {
		PluginCommand tame = plugin.getCommand("tame");
		CommandExecutor exe;

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (args.length < 1) {
					s.sendMessage("-----§6TameManagement Commands§f-----");
					s.sendMessage("§3/tame setowner [Player]§f - Sets Owner of tamed animal.");
					s.sendMessage("§3/tame reload§f - Reload Config File.");
					return true;
				}
				if (args[0].equalsIgnoreCase("reload")) {
					if (!s.hasPermission("tamemanagement.reload")) {
						s.sendMessage("§cYou don't have permission to do that!");
						return true;
					}
					plugin.reloadConfig();
					s.sendMessage("§aTameManagement Config Reloaded.");
				}
				if (args[0].equalsIgnoreCase("setowner")) {
					if (!plugin.getConfig().getBoolean("AllowTransfers")) {
						s.sendMessage("§cThis server does not allow tamed animals to be transferred.");
						return true;
					}
					if (!s.hasPermission("tamemanagement.setowner")) {
						s.sendMessage("§cYou don't have permission to do that!");
						return true;
					}
					if (args.length != 2) {
						s.sendMessage("§3Proper Usage: §6/setowner [Player]");
						return true;
					}
					Player p = Bukkit.getPlayer(args[1]);
					if (p == null) {
						s.sendMessage("§3That player is not online.");
						return true;
					}
					if (p == s) {
						s.sendMessage("§cYou can't transfer ownership to yourself.");
						return true;
					}
					if (MobListener.transfers.containsKey(s.getName())) {
						MobListener.transfers.remove(s.getName());
					}
					MobListener.transfers.put(s.getName(), p.getName());
					s.sendMessage("§aRight Click the tamed mob that you would like to give to §3" + p.getName() + "§a.");
					return true;
				}
				return true;
			}

		}; tame.setExecutor(exe);
	}

}
