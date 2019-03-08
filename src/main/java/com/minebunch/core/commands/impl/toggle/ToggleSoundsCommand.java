package com.minebunch.core.commands.impl.toggle;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.entity.Player;

public class ToggleSoundsCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public ToggleSoundsCommand(CorePlugin plugin) {
        super("togglesounds");
        this.plugin = plugin;
        setAliases("sounds", "ts");
    }

    @Override
    public void execute(Player player, String[] args) {
        CoreProfile profile = plugin.getProfileManager().getProfile(player);
        boolean playingSounds = !profile.isPlayingSounds();

        profile.setPlayingSounds(playingSounds);
        player.sendMessage(playingSounds ? Colors.GREEN + "Sounds enabled." : Colors.RED + "Sounds disabled.");
    }
}
