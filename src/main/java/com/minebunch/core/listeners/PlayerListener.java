package com.minebunch.core.listeners;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.event.player.PlayerRankChangeEvent;
import com.minebunch.core.event.player.PlayerTagChangeEvent;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.StringUtil;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Strings;
import com.minebunch.core.utils.time.TimeUtil;
import com.minebunch.core.utils.time.timer.Timer;
import java.util.Date;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.PermissionAttachment;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
	private static final String[] DISALLOWED_PERMISSIONS = {
			"bukkit.command.version", "bukkit.command.plugins", "bukkit.command.help", "bukkit.command.tps",
			"minecraft.command.tell", "minecraft.command.me", "minecraft.command.help"
	};
	private final CorePlugin plugin;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoginBeforeEnable(AsyncPlayerPreLoginEvent event) {
		if (!CorePlugin.isServerEnabled()) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Colors.RED + "The server is still starting up.");
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPreLogin(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED &&
				(plugin.getPlayerManager().isNameOnline(event.getName()) || plugin.getPlayerManager().getOnlineByIp(event.getAddress()) > 3)) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Colors.RED + "You're already online!");
			return;
		}

		if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
			plugin.getProfileManager().createProfile(event.getName(), event.getUniqueId(), event.getAddress().getHostAddress());
		} else if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_FULL) {
			CoreProfile profile = plugin.getProfileManager().createProfile(event.getName(), event.getUniqueId(), event.getAddress().getHostAddress());

			if (profile.hasDonor()) {
				event.allow();
			} else {
				plugin.getProfileManager().removeProfile(event.getUniqueId());
			}
		} else if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST) {
			CoreProfile profile = plugin.getProfileManager().createProfile(event.getName(), event.getUniqueId(), event.getAddress().getHostAddress());

			if (profile.hasStaff()) {
				event.allow();
			} else {
				plugin.getProfileManager().removeProfile(event.getUniqueId());
			}
		}
	}

	private void addPerm(PermissionAttachment attachment, String perm) {
		attachment.setPermission(perm, true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		CoreProfile profile = plugin.getProfileManager().getProfile(player);

		boolean cleanup = false;

		if (profile == null) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Strings.DATA_LOAD_FAIL);
			return;
		} else if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
			if (profile.hasDonor()) {
				event.allow();
			} else {
				cleanup = true;
			}
		} else if (event.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
			if (profile.hasStaff()) {
				event.allow();
			} else {
				cleanup = true;
			}
		} else if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
			cleanup = true;
		}

		if (cleanup) {
			plugin.getProfileManager().removeProfile(player.getUniqueId());
			return;
		}

		PermissionAttachment attachment = player.addAttachment(plugin);

		if (!profile.hasRank(Rank.ADMIN)) {
			for (String permission : DISALLOWED_PERMISSIONS) {
				attachment.setPermission(permission, false);
			}
		}

		if (profile.hasRank(Rank.ADMIN)) {
			addPerm(attachment, "litebans.*");
			addPerm(attachment, "litebans.notify");
		} else if (profile.hasRank(Rank.MOD)) {
			addPerm(attachment, "litebans.tempmute");
			addPerm(attachment, "litebans.unban.queue");
			addPerm(attachment, "litebans.kick");
			addPerm(attachment, "litebans.warn");
			addPerm(attachment, "litebans.history");
			addPerm(attachment, "litebans.staffhistory");
			addPerm(attachment, "litebans.banlist");
			addPerm(attachment, "litebans.checkban");
			addPerm(attachment, "litebans.checkmute");
			addPerm(attachment, "litebans.lastuuid");
			addPerm(attachment, "litebans.dupeip");
			addPerm(attachment, "litebans.notify");
		}

		Rank visibleRank = profile.getVisibleRank();

		visibleRank.apply(player);

		if (profile.hasStaff()) {
			plugin.getStaffManager().addCachedStaff(profile);
		}

		Date today = new Date();
		if (profile.getFirstLogin() == null) {
			profile.setFirstLogin(today);
		} else {
			profile.setLastLogin(today);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Player player = event.getPlayer();

		plugin.getPlayerManager().addPlayer(player);

		CoreProfile profile = plugin.getProfileManager().getProfile(player);

		plugin.getStaffManager().hideVanishedStaffFromPlayer(player);

		if (profile.hasStaff()) {
			plugin.getStaffManager().messageStaffWithPrefix(profile.getChatFormat() + Colors.PRIMARY + " joined the server.");
		}
	}

	private void onDisconnect(Player player) {
		plugin.getPlayerManager().removePlayer(player);

		CoreProfile profile = plugin.getProfileManager().getProfile(player);

		// in case disconnect is somehow called twice
		if (profile == null) {
			return;
		}

		if (profile.hasStaff()) {
			plugin.getStaffManager().removeCachedStaff(profile);
			plugin.getStaffManager().messageStaffWithPrefix(profile.getChatFormat() + Colors.PRIMARY + " left the server.");
		}

		profile.save(true);
		plugin.getProfileManager().removeProfile(player.getUniqueId());
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		event.setLeaveMessage(null);

		onDisconnect(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		onDisconnect(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		CoreProfile profile = plugin.getProfileManager().getProfile(player);
		String msg = event.getMessage();

		if (!profile.hasStaff()) {
			if (plugin.getServerManager().isGlobalChatMuted()) {
				event.setCancelled(true);
				player.sendMessage(Colors.RED + "Global chat is currently muted.");
				return;
			} else if (plugin.getServerManager().isSlowChatEnabled()) {
				long lastChatTime = profile.getLastChatTime();

				if (lastChatTime != 0) {
					int slowChatTime = plugin.getServerManager().getSlowChatTime();
					long sum = lastChatTime + (slowChatTime * 1000);

					if (sum > System.currentTimeMillis()) {
						event.setCancelled(true);
						String diff = TimeUtil.formatTimeMillis(sum - System.currentTimeMillis());
						player.sendMessage(Colors.RED + "Slow chat is currently enabled. You can talk again in " + diff + ".");
						return;
					}
				}
			}

			Timer timer = profile.getChatCooldownTimer();

			if (timer.isActive()) {
				event.setCancelled(true);
				player.sendMessage(Colors.RED + "You can't chat for another " + timer.formattedExpiration() + ".");
				return;
			}
		} else if (profile.isInStaffChat()) {
			event.setCancelled(true);
			plugin.getStaffManager().messageStaff(profile.getChatFormat(), msg);
			return;
		}

		if (plugin.getFilter().isFiltered(msg)) {
			if (profile.hasStaff()) {
				player.sendMessage(Colors.RED + "That would have been filtered.");
			} else {
				event.setCancelled(true);

				String formattedMessage = profile.getChatFormat() + Colors.R + ": " + msg;

				plugin.getStaffManager().messageStaff(Colors.RED + "(Filtered) " + formattedMessage);
				player.sendMessage(formattedMessage);
				return;
			}
		}

		Iterator<Player> recipients = event.getRecipients().iterator();

		while (recipients.hasNext()) {
			Player recipient = recipients.next();
			CoreProfile recipientProfile = plugin.getProfileManager().getProfile(recipient.getUniqueId());

			if (recipientProfile == null) {
				continue;
			}

			if (recipientProfile.hasPlayerIgnored(player.getUniqueId())
					|| (!recipientProfile.isGlobalChatEnabled() && (!profile.hasStaff() || recipientProfile.hasStaff()))) {
				recipients.remove();
			}
		}

		event.setFormat(profile.getChatFormat() + Colors.R + ": %2$s");

		profile.updateLastChatTime();
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		CoreProfile profile = plugin.getProfileManager().getProfile(player);

		profile.setLastLocation(event.getFrom());
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().startsWith("/op")) {
			event.getPlayer().sendMessage(Colors.RED + "This command is disabled.");
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onRankChange(PlayerRankChangeEvent event) {
		Player player = event.getPlayer();
		CoreProfile profile = event.getProfile();
		Rank newRank = event.getNewRank();

		profile.setRank(newRank);

		if (profile.hasStaff()) {
			if (!plugin.getStaffManager().isInStaffCache(profile)) {
				plugin.getStaffManager().addCachedStaff(profile);
			}
		} else if (plugin.getStaffManager().isInStaffCache(profile)) {
			plugin.getStaffManager().removeCachedStaff(profile);
		}

		newRank.apply(player);
		PlayerTagChangeEvent tagChangeEvent = new PlayerTagChangeEvent(player, profile, profile.getTag(), null);
		plugin.getServer().getPluginManager().callEvent(tagChangeEvent);

		player.sendMessage(Colors.GREEN + "You now have the " + newRank.getColor() + newRank.getName() + Colors.GREEN + " rank!");
	}

	@EventHandler
	public void onTagChange(PlayerTagChangeEvent event) {
		Player player = event.getPlayer();
		CoreProfile profile = event.getProfile();
		Rank newTag = event.getNewTag();

		profile.setTag(newTag);

		Rank visibleRank = profile.getVisibleRank();

		player.setPlayerListName(visibleRank.getColor() + player.getName());

		if (newTag == null) {
			player.sendMessage(Colors.GREEN + "Success! Your tag has been reset.");
		} else {
			player.sendMessage(Colors.GREEN + "Success! You now look like " + StringUtil.getCorrectArticle(newTag.getName())
					+ " " + newTag.getColor() + newTag.getName() + Colors.GREEN + ".");
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		CoreProfile profile = plugin.getProfileManager().getProfile(player);

		if (profile.isVanished()) {
			event.setCancelled(true);
		}
	}
}
