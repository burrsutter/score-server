package com.redhatkeynote.score;

import java.io.Serializable;
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

    public void addTeamScore(TeamScore teamScore) {
        if (teamScore != null) {
            teamScores.add(teamScore);
        }
    }

    public SortedSet<PlayerScore> getTopPlayerScores() {
        return topPlayerScores;
    }

    public void setTopPlayerScores(SortedSet<PlayerScore> topPlayerScores) {
        this.topPlayerScores = topPlayerScores;
    }

    public void addTopPlayerScore(PlayerScore playerScore) {
        if (playerScore != null) {
            topPlayerScores.add(playerScore);
        }
    }

    public int getTopPlayers() {
        return topPlayers;
    }

    public void setTopPlayers(int topPlayers) {
        this.topPlayers = topPlayers;
    }
}
