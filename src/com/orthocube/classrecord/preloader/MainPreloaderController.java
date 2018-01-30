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
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class MainPreloaderController {
    MainPreloader mainPreloader;

    @FXML
    private VBox vbxLoading;

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


    public void setMainPreloader(MainPreloader mainPreloader) {
        this.mainPreloader = mainPreloader;
    }

    public void setProgress(double progress) {
        prgProgress.setProgress(progress);
    }

    public void hideProgressBar() {
        vbxLoading.setVisible(false);
        cmdLogin.setDisable(false);
    }
}
