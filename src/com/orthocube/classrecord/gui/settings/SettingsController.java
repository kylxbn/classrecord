/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.settings;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.util.Dialogs;
import com.orthocube.classrecord.util.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    private RadioButton rdoDark;

    @FXML
    private RadioButton rdoLight;

    @FXML
    private RadioButton rdoEnglish;

    @FXML
    private RadioButton rdoJapanese;
    private Stage dialogStage;
    private MainApp mainApp;

    @FXML
    void cmdApplyAction(ActionEvent event) {
        if (rdoDark.isSelected()) {
            Settings.isDark = true;
            mainApp.setDarkTheme();
        } else {
            Settings.isDark = false;
            mainApp.setLightTheme();
        }

        if (Settings.languageCode == 0) {
            if (rdoJapanese.isSelected()) {
                Settings.language = Locale.JAPANESE;
                Dialogs.info("Restart Needed", "Restart app to apply changes.", "Language changes will not be visible until program restart.");
            }
        } else if (Settings.languageCode == 1) {
            if (rdoEnglish.isSelected()) {
                Settings.language = Locale.ENGLISH;
                Dialogs.info("Restart Needed", "Restart app to apply changes.", "Language changes will not be visible until program restart.");
            }
        }

        Settings.save();
        dialogStage.close();
    }

    @FXML
    void cmdCancelAction(ActionEvent event) {
        dialogStage.close();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Settings.isDark) {
            rdoDark.setSelected(true);
        } else {
            rdoLight.setSelected(true);
        }

        if (Settings.languageCode == 0) {
            rdoEnglish.setSelected(true);
        } else {
            rdoJapanese.setSelected(true);
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
