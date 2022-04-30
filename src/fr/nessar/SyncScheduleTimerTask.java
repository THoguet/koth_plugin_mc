package fr.nessar;

import org.bukkit.scheduler.BukkitRunnable;

public class SyncScheduleTimerTask extends BukkitRunnable {

	Koth plugin;

	public SyncScheduleTimerTask(Koth plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if (!this.plugin.isActiveZoneEmpty())
			this.plugin.decrementTimer();
		if (this.plugin.useScoreBoard())
			this.plugin.getCustomScoreboard().updateScoreboard();
		this.plugin.isTimerEnd();
	}

}
