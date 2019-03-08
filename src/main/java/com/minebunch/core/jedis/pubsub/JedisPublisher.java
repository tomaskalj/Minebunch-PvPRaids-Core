package com.minebunch.core.jedis.pubsub;

import com.google.gson.JsonObject;
import com.minebunch.core.CorePlugin;
import org.apache.commons.lang.Validate;

public class JedisPublisher {
    public void writeToChannel(String channel, JsonObject message) {
        Validate.notNull(CorePlugin.getInstance().getJedisManager().getPool());
        CorePlugin.getInstance().getJedisManager().runCommand(redis -> redis.publish(channel, message.toString()));
    }
}

