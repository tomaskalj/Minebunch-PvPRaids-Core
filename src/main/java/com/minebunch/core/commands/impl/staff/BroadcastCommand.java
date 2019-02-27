package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.StringUtil;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand extends BaseCommand {
	private final CorePlugin plugin;

	public BroadcastCommand(CorePlugin plugin) {
		super("broadcast", Rank.ADMIN);
		this.plugin = plugin;
		setAliases("bc");
		setUsage(Colors.RED + "Usage: /broadcast <message> [-r]");
	}

	@Override
	protected void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(usageMessage);
			return;
		}

		String message = Colors.RED + "[Alert] " + Colors.R
				+ ChatColor.translateAlternateColorCodes('&', StringUtil.buildString(args, 0)).trim();

		if (message.endsWith(" -r")) {
			message = message.substring(12, message.length() - 3).trim();
		}

		plugin.getServer().broadcastMessage(message);
	}
}
