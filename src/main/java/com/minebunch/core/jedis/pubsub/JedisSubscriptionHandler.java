package com.minebunch.core.jedis.pubsub;

import com.google.gson.JsonObject;
import com.minebunch.core.jedis.JedisManager;
import com.minebunch.core.jedis.json.payloads.PayloadType;
import com.minebunch.core.jedis.json.payloads.handler.JsonPayloadHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;


@RequiredArgsConstructor
public class JedisSubscriptionHandler {
    private final JedisManager manager;

    public void processJson(JsonObject json) {
        String payload = json.get("payload").getAsString();
        PayloadType type;

        try {
            type = PayloadType.valueOf(payload);
        } catch (IllegalArgumentException ex) {
            Bukkit.getLogger().warning("Tried to process JSON with invalid payload type: " + payload);
            return;
        }

        JsonPayloadHandler handler = manager.getHandlerByType(type);

        if (handler == null) {
            Bukkit.getLogger().warning("Tried to process JSON payload with no registered handler: " + payload);
            return;
        }

        JsonObject data = json.get("data").getAsJsonObject();

        handler.handlePayload(data);
    }
}
