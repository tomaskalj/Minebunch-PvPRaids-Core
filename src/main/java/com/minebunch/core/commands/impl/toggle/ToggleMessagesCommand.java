package com.minebunch.core.commands.impl.toggle;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.entity.Player;

public class ToggleMessagesCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public ToggleMessagesCommand(CorePlugin plugin) {
        super("togglemessages");
        this.plugin = plugin;
        setAliases("tpm", "togglepm");
    }

    @Override
    public void execute(Player player, String[] args) {
        CoreProfile profile = plugin.getProfileManager().getProfile(player);
        boolean messaging = !profile.isMessaging();

        profile.setMessaging(messaging);
        player.sendMessage(messaging ? Colors.GREEN + "Messages enabled." : Colors.RED + "Messages disabled.");
    }
}
