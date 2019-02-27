package com.minebunch.core.commands.impl;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.event.player.PlayerMessageEvent;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.utils.StringUtil;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Messages;
import org.bukkit.entity.Player;

public class MessageCommand extends PlayerCommand {
	private final CorePlugin plugin;

	public MessageCommand(CorePlugin plugin) {
		super("message");
		this.plugin = plugin;
		setAliases("msg", "m", "whisper", "w", "tell");
		setUsage(Colors.RED + "Usage: /message <player> <message>");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(usageMessage);
			return;
		}

		Player target = plugin.getServer().getPlayer(args[0]);

		if (target == null) {
			player.sendMessage(Messages.PLAYER_NOT_FOUND);
			return;
		}

		CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

		if (targetProfile.hasPlayerIgnored(player.getUniqueId())) {
			player.sendMessage(Colors.RED + "That player is ignoring you!");
			return;
		}

		plugin.getServer().getPluginManager().callEvent(new PlayerMessageEvent(player, target, StringUtil.buildString(args, 1)));
	}
}
