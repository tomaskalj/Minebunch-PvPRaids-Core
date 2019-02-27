package com.minebunch.core.commands.impl;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.player.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PingCommand extends PlayerCommand {
	public PingCommand() {
		super("ping");
	}

	@Override
	public void execute(Player player, String[] args) {
		Player target = args.length < 1 || Bukkit.getPlayer(args[0]) == null ? player : Bukkit.getPlayer(args[0]);
		int targetPing = PlayerUtil.getPing(target);

		if (target == player) {
			player.sendMessage(Colors.PRIMARY + "Your ping is " + Colors.SECONDARY + targetPing + Colors.PRIMARY + " ms.");
		} else {
			int difference = targetPing - PlayerUtil.getPing(player);
			String name = target.getDisplayName();

			player.sendMessage(name + Colors.PRIMARY + "'s ping is " + Colors.SECONDARY + targetPing + Colors.PRIMARY + " ms "
					+ Colors.ACCENT + "(" + (difference > 0 ? "+" : "") + difference + " difference)" + Colors.PRIMARY + ".");
		}
	}
}
