package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.player.rank.Rank;
import org.bukkit.command.CommandSender;

public class RanksCommand extends BaseCommand {
	public RanksCommand() {
		super("ranks", Rank.MOD);
	}

	@Override
	protected void execute(CommandSender sender, String[] args) {
		sender.sendMessage("Ranks: " + Rank.ORDERED_RANKS);
	}
}
