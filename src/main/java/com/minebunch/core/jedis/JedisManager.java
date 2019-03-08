package com.minebunch.core.jedis;

import com.google.gson.JsonObject;
import com.minebunch.core.CorePlugin;
import com.minebunch.core.jedis.json.payloads.JsonPayload;
import com.minebunch.core.jedis.json.payloads.PayloadType;
import com.minebunch.core.jedis.json.payloads.handler.JsonPayloadHandler;
import com.minebunch.core.jedis.json.payloads.handler.impl.PunishmentHandler;
import com.minebunch.core.jedis.json.payloads.handler.impl.StaffChatHandler;
import com.minebunch.core.jedis.json.payloads.handler.impl.StaffJoinHandler;
import com.minebunch.core.jedis.pubsub.JedisPublisher;
import com.minebunch.core.jedis.pubsub.JedisSubscriber;
import com.minebunch.core.jedis.pubsub.JedisSubscriptionHandler;
import com.minebunch.core.utils.storage.Config;
import java.util.EnumMap;
import java.util.function.Consumer;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Getter
public class JedisManager {
    private final EnumMap<PayloadType, JsonPayloadHandler> payloadHandlers = new EnumMap<>(PayloadType.class);
    private final JedisPool pool;
    private final JedisPublisher publisher;
    private final JedisSubscriber subscriber;

    public JedisManager() {
        registerPayloadHandler(new StaffJoinHandler());
        registerPayloadHandler(new StaffChatHandler());
        registerPayloadHandler(new PunishmentHandler());

        Config config = CorePlugin.getInstance().getCoreConfig();
        String host = config.getString("redis.host");
        int port = config.getInt("redis.port");
        String password = config.getString("redis.password");

        JedisSettings settings = new JedisSettings(host, port, password);
        pool = new JedisPool(settings.getAddress(), settings.getPort());

        try (Jedis jedis = pool.getResource()) {
            if (settings.hasPassword()) {
                jedis.auth(settings.getPassword());
            }

            publisher = new JedisPublisher();
            subscriber = new JedisSubscriber("core", settings, new JedisSubscriptionHandler(this));
        }
    }

    public void close() {
        pool.close();
    }

    public boolean isActive() {
        return this.pool != null && !this.pool.isClosed();
    }

    public JsonPayloadHandler getHandlerByType(PayloadType type) {
        return payloadHandlers.get(type);
    }

    public void registerPayloadHandler(JsonPayloadHandler handler) {
        payloadHandlers.put(handler.getType(), handler);
    }

    public void write(JsonPayload payload) {
        JsonObject object = new JsonObject();

        object.addProperty("payload", payload.getType().name());
        object.add("data", payload.getData());

        publisher.writeToChannel("core", object);
    }

    public void runCommand(Consumer<Jedis> command) {
        try (Jedis jedis = pool.getResource()) {
            command.accept(jedis);
        }
    }
}
