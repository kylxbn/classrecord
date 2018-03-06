/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.*;

public class Score {
    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<Enrollee> enrollee = new SimpleObjectProperty<>();
    private final ObjectProperty<Task> task = new SimpleObjectProperty<>();
    private final IntegerProperty score = new SimpleIntegerProperty();
    private final StringProperty notes = new SimpleStringProperty();

    public Score(long id) {
        this.id.set(id);
        this.enrollee.set(null);
        this.task.set(null);
        this.score.set(0);
        this.notes.set("");
    }

    public Score() {
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

    public Enrollee getEnrollee() {
        return enrollee.get();
    }

    public void setEnrollee(Enrollee enrollee) {
        this.enrollee.set(enrollee);
    }

    public ObjectProperty<Enrollee> enrolleeProperty() {
        return enrollee;
    }

    public Task getTask() {
        return task.get();
    }

    public void setTask(Task task) {
        this.task.set(task);
    }

    public ObjectProperty<Task> taskProperty() {
        return task;
    }

    public int getScore() {
        return score.get();
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public IntegerProperty scoreProperty() {
        return score;
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
