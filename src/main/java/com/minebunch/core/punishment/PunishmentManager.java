package com.minebunch.core.punishment;

import com.minebunch.core.CorePlugin;
import com.mongodb.client.MongoCursor;
import lombok.Getter;
import org.bson.Document;

import java.util.*;

@Getter
public class PunishmentManager {

    private Map<UUID, Set<Punishment>> punishmentMap = new HashMap<>();

    public Set<Punishment> getPunishments(UUID playerUuid){
        return punishmentMap.get(playerUuid);
    }

    public void loadPunishments(UUID playerUuid){
        punishmentMap.put(playerUuid, getFromDatabase(playerUuid));
    }

    public Set<Punishment> getFromDatabase(UUID playerUuid){
        Set<Punishment> punishments = new HashSet<>();
        try (MongoCursor<Document> cursor = CorePlugin.getInstance().getMongoStorage()
                .getDocumentsByFilter("punishments", "target_uuid", playerUuid.toString())){

                cursor.forEachRemaining(document -> { Punishment punishment = new Punishment();
                punishment.load(document);
                punishments.add(punishment);
            });
        }
        return punishments;
    }

    public void addPunishment(UUID playerUuid, Punishment punishment){
        punishmentMap.get(playerUuid).add(punishment);
    }

    public Punishment getActiveBan(UUID playerUuid){
        return searchPunishment(playerUuid, true);
    }

    public Punishment getActiveMute(UUID playerUuid){
        return searchPunishment(playerUuid, false);
    }

    private Punishment searchPunishment(UUID playerUuid, boolean ban){
        Set<Punishment> punishments;
        if (punishmentMap.containsKey(playerUuid)) {
            punishments = getPunishments(playerUuid);
        }else{
            punishments = getFromDatabase(playerUuid);
        }
        for (Punishment punishment : punishments) {
            if (ban){
                if (punishment.isBan() && punishment.isActive()) return punishment;
            }else{
                if (!punishment.isBan() && punishment.isActive()) return punishment;
            }
        }
        return null;
    }
}
