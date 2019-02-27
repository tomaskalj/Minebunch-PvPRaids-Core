package com.minebunch.core.managers;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class StaffManager {
	private static final String STAFF_PREFIX = Colors.SECONDARY + "[Staff] ";
	@Getter
	private final Set<UUID> cachedStaff = new HashSet<>();
	private final CorePlugin plugin;

	public void addCachedStaff(CoreProfile profile) {
		cachedStaff.add(profile.getId());
	}

	public boolean isInStaffCache(CoreProfile profile) {
		return cachedStaff.contains(profile.getId());
	}

	public void removeCachedStaff(CoreProfile profile) {
		cachedStaff.remove(profile.getId());
	}

	public void messageStaff(Rank requiredRank, String msg) {
		for (UUID uuid : cachedStaff) {
			CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(uuid);

			if (profile.hasRank(requiredRank)) {
				Player loopPlayer = plugin.getServer().getPlayer(profile.getId());

				if (loopPlayer != null && loopPlayer.isOnline()) {
					loopPlayer.sendMessage(msg);
				}
			}
		}
	}

	public void messageStaff(String msg) {
		messageStaff(Rank.LOWEST_STAFF, msg);
	}

	public void messageStaffWithPrefix(String msg) {
		messageStaff(STAFF_PREFIX + msg);
	}

	public void messageStaff(String displayName, String msg) {
		String formattedMsg = STAFF_PREFIX + displayName + Colors.R + ": " + msg;
		messageStaff(formattedMsg);
	}

	public void hideVanishedStaffFromPlayer(Player player) {
		if (!plugin.getProfileManager().getProfile(player).hasStaff()) {
			for (UUID uuid : cachedStaff) {
				CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(uuid);

				if (profile.isVanished()) {
					Player loopPlayer = plugin.getServer().getPlayer(profile.getId());

					if (loopPlayer != null && loopPlayer.isOnline()) {
						player.hidePlayer(loopPlayer);
					}
				}
			}
		}
	}
}
