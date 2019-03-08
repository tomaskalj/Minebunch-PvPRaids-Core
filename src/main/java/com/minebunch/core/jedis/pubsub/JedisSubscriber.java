package com.minebunch.core.jedis.pubsub;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.minebunch.core.jedis.JedisSettings;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@Getter
public class JedisSubscriber {
    private static final JsonParser JSON_PARSER = new JsonParser();
    private final String channel;
    private final Jedis jedis;
    private JedisPubSub pubSub;
    private JedisSubscriptionHandler subscriptionHandler;

    public JedisSubscriber(String channel, JedisSettings settings, JedisSubscriptionHandler subscriptionHandler) {
        this.channel = channel;
        this.subscriptionHandler = subscriptionHandler;

        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    JsonObject object = JSON_PARSER.parse(message).getAsJsonObject();
                    subscriptionHandler.processJson(object);
                } catch (JsonParseException e) {
                    throw new RuntimeException("Could not parse incoming JSON object", e);
                }
            }
        };

        jedis = new Jedis(settings.getAddress(), settings.getPort());

        if (settings.hasPassword()) {
            jedis.auth(settings.getPassword());
        }

        // Make sure that subscriptions are handled on a different thread
        new Thread(() -> jedis.subscribe(pubSub, channel)).start();
    }

    public void close() {
        if (pubSub != null) {
            pubSub.unsubscribe();
        }

        if (jedis != null) {
            jedis.close();
        }
    }
}
