/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.preloader;

import com.interactivemesh.jfx.importer.col.ColModelImporter;
import com.orthocube.classrecord.MainPreloader;
import com.orthocube.classrecord.util.Dialogs;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPreloaderController implements Initializable {
    private MainPreloader mainPreloader;

    private boolean dark = true;
    private VBox firstStart = null;
    private ValidationSupport support;

    @FXML
    private HBox vbxLoading;

    @FXML
    private VBox vbxLogin;

    @FXML
    private VBox vbxFirstStart;

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
    private Label lblClassRecord2;

    @FXML
    private VBox vbxInfo;

    @FXML
    private Pane pnlBG;
    private ResourceBundle rb;

    @FXML
    void cmdLoginAction(ActionEvent event) {
        if (support.isInvalid()) {
            Dialogs.error("Incomplete Details", "Username and password are required.", "Please complete the fields first.");
            return;
        }
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
            lblDescription.setText(rb.getString("preloader.mainpanel"));
        else if (progress < 0.25)
            lblDescription.setText(rb.getString("preloader.studentstab"));
        else if (progress < 0.35)
            lblDescription.setText(rb.getString("preloader.connecting"));
        else if (progress < 0.45)
            lblDescription.setText(rb.getString("preloader.classestab"));
        else if (progress < 0.55)
            lblDescription.setText(rb.getString("preloader.classes"));
        else if (progress < 0.65)
            lblDescription.setText(rb.getString("preloader.userstab"));
        else if (progress < 0.75)
            lblDescription.setText(rb.getString("preloader.users"));
        else if (progress < 0.85)
            lblDescription.setText(rb.getString("preloader.abouttab"));
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

        FadeTransition stage1b = new FadeTransition(Duration.millis(1000), lblClassRecord2);
        stage1b.setFromValue(0.0);
        stage1b.setToValue(0.0);

        FadeTransition stage1c = new FadeTransition(Duration.millis(1000), vbxLogin);
        stage1c.setFromValue(0.0);
        stage1c.setToValue(0.0);

        FadeTransition stage1d = new FadeTransition(Duration.millis(1000), vbxLoading);
        stage1d.setFromValue(0.0);
        stage1d.setToValue(0.0);

        FadeTransition stage1e = new FadeTransition(Duration.millis(1000), vbxInfo);
        stage1e.setFromValue(0.0);
        stage1e.setToValue(0.0);

        FadeTransition stage1f = new FadeTransition(Duration.millis(1000), pnlBG);
        stage1f.setFromValue(0.0);
        stage1f.setToValue(0.0);

        // STAGE 2 - Title and loading progress shows but everything else is hidden
        FadeTransition stage2a = new FadeTransition(Duration.millis(750), lblClassRecord);
        stage2a.setFromValue(0.0);
        stage2a.setToValue(1.0);

        TranslateTransition stage2b = new TranslateTransition(Duration.millis(750), lblClassRecord);
        stage2b.setFromY(-100);
        stage2b.setToY(0);

        FadeTransition stage2c = new FadeTransition(Duration.millis(750), lblClassRecord2);
        stage2c.setFromValue(0.0);
        stage2c.setToValue(1.0);

        TranslateTransition stage2d = new TranslateTransition(Duration.millis(750), lblClassRecord2);
        stage2d.setFromY(-100);
        stage2d.setToY(0);

        FadeTransition stage2e = new FadeTransition(Duration.millis(750), vbxLoading);
        stage2e.setFromValue(0.0);
        stage2e.setToValue(1.0);

        FadeTransition stage2f = new FadeTransition(Duration.millis(750), vbxInfo);
        stage2f.setFromValue(0.0);
        stage2f.setToValue(1.0);

        FadeTransition stage2g = new FadeTransition(Duration.millis(750), pnlBG);
        stage2g.setFromValue(0.0);
        stage2g.setToValue(1.0);

        TranslateTransition stage2h = new TranslateTransition(Duration.millis(750), pnlBG);
        stage2h.setFromY(-100);
        stage2h.setToY(0);

        // STAGE 3 - Everything else shows
        FadeTransition stage3 = new FadeTransition(Duration.millis(750), vbxLogin);
        stage3.setFromValue(0.0);
        stage3.setToValue(1.0);

        // set up transitions
        ParallelTransition stage1t = new ParallelTransition();
        stage1t.getChildren().addAll(stage1a, stage1b, stage1c, stage1d, stage1e, stage1f);

        ParallelTransition stage2t = new ParallelTransition();
        stage2t.getChildren().addAll(stage2a, stage2b, stage2c, stage2d, stage2e, stage2f, stage2g, stage2h);

        ParallelTransition stage3t = new ParallelTransition();
        stage3t.getChildren().addAll(stage3);

        SequentialTransition animation;

        animation = new SequentialTransition();
        animation.getChildren().addAll(stage1t, stage2t, stage3t);

        animation.play();
    }

    public void hideFirstStart() {
        vbxFirstStart.setVisible(false);
    }

    public void startClosingAnimation() {
        // STAGE 1 - Everything goes down while fading out
        TranslateTransition stage1a = new TranslateTransition(Duration.millis(1000), lblClassRecord);
        stage1a.setFromY(0.0);
        stage1a.setToY(100);

        TranslateTransition stage1b = new TranslateTransition(Duration.millis(1000), lblClassRecord2);
        stage1b.setFromY(0.0);
        stage1b.setToY(100);

        TranslateTransition stage1c = new TranslateTransition(Duration.millis(1000), vbxLogin);
        stage1c.setFromY(0.0);
        stage1c.setToY(100);

        TranslateTransition stage1d = new TranslateTransition(Duration.millis(1000), pnlBG);
        stage1d.setFromY(0.0);
        stage1d.setToY(100);

        // set up transitions
        ParallelTransition stage1t = new ParallelTransition();
        stage1t.getChildren().addAll(stage1a, stage1b, stage1c, stage1d);

        SequentialTransition animation = new SequentialTransition();
        animation.getChildren().addAll(stage1t);
        animation.play();

    }

    public void setDark() {
        pnlBG.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        dark = true;
    }

    public void setLight() {
        pnlBG.setStyle("-fx-background-color: rgba(255,255,255,0.4);");
        dark = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;
        support = new ValidationSupport();

        support.registerValidator(txtUsername, Validator.createEmptyValidator(rb.getString("preloader.usernamerequired")));
        support.registerValidator(txtPassword, Validator.createEmptyValidator(rb.getString("preloader.passwordrequired")));

        if (!(new File("database").exists())) {
            grpMain.setVisible(false);
            vbxFirstStart.setVisible(true);
        } else {
            grpMain.setVisible(true);
            vbxFirstStart.setVisible(false);
        }

        ColModelImporter importer = new ColModelImporter();
        importer.read(getClass().getResource("../../resources/orthocube.dae"));
        Shape3D orthocube = (Shape3D) (((Group) importer.getImport()[0]).getChildren().get(0));
        orthocube.setMaterial(new PhongMaterial(Color.TEAL));
        orthocube.setDrawMode(DrawMode.FILL);

        Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
        Camera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(
                yRotate,
                new Translate(0, 0, -15));
        Group root = new Group();
        root.getChildren().add(camera);
        root.getChildren().add(orthocube);
        SubScene subscene = new SubScene(root, 75, 75, true, SceneAntialiasing.BALANCED);
        subscene.setCamera(camera);
        vbxInfo.getChildren().add(0, subscene);
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        new KeyValue(yRotate.angleProperty(), 0)
                ),
                new KeyFrame(
                        Duration.seconds(15),
                        new KeyValue(yRotate.angleProperty(), 360)
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
