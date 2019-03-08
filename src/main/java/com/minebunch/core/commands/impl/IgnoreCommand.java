package com.minebunch.core.commands.impl;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Strings;
import org.bukkit.entity.Player;

public class IgnoreCommand extends PlayerCommand {
    private final CorePlugin plugin;

    public IgnoreCommand(CorePlugin plugin) {
        super("ignore");
        this.plugin = plugin;
        setAliases("unignore");
        setUsage(Colors.RED + "Usage: /ignore <player>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Strings.PLAYER_NOT_FOUND);
            return;
        }

        if (target.getName().equals(player.getName())) {
            player.sendMessage(Colors.RED + "You can't ignore yourself!");
            return;
        }

        CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

        if (targetProfile.hasStaff()) {
            player.sendMessage(Colors.RED + "You can't ignore a staff member. If this staff member is harrassing you " +
                    "or engaging in other abusive manners, please report this or contact a higher staff member.");
            return;
        }

        CoreProfile profile = plugin.getProfileManager().getProfile(player);

        if (profile.hasPlayerIgnored(target.getUniqueId())) {
            profile.unignore(target.getUniqueId());
            player.sendMessage(Colors.GREEN + "No longer ignoring " + target.getName() + ".");
        } else {
            profile.ignore(target.getUniqueId());
            player.sendMessage(Colors.GREEN + "Now ignoring " + target.getName() + ".");
        }
    }
}
