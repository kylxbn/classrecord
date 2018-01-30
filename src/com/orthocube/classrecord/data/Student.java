/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.*;

import java.awt.image.BufferedImage;

/**
 * @author OrthoCube
 */
public final class Student {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty sid = new SimpleStringProperty();
    private final StringProperty fn = new SimpleStringProperty();
    private final StringProperty mn = new SimpleStringProperty();
    private final StringProperty ln = new SimpleStringProperty();
    private final BooleanProperty isFemale = new SimpleBooleanProperty();
    private final StringProperty contact = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty notes = new SimpleStringProperty();
    private final ObjectProperty<BufferedImage> picture = new SimpleObjectProperty<>();

    public Student(long id, String sid, String fn, String mn, String ln, boolean isFemale, String contact, String address, String notes, BufferedImage picture) {
        this.id.set(id);
        this.sid.set(sid);
        this.fn.set(fn);
        this.mn.set(mn);
        this.ln.set(ln);
        this.isFemale.set(isFemale);
        this.contact.set(contact);
        this.address.set(address);
        this.notes.set(notes);
        this.picture.set(picture);
    }

    public Student(long id) {
        this(id, "", "", "", "", false, "", "", "", null);
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

    public BufferedImage getPicture() {
        return picture.get();
    }

    public void setPicture(BufferedImage picture) {
        this.picture.set(picture);
    }

    public ObjectProperty<BufferedImage> pictureProperty() {
        return picture;
    }
}
