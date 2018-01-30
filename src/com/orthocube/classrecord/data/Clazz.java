/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.*;

import java.util.ArrayList;

/**
 * @author OrthoCube
 */
public final class Clazz {
    //ID     NUMBER PRIMARY KEY NOT NULL
//    Name   TEXT
//    SY     NUMBER
//    Sem    NUMBER
//    Course TEXT
//    Room   TEXT
//    Sched  TEXT
//    Level  TEXT
//    C1     TEXT
//    P1     NUMBER
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty sy = new SimpleIntegerProperty();
    private final IntegerProperty sem = new SimpleIntegerProperty();
    private final IntegerProperty year = new SimpleIntegerProperty();
    private final StringProperty course = new SimpleStringProperty();
    private final StringProperty room = new SimpleStringProperty();
    private final IntegerProperty days = new SimpleIntegerProperty();
    private final BooleanProperty isSHS = new SimpleBooleanProperty();
    private final StringProperty notes = new SimpleStringProperty();
    private ArrayList<String> times;

    public Clazz(long id, String name, int sy, int sem, int year, String course, String room, int days, ArrayList<String> times, boolean isSHS, String notes) {
        this.id.set(id);
        this.name.set(name);
        this.sy.set(sy);
        this.sem.set(sem);
        this.year.set(year);
        this.course.set(course);
        this.room.set(room);
        this.days.set(days);
        this.times = times;
        this.isSHS.set(isSHS);
        this.notes.set(notes);
    }

    public Clazz(long id) {
        this.id.set(id);
        this.name.set("");
        this.sy.set(0);
        this.sem.set(0);
        this.year.set(0);
        this.course.set("");
        this.room.set("");
        this.days.set(0);
        this.times = new ArrayList<>();
        for (int i = 0; i < 14; i++)
            this.times.add("00:00");
        this.isSHS.set(false);
        this.notes.set("");
    }

    public Clazz() {
        this(-1);
    }

    public long getID() {
        return id.get();
    }

    public void setID(long id) {
        this.id.set(id);
    }

    public LongProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getSY() {
        return sy.get();
    }

    public void setSY(int sy) {
        this.sy.set(sy);
    }

    public IntegerProperty syProperty() {
        return sy;
    }

    public int getSem() {
        return sem.get();
    }

    public void setSem(int sem) {
        this.sem.set(sem);
    }

    public IntegerProperty semProperty() {
        return sem;
    }

    public int getYear() {
        return year.get();
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public IntegerProperty yearProperty() {
        return year;
    }

    public String getCourse() {
        return course.get();
    }

    public void setCourse(String course) {
        this.course.set(course);
    }

    public StringProperty courseProperty() {
        return course;
    }

    public String getRoom() {
        return room.get();
    }

    public void setRoom(String room) {
        this.room.set(room);
    }

    public StringProperty roomProperty() {
        return room;
    }

    public int getDays() {
        return days.get();
    }

    public void setDays(int days) {
        this.days.set(days);
    }

    public IntegerProperty daysProperty() {
        return days;
    }

    public ArrayList<String> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<String> times) {
        this.times = times;
    }

    public boolean isSHS() {
        return isSHS.get();
    }

    public void setSHS(boolean isSHS) {
        this.isSHS.set(isSHS);
    }

    public BooleanProperty isSHSProperty() {
        return isSHS;
    }

    public String getNotes() {
        return notes.get();
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public StringProperty notesProperty() {
        return notes;
    }
}
