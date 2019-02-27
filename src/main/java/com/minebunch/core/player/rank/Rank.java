package com.minebunch.core.player.rank;

import com.minebunch.core.utils.message.Colors;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public enum Rank {
	OWNER("Owner", Colors.D_RED),
	DEVELOPER("Developer", Colors.AQUA),
	ADMIN("Admin", Colors.RED),
	MOD("Mod", Colors.PURPLE),
	MEDIA("Media", Colors.PINK),
	HIGHROLLER("HighRoller", Colors.PURPLE + "[HighRoller] %s", Colors.GOLD + Colors.I),
	PRO("Pro", Colors.GOLD),
	MVP("MVP", Colors.BLUE),
	VIP("VIP", Colors.GREEN),
	MEMBER("Member", Colors.WHITE);

	public static final Rank LOWEST_STAFF = MOD;
	public static final Rank LOWEST_DONOR = VIP;

	public static final String ORDERED_RANKS = Arrays.stream(values())
			.map(rank -> rank.getColor() + rank.getName() + Colors.R)
			.collect(Collectors.joining(", "));

	private final String name;
	private final String rawFormat;
	private final String format;
	private final String color;

	Rank(String name, String color) {
		this(name, "%s", color);
	}

	Rank(String name, String rawFormat, String color) {
		this.name = name;
		this.rawFormat = rawFormat;
		this.format = String.format(rawFormat, color, color);
		this.color = color;
	}

	public static Rank getByName(String name) {
		for (Rank rank : values()) {
			if (rank.name().equalsIgnoreCase(name)) {
				return rank;
			}
		}

		return null;
	}

	public void apply(Player player) {
		String coloredName = color + player.getName();

		player.setPlayerListName(coloredName);
		player.setDisplayName(coloredName);
	}
}
