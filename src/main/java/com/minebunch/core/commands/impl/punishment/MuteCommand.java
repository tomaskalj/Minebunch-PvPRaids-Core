package com.minebunch.core.commands.impl.punishment;

import com.google.gson.JsonObject;
import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.jedis.json.payloads.JsonPayload;
import com.minebunch.core.jedis.json.payloads.PayloadType;
import com.minebunch.core.punishment.Punishment;
import com.minebunch.core.punishment.PunishmentType;
import com.minebunch.core.utils.json.JsonChain;
import com.minebunch.core.utils.time.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.UUID;

public class MuteCommand extends BaseCommand{

    private static final String USAGE_MESSAGE = ChatColor.RED + "mute <player> [time] [reason]";

    public MuteCommand() {
        super("mute");
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

        if (CorePlugin.getInstance().getPunishmentManager().getActiveMute(targetUuid) != null) {
            sender.sendMessage(ChatColor.RESET + targetPlayerName + ChatColor.RED + " is already muted.");
            return;
        }

        int duration = TimeUtil.parseTime(time);
        if (duration == 0) {
            sender.sendMessage(ChatColor.RED + "Failed to parse the given duration string.");
            sender.sendMessage(ChatColor.RED + "Example syntax: 1m4w2d1h (1 month, 4 weeks, 2 days, and 1 hour)");
            return;
        }

        UUID staffUuid;
        String staffName;
        if (sender instanceof Player) {
            Player player = (Player)sender;
            staffUuid = player.getUniqueId();
            staffName = player.getName();
        }
        else {
            staffUuid = null;
            staffName = ChatColor.DARK_RED + "Console";
        }

        Punishment punishment = new Punishment();
        if (reason.endsWith(" -p")) {
            reason = reason.substring(0, reason.length() - 3);
            punishment.setSilent(false);
        }

        punishment.setPunishmentUuid(UUID.randomUUID());
        punishment.setType(PunishmentType.MUTE);
        punishment.setTargetUuid(targetUuid);
        punishment.setAddedBy(staffUuid);
        punishment.setAddedReason(reason);
        punishment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        punishment.setExpiration(new Timestamp(System.currentTimeMillis() + duration));
        punishment.setSilent(true);
        punishment.save(false);

        JsonObject data = new JsonChain()
                .addProperty("punishment_uuid", punishment.getPunishmentUuid().toString())
                .addProperty("staff_name", staffName)
                .addProperty("target_name", targetPlayerName)
                .get();

        CorePlugin.getInstance().getJedisManager().write(new JsonPayload(PayloadType.PUNISHMENT, data));
    }
}
