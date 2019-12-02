package edu.csumb.project7.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.sql.Date;

@Entity
public class MyLog {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    @NonNull
    private String type;
    @NonNull
    private String username;
    @NonNull
    private String date;
    private String flightID;
    private Integer reservation;
    private String misc;
    private Integer tickets;

    public MyLog(){}

    @Ignore
    public MyLog(String type, String username, String date, String flightID, Integer reservation, String misc, Integer tickets){
        this.type = type;
        this.username = username;
        this.date = date;
        this.flightID = flightID;
        this.reservation = reservation;
        this.misc = misc;
        this.tickets = tickets;
    }

    @NonNull
    public int getId(){
        return id;
    }

    public void setId(@NonNull int id){
        this.id = id;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public String getFlightID(){return flightID;}

    public void setFlightID(String flightID){this.flightID = flightID;}

    public Integer getReservation(){return reservation;}

    public void setReservation(Integer i){reservation = i;}

    public String getMisc(){return misc;}

    public void setMisc(String misc){this.misc = misc;}

    public Integer getTickets(){return tickets;}

    public void setTickets(Integer i){this.tickets = i;}
}