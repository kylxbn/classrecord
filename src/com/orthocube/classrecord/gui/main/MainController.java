/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.main;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.User;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import com.orthocube.classrecord.util.Settings;
import com.orthocube.classrecord.util.Utils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * @author OrthoCube
 */
public class MainController implements Initializable {
    @FXML
    Label lblCurrentSY;
    @FXML
    Label lblCurrentSem;
    @FXML
    Label lblCurrentPeriod;

    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    MenuItem mnuSettings;
    private MainApp mainApp;
    private ToggleGroup group = null;
    private User currentUser = new User();

    @FXML
    RadioMenuItem mnuLightTheme;
    @FXML
    RadioMenuItem mnuDarkTheme;
    @FXML
    Label lblUser;
    @FXML
    ImageView pboUser;
    @FXML
    Label lblMemory;
    @FXML
    ProgressBar prgMemory;
    @FXML
    Menu mnuFile;
    @FXML
    Menu mnuEdit;
    @FXML
    Menu mnuTools;

    @FXML
    Menu mnuHelp;
    @FXML
    MenuItem mnuSave;
    @FXML
    MenuItem mnuExit;
    @FXML
    MenuItem mnuUndo;
    @FXML
    MenuItem mnuRedo;
    @FXML
    MenuItem mnuDelete;
    @FXML
    MenuItem mnuSQL;
    @FXML
    MenuItem mnuAbout;
    @FXML
    Label lblClassRecord;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblLocation;

    @FXML
    private Button cmdBack;

    @FXML
    private Button cmdNext;

    @FXML
    private ToggleButton cmdDashboard;

    @FXML
    private ToggleButton cmdStudents;

    @FXML
    private ToggleButton cmdClasses;

    @FXML
    private ToggleButton cmdUsers;

    @FXML
    private ToggleButton cmdReminders;

    @FXML
    private ToggleButton cmdTools;

    @FXML
    private ToggleButton cmdHelp;

    @FXML
    private AnchorPane mainPane;
    // </editor-fold>

    @FXML
    void mnuExitAction(ActionEvent event) {
        mainApp.getStage().close();
    }

    @FXML
    void cmdClassesAction(ActionEvent event) {
        mainApp.showClasses();
    }

    @FXML
    void cmdDashboardAction(ActionEvent event) {
        mainApp.showDashboard();
    }

    @FXML
    void cmdStudentsAction(ActionEvent event) {
        mainApp.showStudents();
    }

    @FXML
    void cmdUsersAction(ActionEvent event) {
        mainApp.showUsers();
    }

    @FXML
    void cmdBackAction(ActionEvent event) {
        mainApp.back();
    }

    @FXML
    void cmdNextAction(ActionEvent event) {
        mainApp.next();
    }

    @FXML
    void mnuAboutAction(ActionEvent event) {
        mainApp.showAbout();
    }

    @FXML
    void mnuRemindersAction(ActionEvent event) {
        mainApp.showReminders();
    }

    @FXML
    void mnuSettingsAction(ActionEvent event) {
        mainApp.showSettingsDialog();
    }

    @FXML
    void cmdDetachAction() {
        mainApp.detach();
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setNextDisabled(boolean disabled) {
        cmdNext.setDisable(disabled);
    }

    public void setBackDisabled(boolean disabled) {
        cmdBack.setDisable(disabled);
    }

    public void setPageTitle(String title) {
        lblLocation.setText(title);
    }

    public void clearToggle() {
        group.selectToggle(null);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        group = new ToggleGroup();
        cmdDashboard.setToggleGroup(group);
        cmdStudents.setToggleGroup(group);
        cmdClasses.setToggleGroup(group);
        cmdUsers.setToggleGroup(group);
        cmdReminders.setToggleGroup(group);
        // select the first button to start with
        group.selectToggle(cmdDashboard);

        // enforce rule that one of the ToggleButtons must be selected at any
        // time (that is, it is not valid to have zero ToggleButtons selected).
        // (Fix for RT-34920 that considered this to be a bug)
        final ChangeListener<Toggle> listener =
                (ObservableValue<? extends Toggle> observable,
                 Toggle old, Toggle now) -> {
                    if (now == null) {
                        group.selectToggle(old);
                    }
                };
        group.selectedToggleProperty().addListener(listener);

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {

            Date now = new Date();
            DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, Settings.language);
            String formattedDate = df.format(now);
            DateFormat tf = DateFormat.getTimeInstance(DateFormat.DEFAULT, Settings.language);
            String formattedTime = tf.format(now);
            //System.out.println(hour + ":" + (minute) + ":" + second);
            lblDate.setText(formattedDate);
            lblTime.setText(formattedTime);

            long allocatedMem = Runtime.getRuntime().totalMemory();
            long usedMem = allocatedMem - Runtime.getRuntime().freeMemory();

            prgMemory.setProgress(((double) usedMem) / ((double) allocatedMem));
            lblMemory.setText(String.format(rb.getString("main.mb"), usedMem / 1000000, allocatedMem / 1000000));

        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        int[] sysemperiod = Utils.getSYSemPeriod();

        lblCurrentSY.setText(Integer.toString(sysemperiod[0]) + " - " + Integer.toString(sysemperiod[0] + 1));
        if (sysemperiod[1] == 1)
            lblCurrentSem.setText("1st");
        else if (sysemperiod[1] == 2)
            lblCurrentSem.setText("2nd");
        else
            lblCurrentSem.setText("Summer");

        if (sysemperiod[2] == 0)
            lblCurrentPeriod.setText("Vacation");
        else if (sysemperiod[2] == 1)
            lblCurrentPeriod.setText("Prelims");
        else if (sysemperiod[2] == 2)
            lblCurrentPeriod.setText("Semis");
        else if (sysemperiod[2] == 3)
            lblCurrentPeriod.setText("Prelims");
        else
            lblCurrentPeriod.setText("Finals");

    }

    public void setUser(User user) {
        this.currentUser = user;
        lblUser.setText(currentUser.getNickname().isEmpty() ? currentUser.getUsername() : currentUser.getNickname());
        pboUser.setImage(currentUser.getPicture() == null ? new Image(getClass().getResourceAsStream("/com/orthocube/classrecord/res/Businessman_100px.png")) : currentUser.getPicture());

        if (currentUser.getAccessLevel() < 2) {
            mnuSettings.setDisable(true);
        }
    }

    public void refreshUser() {
        try {
            this.currentUser = DB.getUser(currentUser.getID());
            lblUser.setText(currentUser.getNickname().isEmpty() ? currentUser.getUsername() : currentUser.getNickname());
            pboUser.setImage(currentUser.getPicture() == null ? new Image(getClass().getResourceAsStream("/com/orthocube/classrecord/res/Businessman_100px.png")) : currentUser.getPicture());
        } catch (SQLException | IOException e) {
            Dialogs.exception(e);
        }
    }

    public String getTitle() {
        return lblLocation.getText();
    }
}
