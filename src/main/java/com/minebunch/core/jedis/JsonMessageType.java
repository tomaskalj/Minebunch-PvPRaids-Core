package com.minebunch.core.jedis;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@Getter
public enum JsonMessageType {
    STAFF_CHAT(ChatColor.AQUA + "[Staff]: " + ChatColor.YELLOW + "[{server_name}]" + "{player_rank_colour}"
            + "{player_name}" + ChatColor.GREEN + ": " + ChatColor.AQUA + "{message}" + ""),
    STAFF_JOIN(ChatColor.AQUA + "[Staff]: " + "{player_rank_colour}" + "{player_name}"
            + ChatColor.GREEN + " joined " + ChatColor.AQUA + "{server_name}");

    private String template;
    JsonMessageType(String template){
        this.template = template;
    }
}
