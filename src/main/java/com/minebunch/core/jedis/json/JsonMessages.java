package com.minebunch.core.jedis.json;

import net.md_5.bungee.api.ChatColor;

public final class JsonMessages {
    public static final String STAFF_CHAT = ChatColor.AQUA + "[Staff] " + ChatColor.YELLOW + "[{server_name}] " + "{player_rank_colour}"
            + "{player_name}" + ChatColor.RESET + ": {message}";
    public static final String STAFF_JOIN = ChatColor.AQUA + "[Staff] " + "{player_rank_colour}" + "{player_name}"
            + ChatColor.GREEN + " joined " + ChatColor.AQUA + "{server_name}";
}
