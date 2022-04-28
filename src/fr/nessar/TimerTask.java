package fr.nessar;

import org.bukkit.scheduler.BukkitRunnable;

public class TimerTask extends BukkitRunnable {

	Koth plugin;

	public TimerTask(Koth plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if (!this.plugin.isActiveZoneEmpty())
			this.plugin.decrementTimer();
		this.plugin.getCustomScoreboard().updateScoreboard();
		this.plugin.isTimerEnd();
	}

}
