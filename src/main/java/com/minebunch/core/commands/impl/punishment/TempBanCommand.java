package com.minebunch.core.commands.impl.punishment;

import com.google.gson.JsonObject;
import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.jedis.json.payloads.JsonPayload;
import com.minebunch.core.jedis.json.payloads.PayloadType;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.punishment.Punishment;
import com.minebunch.core.punishment.PunishmentType;
import com.minebunch.core.utils.json.JsonChain;
import com.minebunch.core.utils.time.TimeUtil;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class TempBanCommand extends BaseCommand{

    private static final String USAGE_MESSAGE = ChatColor.RED + "tempban <player> [time] [reason] [-s]";

    public TempBanCommand() {
        super("tempban", Rank.ADMIN);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0){
            sender.sendMessage(USAGE_MESSAGE);
            return;
        }

        String targetPlayerName = args[0];
        String time = args[1];

        StringBuilder builder = new StringBuilder();
        for (int i = 2; i < args.length; i++){
            builder.append(args[i]);
            if (i < args.length - 1) builder.append(" ");
        }
        String reason = builder.toString();

        UUID targetUuid = CorePlugin.getInstance().getUuidCache().getUuid(targetPlayerName);
        if (targetUuid == null){
            sender.sendMessage(ChatColor.RED + "Could not find that player! Did you type the name correctly?");
            return;
        }

        UUID staffUuid;
        String staffName;
        if (sender instanceof Player){
            Player player = (Player)sender;
            staffName = player.getName();
            staffUuid = player.getUniqueId();
        }else{
            staffUuid = null;
            staffName = ChatColor.DARK_RED + "Console";
        }

        Punishment punishment = new Punishment();
        if (reason.endsWith(" -p")) {
            reason = reason.substring(0, reason.length() - 3);
            punishment.setSilent(false);
        }

        int duration = TimeUtil.parseTime(time);
        if (duration < 0) {
            sender.sendMessage(ChatColor.RED + "If you want to permanently ban a player, use /ban instead.");
            return;
        }
        if (duration == 0) {
            sender.sendMessage(ChatColor.RED + "Failed to parse the given duration string.");
            sender.sendMessage(ChatColor.RED + "Example syntax: 1m4w2d1h (1 month, 4 weeks, 2 days, and 1 hour)");
            return;
        }

        punishment.setPunishmentUuid(UUID.randomUUID());
        punishment.setType(PunishmentType.TEMPBAN);
        punishment.setTargetUuid(targetUuid);
        punishment.setTargetName(targetPlayerName);
        punishment.setAddedBy(staffUuid);
        punishment.setAddedReason(reason);
        punishment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        punishment.setExpiration(new Timestamp(System.currentTimeMillis() + duration));
        punishment.save(false);

        JsonObject data = new JsonChain()
                .addProperty("punishment_uuid", punishment.getPunishmentUuid().toString())
                .addProperty("staff_name", staffName)
                .addProperty("target_name", targetPlayerName)
                .get();

        CorePlugin.getInstance().getJedisManager().write(new JsonPayload(PayloadType.PUNISHMENT, data));

        Document playerDocument = CorePlugin.getInstance().getMongoStorage().getDocumentByFilter("players", "uuid",
                targetUuid);
        List<UUID> alts = playerDocument.getList("known_alts", UUID.class);

        // Also create a shared punishment for the alts
        for (UUID altUuid : alts){
            Punishment shared = CorePlugin.getInstance().getPunishmentManager()
                    .createSharedPunishment(punishment, altUuid);
            shared.save(false);
        }
    }
}
