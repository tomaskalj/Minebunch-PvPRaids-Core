package com.minebunch.core.listeners;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.event.player.PlayerMessageEvent;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.utils.message.Colors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class MessageListener implements Listener {
	private final CorePlugin plugin;

	private static void sendMessage(CoreProfile sender, CoreProfile receiver, Player player, String msg) {
		receiver.setConverser(sender.getId());
		player.sendMessage(msg);
	}

	@EventHandler
	public void onMessage(PlayerMessageEvent event) {
		Player sender = event.getPlayer();
		CoreProfile senderProfile = plugin.getProfileManager().getProfile(sender.getUniqueId());

		if (!senderProfile.isMessaging() && !senderProfile.hasStaff()) {
			sender.sendMessage(Colors.RED + "You have messaging disabled.");
			return;
		}

		Player receiver = event.getReceiver();
		CoreProfile receiverProfile = plugin.getProfileManager().getProfile(receiver.getUniqueId());

		if (senderProfile.hasStaff()) {
			// NO-OP
		} else if (!receiverProfile.isMessaging()) {
			sender.sendMessage(Colors.RED + receiver.getName() + " has messaging disabled.");
			return;
		}

		String toMsg = Colors.GRAY + "(To " + receiverProfile.getChatFormat() + Colors.GRAY + ") " + event.getMessage();
		String fromMsg = Colors.GRAY + "(From " + senderProfile.getChatFormat() + Colors.GRAY + ") " + event.getMessage();

		if (plugin.getFilter().isFiltered(event.getMessage())) {
			if (senderProfile.hasStaff()) {
				sender.sendMessage(Colors.RED + "That would have been filtered.");
			} else {
				String filteredMessage = Colors.GRAY + "(" + sender.getDisplayName()
						+ Colors.GRAY + " -> " + receiver.getDisplayName() + Colors.GRAY + ") " + event.getMessage();

				plugin.getStaffManager().messageStaff(Colors.RED + "(Filtered) " + filteredMessage);
				sender.sendMessage(toMsg);
				return;
			}
		}

		sendMessage(senderProfile, receiverProfile, receiver, fromMsg);
		sendMessage(receiverProfile, senderProfile, sender, toMsg);

		if (receiverProfile.isPlayingSounds()) {
			receiver.playSound(receiver.getLocation(), Sound.NOTE_PLING, 1.0F, 2.0F);
		}
	}
}
