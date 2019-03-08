package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.entity.Player;

public class MuteChatCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public MuteChatCommand(CorePlugin plugin) {
        super("mutechat", Rank.MOD);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        boolean globalChatMuted = !plugin.getServerManager().isGlobalChatMuted();

        plugin.getServerManager().setGlobalChatMuted(globalChatMuted);
        plugin.getServer().broadcastMessage(globalChatMuted ? Colors.RED + "Global chat has been muted by " + player.getName() + "."
                : Colors.GREEN + "Global chat has been enabled by " + player.getName() + ".");
    }
}
