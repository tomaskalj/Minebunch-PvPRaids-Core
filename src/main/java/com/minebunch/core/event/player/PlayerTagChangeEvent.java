package com.minebunch.core.event.player;

import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
public class PlayerTagChangeEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final CoreProfile profile;
    private final Rank oldTag, newTag;

    public PlayerTagChangeEvent(Player who, CoreProfile profile, Rank oldTag, Rank newTag) {
        super(who);
        this.profile = profile;
        this.oldTag = oldTag;
        this.newTag = newTag;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
