package com.minebunch.core.jedis.cache;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.utils.ProfileUtil;
import com.minebunch.core.utils.TaskUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class UUIDCache implements JedisCache<String, UUID> {
    private Map<String, UUID> nameToUuid = new HashMap<>();
    private Map<UUID, String> uuidToName = new HashMap<>();

    public UUIDCache() {
        startRunnable();
    }

    public String getName(UUID uuid) {
        if (uuidToName.containsKey(uuid)) {
            return uuidToName.get(uuid);
        }

        // Use an atomic string here because a different thread is being used to get the name
        AtomicReference<String> atomicString = new AtomicReference<>();

        CorePlugin.getInstance().getJedisManager().runCommand((redis) -> {
            atomicString.set(redis.hget("uuid-to-name", uuid.toString()));
        });

        // If Redis does not have uuid cached, try to look through the Mojang API. Else return the result from Redis
        if (atomicString.get() == null) {
            ProfileUtil.MojangProfile mojangProfile = ProfileUtil.lookupProfile(uuid);
            if (mojangProfile != null) {
                return mojangProfile.getName();
            } else {
                return "Unknown";
            }
        } else {
            return atomicString.get();
        }
    }

    private void startRunnable() {
        // Runnable every minute to fetch from Redis
        TaskUtil.runAsyncRepeating(CorePlugin.getInstance(),
                () -> {
                    // Check for exceptions during the fetching process
                    try {
                        this.fetch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 20 * 60 * 2);
    }

    public UUID getUuid(String name) {
        if (nameToUuid.containsKey(name.toLowerCase())) {
            return nameToUuid.get(name.toLowerCase());
        }

        // Use an atomic string here because a different thread is being used to get the name
        AtomicReference<String> atomicString = new AtomicReference<>();

        CorePlugin.getInstance().getJedisManager().runCommand(redis -> {
            atomicString.set(redis.hget("name-to-uuid", name.toLowerCase()));
        });

        // If Redis does not have name cached, try to look through the Mojang API. Else return the result from Redis
        if (atomicString.get() == null) {
            ProfileUtil.MojangProfile mojangProfile = ProfileUtil.lookupProfile(name);
            if (mojangProfile != null) {
                return mojangProfile.getId();
            } else {
                return null;
            }
        } else {
            return UUID.fromString(atomicString.get());
        }
    }

    public void fetch() {
        CorePlugin.getInstance().getJedisManager().runCommand((redis) -> {
            Map<String, String> cached = redis.hgetAll("name-to-uuid");

            if (cached == null || cached.isEmpty()) {
                return;
            }

            Map<String, UUID> ntu = new HashMap<>();
            Map<UUID, String> utn = new HashMap<>();

            for (Map.Entry<String, String> entry : cached.entrySet()) {
                ntu.put(entry.getKey(), UUID.fromString(entry.getValue()));
                utn.put(UUID.fromString(entry.getValue()), entry.getKey());
            }

            nameToUuid = ntu;
            uuidToName = utn;
        });
    }

    public void write(String name, UUID uuid) {
        if (!CorePlugin.getInstance().getJedisManager().isActive()) return;

        nameToUuid.put(name.toLowerCase(), uuid);
        uuidToName.put(uuid, name);

        CorePlugin.getInstance().getJedisManager().runCommand(redis -> {
            redis.hset("name-to-uuid", name.toLowerCase(), uuid.toString());
            redis.hset("uuid-to-name", uuid.toString(), name);
        });
    }
}
