package com.minebunch.core.jedis.json.payloads.handler.impl;

import com.google.gson.JsonObject;
import com.minebunch.core.CorePlugin;
import com.minebunch.core.jedis.json.payloads.PayloadType;
import com.minebunch.core.jedis.json.payloads.handler.JsonPayloadHandler;
import com.minebunch.core.punishment.Punishment;
import com.minebunch.core.utils.TaskUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PunishmentHandler implements JsonPayloadHandler{

    @Override
    public void handlePayload(JsonObject object) {
        UUID uuid = UUID.fromString(object.get("punishment_uuid").getAsString());
        String staffName = object.get("staff_name").getAsString();
        String targetName = object.get("target_name").getAsString();
        Punishment punishment = new Punishment();

        punishment.setPunishmentUuid(uuid);
        punishment.load(CorePlugin.getInstance().getMongoStorage().getDocumentByFilter(
                "punishments", "punishment_uuid", uuid));
        punishment.broadcast(targetName, staffName);

        Player player = Bukkit.getPlayer(punishment.getTargetUuid());
        if (player == null) {
            return;
        }

        CorePlugin.getInstance().getPunishmentManager().addPunishment(player.getUniqueId(), punishment);

        if (!punishment.isActive())return;
        if (punishment.isBan()) {
            // Separate messages for direct bans and shared bans
            String message;
            if (punishment.isShared()) {
                message = punishment.getType().getSharedMessage().replace("{player}", punishment.getAltName());
            }else{
                message = punishment.getType().getMessage();
            }

            String result = message.replace("{expire}", punishment.getTimeLeft())
                    .replace("{server_name}", CorePlugin.getInstance().getCoreConfig().getServerName())
                    .replace("{server_site}", CorePlugin.getInstance().getCoreConfig().getSiteName());

            TaskUtil.runSync(CorePlugin.getInstance(), () -> player.kickPlayer(result));
        } else {
            player.sendMessage(ChatColor.RED + "You have been muted.");
            player.sendMessage(ChatColor.RED + "Time left: " + punishment.getTimeLeft());
        }
    }

    @Override
    public PayloadType getType() {
        return PayloadType.PUNISHMENT;
    }
}
