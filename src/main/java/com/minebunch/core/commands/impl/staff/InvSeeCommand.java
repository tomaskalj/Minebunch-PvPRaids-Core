package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class InvSeeCommand extends PlayerCommand {
    public InvSeeCommand() {
        super("invsee", Rank.ADMIN);
        setUsage(Colors.RED + "Usage: /invsee <player>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Strings.PLAYER_NOT_FOUND);
            return;
        }

        player.openInventory(target.getInventory());
    }
}
