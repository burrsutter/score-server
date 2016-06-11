package com.redhatkeynote.score;

public class PlayerAchievement {

    private String uuid;
    private String achievement;

    public PlayerAchievement() {}

    public PlayerAchievement(String uuid, String achievement) {
        this.uuid = uuid;
        this.achievement = achievement;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof PlayerAchievement) ) return false;

        PlayerAchievement that = (PlayerAchievement) o;

        if ( uuid != null ? !uuid.equals( that.uuid ) : that.uuid != null ) return false;
        return !(achievement != null ? !achievement.equals( that.achievement ) : that.achievement != null);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (achievement != null ? achievement.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PlayerAchievement{" +
               "uuid='" + uuid + '\'' +
               ", achievement='" + achievement + '\'' +
               '}';
    }
}
