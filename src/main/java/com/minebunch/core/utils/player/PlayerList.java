package com.minebunch.core.utils.player;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerList {
	private static final Comparator<Player> RANK_COMPARATOR = (a, b) -> {
		CoreProfile profileA = CorePlugin.getInstance().getProfileManager().getProfile(a);
		CoreProfile profileB = CorePlugin.getInstance().getProfileManager().getProfile(b);
		return profileA.getRank().compareTo(profileB.getRank());
	};

	@Getter
	private final List<Player> onlinePlayers;

	public static PlayerList newList() {
		return new PlayerList(new ArrayList<>(CorePlugin.getInstance().getServer().getOnlinePlayers()));
	}

	public int size() {
		return onlinePlayers.size();
	}

	public PlayerList sortedByRank() {
		onlinePlayers.sort(RANK_COMPARATOR);
		return this;
	}

	public PlayerList getVisible() {
		onlinePlayers.removeIf(player -> CorePlugin.getInstance().getProfileManager().getProfile(player).isVanished());
		return this;
	}

	public List<Player> playersWithRank(Rank rank) {
		return onlinePlayers.stream()
				.filter(player -> CorePlugin.getInstance().getProfileManager().getProfile(player).getRank() == rank)
				.collect(Collectors.toList());
	}

	public String asColoredNames() {
		return onlinePlayers.stream()
				.map(CorePlugin.getInstance().getProfileManager()::getProfile)
				.map(profile -> profile.getRank().getColor() + profile.getName())
				.collect(Collectors.joining(Colors.R + ", ")) + Colors.R;
	}
}
