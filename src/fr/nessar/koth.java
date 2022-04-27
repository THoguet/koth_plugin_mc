package fr.nessar;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import net.md_5.bungee.api.ChatColor;

public class koth extends JavaPlugin implements Listener {

	private static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "KOTH" + ChatColor.GRAY + "] "
			+ ChatColor.RESET;

	// private ConfigManager cfgm;

	private dataManager dManager;

	private List<ClaimedZone> cZones;

	private Selection sel;

	@Override
	public void onEnable() {
		// loadConfigManager();
		// loadConfig();
		Bukkit.broadcastMessage(PREFIX + ChatColor.GREEN + "Loaded.");
		this.dManager = new dataManager();
		this.cZones = this.dManager.loadZones();
		disableAllKOTH();
		if (this.cZones != null) {
			for (int i = 0; i < this.cZones.size(); i++) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(PREFIX + ChatColor.RED + this.cZones.get(i).getName());
			}
		}
		getServer().getPluginManager().registerEvents(this, this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(PREFIX + ChatColor.RED + "Seul les joueurs peuvent utiliser ces commandes.");
			return true;
		}
		Player p = (Player) sender;
		if (commandLabel.equalsIgnoreCase("koth")) {
			if (args[0].equalsIgnoreCase("new") && args.length == 2) {
				sel = getWorldEdit().getSelection(p);
				if (sel == null || sel.getMinimumPoint() == null || sel.getMaximumPoint() == null) {
					sender.sendMessage(
							PREFIX + ChatColor.RED + "Vous devez d'abord selectionner une zone avec WorldEdit !");
					return true;
				}
				ClaimedZone newZone = new ClaimedZone(sel.getMinimumPoint(), sel.getMaximumPoint(), args[1]);
				p.sendMessage(PREFIX + ChatColor.BLUE + "Le KOTH " + ChatColor.BOLD + args[1] + ChatColor.BLUE
						+ " à bien été créé.");
				this.cZones.add(newZone);
				this.dManager.saveZones(this.cZones);
				return true;
			} else if (args[0].equalsIgnoreCase("delete") && args.length == 2) {
				for (int i = 0; i < this.cZones.size(); i++) {
					if (this.cZones.get(i).getName().equalsIgnoreCase(args[1])) {
						this.cZones.remove(i);
						p.sendMessage(PREFIX + "Le KOTH " + args[1] + " a bien été supprimé.");
						this.dManager.saveZones(this.cZones);
						return true;
					}
				}
				p.sendMessage(PREFIX + ChatColor.RED + "Le KOTH " + args[1] + " introuvable");
				return true;
			} else if (args[0].equalsIgnoreCase("list")) {
				p.sendMessage(PREFIX + "Liste des KOTH :");
				ClaimedZone act;
				String actif = "";
				for (int i = 0; i < this.cZones.size(); i++) {
					act = this.cZones.get(i);
					if (this.cZones.get(i).isActive())
						actif = ChatColor.BOLD + "ACTIF";
					else
						actif = "";
					p.sendMessage("- " + ChatColor.BOLD + act.getName() + ChatColor.RESET + ": x: "
							+ String.valueOf(act.getXStart()) + " -> "
							+ String.valueOf(act.getXEnd()) + ", z: " + String.valueOf(act.getZStart()) + " -> "
							+ String.valueOf(act.getZEnd()) + ", monde: " + act.getWorld() + actif);
				}
				return true;
			} else if (args[0].equalsIgnoreCase("start") && args.length == 2) {
				String kothActive = isAnyKOTHActive();
				if (kothActive != null) {
					p.sendMessage(PREFIX + ChatColor.RED + "Le KOTH " + kothActive + " est deja en cours !");
					return true;
				}
				for (int i = 0; i < this.cZones.size(); i++) {
					if (this.cZones.get(i).getName().equalsIgnoreCase(args[1])) {
						this.cZones.get(i).setActive(true);
						this.dManager.saveZones(this.cZones);
						Bukkit.broadcastMessage(PREFIX + "Le KOTH " + ChatColor.BOLD + args[1] + ChatColor.RESET
								+ " à commencé !");
						return true;
					}
				}
				p.sendMessage(PREFIX +
						ChatColor.RED + "Le KOTH " + ChatColor.BOLD + args[1] + ChatColor.RED + " n'existe pas.");
				return true;
			} else if (args[0].equalsIgnoreCase("stop") && args.length == 2) {
				for (int i = 0; i < this.cZones.size(); i++) {
					if (this.cZones.get(i).getName().equalsIgnoreCase(args[1])) {
						if (!this.cZones.get(i).isActive()) {
							p.sendMessage(PREFIX + ChatColor.RED + "Le KOTH " + args[1] + " n'a pas commencé !");
							return true;
						}
						this.cZones.get(i).setActive(false);
						this.dManager.saveZones(this.cZones);
						Bukkit.broadcastMessage(PREFIX + "Le KOTH " + ChatColor.BOLD + args[1] + ChatColor.RESET
								+ " à été arrété !");
						return true;
					}
				}
				p.sendMessage(PREFIX +
						ChatColor.RED + "Le KOTH " + ChatColor.BOLD + args[1] + ChatColor.RED + " n'existe pas.");
				return true;
			}
		}
		p.sendMessage(PREFIX + ChatColor.RED + "Mauvais usage de la commande.");
		return false;
	}

	public String isAnyKOTHActive() {
		for (int i = 0; i < this.cZones.size(); i++) {
			if (this.cZones.get(i).isActive()) {
				return this.cZones.get(i).getName();
			}
		}
		return null;
	}

	public void disableAllKOTH() {
		for (int i = 0; i < this.cZones.size(); i++) {
			this.cZones.get(i).setActive(false);
		}
	}

	public WorldEditPlugin getWorldEdit() {
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (p instanceof WorldEditPlugin)
			return (WorldEditPlugin) p;
		else
			return null;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Location newLoc = event.getTo();
		Location oldLoc = event.getFrom();
		for (int i = 0; i < this.cZones.size(); i++) {
			if (this.cZones.get(i).isInZone(newLoc) && !this.cZones.get(i).isInZone(oldLoc)) {
				p.sendMessage("Vous êtes bien entré dans la zone " + this.cZones.get(i).getName());
				this.cZones.get(i).addPlayer(p.getName());
			} else if (!this.cZones.get(i).isInZone(newLoc) && this.cZones.get(i).isInZone(oldLoc)) {
				this.cZones.get(i).deletePlayer(p.getName());
				p.sendMessage("Vous êtes bien sorti de la zone " + this.cZones.get(i).getName());
			}
		}
	}
	// public void loadConfigManager() {
	// cfgm = new ConfigManager();
	// cfgm.setup();
	// cfgm.savePlayers();
	// cfgm.reloadPlayers();
	// }

	// public void loadConfig() {
	// getConfig().options().copyDefaults(true);
	// saveConfig();
	// }

	@Override
	public void onDisable() {
		this.dManager.saveZones(this.cZones);
		Bukkit.broadcastMessage(PREFIX + ChatColor.RED + "Zones saved.");
		Bukkit.broadcastMessage(PREFIX + ChatColor.RED + "Unloaded.");
	}
}