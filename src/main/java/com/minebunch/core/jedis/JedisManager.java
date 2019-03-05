package com.minebunch.core.jedis;

import com.google.gson.JsonObject;
import com.minebunch.core.CorePlugin;
import com.minebunch.core.jedis.pubsub.JedisPublisher;
import com.minebunch.core.jedis.pubsub.JedisSubscriber;
import com.minebunch.core.jedis.pubsub.JedisSubscriptionHandler;
import com.minebunch.core.utils.config.ConfigReader;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Getter
public class JedisManager {

    private JedisSettings settings;
    private JedisPool pool;
    private JedisPublisher publisher;
    private JedisSubscriber subscriber;

    public JedisManager(){
        ConfigReader reader = new ConfigReader(CorePlugin.getInstance().getConfig(), null);

        String host = reader.getString("redis.host");
        int port = reader.getInt("redis.port");
        String password = reader.getString("redis.password");

        settings = new JedisSettings(host, port, password);
        pool = new JedisPool(settings.getAddress(), settings.getPort());

        try(Jedis jedis = pool.getResource()){
            if (settings.hasPassword()) jedis.auth(settings.getPassword());
            publisher = new JedisPublisher(settings);
            subscriber = new JedisSubscriber("core", settings, new JedisSubscriptionHandler());
        }
    }

    public void write(JsonMessageType payload, JsonObject data) {
        JsonObject object = new JsonObject();

        object.addProperty("payload", payload.name());
        object.add("data", data == null ? new JsonObject() : data);

        publisher.writeToChannel("core", object);
    }


    // This will come in handy when implementing an UUID cache
    @SuppressWarnings("deprecation")
    public void runCommand(JedisCommand redisCommand) {
        Jedis jedis = pool.getResource();

        try {
            redisCommand.executeCommand(jedis);
        } catch (Exception e) {
            e.printStackTrace();

            if (jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }
}
