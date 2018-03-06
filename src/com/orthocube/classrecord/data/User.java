/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.data;

import javafx.beans.property.*;
import javafx.scene.image.Image;

public class User {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty nickname = new SimpleStringProperty();
    private final ObjectProperty<Image> picture = new SimpleObjectProperty<>();
    private final IntegerProperty accessLevel = new SimpleIntegerProperty();

    public User(long id) {
        this.id.set(id);
        this.username.set("");
        this.nickname.set("");
        this.picture.set(null);
        this.accessLevel.set(0);
    }

    public User() {
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

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getNickname() {
        return nickname.get();
    }

    public void setNickname(String nickname) {
        this.nickname.set(nickname);
    }

    public StringProperty nicknameProperty() {
        return nickname;
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

    public int getAccessLevel() {
        return accessLevel.get();
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel.set(accessLevel);
    }

    public IntegerProperty accessLevelProperty() {
        return accessLevel;
    }
}