package com.minebunch.core.commands.impl.staff;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.StringUtil;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.entity.Player;

public class StaffChatCommand extends PlayerCommand {
	private final CorePlugin plugin;

	public StaffChatCommand(CorePlugin plugin) {
		super("staffchat", Rank.MOD);
		this.plugin = plugin;
		setAliases("sc");
	}

	@Override
	public void execute(Player player, String[] args) {
		CoreProfile profile = plugin.getProfileManager().getProfile(player);

		if (args.length == 0) {
			boolean inStaffChat = !profile.isInStaffChat();

			profile.setInStaffChat(inStaffChat);

			player.sendMessage(inStaffChat ? Colors.GREEN + "You are now in staff chat." : Colors.RED + "You are no longer in staff chat.");
		} else {
			String message = StringUtil.buildString(args, 0);

			plugin.getStaffManager().messageStaff(profile.getChatFormat(), message);
		}
	}
}