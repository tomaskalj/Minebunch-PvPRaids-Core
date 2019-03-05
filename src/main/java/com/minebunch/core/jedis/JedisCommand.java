package com.minebunch.core.jedis;

import redis.clients.jedis.Jedis;

public interface JedisCommand {
    void executeCommand(Jedis jedis);
}
