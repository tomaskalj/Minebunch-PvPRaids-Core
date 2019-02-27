package com.minebunch.core.commands.impl;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.event.player.PlayerTagChangeEvent;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;

public class TagCommand extends PlayerCommand {
	private final CorePlugin plugin;

	public TagCommand(CorePlugin plugin) {
		super("tag", Rank.MOD);
		this.plugin = plugin;
		setUsage(Colors.RED + "Usage: /tag <rank|reset>");
	}

	@Override
	public void execute(Player player, String[] args) {
		CoreProfile profile = plugin.getProfileManager().getProfile(player);

		if (args.length < 1) {
			player.sendMessage(usageMessage);

			String ranks = Arrays.stream(Rank.values())
					.filter(profile::hasRank)
					.map(rank -> rank.getColor() + rank.getName() + Colors.R)
					.collect(Collectors.joining(", "));

			player.sendMessage(Colors.PRIMARY + "Available tags: " + ranks);
			return;
		}

		String subCommand = args[0].toLowerCase();

		if (subCommand.equals("reset")) {
			PlayerTagChangeEvent event = new PlayerTagChangeEvent(player, profile, profile.getTag(), null);
			plugin.getServer().getPluginManager().callEvent(event);
			return;
		}

		Rank newTag = Rank.getByName(subCommand);

		if (newTag == null) {
			player.sendMessage(Colors.RED + "That rank doesn't exist.");
			return;
		}


		if (!profile.hasRank(newTag)) {
			player.sendMessage(Colors.RED + "You can't use a tag of a rank you don't have.");
			return;
		}

		PlayerTagChangeEvent event = new PlayerTagChangeEvent(player, profile, profile.getTag(), newTag);
		plugin.getServer().getPluginManager().callEvent(event);
	}
}
