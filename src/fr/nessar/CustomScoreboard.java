package fr.nessar;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.md_5.bungee.api.ChatColor;

public class CustomScoreboard {

	private static final String PREFIX = ChatColor.BLACK + ">> " + ChatColor.GRAY;

	private ScoreboardManager manager;
	private Scoreboard scoreboard;
	private Objective obj;
	private ClaimedZone cZone;
	private String hour, minute, second;
	private Koth KOTH;
	private String lastTimer;
	private String lastPlayer;
	private String lastFaction;

	public CustomScoreboard(Koth koth, ClaimedZone cZone) {
		this.KOTH = koth;
		this.manager = Bukkit.getScoreboardManager();
		this.scoreboard = this.manager.getNewScoreboard();
		this.obj = this.scoreboard
				.registerNewObjective(ChatColor.GRAY + "[" + ChatColor.GOLD + "KOTH" + ChatColor.GRAY + "]", "dummy");
		this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.cZone = cZone;
		this.updateTimer();
		this.updateScores();
		Score spacer1 = this.obj.getScore("");
		spacer1.setScore(8);
		Score spacer2 = this.obj.getScore(" ");
		spacer2.setScore(4);
		Score spacer3 = this.obj.getScore("  ");
		spacer3.setScore(6);
		Score KOTH_name = this.obj.getScore(PREFIX + "KOTH: " + ChatColor.GOLD + this.cZone.getName());
		KOTH_name.setScore(7);
		Score ControlerKOTH = this.obj.getScore(PREFIX + "Controleur du KOTH");
		ControlerKOTH.setScore(3);
		Score timerScore = this.obj.getScore(this.lastTimer);
		timerScore.setScore(5);
		Score playerScore = this.obj.getScore(this.lastPlayer);
		playerScore.setScore(1);
		Score factionScore = this.obj.getScore(this.lastFaction);
		factionScore.setScore(2);
	}

	public String getFactionName(Player p) {
		FPlayer fPlayer = FPlayers.getInstance().getByPlayer(p);
		if (fPlayer.getFaction().isWilderness())
			return "";
		return fPlayer.getFaction().getTag();
	}

	private void updateTimer() {
		int h = this.KOTH.getHourLeft(), m = this.KOTH.getMinuteLeft(), s = this.KOTH.getSecondLeft();
		if (h == 0)
			this.hour = "";
		else {
			this.hour = String.valueOf(h) + ":";
			if (h < 10) {
				this.hour = "0" + this.hour;
			}
		}

		this.minute = String.valueOf(m) + ":";
		if (m < 10) {
			this.minute = "0" + this.minute;
		}
		this.second = String.valueOf(s);
		if (s < 10) {
			this.second = "0" + this.second;
		}
	}

	private void resetScores() {
		this.obj.getScoreboard().resetScores(this.lastTimer);
		this.obj.getScoreboard().resetScores(this.lastPlayer);
		this.obj.getScoreboard().resetScores(this.lastFaction);
	}

	private void updateTimeScore() {
		this.lastTimer = PREFIX + "Temps restant : " + ChatColor.GOLD + this.hour + this.minute + this.second;
	}

	private void updatePlayerScore() {
		this.lastPlayer = this.cZone.getFirstPlayerDisplayName();
		if (this.lastPlayer == null) {
			this.lastPlayer = "Aucun";
		}
		this.lastPlayer = ChatColor.GRAY + "Joueur: " + ChatColor.GOLD + this.lastPlayer;
	}

	private void updateFactionScore() {
		Player p = this.cZone.getFirstPlayer();
		if (p == null) {
			this.lastFaction = "Aucune";
		} else {
			this.lastFaction = getFactionName(this.cZone.getFirstPlayer());
			if (this.lastFaction == "") {
				this.lastFaction = "Aucune";
			}
		}
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
		Score timerSpacer = this.obj.getScore(this.lastTimer);
		timerSpacer.setScore(5);
		Score playerSpacer = obj.getScore(this.lastPlayer);
		playerSpacer.setScore(1);
		Score factionSpacer = this.obj.getScore(this.lastFaction);
		factionSpacer.setScore(2);
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
