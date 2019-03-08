package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.event.player.PlayerRankChangeEvent;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.storage.database.MongoRequest;
import com.minebunch.core.utils.TaskUtil;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Strings;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand extends BaseCommand {
    private final CorePlugin plugin;

    public RankCommand(CorePlugin plugin) {
        super("rank", Rank.ADMIN);
        this.plugin = plugin;
        setUsage(Colors.RED + "Usage: /rank <player> <rank>");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(usageMessage);
            return;
        }

        Rank rank = Rank.getByName(args[1]);

        if (rank == null) {
            sender.sendMessage(Colors.RED + "Rank not found.");
            return;
        }

        if (sender instanceof Player) {
            CoreProfile playerProfile = plugin.getProfileManager().getProfile(((Player) sender).getUniqueId());

            if (!playerProfile.hasRank(rank)) {
                sender.sendMessage(Colors.RED + "You can't give ranks higher than your own.");
                return;
            }
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            TaskUtil.runAsync(plugin, () -> {
                UUID id = CorePlugin.getInstance().getUuidCache().getUuid(args[0]);

                if (id != null && plugin.getMongoStorage().getDocument("players", id) != null) {
                    MongoRequest.newRequest("players", id)
                            .put("rank_name", rank.name())
                            .run();

                    sender.sendMessage(Colors.GREEN + "Set " + args[0] + "'s rank to "
                            + rank.getColor() + rank.getName() + Colors.GREEN + ".");
                } else {
                    sender.sendMessage(Strings.PLAYER_NOT_FOUND);
                }
            });
        } else {
            CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

            plugin.getServer().getPluginManager().callEvent(new PlayerRankChangeEvent(target, targetProfile, rank, targetProfile.getRank()));
            sender.sendMessage(Colors.GREEN + "Set " + target.getName() + "'s rank to "
                    + rank.getColor() + rank.getName() + Colors.GREEN + ".");

            TaskUtil.runAsync(plugin, () -> MongoRequest.newRequest("players", targetProfile.getId())
                    .put("rank_name", rank.name())
                    .run());
        }
    }
}
