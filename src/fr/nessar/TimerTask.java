package fr.nessar;

import org.bukkit.scheduler.BukkitRunnable;

public class TimerTask extends BukkitRunnable {

	Koth plugin;

	public TimerTask(Koth plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		new SyncScheduleTimerTask(this.plugin).run();
	}

}
