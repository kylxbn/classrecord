/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord;

import com.orthocube.classrecord.data.User;
import com.orthocube.classrecord.gui.preloader.MainPreloaderController;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import com.orthocube.classrecord.util.Settings;
import com.orthocube.classrecord.util.license.verification.LicenseKeyResult;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

public class MainPreloader extends Preloader {
    private CredentialsConsumer consumer = null;

    private Stage preloaderStage;
    private Scene scene;
    private String username = null;
    private String password = null;

    private Group topGroup;
    private StackPane preloaderParent;
    private StateChangeNotification evt;

    private MainPreloaderController loaderController;

    @Override
    public void start(Stage stage) throws Exception {
        this.preloaderStage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/preloader/Preloader.fxml"), Settings.bundle);
        StackPane root = loader.load();
        loaderController = loader.getController();
        loaderController.setMainPreloader(this);
        preloaderParent = root;
        topGroup = new Group(preloaderParent);

        this.preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_16px.png")));
        this.preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_30px.png")));
        this.preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_40px.png")));
        this.preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_80px.png")));

        preloaderStage.setTitle(Settings.bundle.getString("preloader.title"));
        preloaderStage.setWidth(1024);
        preloaderStage.setHeight(768);
        preloaderStage.setMinWidth(1024);
        preloaderStage.setMinHeight(768);
        scene = new Scene(topGroup);
        if (Settings.isDark) {
            setDarkTheme();
        } else {
            setLightTheme();
        }
        preloaderStage.setScene(scene);

        preloaderParent.prefHeightProperty().bind(preloaderStage.getScene().heightProperty());
        preloaderParent.prefWidthProperty().bind(preloaderStage.getScene().widthProperty());

        loaderController.startOpeningAnimation();
        preloaderStage.show();
        preloaderStage.toFront();
    }

    private void setDarkTheme() {
        scene.getStylesheets().add(getClass().getResource("res/modena_dark.css").toExternalForm());
        loaderController.setDark();
        //preloaderParent.setStyle("-fx-background-color: #323232;"); // 0x323232");
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        switch (month) {
            case Calendar.MARCH:
            case Calendar.APRIL:
            case Calendar.MAY:
                preloaderParent.getStylesheets().add(getClass().getResource("res/spring_dark.css").toExternalForm());
                break;
            case Calendar.JUNE:
            case Calendar.JULY:
            case Calendar.AUGUST:
                preloaderParent.getStylesheets().add(getClass().getResource("res/summer_dark.css").toExternalForm());
                break;
            case Calendar.SEPTEMBER:
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
                preloaderParent.getStylesheets().add(getClass().getResource("res/fall_dark.css").toExternalForm());
                break;
            case Calendar.DECEMBER:
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
                preloaderParent.getStylesheets().add(getClass().getResource("res/winter_dark.css").toExternalForm());
                break;
        }
        loaderController.setDark();
    }

    private void setLightTheme() {
        scene.getStylesheets().clear();
        loaderController.setLight();
        //preloaderParent.setStyle("-fx-background-color: #323232;"); // 0x323232");
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        switch (month) {
            case Calendar.MARCH:
            case Calendar.APRIL:
            case Calendar.MAY:
                preloaderParent.getStylesheets().add(getClass().getResource("res/spring_light.css").toExternalForm());
                break;
            case Calendar.JUNE:
            case Calendar.JULY:
            case Calendar.AUGUST:
                preloaderParent.getStylesheets().add(getClass().getResource("res/summer_light.css").toExternalForm());
                break;
            case Calendar.SEPTEMBER:
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
                preloaderParent.getStylesheets().add(getClass().getResource("res/fall_light.css").toExternalForm());
                break;
            case Calendar.DECEMBER:
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
                preloaderParent.getStylesheets().add(getClass().getResource("res/winter_light.css").toExternalForm());
                break;
        }
        loaderController.setLight();

    }


    public void loginAction(String username, String password) {
        this.username = username;
        this.password = password;

        loaderController.disableLogin(true);
        mayBeHidden();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        loaderController.setProgress(((ProgressNotification) pn).getProgress());
    }

    private void mayBeHidden() {
        if (preloaderStage.isShowing() && username != null && password != null && consumer != null) {
            try {
                if (DB.userExists(username, password)) {
                    User user = DB.getUser(username, password);
                    consumer.setInitData(user, preloaderStage, scene);
                    //Platform.runLater(() -> preloaderStage.hide());
                    SharedScene appScene = (SharedScene) evt.getApplication();
                    fadeInTo(appScene.getParentNode());
                } else {
                    Dialogs.error(Settings.bundle.getString("preloader.loginerror.title"), Settings.bundle.getString("preloader.loginerror.header"), Settings.bundle.getString("preloader.loginerror.content"));
                    loaderController.disableLogin(false);
                }
            } catch (SQLException | IOException e) {
                Dialogs.exception(e);
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
                Duration.millis(500),
                preloaderParent);
        ft1.setFromValue(1.0);
        ft1.setToValue(1.0);
        ft1.setOnFinished(t -> loaderController.startClosingAnimation());

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
            try {
                evt = info;
                loaderController.hideProgressBar();
                consumer = (CredentialsConsumer) info.getApplication();
                if (DB.isFirstRun()) {
                    loaderController.hideFirstStart();

                    String license;
                    label:
                    do {
                        license = Dialogs.textInput("Program License", "To use this program, please provide a valid license key.", "License Key:");
                        LicenseKeyResult result = LicenseKeyResult.INVALID;
                        if ((license != null) && (license.trim().length() > 0)) {
                            result = DB.isValidLicense(license);
                        } else {
                            Dialogs.error("Program License", "Program will now exit.", "To try again, please restart the program.");
                            Platform.exit();
                            return;
                        }

                        switch (result) {
                            case INVALID:
                                Dialogs.error("Program License", "Invalid license", "To use this program, please provide a valid license key.");
                                break;
                            case BLACKLISTED:
                                Dialogs.error("Program License", "Blacklisted license", "To use this program, please provide a valid license key.");
                                break;
                            case FAKE:
                                Dialogs.error("Program License", "Fake license", "To use this program, please provide a valid license key.");
                                break;
                            case EXPIRED:
                                Dialogs.error("Program License", "Expired license", "To use this program, please provide a valid license key.");
                                break;
                            case TOOEARLY:
                                Dialogs.error("Program License", "License not yet valid", "To use this program, please provide a valid license key.");
                                break;
                            case NEEDSREPAIR:
                                Dialogs.error("Program License", "Invalidated license", "To use this program, please provide a valid license key.");
                                break;
                            case GOOD:
                                DB.setLicense(license);
                                Dialogs.info("Program License", "Thank you!", "Your license key is valid.");
                                Dialogs.info(Settings.bundle.getString("preloader.appinit.title"), Settings.bundle.getString("preloader.appinit.header"), Settings.bundle.getString("preloader.appinit.content"));
                                username = "admin";
                                password = "admin";
                                mayBeHidden();
                                break label;
                        }
                    } while (license.trim().length() > 0);
                } else {
                    LicenseKeyResult result = DB.hasValidLicense();

                    if (result != LicenseKeyResult.GOOD) {
                        switch (result) {
                            case INVALID:
                                Dialogs.error("Program License", "Invalid license", "To use this program, please provide a valid license key.");
                                break;
                            case BLACKLISTED:
                                Dialogs.error("Program License", "Blacklisted license", "To use this program, please provide a valid license key.");
                                break;
                            case FAKE:
                                Dialogs.error("Program License", "Fake license", "To use this program, please provide a valid license key.");
                                break;
                            case EXPIRED:
                                Dialogs.error("Program License", "Expired license", "To use this program, please provide a valid license key.");
                                break;
                            case TOOEARLY:
                                Dialogs.error("Program License", "License not yet valid", "To use this program, please provide a valid license key.");
                                break;
                            case NEEDSREPAIR:
                                Dialogs.error("Program License", "Invalidated license", "To use this program, please provide a valid license key.");
                                break;
                        }

                        String license;
                        label1:
                        do {
                            license = Dialogs.textInput("Program License", "To use this program, please provide a valid license key.", "License Key:");
                            LicenseKeyResult result2 = LicenseKeyResult.INVALID;
                            if ((license != null) && (license.trim().length() > 0)) {
                                result2 = DB.isValidLicense(license);
                            } else {
                                Dialogs.error("Program License", "Program will now exit.", "To try again, please restart the program.");
                                Platform.exit();
                                return;
                            }

                            switch (result2) {
                                case INVALID:
                                    Dialogs.error("Program License", "Invalid license", "To use this program, please provide a valid license key.");
                                    break;
                                case BLACKLISTED:
                                    Dialogs.error("Program License", "Blacklisted license", "To use this program, please provide a valid license key.");
                                    break;
                                case FAKE:
                                    Dialogs.error("Program License", "Fake license", "To use this program, please provide a valid license key.");
                                    break;
                                case EXPIRED:
                                    Dialogs.error("Program License", "Expired license", "To use this program, please provide a valid license key.");
                                    break;
                                case TOOEARLY:
                                    Dialogs.error("Program License", "License not yet valid", "To use this program, please provide a valid license key.");
                                    break;
                                case NEEDSREPAIR:
                                    Dialogs.error("Program License", "Invalidated license", "To use this program, please provide a valid license key.");
                                    break;
                                case GOOD:
                                    DB.setLicense(license);
                                    Dialogs.info("Program License", "Thank you!", "Your license key is valid.");
                                    mayBeHidden();
                                    break label1;
                            }
                        } while (license.trim().length() > 0);
                    } else {
                        mayBeHidden();
                    }
                }
            } catch (SQLException e) {
                Dialogs.exception(e);
            }
        }
    }

    public interface CredentialsConsumer {
        void setInitData(User user, Stage stage, Scene scene);
    }

    public interface SharedScene {
        NotificationPane getParentNode();
    }
}