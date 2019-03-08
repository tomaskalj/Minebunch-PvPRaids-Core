package com.minebunch.core.event.server;

import com.minebunch.core.server.ServerModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public @Data
@AllArgsConstructor
class RedisServerSaveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private ServerModel server;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}
