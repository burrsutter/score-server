package com.redhatkeynote.score;

import java.io.Serializable;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings("serial")
public class ScoreSummary implements Serializable {

    private SortedSet<TeamScore> teamScores = new TreeSet<TeamScore>();
    private SortedSet<PlayerScore> topPlayerScores = new TreeSet<PlayerScore>();

    private int topPlayers;

    public ScoreSummary() {}

    public ScoreSummary(int topPlayers) {
        this.topPlayers = topPlayers;
    }

    public SortedSet<TeamScore> getTeamScores() {
        return teamScores;
    }

    public void setTeamScores(SortedSet<TeamScore> teamScores) {
        this.teamScores = teamScores;
    }

    public void addTeamScores(List<TeamScore> scores) {
        if (scores != null) {
            this.teamScores.addAll(scores);
        }
    }

    public SortedSet<PlayerScore> getTopPlayerScores() {
        return topPlayerScores;
    }

    public void setTopPlayerScores(SortedSet<PlayerScore> topPlayerScores) {
        this.topPlayerScores = topPlayerScores;
    }

    public void addTopPlayerScores(List<PlayerScore> playerScores) {
        if (playerScores != null) {
            this.topPlayerScores.addAll(playerScores);
        }
    }

    public int getTopPlayers() {
        return topPlayers;
    }

    public void setTopPlayers(int topPlayers) {
        this.topPlayers = topPlayers;
    }
}
