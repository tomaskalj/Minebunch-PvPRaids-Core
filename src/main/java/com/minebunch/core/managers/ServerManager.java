package com.minebunch.core.managers;

import com.minebunch.core.utils.message.Colors;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter
@Setter
public class ServerManager {
	private static final String ALERT_PREFIX = Colors.RED + "[Alert] " + Colors.R;
	private boolean globalChatMuted;
	private int slowChatTime = -1;

	public boolean isSlowChatEnabled() {
		return slowChatTime != -1;
	}

	public void disableSlowChat() {
		slowChatTime = -1;
	}

	public void broadcastAlert(String message) {
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ALERT_PREFIX + message);
		Bukkit.broadcastMessage("");
	}
}
