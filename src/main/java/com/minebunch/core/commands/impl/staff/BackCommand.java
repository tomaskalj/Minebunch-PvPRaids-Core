package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BackCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public BackCommand(CorePlugin plugin) {
        super("back", Rank.MOD);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        CoreProfile profile = plugin.getProfileManager().getProfile(player);
        Location last = profile.getLastLocation();

        if (last != null) {
            player.teleport(last);
            player.sendMessage(Colors.GREEN + "Teleported to last location.");
        } else {
            player.sendMessage(Colors.RED + "You don't have a last location stored.");
        }
    }
}
