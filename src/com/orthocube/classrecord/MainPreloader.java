/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord;

import com.orthocube.classrecord.preloader.MainPreloaderController;
import com.orthocube.classrecord.util.Dialogs;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.NotificationPane;

public class MainPreloader extends Preloader {
    CredentialsConsumer consumer = null;

    private Stage preloaderStage;
    private Scene scene;
    String username = null;
    String password = null;
    private MainPreloaderController loadercontroller;

    Group topGroup;
    StackPane preloaderParent;
    StateChangeNotification evt;

    @Override
    public void start(Stage stage) throws Exception {
        this.preloaderStage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("preloader/Preloader.fxml"));
        StackPane root = loader.load();
        loadercontroller = loader.getController();
        loadercontroller.setMainPreloader(this);
        preloaderParent = root;
        topGroup = new Group(preloaderParent);

        this.preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_16px.png")));
        this.preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_30px.png")));
        this.preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_40px.png")));
        this.preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_80px.png")));

        preloaderStage.setTitle("ClassRecord - Log In");
        //preloaderStage.setMaximized(true);
        preloaderStage.setWidth(1024);
        preloaderStage.setHeight(768);
        preloaderStage.setMinWidth(1024);
        preloaderStage.setMinHeight(768);
        scene = new Scene(topGroup);
        setDarkTheme();
        preloaderStage.setScene(scene);

        preloaderParent.prefHeightProperty().bind(preloaderStage.getScene().heightProperty());
        preloaderParent.prefWidthProperty().bind(preloaderStage.getScene().widthProperty());

        loadercontroller.startOpeningAnimation();
        preloaderStage.show();
    }

    public void setDarkTheme() {
        scene.getStylesheets().add(getClass().getResource("res/modena_dark.css").toExternalForm());
        //preloaderParent.setStyle("-fx-background-color: #323232;"); // 0x323232");
        preloaderParent.getStylesheets().add(getClass().getResource("res/darkBG.css").toExternalForm());
    }

    public void setLightTheme() {
        scene.getStylesheets().clear();
        //preloaderParent.setStyle("-fx-background-color: #323232;"); // 0x323232");
        preloaderParent.getStylesheets().add(getClass().getResource("res/lightBG.css").toExternalForm());
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
        if (preloaderStage.isShowing() && username != null && password != null && consumer != null) {
            if (username.equals("orthocube") && password.equals("vurshfki")) {
                consumer.setCredential(username, password, preloaderStage, scene);
                //Platform.runLater(() -> preloaderStage.hide());
                SharedScene appScene = (SharedScene) evt.getApplication();
                fadeInTo(appScene.getParentNode());
            } else {
                Dialogs.error("Login Error", "Invalid username or password.", "The username you provided might be non-existent,\nor that is not the password for that username.");
                loadercontroller.disableLogin(false);
            }
        }
    }

    private void fadeInTo(NotificationPane p) {
        //add application scene to the preloader group
        // (visualized "behind" preloader at this point)
        //Note: list is back to front
        p.prefHeightProperty().bind(preloaderStage.getScene().heightProperty());
        p.prefWidthProperty().bind(preloaderStage.getScene().widthProperty());

        topGroup.getChildren().add(0, p);

        //setup fade transition for preloader part of scene
        // fade out over 5s
        FadeTransition ft1 = new FadeTransition(
                Duration.millis(250),
                preloaderParent);
        ft1.setFromValue(1.0);
        ft1.setToValue(1.0);
        ft1.setOnFinished(t -> loadercontroller.startClosingAnimation());

        FadeTransition ft2 = new FadeTransition(
                Duration.millis(1000),
                preloaderParent);
        ft2.setFromValue(1.0);
        ft2.setToValue(0.0);
        ft2.setOnFinished(t -> topGroup.getChildren().remove(preloaderParent));

        SequentialTransition st = new SequentialTransition();
        st.getChildren().addAll(ft1, ft2);
        st.play();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        // Handle state change notifications.
        StateChangeNotification.Type type = info.getType();
        if (type == Type.BEFORE_START) {
            evt = info;
            loadercontroller.hideProgressBar();
            consumer = (CredentialsConsumer) info.getApplication();
            mayBeHidden();
        }
    }

    public interface CredentialsConsumer {
        void setCredential(String user, String password, Stage stage, Scene scene);
    }

    public interface SharedScene {
        NotificationPane getParentNode();
    }
}