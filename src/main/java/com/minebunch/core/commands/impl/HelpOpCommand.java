package com.minebunch.core.commands.impl;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.utils.StringUtil;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.time.timer.Timer;
import org.bukkit.entity.Player;

public class HelpOpCommand extends PlayerCommand {
	private final CorePlugin plugin;

	public HelpOpCommand(CorePlugin plugin) {
		super("helpop");
		this.plugin = plugin;
		setUsage(Colors.RED + "/helpop <help message>");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return;
		}

		CoreProfile profile = plugin.getProfileManager().getProfile(player);
		Timer cooldownTimer = profile.getReportCooldownTimer();

		if (cooldownTimer.isActive()) {
			player.sendMessage(Colors.RED + "You can't request assistance for another " + cooldownTimer.formattedExpiration() + ".");
			return;
		}

		String request = StringUtil.buildString(args, 0);

		plugin.getStaffManager().messageStaff(Colors.RED + "\n(HelpOp) " + Colors.SECONDARY + player.getName()
				+ Colors.PRIMARY + " requested assistance: " + Colors.SECONDARY + request + Colors.PRIMARY + ".\n ");

		player.sendMessage(Colors.GREEN + "Request sent: " + Colors.R + request);
	}
}
