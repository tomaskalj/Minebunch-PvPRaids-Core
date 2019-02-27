package com.minebunch.core.task;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.utils.message.Colors;
import lombok.RequiredArgsConstructor;

// TODO: load messages from db?
@RequiredArgsConstructor
public class BroadcastTask implements Runnable {
	private static final String[] MESSAGES = {
			"Test"
	};
	private final CorePlugin plugin;
	private int currentIndex;

	@Override
	public void run() {
		String message = MESSAGES[currentIndex];

		plugin.getServerManager().broadcastAlert(Colors.PRIMARY + message);

		if (++currentIndex == MESSAGES.length) {
			currentIndex = 0;
		}
	}
}
