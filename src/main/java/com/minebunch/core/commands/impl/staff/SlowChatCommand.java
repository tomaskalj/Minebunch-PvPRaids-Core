package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.NumberUtil;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.command.CommandSender;

public class SlowChatCommand extends BaseCommand {
	private final CorePlugin plugin;

	public SlowChatCommand(CorePlugin plugin) {
		super("slowchat", Rank.MOD);
		this.plugin = plugin;
		setUsage(Colors.RED + "Usage: /slowchat <seconds|disable>");
	}

	@Override
	protected void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(usageMessage);
			return;
		}

		String arg = args[0];

		switch (arg.toLowerCase()) {
			case "off":
			case "toggle":
			case "disable":
				if (!plugin.getServerManager().isSlowChatEnabled()) {
					sender.sendMessage(Colors.RED + "Slow chat is already disabled!");
				} else {
					plugin.getServerManager().disableSlowChat();
					plugin.getServer().broadcastMessage(Colors.RED + "Slow chat has been disabled by " + sender.getName() + ".");
				}
				break;
			default:
				Integer time = NumberUtil.getInteger(arg);

				if (time == null || time < 5 || time > 60) {
					sender.sendMessage(Colors.RED + "You must enter a valid time between 5 and 60 seconds.");
				} else {
					plugin.getServerManager().setSlowChatTime(time);
					plugin.getServer().broadcastMessage(Colors.YELLOW + "Slow chat has been enabled and set to " + time
							+ " seconds by " + sender.getName() + ".");
				}
				break;
		}
	}
}
