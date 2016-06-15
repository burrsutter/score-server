package com.redhatkeynote.score;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TeamScore implements Comparable<TeamScore>, Serializable {

    private Integer team;
    private Integer score;

    public TeamScore() {}

    public TeamScore(Integer team, Number score) {
        setTeam(team);
        setScore(score != null ? score.intValue() : null);
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
        return true;
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
        int thisScore = this.score != null ? this.score.intValue() : 0;
        int thatScore = that != null ? (that.score != null ? that.score.intValue() : 0) : 0;
        // descending
        int c = Integer.valueOf(thatScore).compareTo(thisScore);
        if (c == 0) {
            // compare summary second
            Integer thisTeam = this.team != null ? this.team : 0;
            Integer thatTeam = that != null ? (that.team != null ? that.team : 0) : 0;
            c = thisTeam.compareTo(thatTeam);
        }
        return c;
    }

}
