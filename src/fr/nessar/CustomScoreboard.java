package fr.nessar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.md_5.bungee.api.ChatColor;

public class CustomScoreboard {

	private static final String PREFIX = ChatColor.DARK_GRAY + ">> " + ChatColor.GRAY;

	private static final String SPACER1 = "";
	private static final String SPACER2 = " ";
	private static final String SPACER3 = "  ";
	private static final String CONTROLERKOTH = PREFIX + "Controleur du KOTH";
	private static final String TITLEKOTH = ChatColor.GRAY + "[" + ChatColor.GOLD + "KOTH" + ChatColor.GRAY + "]";
	private final String KOTHNAME;

	private ScoreboardManager manager;
	private Scoreboard scoreboard;
	private Objective obj;
	private ClaimedZone cZone;
	private String hour, minute, second;
	private Koth plugin;
	private String lastTimer;
	private String lastPlayer;
	private String lastFaction;

	public CustomScoreboard(Koth plugin, ClaimedZone cZone) {
		this.plugin = plugin;
		this.manager = Bukkit.getScoreboardManager();
		this.scoreboard = this.manager.getNewScoreboard();
		this.obj = this.scoreboard
				.registerNewObjective(TITLEKOTH, "dummy");
		this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.cZone = cZone;
		this.KOTHNAME = PREFIX + "KOTH: " + ChatColor.GOLD + this.cZone.getName();
		this.updateScoreboard();
	}

	private void updateTimer() {
		this.hour = this.plugin.getHourLeftString(2);
		if (Integer.valueOf(this.hour) == 0)
			this.hour = "";
		else
			this.hour += ":";
		this.minute = this.plugin.getMinuteLeftString(2) + ":";
		this.second = this.plugin.getSecondLeftString(2);
	}

	private void resetScores() {
		if (this.lastTimer != null)
			this.obj.getScoreboard().resetScores(this.lastTimer);
		if (this.lastPlayer != null)
			this.obj.getScoreboard().resetScores(this.lastPlayer);
		if (this.lastFaction != null)
			this.obj.getScoreboard().resetScores(this.lastFaction);
		this.obj.getScoreboard().resetScores(SPACER1);
		this.obj.getScoreboard().resetScores(SPACER2);
		this.obj.getScoreboard().resetScores(SPACER3);
		this.obj.getScoreboard().resetScores(CONTROLERKOTH);
		this.obj.getScoreboard().resetScores(KOTHNAME);
	}

	private void updateTimeScore() {
		this.lastTimer = PREFIX + "Temps restant : " + ChatColor.GOLD + this.hour + this.minute + this.second;
	}

	private void updatePlayerScore() {
		this.lastPlayer = this.plugin.getControler(this.cZone);
		this.lastPlayer = ChatColor.GRAY + "Joueur: " + ChatColor.GOLD + this.lastPlayer;
	}

	private void updateFactionScore() {
		this.lastFaction = this.plugin.getPlayerFactionName(this.plugin.getControler(this.cZone));
		this.lastFaction = ChatColor.GRAY + "Faction: " + ChatColor.GOLD + this.lastFaction;
	}

	private void updateScores() {
		this.updatePlayerScore();
		this.updateFactionScore();
		this.updateTimeScore();
	}

	public void updateScoreboard() {
		this.updateTimer();
		this.resetScores();
		this.updateScores();
		Score spacer1 = this.obj.getScore(SPACER1);
		spacer1.setScore(8);
		Score KOTH_name = this.obj.getScore(KOTHNAME);
		KOTH_name.setScore(7);
		Score spacer3 = this.obj.getScore(SPACER3);
		spacer3.setScore(6);
		Score timerSpacer = this.obj.getScore(this.lastTimer);
		timerSpacer.setScore(5);
		Score spacer2 = this.obj.getScore(SPACER2);
		spacer2.setScore(4);
		Score ControlerKOTH = this.obj.getScore(CONTROLERKOTH);
		ControlerKOTH.setScore(3);
		Score factionSpacer = this.obj.getScore(this.lastFaction);
		factionSpacer.setScore(2);
		Score playerSpacer = obj.getScore(this.lastPlayer);
		playerSpacer.setScore(1);
	}

	public void deleteScoreboard() {
		scoreboard.clearSlot(DisplaySlot.SIDEBAR);
	}

	public void setScoreboardToPlayer(Player p) {
		p.setScoreboard(this.scoreboard);
	}

	public void setScoreboardAllPlayers() {
		Bukkit.getOnlinePlayers().forEach(player -> player.setScoreboard(this.scoreboard));
	}
}
