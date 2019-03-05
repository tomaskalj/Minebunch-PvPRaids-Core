package com.minebunch.core.jedis.pubsub;

import com.google.gson.JsonObject;
import com.minebunch.core.CorePlugin;
import com.minebunch.core.event.player.PlayerTagChangeEvent;
import com.minebunch.core.jedis.json.JsonMessages;
import com.minebunch.core.jedis.json.JsonPayloadType;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;

public class JedisSubscriptionHandler {

    public void processJson(JsonObject json){
        JsonPayloadType type;

        try{
            type = JsonPayloadType.valueOf(json.get("type").getAsString());
        }catch (IllegalArgumentException e){
            CorePlugin.getInstance().getLogger().warning("Could not parse an incoming Json object!");
            return;
        }

        JsonObject data = json.get("data").getAsJsonObject();

        switch(type){
            case STAFF_CHAT: {
                String serverName = data.get("server_name").getAsString();
                String playerRank = data.get("player_rank").getAsString();
                String playerName = data.get("player_name").getAsString();
                String playerMessage = data.get("message").getAsString();

                Rank rank = Rank.getByName(playerRank);
                if (rank == null){
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

            case STAFF_JOIN: {
                String serverName = data.get("server_name").getAsString();
                String playerRank = data.get("player_rank").getAsString();
                String playerName = data.get("player_name").getAsString();

                Rank rank = Rank.getByName(playerRank);
                if (rank == null){
                    CorePlugin.getInstance().getLogger().warning("Invalid rank name passed in Json message!");
                    return;
                }

                String message = JsonMessages.STAFF_JOIN
                        .replace("{server_name}", serverName)
                        .replace("{player_rank_colour}", rank.getColor())
                        .replace("{player_name}", playerName);

                CorePlugin.getInstance().getStaffManager().messageStaff(message);
            }

            case RANK_CHANGE: {

            }
        }
    }
}
