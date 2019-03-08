package com.minebunch.core.commands;

import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerCommand extends BaseCommand {
    protected PlayerCommand(String name, Rank requiredRank) {
        super(name, requiredRank);
    }

    protected PlayerCommand(String name) {
        super(name, Rank.MEMBER);
    }

    @Override
    protected final void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            execute((Player) sender, args);
        } else {
            sender.sendMessage(Colors.RED + "Only players can perform this command.");
        }
    }

    public abstract void execute(Player player, String[] args);
}
