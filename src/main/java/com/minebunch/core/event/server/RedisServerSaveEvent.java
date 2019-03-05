package com.minebunch.core.event.server;

import com.minebunch.core.server.ServerModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;

public @Data @AllArgsConstructor class RedisServerSaveEvent extends Event {

    private ServerModel server;

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
