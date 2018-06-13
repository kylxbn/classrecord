/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.about;

import com.interactivemesh.jfx.importer.col.ColModelImporter;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import com.orthocube.classrecord.util.Settings;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author OrthoCube
 */
public class AboutController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(AboutController.class.getName());
    @FXML
    Label lblJavaVersion;
    @FXML
    Label lblVendor;
    @FXML
    Label lblOSName;
    @FXML
    Label lblOSVersion;
    @FXML
    Label lblCPUISA;
    @FXML
    TextArea txtChangelog;
    @FXML
    TextArea txtCredits;
    @FXML
    Label lblDatabaseVersion;
    @FXML
    VBox subscenebox;

    public String getTitle() {
        return Settings.bundle.getString("about.about");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");

        lblJavaVersion.setText(System.getProperty("java.version"));
        lblVendor.setText(System.getProperty("java.vendor"));
        lblOSName.setText(System.getProperty("os.name"));
        lblOSVersion.setText(System.getProperty("os.version"));
        lblCPUISA.setText(System.getProperty("os.arch"));

        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        BufferedReader reader2 = null;
        StringBuilder sb2 = new StringBuilder();
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream("com/orthocube/classrecord/resources/changelog.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            in = getClass().getClassLoader().getResourceAsStream("com/orthocube/classrecord/resources/credits.txt");
            reader2 = new BufferedReader(new InputStreamReader(in));
            while ((line = reader2.readLine()) != null) {
                sb2.append(line);
                sb2.append("\n");
            }

        } catch (IOException e) {
            Dialogs.exception(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Dialogs.exception(ex);
                }
            }
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (IOException ex) {
                    Dialogs.exception(ex);
                }
            }
        }

        txtChangelog.setText(sb.toString());
        txtCredits.setText(sb2.toString());

        try {
            lblDatabaseVersion.setText(String.format(Settings.bundle.getString("about.dbversion"), DB.getDatabaseVersion()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ColModelImporter importer = new ColModelImporter();
        importer.read(getClass().getClassLoader().getResource("com/orthocube/classrecord/resources/orthocube.dae"));
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
        SubScene subscene = new SubScene(root, 150, 150, true, SceneAntialiasing.BALANCED);
        subscene.setCamera(camera);
        subscenebox.getChildren().add(0, subscene);
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
