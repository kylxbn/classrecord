/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.preloader;

import com.orthocube.classrecord.MainPreloader;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPreloaderController implements Initializable {
    MainPreloader mainPreloader;

    boolean dark = true;

    @FXML
    private HBox vbxLoading;

    @FXML
    private Label lblDescription;

    @FXML
    private ProgressBar prgProgress;

    @FXML
    private Button cmdLogin;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private VBox vbxMain;

    @FXML
    private GridPane grpMain;

    @FXML
    private Label lblClassRecord;

    @FXML
    private VBox vbxInfo;

    @FXML
    private Pane pnlBG;

    @FXML
    void cmdLoginAction(ActionEvent event) {
        mainPreloader.loginAction(txtUsername.getText(), txtPassword.getText());
    }

    public void disableLogin(boolean v) {
        cmdLogin.setDisable(v);
        txtUsername.setDisable(v);
        txtPassword.setDisable(v);
    }

    public void setMainPreloader(MainPreloader mainPreloader) {
        this.mainPreloader = mainPreloader;
    }

    public void setProgress(double progress) {
        prgProgress.setProgress(progress);
        if (progress < 0.15)
            lblDescription.setText("Loading Main panel...");
        else if (progress < 0.25)
            lblDescription.setText("Loading Students tab...");
        else if (progress < 0.35)
            lblDescription.setText("Connecting to database...");
        else if (progress < 0.45)
            lblDescription.setText("Loading Classes tab...");
        else if (progress < 0.55)
            lblDescription.setText("Loading classes from database...");
        else if (progress < 0.65)
            lblDescription.setText("Loading Users tab...");
        else if (progress < 0.75)
            lblDescription.setText("Loading users from database...");
        else if (progress < 0.85)
            lblDescription.setText("Loading About tab...");
    }

    public void hideProgressBar() {
        FadeTransition stage2c = new FadeTransition(Duration.millis(750), vbxLoading);
        stage2c.setFromValue(1.0);
        stage2c.setToValue(0.0);
        stage2c.play();
        //vbxLoading.setVisible(false);
        cmdLogin.setDisable(false);
    }

    public void startOpeningAnimation() {
        // STAGE 1 - Everything is hidden (250ms)
        FadeTransition stage1a = new FadeTransition(Duration.millis(1000), lblClassRecord);
        stage1a.setFromValue(0.0);
        stage1a.setToValue(0.0);

        FadeTransition stage1b = new FadeTransition(Duration.millis(1000), grpMain);
        stage1b.setFromValue(0.0);
        stage1b.setToValue(0.0);

        FadeTransition stage1c = new FadeTransition(Duration.millis(1000), vbxLoading);
        stage1c.setFromValue(0.0);
        stage1c.setToValue(0.0);

        FadeTransition stage1d = new FadeTransition(Duration.millis(1000), vbxInfo);
        stage1d.setFromValue(0.0);
        stage1d.setToValue(0.0);

        FadeTransition stage1e = new FadeTransition(Duration.millis(1000), pnlBG);
        stage1e.setFromValue(0.0);
        stage1e.setToValue(0.0);

        // STAGE 2 - Title and loading progress shows but everything else is hidden
        FadeTransition stage2a = new FadeTransition(Duration.millis(750), lblClassRecord);
        stage2a.setFromValue(0.0);
        stage2a.setToValue(1.0);

        TranslateTransition stage2b = new TranslateTransition(Duration.millis(750), lblClassRecord);
        stage2b.setFromY(-100);
        stage2b.setToY(0);

        FadeTransition stage2c = new FadeTransition(Duration.millis(750), vbxLoading);
        stage2c.setFromValue(0.0);
        stage2c.setToValue(1.0);

        FadeTransition stage2d = new FadeTransition(Duration.millis(750), vbxInfo);
        stage2d.setFromValue(0.0);
        stage2d.setToValue(1.0);

        FadeTransition stage2e = new FadeTransition(Duration.millis(750), pnlBG);
        stage2e.setFromValue(0.0);
        stage2e.setToValue(1.0);

        TranslateTransition stage2f = new TranslateTransition(Duration.millis(750), pnlBG);
        stage2f.setFromY(-100);
        stage2f.setToY(0);

        // STAGE 3 - Everything else shows
        FadeTransition stage3 = new FadeTransition(Duration.millis(750), grpMain);
        stage3.setFromValue(0.0);
        stage3.setToValue(1.0);

        // set up transitions
        ParallelTransition stage1t = new ParallelTransition();
        stage1t.getChildren().addAll(stage1a, stage1b, stage1c, stage1d, stage1e);

        ParallelTransition stage2t = new ParallelTransition();
        stage2t.getChildren().addAll(stage2a, stage2b, stage2c, stage2d, stage2e, stage2f);

        ParallelTransition stage3t = new ParallelTransition();
        stage3t.getChildren().addAll(stage3);

        SequentialTransition animation = new SequentialTransition();
        animation.getChildren().addAll(stage1t, stage2t, stage3t);
        animation.play();
    }

    public void startClosingAnimation() {
        // STAGE 1 - Everything goes down while fading out
        TranslateTransition stage1a = new TranslateTransition(Duration.millis(1000), lblClassRecord);
        stage1a.setFromY(0.0);
        stage1a.setToY(100);

        TranslateTransition stage1b = new TranslateTransition(Duration.millis(1000), grpMain);
        stage1b.setFromY(0.0);
        stage1b.setToY(100);

        TranslateTransition stage1c = new TranslateTransition(Duration.millis(1000), pnlBG);
        stage1c.setFromY(0.0);
        stage1c.setToY(100);

        // set up transitions
        ParallelTransition stage1t = new ParallelTransition();
        stage1t.getChildren().addAll(stage1a, stage1b, stage1c);

        SequentialTransition animation = new SequentialTransition();
        animation.getChildren().addAll(stage1t);
        animation.play();
    }

    public void setDark() {
        pnlBG.setStyle("-fx-background-color: rgba(0,0,0,0.25);");
    }

    public void setLight() {
        pnlBG.setStyle("-fx-background-color: rgba(255,255,255,0.25);");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ValidationSupport support = new ValidationSupport();

        support.registerValidator(txtUsername, Validator.createEmptyValidator("Username is required"));
        support.registerValidator(txtPassword, Validator.createEmptyValidator("Password is required"));
    }
}
