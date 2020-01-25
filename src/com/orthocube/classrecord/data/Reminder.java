/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

public class Reminder {
//                s.executeUpdate("CREATE TABLE Reminders (ID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
//                        "StartDate DATE NOT NULL, " +
//                        "StartTime TIME NOT NULL, " +
//                        "EndDate DATE NOT NULL, " +
//                        "EndTime TIME NOT NULL, " +
//                        "Title VARCHAR(64) NOT NULL, " +
//                        "Notes VARCHAR(255) NOT NULL, " +
//                        "Location VARCHAR(64) NOT NULL)");

    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<Time> startTime = new SimpleObjectProperty<>(), endTime = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> startDate = new SimpleObjectProperty<>(), endDate = new SimpleObjectProperty<>();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty notes = new SimpleStringProperty();
    private final StringProperty location = new SimpleStringProperty();
    private final BooleanProperty done = new SimpleBooleanProperty();

    public Reminder() {
        this(-1);
    }

    public Reminder(long id) {
        this.id.set(id);
        this.startDate.set(Date.valueOf(LocalDate.now()));
        this.startTime.set(null);
        this.endDate.set(Date.valueOf(LocalDate.now()));
        this.endTime.set(null);
        this.title.set("");
        this.notes.set("");
        this.location.set("");
        this.done.set(false);
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

    public Time getStartTime() {
        return startTime.get();
    }

    public void setStartTime(Time startTime) {
        this.startTime.set(startTime);
    }

    public ObjectProperty<Time> startTimeProperty() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime.get();
    }

    public void setEndTime(Time endTime) {
        this.endTime.set(endTime);
    }

    public ObjectProperty<Time> endTimeProperty() {
        return endTime;
    }

    public Date getStartDate() {
        return startDate.get();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate.set(Date.valueOf(startDate));
    }

    public ObjectProperty<Date> startDateProperty() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate.get();
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate.set(Date.valueOf(endDate));
    }

    public ObjectProperty<Date> endDateProperty() {
        return endDate;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
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

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public StringProperty locationProperty() {
        return location;
    }

    public boolean isDone() {
        return done.get();
    }

    public void setDone(boolean value) {
        this.done.set(value);
    }

    public BooleanProperty doneProperty() {
        return done;
    }


}
