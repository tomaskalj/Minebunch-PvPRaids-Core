package com.minebunch.core.punishment;

import com.minebunch.core.CorePlugin;
import com.mongodb.client.MongoCursor;
import lombok.Getter;
import org.bson.Document;

import java.util.*;

@Getter
public class PunishmentManager {

    private Map<UUID, Set<Punishment>> punishmentMap = new HashMap<>();

    public Set<Punishment> getPunishments(UUID playerUuid) {
        return punishmentMap.get(playerUuid);
    }

    public void loadPunishments(UUID playerUuid) {
        punishmentMap.put(playerUuid, getFromDatabase(playerUuid));
    }

    public Set<Punishment> getFromDatabase(UUID playerUuid) {
        Set<Punishment> punishments = new HashSet<>();
        try (MongoCursor<Document> cursor = CorePlugin.getInstance().getMongoStorage()
                .getDocumentsByFilter("punishments", "target_uuid", playerUuid.toString())) {

            cursor.forEachRemaining(document -> {
                Punishment punishment = new Punishment();
                punishment.load(document);
                punishments.add(punishment);
            });
        }
        return punishments;
    }

    public void addPunishment(UUID playerUuid, Punishment punishment) {
        Set<Punishment> punishments = punishmentMap.get(playerUuid);
        punishments.removeIf(check -> check.getPunishmentUuid().equals(punishment.getPunishmentUuid()));
        punishments.add(punishment);
    }

    public Punishment getActiveBan(UUID playerUuid) {
        return searchPunishment(playerUuid, true);
    }

    public Punishment getActiveBan(UUID playerUuid, String loginAddress) {
        Punishment ban = searchPunishment(playerUuid, true);

        // If there is no direct ban, check if the address is banned
        if (ban == null) {
            ban = getActiveAddressBan(playerUuid, loginAddress);
        }
        return ban;
    }

    public Punishment getActiveMute(UUID playerUuid) {
        return searchPunishment(playerUuid, false);
    }

    private Punishment searchPunishment(UUID playerUuid, boolean ban) {
        Set<Punishment> punishments;
        if (punishmentMap.containsKey(playerUuid)) {
            punishments = getPunishments(playerUuid);
        } else {
            punishments = getFromDatabase(playerUuid);
        }
        for (Punishment punishment : punishments) {
            if (ban) {
                if (punishment.isBan() && punishment.isActive()) {
                    return punishment;
                }
            } else {
                if (!punishment.isBan() && punishment.isActive()) {
                    return punishment;
                }
            }
        }
        return null;
    }

    private Set<Punishment> searchPunishmentsByAddress(String address) {
        Set<Punishment> punishments = new HashSet<>();
        try (MongoCursor<Document> cursor = CorePlugin.getInstance().getMongoStorage()
                .getDocumentsByFilter("punishments", "target_address", address)) {

            cursor.forEachRemaining(document -> {
                Punishment punishment = new Punishment();
                punishment.load(document);
                punishments.add(punishment);
            });
        }
        return punishments;
    }

    private Punishment getActiveAddressBan(UUID playerUuid, String loginAdress) {
        Set<Punishment> sharedPunishments = searchPunishmentsByAddress(loginAdress);
        for (Punishment punishment : sharedPunishments) {
            if (punishment.isBan() && punishment.isActive()) {
                return createSharedPunishment(punishment, playerUuid);
            }
        }
        return null;
    }

    public Punishment createSharedPunishment(Punishment punishment, UUID altUuid) {
        Punishment sharedPunishment = new Punishment();
        sharedPunishment.setType(punishment.getType());
        sharedPunishment.setPunishmentUuid(punishment.getPunishmentUuid());
        sharedPunishment.setTargetUuid(altUuid);
        sharedPunishment.setTargetAddress(punishment.getTargetAddress());
        sharedPunishment.setAddedBy(punishment.getAddedBy());
        sharedPunishment.setAddedReason(punishment.getAddedReason());
        sharedPunishment.setRemovedBy(punishment.getRemovedBy());
        sharedPunishment.setRemoveReason(punishment.getRemoveReason());
        sharedPunishment.setTimestamp(punishment.getTimestamp());
        sharedPunishment.setExpiration(punishment.getExpiration());
        sharedPunishment.setHidden(punishment.isHidden());
        sharedPunishment.setSilent(punishment.isSilent());
        sharedPunishment.setAltUuid(punishment.getTargetUuid());
        sharedPunishment.setAltName(punishment.getTargetName());
        sharedPunishment.setRemovedBy(punishment.getRemovedBy());
        sharedPunishment.setRemoveReason(punishment.getRemoveReason());
        return sharedPunishment;
    }
}
