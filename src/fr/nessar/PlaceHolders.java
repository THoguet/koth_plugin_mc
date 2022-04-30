package fr.nessar;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceHolders extends PlaceholderExpansion {

	private final Koth plugin;

	public PlaceHolders(Koth plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getAuthor() {
		return "Nessar";
	}

	@Override
	public String getIdentifier() {
		return "KOTH";
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}

	public ClaimedZone getCzone() {
		int i = plugin.getActiveKoth();
		if (i == -1)
			return null;
		return plugin.getCZones().get(i);
	}

	@Override
	public String onRequest(OfflinePlayer player, String params) {
		if (params.equalsIgnoreCase("active")) {
			return plugin.getActiveKoth() != -1 ? "true" : "false";
		}
		if (params.equalsIgnoreCase("name")) {
			ClaimedZone cZone = this.getCzone();
			if (cZone == null)
				return "Aucun";
			return cZone.getName();
		}
		if (params.startsWith("timer_H")) {
			return plugin.getHourLeftString(params.length() - 6);
		}
		if (params.startsWith("timer_M")) {
			return plugin.getMinuteLeftString(params.length() - 6);
		}
		if (params.startsWith("timer_S")) {
			return plugin.getSecondLeftString(params.length() - 6);
		}
		if (params.equalsIgnoreCase("controler_faction")) {
			ClaimedZone cZone = this.getCzone();
			if (cZone == null)
				return "Aucune";
			return plugin.getPlayerFactionName(plugin.getControlerName(cZone));
		}
		if (params.equalsIgnoreCase("controler_name")) {
			ClaimedZone cZone = this.getCzone();
			if (cZone == null)
				return "Aucun";
			return plugin.getControlerDisplayName(cZone);
		}
		return null;
	}

}
