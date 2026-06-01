package org.kerix.karaapi.paper.scoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PaperSidebarRenderer {

    private static final int MAX_LINES = 15;

    private static final String[] ENTRIES = {
            "§0", "§1", "§2", "§3", "§4",
            "§5", "§6", "§7", "§8", "§9",
            "§a", "§b", "§c", "§d", "§e"
    };

    private final Player player;
    private final Scoreboard previousScoreboard;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final List<String> activeEntries = new ArrayList<>();

    public PaperSidebarRenderer(Player player, Component title) {
        this.player = Objects.requireNonNull(player, "player");
        this.previousScoreboard = player.getScoreboard();

        this.scoreboard = Objects.requireNonNull(
                Bukkit.getScoreboardManager(),
                "ScoreboardManager is not available"
        ).getNewScoreboard();

        this.objective = scoreboard.registerNewObjective(
                "kara_sidebar",
                Criteria.DUMMY,
                title
        );

        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(scoreboard);
    }

    public void title(Component title) {
        objective.displayName(title);
    }

    public void lines(List<Component> lines) {
        Objects.requireNonNull(lines, "lines");

        clearLines();

        int size = Math.min(lines.size(), MAX_LINES);

        for (int index = 0; index < size; index++) {
            Component line = lines.get(index);
            String entry = ENTRIES[index];
            String teamName = "kara_line_" + index;

            Team team = scoreboard.getTeam(teamName);

            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
            }

            if (!team.hasEntry(entry)) {
                team.addEntry(entry);
            }

            team.prefix(line);
            objective.getScore(entry).setScore(size - index);

            activeEntries.add(entry);
        }
    }

    public void clearLines() {
        for (String entry : activeEntries) {
            scoreboard.resetScores(entry);
        }

        activeEntries.clear();

        for (Team team : scoreboard.getTeams()) {
            if (team.getName().startsWith("kara_line_")) {
                team.unregister();
            }
        }
    }

    public void hide() {
        clearLines();

        objective.unregister();
        player.setScoreboard(previousScoreboard);
    }

    public Player player() {
        return player;
    }
}
