package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FeedCommand extends PlayerCommand {
	public FeedCommand() {
		super("feed", Rank.ADMIN);
	}

	@Override
	public void execute(Player player, String[] args) {
		Player target = args.length < 1 || Bukkit.getPlayer(args[0]) == null ? player : Bukkit.getPlayer(args[0]);

		if (target.isDead()) {
			player.sendMessage(Colors.RED + "You can't feed a dead player.");
			return;
		}

		target.setFoodLevel(20);
		target.setSaturation(5.0F);
		target.sendMessage(Colors.GREEN + "You have been fed.");

		if (target != player) {
			player.sendMessage(Colors.GREEN + "Fed " + target.getDisplayName() + Colors.GREEN + ".");
		}
	}
}
