package com.minebunch.core.event.player;

import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
public class PlayerRankChangeEvent extends PlayerEvent {
	private static final HandlerList HANDLERS = new HandlerList();
	private final CoreProfile profile;
	private final Rank newRank, oldRank;

	public PlayerRankChangeEvent(Player who, CoreProfile profile, Rank newRank, Rank oldRank) {
		super(who);
		this.profile = profile;
		this.newRank = newRank;
		this.oldRank = oldRank;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
}
