package com.minebunch.core.jedis.cache;

public interface JedisCache<K, V> {
    void fetch();

    void write(K key, V value);
}
