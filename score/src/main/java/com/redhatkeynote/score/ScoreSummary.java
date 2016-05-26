package com.redhatkeynote.score;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings("serial")
public class ScoreSummary implements Serializable {

    private SortedSet<TeamScore> teamScores = new TreeSet<TeamScore>();
    private SortedSet<PlayerScore> topPlayerScores = new TreeSet<PlayerScore>();

    public ScoreSummary() {}

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

}
