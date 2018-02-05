/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.*;

public class Criteria {
    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<Clazz> clazz = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty percentage = new SimpleIntegerProperty();
    private final IntegerProperty terms = new SimpleIntegerProperty();

    public Criteria(long id, Clazz clazz, String name, int percentage, int terms) {
        this.id.set(id);
        this.clazz.set(clazz);
        this.name.set(name);
        this.percentage.set(percentage);
        this.terms.set(terms);
    }

    public Criteria(long id) {
        this(id, null, "", 0, 0);
    }

    public Criteria() {
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

    public Clazz getClazz() {
        return clazz.get();
    }

    public ObjectProperty<Clazz> classProperty() {
        return clazz;
    }

    public void setClass(Clazz clazz) {
        this.clazz.set(clazz);
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

    public int getPercentage() {
        return percentage.get();
    }

    public void setPercentage(int percentage) {
        this.percentage.set(percentage);
    }

    public IntegerProperty percentageProperty() {
        return percentage;
    }

    public int getTerms() {
        return terms.get();
    }

    public void setTerms(int terms) {
        this.terms.set(terms);
    }

    public IntegerProperty termsProperty() {
        return terms;
    }
}
