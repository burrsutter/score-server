package com.redhatkeynote.score;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="player")
@SuppressWarnings("serial")
public class Player implements Serializable {

    @Id
    @Column(name="id")
    @GeneratedValue
    private Integer id;

    @Column(name="uuid", unique=true)
    private String uuid;

    @Column(name="username")//, unique=true)
    private String username;

    @Column(name="team")
    private Integer team;

    @Column(name="score")
    private Integer score;

    public Player() {}

    public Player(String uuid, String username, Integer team, Integer score) {
        setUuid(uuid);
        setUsername(username);
        setTeam(team);
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

}
