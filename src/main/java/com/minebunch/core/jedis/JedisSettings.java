package com.minebunch.core.jedis;

import lombok.Data;

@Data
public class JedisSettings {

    private final String address;
    private final int port;
    private final String password;

    public boolean hasPassword() {
        return password != null && !password.isEmpty();
    }
}
