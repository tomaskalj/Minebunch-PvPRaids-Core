package com.minebunch.core.utils.message;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;

@UtilityClass
public class Strings {
    // random strings
    public final String SPLITTER = "┃";
    public final String SEPARATOR = Colors.GRAY + Colors.S + StringUtils.repeat("-", 20);

    // when sent to a player who is using the cracked Vape client, it will notify the server that they are cheating
    public final String CRACKED_VAPE_MESSAGE = "§8 §8 §1 §3 §3 §7 §8 §r";

    // messages
    public static final String PLAYER_NOT_FOUND = Colors.RED + "Player not found.";
    public static final String DATA_LOAD_FAIL = Colors.RED + "Your data failed to load; try logging in again.\n" +
            "If that doesn't fix the issue, please contact a staff member.";
}
