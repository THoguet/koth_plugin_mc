package fr.nessar;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;

public class dataManager {
	private koth plugin = koth.getPlugin(koth.class);

	// Files & File Configs Here
	public File zoneFile;

	public dataManager() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}

		this.zoneFile = new File(plugin.getDataFolder(), "data.json");

		if (!this.zoneFile.exists()) {
			try {
				this.zoneFile.createNewFile();
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.GREEN + "The data.json file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.RED + "Could not create the data.json file");
			}
		} else {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "data.json found");
		}
	}

	public void saveZones(List<ClaimedZone> zones) {
		Gson gson = new Gson();
		try {
			Files.write(this.zoneFile.toPath(), gson.toJson(zones).getBytes());
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.RED + "Couldn't save in the data.json file");
		}
	}

	public List<ClaimedZone> loadZones() {
		Gson gson = new Gson();
		List<ClaimedZone> ret = new ArrayList<>();
		Type listofClaimedZone = new TypeToken<ArrayList<ClaimedZone>>() {
		}.getType();
		String jsonString = null;
		try {
			byte[] bytes = Files.readAllBytes(zoneFile.toPath());
			jsonString = new String(bytes);
		} catch (IOException e1) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.RED + "Couldn't read the data.json file");
		}
		if (jsonString != null) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.GREEN + jsonString);
			try {
				ret = gson.fromJson(jsonString, listofClaimedZone);
			} catch (JsonSyntaxException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(ChatColor.RED + "Couldn't convert to json data.json");
			}
		}
		return ret;
	}

}
