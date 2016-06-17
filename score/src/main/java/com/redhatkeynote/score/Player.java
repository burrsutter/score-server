package com.redhatkeynote.score;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

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
    private Boolean goldenSnitch;

    @ManyToMany
    private Set<Achievement> achievements = new HashSet<Achievement>();

    public Player() {
    }

    public Player(String uuid, String username, Integer team, Integer score, Integer consecutivePops, Boolean goldenSnitch ) {
        setUuid( uuid );
        setUsername( username );
        setTeam( team );
        setScore( score );
        setConsecutivePops( consecutivePops );
        setGoldenSnitch( goldenSnitch );
    }

    public Integer getId() {
        return this.id;
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

    public Set<Achievement> getAchievements() {
        return this.achievements;
    }

    public void setAchievements( Set<Achievement> achievements ) {
        this.achievements = achievements;
    }

    public boolean hasAchievement( Achievement achievement ) {
        if (achievement != null) {
            final String desc = achievement.getDesc();
            for(Achievement current: achievements) {
                if (current.getDesc().equals(desc)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addAchievement( Achievement achievement ) {
        this.achievements.add( achievement );
    }

    public void addAchievements( Set<Achievement> achievements ) {
        this.achievements.addAll( achievements );
    }

    public Integer getConsecutivePops() {
        return consecutivePops;
    }

    public void setConsecutivePops(Integer consecutivePops) {
        this.consecutivePops = consecutivePops;
    }

    public Boolean getGoldenSnitch() {
        return goldenSnitch;
    }

    public void setGoldenSnitch(Boolean goldenSnitch) {
        this.goldenSnitch = goldenSnitch;
    }
}
