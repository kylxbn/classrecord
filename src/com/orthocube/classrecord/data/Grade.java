/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Grade {
    private final StringProperty fname = new SimpleStringProperty();
    private final StringProperty lname = new SimpleStringProperty();
    private final StringProperty prelim = new SimpleStringProperty();
    private final StringProperty semis = new SimpleStringProperty();
    private final StringProperty midterms = new SimpleStringProperty();
    private final StringProperty finals = new SimpleStringProperty();
    private final StringProperty _final = new SimpleStringProperty();
    private final IntegerProperty classCard = new SimpleIntegerProperty();
    private final StringProperty course = new SimpleStringProperty();

    public String getFName() {
        return fname.get();
    }

    public void setFName(String name) {
        this.fname.set(name);
    }

    public StringProperty fnameProperty() {
        return fname;
    }

    public String getLName() {
        return lname.get();
    }

    public void setLName(String name) {
        this.lname.set(name);
    }

    public StringProperty lnameProperty() {
        return lname;
    }

    public String getPrelim() {
        return prelim.get();
    }

    public void setPrelim(String prelim) {
        this.prelim.set(prelim);
    }

    public StringProperty prelimProperty() {
        return prelim;
    }

    public String getSemis() {
        return semis.get();
    }

    public void setSemis(String semis) {
        this.semis.set(semis);
    }

    public StringProperty semisProperty() {
        return semis;
    }

    public String getMidterms() {
        return midterms.get();
    }

    public void setMidterms(String midterms) {
        this.midterms.set(midterms);
    }

    public StringProperty midtermsProperty() {
        return midterms;
    }

    public String getFinals() {
        return finals.get();
    }

    public void setFinals(String finals) {
        this.finals.set(finals);
    }

    public StringProperty finalsProperty() {
        return finals;
    }

    public String getFinal() {
        return _final.get();
    }

    public void setFinal(String _final) {
        this._final.set(_final);
    }

    public StringProperty finalProperty() {
        return _final;
    }

    public int getClassCard() {
        return classCard.get();
    }

    public void setClassCard(int classCard) {
        this.classCard.set(classCard);
    }

    public IntegerProperty classCardProperty() {
        return classCard;
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
