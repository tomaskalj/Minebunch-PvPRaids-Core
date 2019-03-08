package com.minebunch.core.jedis.cache;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.utils.ProfileUtil;
import com.minebunch.core.utils.data.AtomicString;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;

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
        AtomicString atomic = new AtomicString();
        CorePlugin.getInstance().getJedisManager().runCommand((redis) -> {
            atomic.setString(redis.hget("uuid-to-name", uuid.toString()));
        });

        // If Redis does not have uuid cached, try to look through the Mojang API. Else return the result from Redis
        if (atomic.getString() == null) {
            ProfileUtil.MojangProfile mojangProfile = ProfileUtil.lookupProfile(uuid);
            if (mojangProfile != null) {
                return mojangProfile.getName();
            } else {
                return "Unknown";
            }
        } else {
            return atomic.getString();
        }
    }

    private void startRunnable() {
        // Runnable every minute to fetch from Redis
        Bukkit.getScheduler().runTaskTimerAsynchronously(CorePlugin.getInstance(), () -> {
            // Check for exceptions during the fetching process
            try {
                this.fetch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, 20L * 60L * 2L);
    }

    public UUID getUuid(String name) {
        if (nameToUuid.containsKey(name.toLowerCase())) {
            return nameToUuid.get(name.toLowerCase());
        }

        // Use an atomic string here because a different thread is being used to get the name
        AtomicString atomic = new AtomicString();

        CorePlugin.getInstance().getJedisManager().runCommand(redis -> {
            atomic.setString(redis.hget("name-to-uuid", name.toLowerCase()));
        });

        // If Redis does not have name cached, try to look through the Mojang API. Else return the result from Redis
        if (atomic.getString() == null) {
            ProfileUtil.MojangProfile mojangProfile = ProfileUtil.lookupProfile(name);
            if (mojangProfile != null) {
                return mojangProfile.getId();
            } else {
                return null;
            }
        } else {
            return UUID.fromString(atomic.getString());
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

        CorePlugin.getInstance().getJedisManager().runCommand((redis) -> {
            redis.hset("name-to-uuid", name.toLowerCase(), uuid.toString());
            redis.hset("uuid-to-name", uuid.toString(), name);
        });
    }
}
