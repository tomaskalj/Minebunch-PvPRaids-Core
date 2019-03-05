package com.minebunch.core.jedis.pubsub;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.minebunch.core.jedis.JedisSettings;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

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
                    JedisSubscriber.this.subscriptionHandler.processJson(object);
                } catch (JsonParseException e) {
                    System.out.println("Could not parse Json message!");
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
