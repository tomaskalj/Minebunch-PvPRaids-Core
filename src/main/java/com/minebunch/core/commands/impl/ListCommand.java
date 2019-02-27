package com.minebunch.core.commands.impl;

import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.player.PlayerList;
import org.bukkit.command.CommandSender;

public class ListCommand extends BaseCommand {
	public ListCommand() {
		super("list");
		setAliases("online", "players", "who");
	}

	@Override
	protected void execute(CommandSender sender, String[] args) {
		PlayerList onlinePlayerList = PlayerList.newList().sortedByRank().getVisible();
		int playerCount = onlinePlayerList.size();

		sender.sendMessage(Rank.ORDERED_RANKS);
		sender.sendMessage(onlinePlayerList.asColoredNames() + " (" + playerCount + " " + (playerCount == 1 ? "player" : "players") + " online)");
	}
}
