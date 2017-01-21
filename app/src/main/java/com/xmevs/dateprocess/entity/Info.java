package com.xmevs.dateprocess.entity;

/**
 * Created by MSI on 2016/12/27.
 */

public class Info {

    int id;
    String timeslot;
    String slotname;

    public Info(int id, String timeslot, String slotname) {
        this.id = id;
        this.timeslot = timeslot;
        this.slotname = slotname;
    }

    public int getId() {
        return id;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public String getSlotname() {
        return slotname;
    }
}
