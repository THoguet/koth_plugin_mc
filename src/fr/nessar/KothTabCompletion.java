package fr.nessar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class KothTabCompletion implements TabCompleter {

	private static final List<String> firstArg = new ArrayList<>(
			Arrays.asList("new", "delete", "list", "start", "stop", "setchest", "save", "reloadconfig"));
	private List<ClaimedZone> cZones;

	public KothTabCompletion(Koth plugin) {
		this.cZones = plugin.getCZones();
	}

	@Override
	public List<String> onTabComplete(CommandSender sen, Command cmd, String alias, String[] args) {
		List<String> ret = new ArrayList<>();
		if (args.length == 1) {
			for (String st : firstArg) {
				if (st.startsWith(args[0]))
					ret.add(st);
			}
		}
		if (args.length == 2 && (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("start"))) {
			List<String> listKOTHnames = new ArrayList<>();
			for (ClaimedZone cZ : this.cZones) {
				listKOTHnames.add(cZ.getName());
			}
			for (String st : listKOTHnames) {
				if (st.startsWith(args[1]))
					ret.add(st);
			}
		}
		return ret;
	}

}
