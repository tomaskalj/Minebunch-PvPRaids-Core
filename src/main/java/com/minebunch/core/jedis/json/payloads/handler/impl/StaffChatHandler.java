package com.minebunch.core.jedis.json.payloads.handler.impl;

import com.google.gson.JsonObject;
import com.minebunch.core.CorePlugin;
import com.minebunch.core.jedis.json.JsonMessages;
import com.minebunch.core.jedis.json.payloads.PayloadType;
import com.minebunch.core.jedis.json.payloads.handler.JsonPayloadHandler;
import com.minebunch.core.player.rank.Rank;

public class StaffChatHandler implements JsonPayloadHandler {

    @Override
    public void handlePayload(JsonObject object) {
        String serverName = object.get("server_name").getAsString();
        String playerRank = object.get("player_rank").getAsString();
        String playerName = object.get("player_name").getAsString();
        String playerMessage = object.get("message").getAsString();

        Rank rank = Rank.getByName(playerRank);
        if (rank == null) {
            CorePlugin.getInstance().getLogger().warning("Invalid rank name passed in Json message!");
            return;
        }

        String message = JsonMessages.STAFF_CHAT
                .replace("{server_name}", serverName)
                .replace("{player_rank_colour}", rank.getColor())
                .replace("{player_name}", playerName)
                .replace("{message}", playerMessage);

        CorePlugin.getInstance().getStaffManager().messageStaff(message);
    }

    @Override
    public PayloadType getType() {
        return PayloadType.STAFF_CHAT;
    }
}
