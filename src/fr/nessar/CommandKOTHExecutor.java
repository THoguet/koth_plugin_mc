package fr.nessar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.block.Chest;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class CommandKOTHExecutor implements CommandExecutor {

	private Selection sel;
	private Koth plugin;

	public CommandKOTHExecutor(Koth plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Koth.getPREFIX() + ChatColor.RED + "Seul les joueurs peuvent utiliser ces commandes.");
			return true;
		}
		Player p = (Player) sender;
		if (commandLabel.equalsIgnoreCase("koth") && args.length >= 1) {
			if (args[0].equalsIgnoreCase("new") && args.length == 2) {
				sel = getWorldEdit().getSelection(p);
				if (sel == null || sel.getMinimumPoint() == null || sel.getMaximumPoint() == null) {
					sender.sendMessage(
							Koth.getPREFIX() + ChatColor.RED
									+ "Vous devez d'abord selectionner une zone avec WorldEdit !");
					return true;
				}
				ClaimedZone newZone;
				try {
					newZone = new ClaimedZone(sel.getMinimumPoint(), sel.getMaximumPoint(), args[1]);
				} catch (Exception e) {
					p.sendMessage(Koth.getPREFIX() + ChatColor.RED + "ERROR");
					p.sendMessage(e.getMessage());
					return true;
				}
				p.sendMessage(Koth.getPREFIX() + ChatColor.BLUE + "Le KOTH " + ChatColor.BOLD + args[1] + ChatColor.BLUE
						+ " à bien été créé.");
				plugin.getCZones().add(newZone);
				plugin.getdManager().saveZones(plugin.getCZones());
				return true;
			} else if (args[0].equalsIgnoreCase("delete") && args.length == 2) {
				for (int i = 0; i < plugin.getCZones().size(); i++) {
					if (plugin.getCZones().get(i).getName().equalsIgnoreCase(args[1])) {
						plugin.getCZones().remove(i);
						p.sendMessage(Koth.getPREFIX() + "Le KOTH " + args[1] + " a bien été supprimé.");
						plugin.getdManager().saveZones(plugin.getCZones());
						return true;
					}
				}
				p.sendMessage(Koth.getPREFIX() + ChatColor.RED + "Le KOTH " + args[1] + " introuvable");
				return true;
			} else if (args[0].equalsIgnoreCase("list")) {
				if (plugin.getCZones().size() == 0) {
					p.sendMessage(Koth.getPREFIX() + "Il n'existe pas encore de KOTH.");
					return true;
				}
				p.sendMessage(Koth.getPREFIX() + "Liste des KOTH :");
				ClaimedZone act;
				String actif = "";
				for (int i = 0; i < plugin.getCZones().size(); i++) {
					act = plugin.getCZones().get(i);
					if (plugin.getCZones().get(i).isActive())
						actif = ChatColor.BOLD + "ACTIF";
					else
						actif = "";
					p.sendMessage("- " + ChatColor.BOLD + act.getName() + ChatColor.RESET + ": x: "
							+ String.valueOf(act.getXStart()) + " -> "
							+ String.valueOf(act.getXEnd()) + ", z: " + String.valueOf(act.getZStart()) + " -> "
							+ String.valueOf(act.getZEnd()) + ", monde: " + act.getWorld() + actif);
				}
				return true;
			} else if (args[0].equalsIgnoreCase("start") && args.length == 3) {
				plugin.isChestCorrect();
				if (plugin.getRewardChest() == null) {
					p.sendMessage(
							Koth.getPREFIX() + ChatColor.RED + "Vous devez d'abord enrgistrer la postion du coffre !");
					return true;
				}
				int kothActive = plugin.getActiveKoth();
				if (kothActive != -1) {
					p.sendMessage(
							Koth.getPREFIX() + ChatColor.RED + "Le KOTH " + ChatColor.BOLD
									+ plugin.getKOTHName().get(kothActive) + " est deja en cours !");
					return true;
				}
				int timer;
				try {
					timer = Integer.parseInt(args[2]) * 10;
				} catch (NumberFormatException e) {
					p.sendMessage(Koth.getPREFIX() + ChatColor.RED + "Temps incorrect !");
					return true;
				}
				for (int i = 0; i < plugin.getCZones().size(); i++) {
					if (plugin.getCZones().get(i).getName().equalsIgnoreCase(args[1])) {
						plugin.setTimer(timer);
						plugin.setCScorboard(new CustomScoreboard(plugin, plugin.getCZones().get(i)));
						plugin.newTask();
						plugin.startTimer();
						plugin.getCZones().get(i).start();
						Bukkit.broadcastMessage(
								Koth.getPREFIX() + "Le KOTH " + ChatColor.BOLD + args[1] + ChatColor.RESET
										+ " à commencé !");
						return true;
					}
				}
				p.sendMessage(Koth.getPREFIX() +
						ChatColor.RED + "Le KOTH " + ChatColor.BOLD + args[1] + ChatColor.RED + " n'existe pas.");
				return true;
			} else if (args[0].equalsIgnoreCase("stop")) {
				for (int i = 0; i < plugin.getCZones().size(); i++) {
					if (plugin.getCZones().get(i).isActive()) {

						plugin.getCZones().get(i).stop();
						plugin.stopTimer();
						Bukkit.broadcastMessage(
								Koth.getPREFIX() + "Le KOTH " + plugin.getCZones().get(i).getName() + ChatColor.RESET
										+ " à été arrété !");
						return true;
					}
				}
				p.sendMessage(Koth.getPREFIX() + ChatColor.RED + "Aucun KOTH n'est actif !");
				return true;
			} else if (args[0].equalsIgnoreCase("setchest")) {
				List<Block> lbloc = p.getLineOfSight((Set<org.bukkit.Material>) null, 50);
				for (int i = 0; i < lbloc.size(); i++) {
					if (lbloc.get(i).getType() == Material.CHEST) {
						plugin.setRewardChest((Chest) lbloc.get(i).getWorld().getBlockAt(lbloc.get(i).getLocation())
								.getState());
						List<Integer> locChest = new ArrayList<Integer>(Arrays.asList(plugin.getRewardChest().getX(),
								plugin.getRewardChest().getY(), plugin.getRewardChest().getZ()));
						plugin.reloadConfig();
						plugin.getConfig().set("chestlocation", locChest);
						plugin.saveConfig();
						p.sendMessage(
								Koth.getPREFIX() + ChatColor.GREEN + "La position du coffre a bien été enregistré");
						return true;
					}
				}
				p.sendMessage(
						Koth.getPREFIX() + ChatColor.RED + "Aucun coffre n'a été trouvé dans ton champ de vision.");
				return true;
			} else if (args[0].equalsIgnoreCase("save")) {
				plugin.saveInv(p);
				p.sendMessage(
						Koth.getPREFIX() + ChatColor.GREEN
								+ "Votre inventaire a été sauvgardé dans le fichier config.yml");
				return true;
			} else if (args[0].equalsIgnoreCase("reloadconfig")) {
				plugin.reloadConfig();
				p.sendMessage(Koth.getPREFIX() + ChatColor.GREEN + "La config a bien été reload !");
				return true;
			}
		}
		p.sendMessage(Koth.getPREFIX() + ChatColor.RED + "Mauvais usage de la commande.");
		return false;
	}

	public WorldEditPlugin getWorldEdit() {
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (p instanceof WorldEditPlugin)
			return (WorldEditPlugin) p;
		else
			return null;
	}

}
