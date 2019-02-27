package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.NumberUtil;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TpPosCommand extends PlayerCommand {
	public TpPosCommand() {
		super("tppos", Rank.ADMIN);
		setUsage(Colors.RED + "Usage: /tppos <x> [y] <z>");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(usageMessage);
			return;
		}

		if (args.length == 2) {
			Double x = NumberUtil.getDouble(args[0]);
			Double z = NumberUtil.getDouble(args[1]);

			if (x == null || z == null) {
				player.sendMessage(Colors.RED + "You must provide valid coordinates.");
				return;
			}

			player.teleport(new Location(player.getWorld(), x, 100, z));
		} else if (args.length == 3) {
			Double x = NumberUtil.getDouble(args[0]);
			Double y = NumberUtil.getDouble(args[1]);
			Double z = NumberUtil.getDouble(args[2]);

			if (x == null || y == null || z == null) {
				player.sendMessage(Colors.RED + "You must provide valid coordinates.");
				return;
			}

			player.teleport(new Location(player.getWorld(), x, y, z));
		}
	}
}
