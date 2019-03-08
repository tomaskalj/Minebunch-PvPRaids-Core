package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.rank.Rank;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends PlayerCommand {
    public SetSpawnCommand() {
        super("spawn", Rank.ADMIN);
    }

    @Override
    public void execute(Player player, String[] args) {
        Location location = player.getLocation();
        player.getWorld().setSpawnLocation(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
    }
}
