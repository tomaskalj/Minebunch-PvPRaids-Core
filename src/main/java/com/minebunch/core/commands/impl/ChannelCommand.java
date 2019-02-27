package com.minebunch.core.commands.impl;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.entity.Player;

public class ChannelCommand extends PlayerCommand {
	private static final String CHANNEL_REGEX = "^(https?\\:\\/\\/)?(www\\.)?(youtube\\.com|youtu\\.?be)\\/.+$";
	private final CorePlugin plugin;

	public ChannelCommand(CorePlugin plugin) {
		super("channel", Rank.MEDIA);
		this.plugin = plugin;
		setUsage(Colors.RED + "Usage: /channel <url|reset>");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return;
		}

		String arg = args[0].toLowerCase();
		CoreProfile profile = plugin.getProfileManager().getProfile(player);

		if (arg.equals("reset")) {
			profile.setYouTubeUrl(null);
			player.sendMessage(Colors.GREEN + "Your channel has been reset.");
			return;
		}

		if (!arg.matches(CHANNEL_REGEX)) {
			player.sendMessage(Colors.RED + "You must enter a valid YouTube channel URL!");
			return;
		}

		profile.setYouTubeUrl(arg);
		player.sendMessage(Colors.GREEN + "Successfully updated your YouTube channel URL!");
	}
}
