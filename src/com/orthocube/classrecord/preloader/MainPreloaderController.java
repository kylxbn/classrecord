/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.preloader;

import com.orthocube.classrecord.MainPreloader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPreloaderController implements Initializable {
    MainPreloader mainPreloader;

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
        vbxLoading.setVisible(false);
        cmdLogin.setDisable(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ValidationSupport support = new ValidationSupport();

        Validator<String> validator = new Validator<String>() {
            @Override
            public ValidationResult apply(Control control, String value) {
                boolean condition =
                        value != null
                                ? !value
                                .matches(
                                        "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*")
                                : value == null;

                System.out.println(condition);

                return ValidationResult.fromMessageIf(control, "not a number", Severity.ERROR, condition);
            }
        };

        support.registerValidator(txtUsername, Validator.createEmptyValidator("Username is required"));
        support.registerValidator(txtPassword, Validator.createEmptyValidator("Password is required"));
    }
}
