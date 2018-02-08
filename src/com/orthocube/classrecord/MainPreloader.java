/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord;

import com.orthocube.classrecord.preloader.MainPreloaderController;
import com.orthocube.classrecord.util.Dialogs;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainPreloader extends Preloader {
    CredentialsConsumer consumer = null;

    private Stage preloaderStage;
    private Scene scene;
    String username = null;
    String password = null;
    private MainPreloaderController loadercontroller;

    @Override
    public void start(Stage stage) throws Exception {
        this.preloaderStage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("preloader/Preloader.fxml"));
        VBox root = loader.load();
        loadercontroller = loader.getController();
        loadercontroller.setMainPreloader(this);
        scene = new Scene(root);
        this.preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_16px.png")));
        this.preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_30px.png")));
        this.preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_40px.png")));
        this.preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_80px.png")));

        setDarkTheme();
        preloaderStage.setTitle("ClassRecord - Log In");
        preloaderStage.setScene(scene);
        preloaderStage.show();
    }

    public void setDarkTheme() {
        scene.getStylesheets().add(getClass().getResource("res/modena_dark.css").toExternalForm());
    }

    public void setLightTheme() {
        scene.getStylesheets().clear();
    }


    public void loginAction(String username, String password) {
        this.username = username;
        this.password = password;

        loadercontroller.disableLogin(true);
        mayBeHidden();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        loadercontroller.setProgress(((ProgressNotification) pn).getProgress());
    }

    private void mayBeHidden() {
        if (true) {//preloaderStage.isShowing() && username != null && password != null && consumer != null) {
            if (true) {//username.equals("orthocube") && password.equals("vurshfki")) {
                consumer.setCredential(username, password);
                Platform.runLater(() -> preloaderStage.hide());
            } else {
                Dialogs.error("Login Error", "Invalid username or password.", "The username you provided might be non-existent,\nor that is not the password for that username.");
                loadercontroller.disableLogin(false);
            }
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        // Handle state change notifications.
        StateChangeNotification.Type type = info.getType();
        if (type == Type.BEFORE_START) {
            loadercontroller.hideProgressBar();
            consumer = (CredentialsConsumer) info.getApplication();
            mayBeHidden();
        }
    }

    public interface CredentialsConsumer {
        void setCredential(String user, String password);
    }
}