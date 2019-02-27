package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.event.server.ServerShutdownCancelEvent;
import com.minebunch.core.event.server.ServerShutdownScheduleEvent;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.task.ShutdownTask;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.time.TimeUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

public class ShutdownCommand extends BaseCommand {
	private final CorePlugin plugin;
	private BukkitTask shutdownTask;

	public ShutdownCommand(CorePlugin plugin) {
		super("shutdown", Rank.ADMIN);
		this.plugin = plugin;
		setUsage(Colors.RED + "Usage: /shutdown <time|cancel>");
	}

	@Override
	protected void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(usageMessage);
			return;
		}

		String arg = args[0];

		if (arg.equalsIgnoreCase("cancel")) {
			if (shutdownTask == null) {
				sender.sendMessage(Colors.RED + "There is no shutdown in progress.");
			} else {
				plugin.getServer().getPluginManager().callEvent(new ServerShutdownCancelEvent());

				shutdownTask.cancel();
				shutdownTask = null;
				plugin.getServer().broadcastMessage(Colors.GREEN + "The shutdown in-progress has been cancelled by " + sender.getName() + ".");
			}
			return;
		}

		if (shutdownTask != null) {
			sender.sendMessage(Colors.RED + "There is already a shutdown in progress.");
			return;
		}

		int seconds = TimeUtil.parseTime(arg);

		if (seconds >= 5 && seconds <= 300) {
			plugin.getServer().getPluginManager().callEvent(new ServerShutdownScheduleEvent());

			shutdownTask = new ShutdownTask(seconds).runTaskTimer(plugin, 0L, 20L);
			plugin.getServer().broadcastMessage(Colors.YELLOW + "A server shutdown was initiated by " + sender.getName() + " which will occur in " + arg + ".");
		} else {
			sender.sendMessage(Colors.RED + "Please enter a time between 5 seconds and 5 minutes.");
		}
	}
}
