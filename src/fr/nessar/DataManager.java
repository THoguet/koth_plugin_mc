package fr.nessar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;

import com.google.gson.Gson;

import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;

public class DataManager {
	private Koth plugin = Koth.getPlugin(Koth.class);

	// Files & File Configs Here
	public File zoneFile;

	public DataManager() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}

		this.zoneFile = new File(plugin.getDataFolder(), "data.json");

		if (!this.zoneFile.exists()) {
			try {
				this.zoneFile.createNewFile();
				Bukkit.getServer().getConsoleSender()
						.sendMessage(Koth.getPREFIX() + ChatColor.GREEN + "The data.json file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(Koth.getPREFIX() + ChatColor.RED + "Could not create the data.json file");
			}
		} else {
			Bukkit.getServer().getConsoleSender().sendMessage(Koth.getPREFIX() + ChatColor.GREEN + "data.json found");
		}
	}

	public void saveZones(List<ClaimedZone> zones) {
		Gson gson = new Gson();
		try {
			Files.write(this.zoneFile.toPath(), gson.toJson(zones).getBytes());
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(Koth.getPREFIX() + ChatColor.RED + "Couldn't save in the data.json file");
		}
	}

	public List<ClaimedZone> loadZones() {
		Gson gson = new Gson();
		List<ClaimedZone> ret = null;
		ClaimedZone[] cZones = null;
		if (zoneFile.exists()) {
			Reader reader;
			try {
				reader = new FileReader(zoneFile);
			} catch (FileNotFoundException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(Koth.getPREFIX() + ChatColor.RED + "Couldn't found data.json");
				return new ArrayList<ClaimedZone>();
			}
			cZones = gson.fromJson(reader, ClaimedZone[].class);
			if (cZones != null) {
				ret = new ArrayList<ClaimedZone>(Arrays.asList(cZones));
			} else {
				ret = new ArrayList<ClaimedZone>();
			}
		}
		return ret;
	}

}
