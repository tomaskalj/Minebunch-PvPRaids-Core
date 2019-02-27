package com.minebunch.core.commands.impl.toggle;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.utils.message.Colors;
import org.bukkit.entity.Player;

public class ToggleGlobalChat extends PlayerCommand {
	private final CorePlugin plugin;

	public ToggleGlobalChat(CorePlugin plugin) {
		super("toggleglobalchat");
		this.plugin = plugin;
		setAliases("togglechat", "tgc");
	}

	@Override
	public void execute(Player player, String[] args) {
		CoreProfile profile = plugin.getProfileManager().getProfile(player);
		boolean enabled = !profile.isGlobalChatEnabled();

		profile.setGlobalChatEnabled(enabled);
		player.sendMessage(enabled ? Colors.GREEN + "Global chat enabled." : Colors.RED + "Global chat disabled.");
	}
}
