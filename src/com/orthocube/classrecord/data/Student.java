/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.time.LocalDate;

/**
 * @author OrthoCube
 */
public final class Student {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty sid = new SimpleStringProperty();
    private final StringProperty fn = new SimpleStringProperty();
    private final StringProperty mn = new SimpleStringProperty();
    private final StringProperty ln = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> birthdate = new SimpleObjectProperty<>();
    private final BooleanProperty isFemale = new SimpleBooleanProperty();
    private final StringProperty contact = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty notes = new SimpleStringProperty();
    private final ObjectProperty<Image> picture = new SimpleObjectProperty<>();

    public Student(long id) {
        this.id.set(id);
        this.sid.set("");
        this.fn.set("");
        this.mn.set("");
        this.ln.set("");
        this.isFemale.set(false);
        this.contact.set("");
        this.address.set("");
        this.notes.set("");
        this.picture.set(null);
    }

    public Student() {
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

    public LocalDate getBirthdate() {
        return birthdate.get();
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate.set(birthdate);
    }

    public ObjectProperty<LocalDate> birthdateProperty() {
        return birthdate;
    }

    public String getSID() {
        return sid.get();
    }

    public void setSID(String sid) {
        this.sid.set(sid);
    }

    public StringProperty sidProperty() {
        return sid;
    }

    public String getFN() {
        return fn.get();
    }

    public void setFN(String fn) {
        this.fn.set(fn);
    }

    public StringProperty fnProperty() {
        return fn;
    }

    public String getMN() {
        return mn.get();
    }

    public void setMN(String mn) {
        this.mn.set(mn);
    }

    public StringProperty mnProperty() {
        return mn;
    }

    public String getLN() {
        return ln.get();
    }

    public void setLN(String ln) {
        this.ln.set(ln);
    }

    public StringProperty lnProperty() {
        return ln;
    }

    public boolean isFemale() {
        return isFemale.get();
    }

    public void setFemale(boolean isFemale) {
        this.isFemale.set(isFemale);
    }

    public BooleanProperty isFemaleProperty() {
        return isFemale;
    }

    public String getContact() {
        return contact.get();
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    public StringProperty contactProperty() {
        return contact;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
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

    public Image getPicture() {
        return picture.get();
    }

    public void setPicture(Image picture) {
        this.picture.set(picture);
    }

    public ObjectProperty<Image> pictureProperty() {
        return picture;
    }
}
