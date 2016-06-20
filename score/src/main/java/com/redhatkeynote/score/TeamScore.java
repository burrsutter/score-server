package com.redhatkeynote.score;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TeamScore implements Comparable<TeamScore>, Serializable {

    private Integer team;
    private Integer score;
    private Integer numPlayers;

    public TeamScore() {}

    public TeamScore(Integer team, Number score, Number numPlayers) {
        setTeam(team);
        if (score != null) {
            setScore(score.intValue());
        }
        if (numPlayers != null) {
            setNumPlayers(numPlayers.intValue());
        }
    }

    public Integer getTeam() {
        return this.team;
    }

    public void setTeam(Integer team) {
        this.team = team;
    }

    public Integer getScore() {
        return this.score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getNumPlayers() {
        return this.numPlayers;
    }

    public void setNumPlayers(Integer numPlayers) {
        this.numPlayers = numPlayers;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TeamScore other = (TeamScore) obj;
        if (score == null) {
            if (other.score != null)
                return false;
        } else if (!score.equals(other.score))
            return false;
        if (team == null) {
            if (other.team != null)
                return false;
        } else if (!team.equals(other.team))
            return false;
        if (numPlayers == null) {
            return (other.numPlayers == null);
        } else {
            return numPlayers.equals(other.numPlayers);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((score == null) ? 0 : score.hashCode());
        result = prime * result + ((team == null) ? 0 : team.hashCode());
        return result;
    }

    @Override
    public int compareTo(TeamScore that) {
        if (this == that) {
            return 0;
        }
        // compare score first
        int thisScore = this.score != null ? this.score : 0;
        int thatScore = that != null ? (that.score != null ? that.score : 0) : 0;
        // descending
        int c = compareInt(thatScore, thisScore);
        if (c == 0) {
            // compare team second
            int thisTeam = this.team != null ? this.team : 0;
            int thatTeam = that != null ? (that.team != null ? that.team : 0) : 0;
            c = compareInt(thisTeam, thatTeam);
        }
        return c;
    }

    private static int compareInt(final int lhs, final int rhs) {
        return (lhs < rhs) ? -1 : ((lhs == rhs) ? 0 : 1);
    }
}
