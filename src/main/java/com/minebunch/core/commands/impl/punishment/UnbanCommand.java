package com.minebunch.core.commands.impl.punishment;

import com.google.gson.JsonObject;
import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.jedis.json.payloads.JsonPayload;
import com.minebunch.core.jedis.json.payloads.PayloadType;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.punishment.Punishment;
import com.minebunch.core.utils.json.JsonChain;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class UnbanCommand extends BaseCommand{

    private static final String USAGE_MESSAGE = ChatColor.RED + "unban <player> [reason] [-s]";

    public UnbanCommand(){
        super("unban", Rank.LOWEST_STAFF);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0){
            sender.sendMessage(USAGE_MESSAGE);
            return;
        }

        String targetPlayerName = args[0];

        StringBuilder builder = new StringBuilder("");
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

        Punishment ban = CorePlugin.getInstance().getPunishmentManager().getActiveBan(targetUuid);
        if (ban == null){
            sender.sendMessage(ChatColor.RED + "This player is not banned!");
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

        if (reason.endsWith(" -p")) {
            reason = reason.substring(0, reason.length() - 3);
            ban.setSilent(false);
        }

        ban.setRemovedBy(staffUuid);
        ban.setRemoveReason(reason);
        ban.save(false);

        String result = reason;
        try (MongoCursor<Document> cursor = CorePlugin.getInstance().getMongoStorage()
                .getDocumentsByFilter("punishments", "punishment_uuid", ban.getPunishmentUuid())){
            cursor.forEachRemaining(document -> { Punishment shared = new Punishment();
                shared.load(document);
                shared.setRemovedBy(staffUuid);
                shared.setRemoveReason(result);
                shared.save(false);
            });
        }

        JsonObject data = new JsonChain()
                .addProperty("punishment_uuid", ban.getPunishmentUuid().toString())
                .addProperty("staff_name", staffName)
                .addProperty("target_name", targetPlayerName)
                .get();

        CorePlugin.getInstance().getJedisManager().write(new JsonPayload(PayloadType.PUNISHMENT, data));
    }
}
