package com.minebunch.core.player;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.storage.database.MongoRequest;
import com.minebunch.core.utils.time.timer.Timer;
import com.minebunch.core.utils.time.timer.impl.DoubleTimer;
import com.minebunch.core.utils.time.timer.impl.IntegerTimer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Setter
@Getter
public class CoreProfile implements Serializable{
	private final List<UUID> ignored = new ArrayList<>();
	private final List<String> knownAddresses = new ArrayList<>();

	private final String name;
	private final UUID id;

	private Rank rank = Rank.MEMBER;
	private Rank tag;
	private Date firstLogin;
	private Date lastLogin;


	private boolean playingSounds = true;
	private boolean messaging = true;
	private boolean globalChatEnabled = true;
	private boolean inStaffChat;

	private transient UUID converser;
	private transient Location lastLocation;
	private transient boolean vanished;

	private transient final Timer commandCooldownTimer = new DoubleTimer(1);
	private transient final Timer reportCooldownTimer = new IntegerTimer(TimeUnit.SECONDS, 60);

	private transient long lastChatTime;
	private transient Timer chatCooldownTimer;

	// TODO: optimize loading and saving
	@SuppressWarnings("unchecked")
	public CoreProfile(String name, UUID id, String address) {
		this.name = name;
		this.id = id;
		this.knownAddresses.add(address);

		CorePlugin.getInstance().getMongoStorage().getOrCreateDocument("players", id, (document, exists) -> {
			if (exists) {
				this.inStaffChat = document.getBoolean("staff_chat_enabled", inStaffChat);
				this.messaging = document.getBoolean("messaging_enabled", messaging);
				this.playingSounds = document.getBoolean("playing_sounds", playingSounds);

				String tagName = document.getString("tag_name");

				Rank tag = Rank.getByName(tagName);

				if (tag != null) {
					this.tag = tag;
				}

				String rankName = document.get("rank_name", rank.name());
				Rank rank = Rank.getByName(rankName);

				if (rank != null) {
					this.rank = rank;
				}

				List<UUID> ignored = (List<UUID>) document.get("ignored_ids");

				if (ignored != null) {
					this.ignored.addAll(ignored);
				}

				List<String> knownAddresses = (List<String>) document.get("known_addresses");

				if (knownAddresses != null) {
					for (String knownAddress : knownAddresses) {
						if (knownAddress.equals(address)) {
							continue;
						}

						this.knownAddresses.add(knownAddress);
					}
				}

				this.firstLogin = document.getDate("first_login");
				this.lastLogin = document.getDate("last_login");
			}

			save(false);
		});
	}

	public void save(boolean async) {
		MongoRequest request = MongoRequest.newRequest("players", id)
				.put("name", name)
				.put("lowername", name.toLowerCase())
				.put("staff_chat_enabled", inStaffChat)
				.put("messaging_enabled", messaging)
				.put("playing_sounds", playingSounds)
				.put("rank_name", rank.name())
				.put("ignored_ids", ignored)
				.put("known_addresses", knownAddresses)
				.put("first_login", firstLogin)
				.put("last_login", lastLogin)
				.put("tag_name", tag == null ? "null" : tag.name());

		if (async) {
			CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), request::run);
		} else {
			request.run();
		}
	}

	public Timer getChatCooldownTimer() {
		if (chatCooldownTimer == null) {
			if (hasDonor()) {
				chatCooldownTimer = new DoubleTimer(1);
			} else {
				chatCooldownTimer = new DoubleTimer(3);
			}
		}

		return chatCooldownTimer;
	}

	public String getChatFormat() {
		String rankColor = getVisibleRank().getColor();
		return String.format(getVisibleRank().getRawFormat(), rankColor) + name;
	}

	public Rank getVisibleRank() {
		return tag == null ? rank : tag;
	}

	public void updateLastChatTime() {
		lastChatTime = System.currentTimeMillis();
	}

	public boolean hasRank(Rank requiredRank) {
		return rank.ordinal() <= requiredRank.ordinal();
	}

	public boolean hasStaff() {
		return hasRank(Rank.LOWEST_STAFF);
	}

	public boolean hasDonor() {
		return hasRank(Rank.LOWEST_DONOR);
	}

	public void ignore(UUID id) {
		ignored.add(id);
	}

	public void unignore(UUID id) {
		ignored.remove(id);
	}

	public boolean hasPlayerIgnored(UUID id) {
		return ignored.contains(id);
	}
}
