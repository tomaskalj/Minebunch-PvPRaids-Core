package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public GameModeCommand(CorePlugin plugin) {
        super("gamemode", Rank.ADMIN);
        this.plugin = plugin;
        setAliases("gm");
        setUsage(Colors.RED + "Usage: /gamemode <mode|id> [player]");
    }

    private static GameMode parseGameMode(String arg) {
        switch (arg.toLowerCase()) {
            case "survival":
            case "s":
            case "0":
            default:
                return GameMode.SURVIVAL;
            case "creative":
            case "c":
            case "1":
                return GameMode.CREATIVE;
            case "adventure":
            case "a":
            case "2":
                return GameMode.ADVENTURE;
        }
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        GameMode mode = parseGameMode(args[0]);
        Player target = args.length < 2 || plugin.getServer().getPlayer(args[1]) == null ? player : plugin.getServer().getPlayer(args[1]);
        String modeName = StringUtils.capitalize(mode.name().toLowerCase());

        target.setGameMode(mode);
        target.sendMessage(Colors.PRIMARY + "Your game mode has been set to " + Colors.SECONDARY + modeName + Colors.PRIMARY + ".");

        if (target != player) {
            player.sendMessage(Colors.PRIMARY + "Set " + target.getDisplayName() + Colors.PRIMARY + "'s game mode to "
                    + Colors.SECONDARY + modeName + Colors.PRIMARY + ".");
        }
    }
}
