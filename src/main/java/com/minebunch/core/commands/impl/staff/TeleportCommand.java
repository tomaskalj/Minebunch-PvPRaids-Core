package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Messages;
import org.bukkit.entity.Player;

public class TeleportCommand extends PlayerCommand {
	private final CorePlugin plugin;

	public TeleportCommand(CorePlugin plugin) {
		super("tp", Rank.ADMIN);
		this.plugin = plugin;
		setAliases("teleport");
		setUsage(Colors.RED + "Usage: /teleport <player> [player]");
	}

	private static boolean isOffline(Player checker, Player target) {
		if (target == null) {
			checker.sendMessage(Messages.PLAYER_NOT_FOUND);
			return true;
		}

		return false;
	}

	private void teleport(Player to, Player from) {
		to.teleport(from);
		to.sendMessage(Colors.GREEN + "You have been teleported to " + from.getName() + ".");

		CoreProfile fromProfile = plugin.getProfileManager().getProfile(from.getUniqueId());

		if (fromProfile.hasStaff()) {
			from.sendMessage(Colors.GREEN + to.getName() + " has been teleported to you.");
		}
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return;
		}

		Player target = plugin.getServer().getPlayer(args[0]);

		if (isOffline(player, target)) {
			return;
		}

		if (args.length < 2) {
			teleport(player, target);
		} else {
			Player target2 = plugin.getServer().getPlayer(args[1]);

			if (isOffline(player, target2)) {
				return;
			}

			teleport(target, target2);

			player.sendMessage(Colors.GREEN + "Teleported " + target.getName() + " to " + target2.getName() + ".");
		}
	}
}
