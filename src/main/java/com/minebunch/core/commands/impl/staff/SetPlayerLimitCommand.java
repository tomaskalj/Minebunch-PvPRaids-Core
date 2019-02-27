package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.NumberUtil;
import com.minebunch.core.utils.ServerUtil;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class SetPlayerLimitCommand extends BaseCommand {
	public SetPlayerLimitCommand() {
		super("setplayerlimit", Rank.ADMIN);
		setUsage(Colors.RED + "/setplayerlimit <count>");
		setAliases("playerlimit");
	}

	@Override
	protected void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(usageMessage);
			return;
		}

		Integer count = NumberUtil.getInteger(args[0]);

		if (count == null || count < 1 || count > 1000) {
			sender.sendMessage(Colors.RED + "Player limit must be between 1 and 1,000.");
			return;
		}

		ServerUtil.setMaxPlayers(Bukkit.getServer(), count);
		sender.sendMessage(Colors.GREEN + "Player limit updated to " + count + ".");
	}
}
