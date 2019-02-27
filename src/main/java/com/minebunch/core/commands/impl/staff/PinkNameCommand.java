package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.storage.database.MongoRequest;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Messages;
import com.minebunch.core.utils.player.PlayerUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PinkNameCommand extends PlayerCommand {
	private final CorePlugin plugin;

	public PinkNameCommand(CorePlugin plugin) {
		super("pinkname", Rank.ADMIN);
		this.plugin = plugin;
		setUsage(Colors.RED + "Usage: /pinkname <on|off> <player>");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(usageMessage);
			return;
		}

		String arg = args[0].toLowerCase();

		if (!arg.equals("off") && !arg.equals("on")) {
			player.sendMessage(Colors.RED + "You must specify to turn the pink username on or off.");
			return;
		}

		boolean pinkName = !arg.equals("off");
		String name = args[1];

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			OfflinePlayer target = plugin.getServer().getOfflinePlayer(name);

			if (target == null) {
				player.sendMessage(Messages.PLAYER_NOT_FOUND);
				return;
			}

			if (target.isOnline()) {
				CoreProfile profile = plugin.getProfileManager().getProfile(target.getUniqueId());
				profile.setPinkName(pinkName);
				PlayerUtil.setPinkName(player, pinkName);
				target.getPlayer().sendMessage(pinkName ? Colors.GREEN + "You now have a pink name." : Colors.RED + "You no longer have a pink name.");
			} else {
				MongoRequest.newRequest("players", target.getUniqueId())
						.put("pink_name", pinkName)
						.run();
			}

			player.sendMessage(pinkName ? Colors.GREEN + "Set target's name to pink." : Colors.RED + "Removed target's pink name.");
		});
	}
}
