package com.minebunch.core.utils.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ScoreboardAPI implements Listener {
    private final JavaPlugin plugin;
    private final ScoreboardAdapter adapter;

    public ScoreboardAPI(JavaPlugin plugin, ScoreboardAdapter adapter, int updateRate) {
        this.plugin = plugin;
        this.adapter = adapter;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new ScoreboardUpdateTask(plugin.getServer()), 1L, updateRate);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setScoreboard(plugin.getServer().getScoreboardManager().getNewScoreboard());
    }

    @EventHandler
    private void onScoreboardUpdate(ScoreboardUpdateEvent event) {
        adapter.onScoreboardUpdate(event);
    }
}
