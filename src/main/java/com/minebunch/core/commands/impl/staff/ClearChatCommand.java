package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import java.util.Collections;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand extends BaseCommand {
	private static final String BLANK_MESSAGE = String.join("", Collections.nCopies(150, "§8 §8 §1 §3 §3 §7 §8 §r\n"));
	private final CorePlugin plugin;

	public ClearChatCommand(CorePlugin plugin) {
		super("clearchat", Rank.MOD);
		this.plugin = plugin;
		setAliases("cc");
	}

	@Override
	protected void execute(CommandSender sender, String[] args) {
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			CoreProfile profile = plugin.getProfileManager().getProfile(player);

			if (!profile.hasStaff()) {
				player.sendMessage(BLANK_MESSAGE);
			}
		}

		plugin.getServer().broadcastMessage(Colors.GREEN + "The chat was cleared by " + sender.getName() + ".");
		sender.sendMessage(Colors.YELLOW + "Don't worry, staff can still see cleared messages.");
	}
}
