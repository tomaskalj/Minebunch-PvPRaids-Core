package com.minebunch.core.jedis.cache;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.utils.SerializeUtil;
import com.minebunch.core.utils.data.AtomicString;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileCache implements JedisCache<UUID, CoreProfile>{
    private Map<UUID, CoreProfile> uuidToProfile = new HashMap<>();

    public CoreProfile getProfile(UUID uuid) {
        if (uuidToProfile.containsKey(uuid)) {
            return uuidToProfile.get(uuid);
        }

        // Use an atomic string here because a different thread is being used to get the name
        AtomicString atomic = new AtomicString();

        CorePlugin.getInstance().getJedisManager().runCommand(redis -> {
            atomic.setString(redis.hget("profile", uuid.toString()));
        });

        if (atomic.getString() == null) {
            return null;
        } else {
            return (CoreProfile) SerializeUtil.deserialize(atomic.getString());
        }
    }

    public void fetch() {
        CorePlugin.getInstance().getJedisManager().runCommand((redis) -> {
            Map<String, String> cached = redis.hgetAll("profile");

            if (cached == null || cached.isEmpty()) {
                return;
            }

            Map<UUID, CoreProfile> profileMap = new HashMap<>();

            for (Map.Entry<String, String> entry : cached.entrySet()) {
                profileMap.put(UUID.fromString(entry.getKey()),
                        (CoreProfile)SerializeUtil.deserialize(entry.getValue()));
            }

            uuidToProfile = profileMap;
        });
    }

    public void write(UUID uuid, CoreProfile profile) {
        uuidToProfile.put(uuid, profile);

        CorePlugin.getInstance().getJedisManager().runCommand((redis) -> {
            redis.hset("profiles", uuid.toString(), SerializeUtil.serialize(profile));
        });
    }
}
