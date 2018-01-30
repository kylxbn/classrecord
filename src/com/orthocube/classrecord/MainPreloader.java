/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord;

import com.orthocube.classrecord.preloader.MainPreloaderController;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
        Scene scene = new Scene(root);
        preloaderStage.setScene(scene);
        preloaderStage.show();
    }

    public void loginAction(String username, String password) {
        this.username = username;
        this.password = password;

        mayBeHidden();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        loadercontroller.setProgress(((ProgressNotification) pn).getProgress());
    }

    private void mayBeHidden() {
        if (preloaderStage.isShowing() && username != null && consumer != null) {
            consumer.setCredential(username, password);
            Platform.runLater(() -> preloaderStage.hide());
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