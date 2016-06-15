package com.redhatkeynote.score;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="player")
@SuppressWarnings("serial")
public class Player implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "username")//, unique=true)
    private String username;

    @Column(name = "summary")
    private Integer team;

    @Column(name = "score")
    private Integer score;

    @Column(name = "pops")
    private Integer consecutivePops;

    @Column(name = "goldenSnitch")
    private boolean goldenSnitch;

    @OneToMany
    private List<Achievement> achievements = new ArrayList<Achievement>();

    public Player() {
    }

    public Player(String uuid, String username, Integer team, Integer score, Integer consecutivePops, boolean goldenSnitch ) {
        setUuid( uuid );
        setUsername( username );
        setTeam( team );
        setScore( score );
        setConsecutivePops( consecutivePops );
        setGoldenSnitch( goldenSnitch );
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

    public List<Achievement> getAchievements() {
        return this.achievements;
    }

    public void setAchievements( List<Achievement> achievements ) {
        this.achievements = achievements;
    }

    public boolean hasAchievement( Achievement achievement ) {
        return this.achievements.contains( achievement );
    }

    public void addAchievement( Achievement achievement ) {
        this.achievements.add( achievement );
    }

    public Integer getConsecutivePops() {
        return consecutivePops;
    }

    public void setConsecutivePops(Integer consecutivePops) {
        this.consecutivePops = consecutivePops;
    }

    public boolean isGoldenSnitch() {
        return goldenSnitch;
    }

    public void setGoldenSnitch(boolean goldenSnitch) {
        this.goldenSnitch = goldenSnitch;
    }
}
