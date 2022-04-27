package fr.nessar;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.md_5.bungee.api.ChatColor;

public class ConfigManager {
	private koth plugin = koth.getPlugin(koth.class);

	// Files & File Configs Here
	public FileConfiguration playerscfg;
	public File playersfile;
	// --------------------------

	public void setup() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}

		playersfile = new File(plugin.getDataFolder(), "players.yml");

		if (!playersfile.exists()) {
			try {
				playersfile.createNewFile();
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.GREEN + "The players.yml file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.RED + "Could not create the players.yml file");
			}
		}

		playerscfg = YamlConfiguration.loadConfiguration(playersfile);
	}

	public FileConfiguration getPlayers() {
		return playerscfg;
	}

	public void savePlayers() {
		try {
			playerscfg.save(playersfile);
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "The players.yml file has been saved");

		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the players.yml file");
		}
	}

	public void reloadPlayers() {
		playerscfg = YamlConfiguration.loadConfiguration(playersfile);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "The players.yml file has been reload");

	}
}
