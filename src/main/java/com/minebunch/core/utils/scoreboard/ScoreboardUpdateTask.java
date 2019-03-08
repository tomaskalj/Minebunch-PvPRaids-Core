package com.minebunch.core.utils.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ScoreboardUpdateTask implements Runnable {
    private static final String OBJECTIVE_ID = "objective";
    private final Server server;

    @Override
    public void run() {
        for (Player player : server.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }

    private void updateScoreboard(Player player) {
        Scoreboard board = player.getScoreboard();
        Objective objective = board.getObjective(OBJECTIVE_ID);

        if (objective == null) {
            objective = board.registerNewObjective(OBJECTIVE_ID, "dummy");
            objective.setDisplayName("");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        ScoreboardUpdateEvent event = new ScoreboardUpdateEvent(player, objective.getDisplayName());

        server.getPluginManager().callEvent(event);

        if (!objective.getDisplayName().equals(event.getTitle())) {
            objective.setDisplayName(event.getTitle());
        }

        if (event.getLines().size() > 0) {
            if (!event.getHeader().isEmpty()) {
                event.insertLine(0, event.getHeader());
            }

            if (!event.getFooter().isEmpty()) {
                event.addLine(event.getFooter());
            }
        }

        List<Team> teams = new ArrayList<>();

        for (int i = 0; i < ChatColor.values().length; i++) {
            if (board.getTeam("#line-" + i) == null) {
                board.registerNewTeam("#line-" + i);
            }

            teams.add(board.getTeam("#line-" + i));
        }

        int linesSize = event.getLines().size();

        for (int i = 0; i < linesSize; i++) {
            Team team = teams.get(i);

            ScoreboardLine line = event.getLine(i);

            String prefix = line.getPrefix();
            String suffix = line.getSuffix();

            if (!team.getPrefix().equals(prefix)) {
                team.setPrefix(prefix);
            }

            if (!team.getSuffix().equals(suffix)) {
                team.setSuffix(line.getSuffix());
            }

            String entry = ChatColor.values()[i] + line.getFinalPrefixColor();
            Set<String> entries = team.getEntries();

            if (entries.size() == 0) {
                team.addEntry(entry);
                objective.getScore(entry).setScore(linesSize - i);
            } else if (entries.size() == 1) {
                String already = entries.iterator().next();

                if (!entry.equals(already)) {
                    board.resetScores(already);
                    team.removeEntry(already);
                    team.addEntry(entry);
                    objective.getScore(entry).setScore(linesSize - i);
                } else {
                    objective.getScore(already).setScore(linesSize - i);
                }
            }
        }

        for (int i = linesSize; i < ChatColor.values().length; i++) {
            Team team = teams.get(i);
            Set<String> entries = team.getEntries();

            if (entries.size() > 0) {
                for (String entry : entries) {
                    board.resetScores(entry);
                    team.removeEntry(entry);
                }
            }
        }
    }
}
