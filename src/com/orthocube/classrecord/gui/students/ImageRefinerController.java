/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.students;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageRefinerController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(StudentsController.class.getName());
    private ResourceBundle bundle;

    private Stage dialogStage;
    private BufferedImage image;
    private BufferedImage result;

    private int stage = 0;
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    @FXML
    private Label lblInstructions;
    @FXML
    private ImageView pboPicture;
    @FXML
    private Button cmdReset;
    @FXML
    private Button cmdDone;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        this.result = image;
        pboPicture.setFitWidth(result.getWidth());
        pboPicture.setFitHeight(result.getHeight());
        pboPicture.setImage(SwingFXUtils.toFXImage(result, null));
    }

    public BufferedImage getResult() {
        return result;
    }

    @FXML
    void cmdDoneAction(ActionEvent event) {
        dialogStage.close();
    }

    @FXML
    void cmdResetAction(ActionEvent event) {
        this.result = image;
        pboPicture.setFitWidth(result.getWidth());
        pboPicture.setFitHeight(result.getHeight());
        pboPicture.setImage(SwingFXUtils.toFXImage(result, null));
        lblInstructions.setText("Please click on left eye or click [Done] to accept the image as is.");
        stage = 0;
    }

    @FXML
    void pboPictureAction(MouseEvent evt) {
        switch (stage) {
            case 0:
                x1 = evt.getX();
                y1 = evt.getY();
                stage = 1;
                lblInstructions.setText("Now please click on right eye or click [Done] to accept the image as is.");
                break;
            case 1:
                x2 = evt.getX();
                y2 = evt.getY();
                stage = 2;
                lblInstructions.setText("Finally, please click on mouth or click [Done] to accept the image as is.");
                break;
            case 2:
                double x3 = evt.getX();
                double y3 = evt.getY();

                AffineTransform at = new AffineTransform();
                double s = ((double) image.getWidth() / 4.0) / Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
                at.translate(image.getWidth() / 2, image.getHeight() / 2);
                at.scale(s, s);
                at.rotate(-Math.atan2(y2 - y1, x2 - x1));
                at.translate(-((x1 + x2 + x3) / 3), -((y1 + y2 + y3) / 3));

                result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g = (Graphics2D) result.getGraphics();
                g.drawImage(image, at, null);
                g.dispose();

                pboPicture.setFitWidth(result.getWidth());
                pboPicture.setFitHeight(result.getHeight());
                pboPicture.setImage(SwingFXUtils.toFXImage(result, null));
                stage = 3;
                lblInstructions.setText("Please click [Done] to accept the image or [Reset] to start over again.");
                break;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = resources;

        lblInstructions.setText("Please click on left eye or click [Done] to accept the image as is.");
    }
}
