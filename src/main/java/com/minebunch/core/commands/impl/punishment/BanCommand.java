package com.minebunch.core.commands.impl.punishment;

import com.google.gson.JsonObject;
import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.jedis.json.payloads.JsonPayload;
import com.minebunch.core.jedis.json.payloads.PayloadType;
import com.minebunch.core.punishment.Punishment;
import com.minebunch.core.punishment.PunishmentType;
import com.minebunch.core.utils.json.JsonChain;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class BanCommand extends BaseCommand {

    private static final String USAGE_MESSAGE = ChatColor.RED + "ban <player> [reason] [-s]";

    public BanCommand() {
        super("ban");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0){
            sender.sendMessage(USAGE_MESSAGE);
            return;
        }

        String targetPlayerName = args[0];

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++){
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

        punishment.setPunishmentUuid(UUID.randomUUID());
        punishment.setType(PunishmentType.BAN);
        punishment.setTargetUuid(targetUuid);
        punishment.setTargetName(targetPlayerName);
        punishment.setAddedBy(staffUuid);
        punishment.setAddedReason(reason);
        punishment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        punishment.save(false);

        sendJsonPayload(punishment, staffName, targetPlayerName);

        Document playerDocument = CorePlugin.getInstance().getMongoStorage().getDocumentByFilter("players", "uuid",
                targetUuid);
        List<UUID> alts = playerDocument.getList("known_alts", UUID.class);

        // Send a separate payload for each alt, so that all online alts get kicked.
        for (UUID altUuid : alts){
            Punishment shared = CorePlugin.getInstance().getPunishmentManager()
                    .createSharedPunishment(punishment, altUuid);
            shared.save(false);

            sendJsonPayload(shared, staffName, targetPlayerName);
        }
    }

    private void sendJsonPayload(Punishment punishment, String staffName, String targetPlayerName){
        JsonObject data = new JsonChain()
                .addProperty("punishment_uuid", punishment.getPunishmentUuid().toString())
                .addProperty("staff_name", staffName)
                .addProperty("target_name", targetPlayerName)
                .get();

        CorePlugin.getInstance().getJedisManager().write(new JsonPayload(PayloadType.PUNISHMENT, data));
    }
}
