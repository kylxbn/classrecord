/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.*;

public final class Enrollee {
    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<Clazz> clazz = new SimpleObjectProperty<>();
    private final ObjectProperty<Student> student = new SimpleObjectProperty<>();
    private final IntegerProperty classcard = new SimpleIntegerProperty();
    private final StringProperty notes = new SimpleStringProperty();
    private final StringProperty course = new SimpleStringProperty();

    @Override
    public int hashCode() {
        return (int) id.get();
    }

    public Enrollee(long id, Clazz clazz, Student student, int classcard, String notes, String course) {
        this.id.set(id);
        this.clazz.set(clazz);
        this.student.set(student);
        this.classcard.set(classcard);
        this.notes.set(notes);
        this.course.set(course);
    }

    public Enrollee(long id) {
        this(id, null, null, 0, "", "");
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

    public Clazz getClazz() {
        return clazz.get();
    }

    public ObjectProperty<Clazz> classIDProperty() {
        return clazz;
    }

    public void setClass(Clazz clazz) {
        this.clazz.set(clazz);
    }

    public Student getStudent() {
        return student.get();
    }

    public void setStudent(Student student) {
        this.student.set(student);
    }

    public ObjectProperty<Student> studentProperty() {
        return student;
    }

    public int getClasscard() {
        return classcard.get();
    }

    public void setClasscard(int classcard) {
        this.classcard.set(classcard);
    }

    public IntegerProperty classcardProperty() {
        return classcard;
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

    public String getCourse() {
        return course.get();
    }

    public void setCourse(String course) {
        this.course.set(course);
    }

    public StringProperty courseProperty() {
        return course;
    }
}
