package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HealCommand extends PlayerCommand {
    public HealCommand() {
        super("heal", Rank.ADMIN);
    }

    @Override
    public void execute(Player player, String[] args) {
        Player target = args.length < 1 || Bukkit.getPlayer(args[0]) == null ? player : Bukkit.getPlayer(args[0]);

        if (target.isDead()) {
            player.sendMessage(Colors.RED + "You can't heal a dead player.");
            return;
        }

        target.setHealth(target.getMaxHealth());
        target.sendMessage(Colors.GREEN + "You have been healed.");

        if (target != player) {
            player.sendMessage(Colors.GREEN + "Healed " + target.getDisplayName() + Colors.GREEN + ".");
        }
    }
}
