/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.dashboard;

import com.orthocube.classrecord.MainApp;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private VBox vbxNotifications;
    @FXML
    private ProgressIndicator prgLoading;

    public void setMainApp(MainApp mainApp) {
        MainApp mainApp1 = mainApp;
    }

    public String getTitle() {
        return "Dashboard"; // TODO: Properly TL this shit
    }

    private void populateNotifications() {
        DashboardPoller poller = new DashboardPoller();
        poller.stateProperty().addListener((obs, oldv, newv) -> {
            if (newv == Worker.State.SUCCEEDED) {
                ParallelTransition transitionIn = new ParallelTransition();
                for (Node n : poller.getValue()) {
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), n);
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    TranslateTransition flyIn = new TranslateTransition(Duration.millis(500), n);
                    flyIn.setFromY(150);
                    flyIn.setToY(0);
                    flyIn.setInterpolator(Interpolator.EASE_OUT);
                    ParallelTransition parallelTransition = new ParallelTransition(fadeIn, flyIn);
                    transitionIn.getChildren().add(parallelTransition);
                }
                vbxNotifications.getChildren().setAll(poller.getValue());
                transitionIn.setOnFinished(e -> prgLoading.setVisible(false));
                transitionIn.play();
            } else if (newv == Worker.State.FAILED) {
                poller.getException().printStackTrace();
            }
        });

        new Thread(poller).start();
    }

    @FXML
    private void cmdRefreshAction(ActionEvent event) {
        prgLoading.setVisible(true);

        // remove all notifications first
        ParallelTransition transitionOut = new ParallelTransition();
        for (Node n : vbxNotifications.getChildren()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), n);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            TranslateTransition flyOut = new TranslateTransition(Duration.millis(500), n);
            flyOut.setFromY(0);
            flyOut.setToY(-150);
            flyOut.setInterpolator(Interpolator.EASE_IN);
            ParallelTransition parallelTransition = new ParallelTransition(fadeOut, flyOut);
            transitionOut.getChildren().add(parallelTransition);
        }
        transitionOut.setOnFinished(e -> populateNotifications());
        transitionOut.play();
    }

    public void refresh() {
        cmdRefreshAction(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setDark() {
        for (Node n : vbxNotifications.getChildren()) {
            n.setStyle("-fx-background-color: #444; -fx-background-radius: 8;");
        }
    }


    public void setLight() {
        for (Node n : vbxNotifications.getChildren()) {
            n.setStyle("-fx-background-color: #CCC; -fx-background-radius: 8;");
        }
    }

    public void clear() {
        vbxNotifications.getChildren().clear();
    }
}
