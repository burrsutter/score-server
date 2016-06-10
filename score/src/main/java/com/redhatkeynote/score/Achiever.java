package com.redhatkeynote.score;

public class Achiever {

    private String uuid;
    private Integer score;
    private String achievement;

    public Achiever() {}

    public Achiever(String uuid, Integer score) {
        this.uuid = uuid;
        this.score = score != null ? score : Integer.valueOf(0);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

}
