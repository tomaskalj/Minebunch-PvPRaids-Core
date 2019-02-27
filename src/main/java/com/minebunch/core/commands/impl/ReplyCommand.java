package com.minebunch.core.commands.impl;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.event.player.PlayerMessageEvent;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.utils.StringUtil;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.entity.Player;

public class ReplyCommand extends PlayerCommand {
	private final CorePlugin plugin;

	public ReplyCommand(CorePlugin plugin) {
		super("reply");
		this.plugin = plugin;
		setAliases("r");
		setUsage(Colors.RED + "Usage: /reply <message>");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return;
		}

		CoreProfile profile = plugin.getProfileManager().getProfile(player);

		Player target = plugin.getServer().getPlayer(profile.getConverser());

		if (target == null) {
			player.sendMessage(Colors.RED + "You are not in a conversation.");
			return;
		}

		CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

		if (targetProfile.hasPlayerIgnored(player.getUniqueId())) {
			player.sendMessage(Colors.RED + "That player is ignoring you!");
			return;
		}

		plugin.getServer().getPluginManager().callEvent(new PlayerMessageEvent(player, target, StringUtil.buildString(args, 0)));
	}
}
