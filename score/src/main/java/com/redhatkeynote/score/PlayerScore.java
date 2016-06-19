package com.redhatkeynote.score;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PlayerScore implements Comparable<PlayerScore>, Serializable {

    private String uuid;
    private String username;
    private Integer score;

    public PlayerScore() {}

    public PlayerScore(String uuid, String username, Integer score) {
        setUuid(uuid);
        setUsername(username);
        setScore(score);
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        PlayerScore other = (PlayerScore) obj;
        if (score == null) {
            if (other.score != null)
                return false;
        } else if (!score.equals(other.score))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return (uuid == null) ? (other.uuid == null) : uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((score == null) ? 0 : score.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public int compareTo(PlayerScore that) {
        if (this == that) {
            return 0;
        }
        // compare score first
        int thisScore = this.score != null ? this.score.intValue() : 0;
        int thatScore = that != null ? (that.score != null ? that.score.intValue() : 0) : 0;
        // descending
        int c = Integer.valueOf(thatScore).compareTo(thisScore);
        if (c == 0) {
            // compare username second
            String thisUsername = this.username != null ? this.username : "";
            String thatUsername = that != null ? (that.username != null ? that.username : "") : "";
            c = thisUsername.compareTo(thatUsername);
        }
        return c;
    }

}
