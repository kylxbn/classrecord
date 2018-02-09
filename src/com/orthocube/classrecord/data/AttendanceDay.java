/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.sql.Date;
import java.time.LocalDate;

public class AttendanceDay {
    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<Date> date = new SimpleObjectProperty<>();
    private final ObjectProperty<Clazz> clazz = new SimpleObjectProperty<>();
    private final StringProperty notes = new SimpleStringProperty();
    private final ObjectProperty<ObservableList<AttendanceList>> attendanceList = new SimpleObjectProperty<>();

    public AttendanceDay(long id, Date date, Clazz clazz, String notes) {
        this.id.set(id);
        this.date.set(date);
        this.clazz.set(clazz);
        this.notes.set(notes);
    }

    public AttendanceDay(long id) {
        //this(id, Date.valueOf((new SimpleDateFormat("yyyy-mm-dd")).format(new java.util.Date())), null, "");
        this(id, Date.valueOf("2018-01-01"), null, "");
    }

    public AttendanceDay() {
        this(-1);
    }

    public ObservableList<AttendanceList> getAttendanceList() {
        return attendanceList.get();
    }

    public void setAttendanceList(ObservableList<AttendanceList> attendanceList) {
        this.attendanceList.set(attendanceList);
    }

    public ObjectProperty<ObservableList<AttendanceList>> attendanceListProperty() {
        return attendanceList;
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

    public Date getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(Date.valueOf(date));
    }

    public ObjectProperty<Date> dateProperty() {
        return date;
    }

    public Clazz getClazz() {
        return clazz.get();
    }

    public ObjectProperty<Clazz> clazzProperty() {
        return clazz;
    }

    public void setClass(Clazz clazz) {
        this.clazz.set(clazz);
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
