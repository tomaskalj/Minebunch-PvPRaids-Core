package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends BaseCommand {
	private final CorePlugin plugin;

	public VanishCommand(CorePlugin plugin) {
		super("vanish", Rank.ADMIN);
		setAliases("v");
		this.plugin = plugin;
	}

	@Override
	protected void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		CoreProfile profile = plugin.getProfileManager().getProfile(player);
		boolean vanished = !profile.isVanished();

		profile.setVanished(vanished);

		if (vanished) {
			for (Player online : plugin.getServer().getOnlinePlayers()) {
				CoreProfile onlineProfile = CorePlugin.getInstance().getProfileManager().getProfile(online.getUniqueId());

				if (!onlineProfile.hasRank(Rank.ADMIN)) {
					online.hidePlayer(player);
				}
			}

			String oldListName = player.getPlayerListName();
			String newListName = Colors.WHITE + "* " + Colors.R + oldListName;

			player.setPlayerListName(newListName);
		} else {
			for (Player online : plugin.getServer().getOnlinePlayers()) {
				if (!online.canSee(player)) {
					online.showPlayer(player);
				}
			}

			String listName = player.getPlayerListName();

			if (listName.contains("*")) {
				player.setPlayerListName(listName.replace("* ", ""));
			}
		}

		player.sendMessage(vanished ? Colors.GREEN + "Poof, you vanished." : Colors.RED + "You're visible again.");
	}
}
