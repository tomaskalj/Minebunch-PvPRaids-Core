package com.minebunch.core.commands.impl;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.utils.StringUtil;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Strings;
import com.minebunch.core.utils.time.timer.Timer;
import org.bukkit.entity.Player;

public class ReportCommand extends PlayerCommand {
	private final CorePlugin plugin;

	public ReportCommand(CorePlugin plugin) {
		super("report");
		this.plugin = plugin;
		setUsage(Colors.RED + "Usage: /report <player> <reason>");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(usageMessage);
			return;
		}

		Player target = plugin.getServer().getPlayer(args[0]);

		if (target == null) {
			player.sendMessage(Strings.PLAYER_NOT_FOUND);
			return;
		}

		if (player == target) {
			player.sendMessage(Colors.RED + "You can't report yourself!");
			return;
		}

		CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

		if (targetProfile.hasStaff()) {
			player.sendMessage(Colors.RED + "You can't report a staff members. Contact a higher-ranked staff member to report them.");
			return;
		}

		CoreProfile profile = plugin.getProfileManager().getProfile(player);
		Timer cooldownTimer = profile.getReportCooldownTimer();

		if (cooldownTimer.isActive()) {
			player.sendMessage(Colors.RED + "You can't report a player for another " + cooldownTimer.formattedExpiration() + ".");
			return;
		}

		String report = StringUtil.buildString(args, 1);

		plugin.getStaffManager().messageStaff("");
		plugin.getStaffManager().messageStaff(Colors.RED + "[Report] " + Colors.SECONDARY + player.getName() + Colors.PRIMARY
				+ " reported " + Colors.SECONDARY + target.getName() + Colors.PRIMARY + " for " + Colors.SECONDARY + report + Colors.PRIMARY + ".");
		plugin.getStaffManager().messageStaff("");

		player.sendMessage(Colors.GREEN + "Report sent for " + target.getDisplayName() + Colors.GREEN + ": " + Colors.R + report);
	}
}
