package com.minebunch.core.managers;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.player.CoreProfile;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ProfileManager {
    private final Map<UUID, CoreProfile> profiles = new ConcurrentHashMap<>();

    public CoreProfile createProfile(String name, UUID id, String address) {
        CoreProfile profile = new CoreProfile(name, id, address);
        profiles.put(id, profile);
        return profile;
    }

    public CoreProfile getProfile(String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        CoreProfile playerData;

        if (target == null) {
            UUID uuid = CorePlugin.getInstance().getUuidCache().getUuid(playerName);
            if (uuid != null) {
                playerData = getProfile(uuid);
            } else {
                return null;
            }
        } else {
            playerData = getProfile(target.getUniqueId());
        }

        return playerData;
    }

    public CoreProfile getProfile(UUID id) {
        return profiles.get(id);
    }

    public CoreProfile getProfile(Player player) {
        return getProfile(player.getUniqueId());
    }

    public void removeProfile(UUID id) {
        profiles.remove(id);
    }

    public void saveProfiles() {
        for (CoreProfile profile : profiles.values()) {
            profile.save(false);
        }
    }
}
