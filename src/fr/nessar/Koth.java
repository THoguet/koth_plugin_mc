package fr.nessar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

public class Koth extends JavaPlugin implements Listener {

	private static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "KOTH" + ChatColor.GRAY + "] "
			+ ChatColor.RESET;

	private DataManager dManager;

	private boolean useScoreBoard = true;

	private List<ClaimedZone> cZones;
	private List<String> cZonesName;

	private BukkitTask timerTask;

	private CustomScoreboard cScoreboard;
	private TimerTask task;
	private int timer, originalTimer;
	private boolean isActiveZoneEmpty;

	private boolean papi;
	private Chest RewardChest;

	public static String getPREFIX() {
		return PREFIX;
	}

	@Override
	public void onEnable() {
		this.getConfig().options().copyDefaults();
		this.saveDefaultConfig();
		this.initChest();
		if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(PREFIX + ChatColor.RED + "This plugin need WorldEdit to work !");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		this.papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
		this.useScoreBoard = this.getConfig().getBoolean("scoreboard");
		this.timer = 0;
		this.originalTimer = 0;
		this.isActiveZoneEmpty = true;
		this.cScoreboard = null;
		this.dManager = new DataManager();
		this.cZones = this.dManager.loadZones();
		if (this.cZones == null) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(Koth.PREFIX + ChatColor.RED + "ERROR CANNOT LOAD DATA, CREATING EMPTY ONE");
			this.cZones = new ArrayList<>();
		}
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
			new PlaceHolders(this).register();
		if (this.cZones != null) {
			for (int i = 0; i < this.cZones.size(); i++) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(Koth.PREFIX + ChatColor.RED + this.cZones.get(i).getName());
			}
		}
		getCommand("koth").setExecutor(new CommandKOTHExecutor(this));
		getCommand("koth").setTabCompleter(new KothTabCompletion(this));
		getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getConsoleSender().sendMessage(Koth.PREFIX + ChatColor.GREEN + "Loaded.");
	}

	private void initChest() {
		List<Integer> locChest = this.getConfig().getIntegerList("chestlocation");
		if (locChest.size() != 3)
			this.RewardChest = null;
		else {
			if (Bukkit.getWorld("world").getBlockAt(locChest.get(0), locChest.get(1),
					locChest.get(2)).getType() != Material.CHEST) {
				this.getConfig().set("chestlocation", "");
				this.RewardChest = null;
				return;
			}
			this.RewardChest = (Chest) Bukkit.getWorld("world").getBlockAt(locChest.get(0), locChest.get(1),
					locChest.get(2)).getState();
		}
	}

	public boolean usePapi() {
		return this.papi;
	}

	public boolean useScoreBoard() {
		return this.useScoreBoard;
	}

	public String getFactionName(Player p) {
		if (!this.papi)
			return "";
		String ret = "%factionsuuid_faction_name%";
		ret = PlaceholderAPI.setPlaceholders(p, ret);
		if (ret.contains("no-faction"))
			return "";
		return ret;
	}

	public String getFactionName(String pName) {
		if (!this.papi)
			return "";
		Player p = Bukkit.getServer().getPlayer(pName);
		if (p == null)
			return "";
		return getFactionName(p);
	}

	public String getPlayerFactionName(String playerName) {
		if (playerName == "Aucun") {
			return "Aucune";
		} else {
			String ret = this.getFactionName(playerName);
			if (ret == "") {
				ret = "Aucune";
			}
			return ret;
		}
	}

	public String getControlerName(ClaimedZone cZone) {
		String ret = cZone.getFirstPlayerName();
		if (ret == null) {
			ret = "Aucun";
		}
		return ret;
	}

	public String getControlerDisplayName(ClaimedZone cZone) {
		String ret = cZone.getFirstPlayerDisplayName();
		if (ret == null) {
			ret = "Aucun";
		}
		return ret;
	}

	public Chest getRewardChest() {
		return this.RewardChest;
	}

	public void setCScorboard(CustomScoreboard toAdd) {
		this.cScoreboard = toAdd;
	}

	public void setRewardChest(Chest toAdd) {
		this.RewardChest = toAdd;
	}

	public List<String> getKOTHName() {
		this.updateKOTHname();
		return this.cZonesName;
	}

	public void updateKOTHname() {
		List<String> newlist = new ArrayList<>();
		for (ClaimedZone cz : this.cZones) {
			newlist.add(cz.getName());
		}
		this.cZonesName = newlist;
	}

	public List<ClaimedZone> getCZones() {
		return this.cZones;
	}

	public DataManager getdManager() {
		return this.dManager;
	}

	public boolean isActiveZoneEmpty() {
		this.updateIsActiveZoneEmpty();
		return this.isActiveZoneEmpty;
	}

	public void updateIsActiveZoneEmpty() {
		int kothActive = this.getActiveKoth();
		if (kothActive != -1) {
			String firstPlayerDisplayName = this.cZones.get(kothActive).getFirstPlayerDisplayName();
			this.isActiveZoneEmpty = firstPlayerDisplayName == null;
		} else {
			this.isActiveZoneEmpty = true;
		}
	}

	public boolean isChestCorrect() {
		if (this.RewardChest == null) {
			return false;
		}
		Location locChest = this.RewardChest.getLocation();
		Block b = Bukkit.getWorld(locChest.getWorld().getName()).getBlockAt(locChest);
		if (b.getType() != Material.CHEST) {
			this.RewardChest = null;
			this.getConfig().set("chestlocation", "");
			return false;
		}
		this.RewardChest = (Chest) Bukkit.getWorld(locChest.getWorld().getName()).getBlockAt(locChest).getState();
		return true;
	}

	public int getHourLeft() {
		return this.timer / 36000;
	}

	public String duplicateString(String toDup, int times) {
		String ret = "";
		for (int i = 0; i < times; i++) {
			ret = ret + toDup;
		}
		return ret;
	}

	public String getLeftString(int number, int times) {
		if (number <= Math.pow(10, times - 1)) {
			return duplicateString("0", times - String.valueOf(number).length()) + String.valueOf(number);
		}
		return String.valueOf(number);
	}

	public String getHourLeftString(int i) {
		int h = this.getHourLeft();
		return getLeftString(h, i);
	}

	public int getMinuteLeft() {
		return this.timer % 36000 / 600;
	}

	public String getMinuteLeftString(int i) {
		int m = this.getMinuteLeft();
		return getLeftString(m, i);
	}

	public int getSecondLeft() {
		return this.timer % 600 / 10;
	}

	public String getSecondLeftString(int i) {
		int s = this.getSecondLeft();
		return getLeftString(s, i);
	}

	public void newTask() {
		this.task = new TimerTask(this);
	}

	public void setTimer(int newT) {
		this.timer = newT;
		this.originalTimer = newT;
	}

	public void decrementTimer() {
		this.timer--;
	}

	public boolean isTimerEnd() {
		if (this.timer <= 0) {
			this.timerTask.cancel();
			int activeKoth = getActiveKoth();
			if (activeKoth != -1)
				this.kothEnded(this.cZones.get(activeKoth).getFirstPlayer());
			this.stopTimer();
			return true;
		}
		return false;
	}

	public int getTimer() {
		return this.timer;
	}

	public void resetTimer() {
		this.timerTask.cancel();
		this.timer = this.originalTimer;
		this.newTask();
		this.startTimer();
	}

	public void startTimer() {
		if (this.useScoreBoard)
			this.cScoreboard.setScoreboardAllPlayers();
		this.timerTask = this.task.runTaskTimerAsynchronously(this, 0L, 2L);
	}

	public void stopTimer() {
		if (this.useScoreBoard)
			this.cScoreboard.deleteScoreboard();
		this.timerTask.cancel();
		int activeKoth = getActiveKoth();
		if (activeKoth != -1)
			this.cZones.get(activeKoth).stop();
	}

	public CustomScoreboard getCustomScoreboard() {
		return cScoreboard;
	}

	public int getActiveKoth() {
		for (int i = 0; i < this.cZones.size(); i++) {
			if (this.cZones.get(i).isActive()) {
				return i;
			}
		}
		return -1;
	}

	public void kothEnded(Player winner) {
		this.stopTimer();
		Bukkit.broadcastMessage(
				Koth.PREFIX + ChatColor.GREEN + "LE KOTH est terminé, le gagnant est: " + winner.getDisplayName()
						+ ".");
		try {
			this.loadRewardChest(this.RewardChest, this.getRandomItems());
		} catch (Exception e) {
			Bukkit.getServer().getConsoleSender().sendMessage(Koth.PREFIX + ChatColor.RED + e);
			return;
		}
		Location locTpWinner = this.RewardChest.getLocation();
		locTpWinner.add(2.5, -1, 0.5);
		locTpWinner.setPitch(0);
		locTpWinner.setYaw(90);
		winner.teleport(locTpWinner);
	}

	public void saveInv(Player p) {
		final List<ItemStack> invCont = new ArrayList<>();
		final List<ItemStack> armorCont = new ArrayList<>();
		for (ItemStack stack : p.getInventory().getContents()) {
			if (stack != null)
				invCont.add(stack);
		}
		for (ItemStack stack : p.getInventory().getArmorContents()) {
			if (stack != null)
				armorCont.add(stack);
		}
		this.reloadConfig();
		this.getConfig().set("chestreward", invCont);
		this.saveConfig();
	}

	public List<ItemStack> getRandomItems() throws Exception {
		List<ItemStack> listItem = getListItemReward();
		List<ItemStack> ret = new ArrayList<>();
		List<Integer> percentageItem = this.getConfig().getIntegerList("chestreward_percentage");
		if (percentageItem.size() != listItem.size()) {
			throw new Exception("ERROR: Invalid config.yml, percentage (" + String.valueOf(percentageItem.size())
					+ ") != item (" + String.valueOf(listItem.size()) + ")!");
		}
		int rand;
		for (int i = 0; i < listItem.size(); i++) {
			rand = (int) (Math.random() * 100 + 1);
			if (percentageItem.get(i) > rand) {
				ret.add(listItem.get(i));
			}
		}
		return ret;
	}

	public void loadRewardChest(Chest c, List<ItemStack> invCont) {
		if (invCont == null)
			return;
		for (ItemStack stack : invCont) {
			c.getInventory().addItem(stack);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ItemStack> getListItemReward() {
		return (List<ItemStack>) this.getConfig().getList("chestreward");
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Location newLoc = event.getTo();
		Location oldLoc = event.getFrom();
		for (ClaimedZone cZone : this.cZones) {
			if (cZone.isInZone(newLoc) && !cZone.isInZone(oldLoc)) {
				cZone.addPlayer(p);
			} else if (!cZone.isInZone(newLoc) && cZone.isInZone(oldLoc)) {
				this.playerLeftZone(cZone, p);
			}
		}
	}

	public void playerLeftZone(ClaimedZone cZ, Player p) {
		if (cZ.isActive() && cZ.getFirstPlayerDisplayName().equalsIgnoreCase(p.getDisplayName())) {
			Bukkit.broadcastMessage(Koth.PREFIX + "Le contrôleur du KOTH a quitté la zone, le Timer a été reset !");
			this.resetTimer();
		}
		cZ.deletePlayer(p);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		for (ClaimedZone cZone : this.cZones) {
			if (cZone.isInZone(p.getLocation())) {
				cZone.addPlayer(p);
				break;
			}
		}
		if (getActiveKoth() != -1) {
			this.cScoreboard.setScoreboardToPlayer(p);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		for (ClaimedZone cZone : this.cZones) {
			if (cZone.isInZone(p.getLocation())) {
				this.playerLeftZone(cZone, p);
			}
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		this.onPlayerMove(e);
	}

	@Override
	public void onDisable() {
		this.dManager.saveZones(this.cZones);
		Bukkit.getConsoleSender().sendMessage(Koth.PREFIX + ChatColor.RED + "Zones saved.");
		Bukkit.getConsoleSender().sendMessage(Koth.PREFIX + ChatColor.RED + "Unloaded.");
	}
}