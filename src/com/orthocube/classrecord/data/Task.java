/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.*;

public class Task {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Clazz> clazz = new SimpleObjectProperty<>();
    private final ObjectProperty<Criteria> criteria = new SimpleObjectProperty<>();
    private final IntegerProperty term = new SimpleIntegerProperty();
    private final IntegerProperty items = new SimpleIntegerProperty();

    public Task(long id, String name, Clazz clazz, Criteria criteria, int term, int items) {
        this.id.set(id);
        this.name.set(name);
        this.clazz.set(clazz);
        this.criteria.set(criteria);
        this.term.set(term);
        this.items.set(items);
    }

    public Task(long id) {
        this(id, "", null, null, 0, 0);
    }

    public Task() {
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

    public Clazz getClazz() {
        return clazz.get();
    }

    public ObjectProperty<Clazz> classProperty() {
        return clazz;
    }

    public void setClass(Clazz clazz) {
        this.clazz.set(clazz);
    }

    public Criteria getCriteria() {
        return criteria.get();
    }

    public void setCriteria(Criteria criteria) {
        this.criteria.set(criteria);
    }

    public ObjectProperty<Criteria> criteriaProperty() {
        return criteria;
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
