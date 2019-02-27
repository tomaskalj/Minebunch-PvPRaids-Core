package com.minebunch.core.task;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.time.TimeUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class ShutdownTask extends BukkitRunnable {
	private int shutdownSeconds;

	@Override
	public void run() {
		if (shutdownSeconds == 0) {
			Bukkit.shutdown();
		} else if (shutdownSeconds % 60 == 0 || shutdownSeconds == 30 || shutdownSeconds == 10 || shutdownSeconds <= 5) {
			CorePlugin.getInstance().getServerManager().broadcastAlert(Colors.RED + "The server is restarting in " + TimeUtil.formatTimeSeconds(shutdownSeconds) + ".");
		}

		shutdownSeconds--;
	}
}
