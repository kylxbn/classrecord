/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Task {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Clazz> clazz = new SimpleObjectProperty<>();
    private final ObjectProperty<Criterion> criterion = new SimpleObjectProperty<>();
    private final ObjectProperty<ObservableList<Score>> scores = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty term = new SimpleIntegerProperty();
    private final IntegerProperty items = new SimpleIntegerProperty();

    public Task(long id) {
        this.id.set(id);
        this.name.set("");
        this.clazz.set(null);
        this.criterion.set(null);
        this.term.set(0);
        this.items.set(0);
    }

    public Task() {
        this(-1);
    }

    public ObservableList<Score> getScores() {
        return scores.get();
    }

    public void setScores(ObservableList<Score> scores) {
        this.scores.set(scores);
    }

    public ObjectProperty<ObservableList<Score>> scoresProperty() {
        return scores;
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

    public Clazz getClazz() {
        return clazz.get();
    }

    public ObjectProperty<Clazz> classProperty() {
        return clazz;
    }

    public void setClass(Clazz clazz) {
        this.clazz.set(clazz);
    }

    public Criterion getCriterion() {
        return criterion.get();
    }

    public void setCriterion(Criterion criteria) {
        this.criterion.set(criteria);
    }

    public ObjectProperty<Criterion> criterionProperty() {
        return criterion;
    }

    public int getTerm() {
        return term.get();
    }

    public void setTerm(int term) {
        this.term.set(term);
    }

    public IntegerProperty termProperty() {
        return term;
    }

    public int getItems() {
        return items.get();
    }

    public void setItems(int items) {
        this.items.set(items);
    }

    public IntegerProperty itemsProperty() {
        return items;
    }
}
