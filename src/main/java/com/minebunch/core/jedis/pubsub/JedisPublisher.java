package com.minebunch.core.jedis.pubsub;

import com.google.gson.JsonObject;
import com.minebunch.core.CorePlugin;
import com.minebunch.core.jedis.JedisSettings;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;

@RequiredArgsConstructor
public class JedisPublisher {

    private final JedisSettings jedisSettings;

    public void writeToChannel(String channel, JsonObject message) {
        Validate.notNull(CorePlugin.getInstance().getJedisManager().getPool());

        CorePlugin.getInstance().getJedisManager().runCommand(redis -> {
            if (jedisSettings.hasPassword()) {
                redis.auth(jedisSettings.getPassword());
            }

            redis.publish(channel, message.toString());
        });
    }
}

