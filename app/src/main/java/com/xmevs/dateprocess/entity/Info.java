package com.xmevs.dateprocess.entity;

/**
 * Created by MSI on 2016/12/27.
 */

public class Info {

    int id;
    String timeslot;

    public int getId() {
        return id;
    }

    public Info(int id, String timeslot) {
        this.id = id;
        this.timeslot = timeslot;
    }

    public String getTimeslot() {
        return timeslot;
    }
}
