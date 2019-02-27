package com.minebunch.core.task;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.player.PlayerList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class HighRollerTask implements Runnable {
	private final CorePlugin plugin;

	@Override
	public void run() {
		List<Player> highRollerPlayers = PlayerList.newList().playersWithRank(Rank.HIGHROLLER);

		if (highRollerPlayers.size() > 0) {
			plugin.getServer().broadcastMessage(Colors.GOLD + "Online HighRollers: " +
					highRollerPlayers.stream()
							.map(player -> Colors.YELLOW + player.getName())
							.collect(Collectors.joining(Colors.R + ", ")));
		}
	}
}
