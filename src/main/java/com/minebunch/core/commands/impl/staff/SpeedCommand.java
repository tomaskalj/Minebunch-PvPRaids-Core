package com.minebunch.core.commands.impl.staff;

import com.google.common.base.Objects;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.NumberUtil;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpeedCommand extends PlayerCommand {
	public SpeedCommand() {
		super("speed", Rank.ADMIN);
		setUsage(Colors.RED + "Usage: /speed <speed|reset> [player]");
	}

	private static int parseSpeed(boolean flying, String arg) {
		int defaultSpeed = flying ? 1 : 2;

		if (arg.toLowerCase().equals("reset")) {
			return defaultSpeed;
		}

		return Objects.firstNonNull(NumberUtil.getInteger(arg), defaultSpeed);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return;
		}

		Player target = args.length < 2 || Bukkit.getPlayer(args[1]) == null ? player : Bukkit.getPlayer(args[1]);
		boolean flying = target.getAllowFlight();
		int speed = parseSpeed(flying, args[0]);

		if (speed < 0 || speed > 10) {
			player.sendMessage(Colors.RED + "You must enter a valid speed from 0 to 10!");
			return;
		}

		float actualSpeed = 0.1F * speed;

		if (flying) {
			target.setFlySpeed(actualSpeed);
		} else {
			target.setWalkSpeed(actualSpeed);
		}

		target.sendMessage(Colors.PRIMARY + "Your " + (flying ? "fly" : "walk") + " speed has been set to "
				+ Colors.SECONDARY + speed + Colors.PRIMARY + ".");

		if (target != player) {
			player.sendMessage(Colors.PRIMARY + "Set " + target.getDisplayName() + Colors.PRIMARY + "'s "
					+ (flying ? "fly" : "walk") + " speed to " + Colors.SECONDARY + speed + Colors.PRIMARY + ".");
		}
	}
}
