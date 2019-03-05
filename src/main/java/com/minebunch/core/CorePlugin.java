package com.minebunch.core;

import com.minebunch.core.commands.impl.HelpOpCommand;
import com.minebunch.core.commands.impl.IgnoreCommand;
import com.minebunch.core.commands.impl.ListCommand;
import com.minebunch.core.commands.impl.MessageCommand;
import com.minebunch.core.commands.impl.PingCommand;
import com.minebunch.core.commands.impl.ReplyCommand;
import com.minebunch.core.commands.impl.ReportCommand;
import com.minebunch.core.commands.impl.TagCommand;
import com.minebunch.core.commands.impl.staff.BackCommand;
import com.minebunch.core.commands.impl.staff.BroadcastCommand;
import com.minebunch.core.commands.impl.staff.ClearChatCommand;
import com.minebunch.core.commands.impl.staff.FeedCommand;
import com.minebunch.core.commands.impl.staff.GameModeCommand;
import com.minebunch.core.commands.impl.staff.HealCommand;
import com.minebunch.core.commands.impl.staff.InvSeeCommand;
import com.minebunch.core.commands.impl.staff.MuteChatCommand;
import com.minebunch.core.commands.impl.staff.RankCommand;
import com.minebunch.core.commands.impl.staff.SetPlayerLimitCommand;
import com.minebunch.core.commands.impl.staff.ShutdownCommand;
import com.minebunch.core.commands.impl.staff.SlowChatCommand;
import com.minebunch.core.commands.impl.staff.SpeedCommand;
import com.minebunch.core.commands.impl.staff.StaffChatCommand;
import com.minebunch.core.commands.impl.staff.TeleportCommand;
import com.minebunch.core.commands.impl.staff.TpPosCommand;
import com.minebunch.core.commands.impl.staff.VanishCommand;
import com.minebunch.core.commands.impl.toggle.ToggleGlobalChat;
import com.minebunch.core.commands.impl.toggle.ToggleMessagesCommand;
import com.minebunch.core.commands.impl.toggle.ToggleSoundsCommand;
import com.minebunch.core.jedis.JedisManager;
import com.minebunch.core.listeners.MessageListener;
import com.minebunch.core.listeners.PlayerListener;
import com.minebunch.core.managers.PlayerManager;
import com.minebunch.core.managers.ProfileManager;
import com.minebunch.core.managers.ServerManager;
import com.minebunch.core.managers.StaffManager;
import com.minebunch.core.server.filter.Filter;
import com.minebunch.core.storage.database.MongoStorage;
import com.minebunch.core.storage.flatfile.Config;
import com.minebunch.core.task.BroadcastTask;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.structure.Cuboid;
import java.lang.reflect.Field;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

@Getter
public class CorePlugin extends JavaPlugin {
	@Getter
	private static CorePlugin instance;
	@Getter
	private static boolean isServerEnabled = false;

	private Filter filter;
	private MongoStorage mongoStorage;
	private ProfileManager profileManager;
	private StaffManager staffManager;
	private PlayerManager playerManager;
	private ServerManager serverManager;
	private JedisManager jedisManager;

	private Config config;
	private String serverName;

	private static void registerSerializableClass(Class<?> clazz) {
		if (ConfigurationSerializable.class.isAssignableFrom(clazz)) {
			Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
			ConfigurationSerialization.registerClass(serializable);
		}
	}

	@Override
	public void onEnable() {
		instance = this;

		registerSerializableClass(Cuboid.class);

		config = new Config(this, "config.yml");
		serverName = config.getString("server.name");

		filter = new Filter();
		mongoStorage = new MongoStorage();
		profileManager = new ProfileManager();
		staffManager = new StaffManager(this);
		playerManager = new PlayerManager();
		serverManager = new ServerManager();
		jedisManager = new JedisManager();

		registerCommands(
				new BroadcastCommand(this), new ClearChatCommand(this), new IgnoreCommand(this),
				new ListCommand(), new MessageCommand(this), new RankCommand(this),
				new ReplyCommand(this), new StaffChatCommand(this), new TeleportCommand(this),
				new ToggleMessagesCommand(this), new ToggleGlobalChat(this), new ToggleSoundsCommand(this),
				new VanishCommand(this), new ReportCommand(this), new HelpOpCommand(this),
				new PingCommand(), new MuteChatCommand(this), new SlowChatCommand(this),
				new GameModeCommand(this), new ShutdownCommand(this), new TagCommand(this),
				new TpPosCommand(), new HealCommand(), new FeedCommand(), new SpeedCommand(), new InvSeeCommand(),
				new SetPlayerLimitCommand(), new BackCommand(this)
		);
		registerListeners(
				new PlayerListener(this), new MessageListener(this)
		);

		BukkitScheduler scheduler = getServer().getScheduler();

		scheduler.runTaskTimerAsynchronously(this, new BroadcastTask(this), 20 * 60L, 20 * 60L);
		scheduler.runTaskLater(this, () -> isServerEnabled = true, 20 * 3L);
	}

	private void registerCommands(Command... commands) {
		try {
			Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
			final boolean accessible = commandMapField.isAccessible();

			commandMapField.setAccessible(true);

			CommandMap commandMap = (CommandMap) commandMapField.get(getServer());

			for (Command command : commands) {
				commandMap.register(command.getName(), getName(), command);
			}

			commandMapField.setAccessible(accessible);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			getLogger().severe("An error occurred while registering commands.");
			e.printStackTrace();
		}
	}


	private void registerListeners(Listener... listeners) {
		for (Listener listener : listeners) {
			getServer().getPluginManager().registerEvents(listener, this);
		}
	}

	@Override
	public void onDisable() {
		profileManager.saveProfiles();

		// properly disconnect players since Spigot doesn't
		for (Player player : getServer().getOnlinePlayers()) {
			player.kickPlayer(Colors.RED + "The server is restarting.");
		}
	}
}
