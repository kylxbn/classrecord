/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.*;

public class AttendanceList {
    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<AttendanceDay> attendanceDay = new SimpleObjectProperty<>();
    private final ObjectProperty<Enrollee> enrollee = new SimpleObjectProperty<>();
    private final StringProperty remarks = new SimpleStringProperty();
    private final StringProperty notes = new SimpleStringProperty();

    public AttendanceList(long id, AttendanceDay ad, Enrollee e, String rem, String n) {
        this.id.set(id);
        this.attendanceDay.set(ad);
        this.enrollee.set(e);
        this.remarks.set(rem);
        this.notes.set(n);
    }

    public AttendanceList(long id) {
        this(id, null, null, "", "");
    }

    public AttendanceList() {
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

    public AttendanceDay getAttendanceDay() {
        return attendanceDay.get();
    }

    public void setAttendanceDay(AttendanceDay attendanceDay) {
        this.attendanceDay.set(attendanceDay);
    }

    public ObjectProperty<AttendanceDay> attendanceDayProperty() {
        return attendanceDay;
    }

    public Enrollee getEnrollee() {
        return enrollee.get();
    }

    public void setEnrollee(Enrollee enrollee) {
        this.enrollee.set(enrollee);
    }

    public ObjectProperty<Enrollee> enrolleeProperty() {
        return enrollee;
    }

    public String getRemarks() {
        return remarks.get();
    }

    public void setRemarks(String remarks) {
        this.remarks.set(remarks);
    }

    public StringProperty remarksProperty() {
        return remarks;
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
