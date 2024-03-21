package org.kerix.api.ui;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;
import java.util.HashMap;
import java.util.logging.Logger;

import static org.kerix.api.utils.StringBuilder.*;

public class ScoreBoardBuilder {

    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private final Objective objective;
    private final HashMap<String , Team> teams = new HashMap<>();
    private String title;

    public ScoreBoardBuilder(String title ){
        objective = scoreboard.registerNewObjective("KaraAPI" , Criteria.AIR , buildString(title));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.title = title;
    }

    public ScoreBoardBuilder setTitle(String title){
        objective.displayName(buildString(title));
        this.title = title;
        return this;
    }
    public ScoreBoardBuilder setLines(String... args) {
        int i = 15;
        for (String line : args) {
            String teamName = title + "_line_" + i;
            String entry = "§" + (i > 9 ? "1" : "0") + "§" +(i > 9 ? (i - 10) : i) + "§" + (i > 9 ? (i - 10) : i) + "§n";
            Team team = scoreboard.registerNewTeam(teamName);
            if(teams.get(teamName) != null) {
                team = teams.get(teamName);
                team.addEntry(entry);
                team.prefix(buildString(line));
            } else {
                team.addEntry(entry);
                team.prefix(buildString(line));
                teams.put(teamName, team);
            }
            objective.getScore(entry).setScore(i);
            i--;
        }
        return this;
    }

    public ScoreBoardBuilder setLine( int i , String arg){
        String teamName = title + "_line_" + i;
        Team team = teams.get(teamName);
        if(team == null) return this;
        team.prefix(buildString(arg));
        objective.getScore("§" + (i > 9 ? "1" : "0") + (i > 9 ? (i - 10) : i) + "§" + (i > 9 ? (i - 10) : i) + "§n").setScore(i-1);
        return this;
    }
    public Scoreboard build() {
        return scoreboard;
    }
}
