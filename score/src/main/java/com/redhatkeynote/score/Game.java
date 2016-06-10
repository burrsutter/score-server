package com.redhatkeynote.score;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="game")
@SuppressWarnings("serial")
public class Game implements Serializable {

    @Id
    @Column(name="id")
    @GeneratedValue
    private Integer id;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private Status status;

    public Game() {}

    public Game(Status status) {
        setStatus(status);
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
