package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Strings;
import java.util.Collections;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand extends BaseCommand {
	private static final String BLANK_MESSAGE = String.join("", Collections.nCopies(150, Strings.CRACKED_VAPE_MESSAGE + "\n"));
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

		String name = sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName();

		plugin.getServer().broadcastMessage(Colors.GREEN + "The chat was cleared by " + name + ".");
		sender.sendMessage(Colors.YELLOW + "Don't worry, staff can still see cleared messages.");
	}
}
