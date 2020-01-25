/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord;

import com.orthocube.classrecord.data.*;
import com.orthocube.classrecord.gui.about.AboutController;
import com.orthocube.classrecord.gui.classes.*;
import com.orthocube.classrecord.gui.dashboard.DashboardController;
import com.orthocube.classrecord.gui.main.MainController;
import com.orthocube.classrecord.gui.reminders.RemindersController;
import com.orthocube.classrecord.gui.settings.SettingsController;
import com.orthocube.classrecord.gui.students.ImageRefinerController;
import com.orthocube.classrecord.gui.students.StudentChooserController;
import com.orthocube.classrecord.gui.students.StudentEnrolledInController;
import com.orthocube.classrecord.gui.students.StudentsController;
import com.orthocube.classrecord.gui.users.UsersController;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import com.orthocube.classrecord.util.Settings;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.coobird.thumbnailator.Thumbnails;
import org.controlsfx.control.NotificationPane;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author OrthoCube
 */
public class MainApp extends Application implements MainPreloader.CredentialsConsumer, MainPreloader.SharedScene {
    private final static Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    private final ArrayList<SplitPane> history = new ArrayList<>();
    private final ArrayList<String> historyTitles = new ArrayList<>();
    private int currentHistory = -1;

    private BorderPane root;
    private NotificationPane rootNotification;

    private SplitPane dashboard;
    private SplitPane students;
    private SplitPane classes;
    private SplitPane users;
    private SplitPane about;
    private SplitPane reminders;

    private MainController mainController;
    private DashboardController dashboardController;
    private StudentsController studentsController;
    private ClassesController classesController;
    private UsersController usersController;
    private AboutController aboutController;
    private RemindersController remindersController;

    private ObservableList<Student> studentsModel;

    private Stage stage;
    private Scene scene;

    private User currentUser;

    public NotificationPane getRootNotification() {
        return rootNotification;
    }

    public NotificationPane getParentNode() {
        return rootNotification;
    }

    private void mayBeShown() {
        if (stage != null && currentUser != null) {
            mainController.setUser(currentUser);
            studentsController.setUser(currentUser);
            classesController.setUser(currentUser);
            usersController.setUser(currentUser);
            remindersController.setUser(currentUser);
            Platform.runLater(() -> {
                //stage.show();
                rootNotification.show("Welcome, " + (currentUser.getNickname().isEmpty() ? currentUser.getUsername() : currentUser.getNickname()) + "!");
            });
        }
    }

    public void setInitData(User user, Stage stage, Scene scene) {
        this.currentUser = user;
        this.scene = scene;
        this.stage = stage;
        if (Settings.isDark)
            setDarkTheme();
        else
            setLightTheme();
        mayBeShown();

        //setDarkTheme();
        Platform.setImplicitExit(true);
        // TODO: fix blank notification/confirmation window on Linux
//        stage.setOnCloseRequest(e -> {
//            if (Dialogs.confirm("Close Application", "Are you sure you want to leave?", "Any unsaved changes will be lost.") == ButtonType.CANCEL) {
//                e.consume();
//            }
//        });

        this.stage.setTitle(Settings.bundle.getString("main.classrecord"));

    }

    @Override
    public void init() throws Exception {
        prepareApp();
        showDashboard();
    }

    @Override
    public void start(Stage stage) {

        mayBeShown();
    }

    public void setDarkTheme() {
        scene.getStylesheets().add(getClass().getResource("res/modena_dark.css").toExternalForm());
        rootNotification.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
        remindersController.setDark();
        dashboardController.setDark();
    }

    public void setLightTheme() {
        scene.getStylesheets().clear();
        rootNotification.getStyleClass().remove(rootNotification.getStyleClass().size() - 1);
        remindersController.setLight();
        dashboardController.setLight();
    }

    @Override
    public void stop() {
        DB.close();
        LOGGER.log(Level.INFO, "Java FX closing...");
    }

    private void prepareApp() throws Exception {
        LOGGER.log(Level.INFO, "Loading FXMLs...");


        // ----------- LOAD MAIN ----------------
        LOGGER.log(Level.INFO, "Loading Main.fxml...");
        FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("gui/main/Main.fxml"));
        rootLoader.setResources(Settings.bundle);
        root = rootLoader.load();
        rootNotification = new NotificationPane(root);
        rootNotification.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        //rootNotification.setCloseButtonVisible(false);
        rootNotification.setOnShown(e -> {
            LOGGER.log(Level.INFO, "Notification show listener triggered");
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                LOGGER.log(Level.INFO, "Hiding notification");
                rootNotification.hide();
            });
            delay.play();
        });
        //rootNotification.setShowFromTop(false);
        mainController = rootLoader.getController();
        mainController.setMainApp(this);
        notifyPreloader(new MainPreloader.ProgressNotification(0.2));

        // ------------- LOAD DASHBOARD --------------
        LOGGER.log(Level.INFO, "Loading Dashboards.fxml...");
        FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("gui/dashboard/Dashboard.fxml"));
        dashboardLoader.setResources(Settings.bundle);
        dashboard = dashboardLoader.load();
        dashboardController = dashboardLoader.getController();
        dashboardController.setMainApp(this);

        // ------------ LOAD STUDENTS ----------------
        LOGGER.log(Level.INFO, "Loading Students.fxml...");
        FXMLLoader studentLoader = new FXMLLoader(getClass().getResource("gui/students/Students.fxml"));
        studentLoader.setResources(Settings.bundle);
        students = studentLoader.load();
        studentsController = studentLoader.getController();
        studentsController.setMainApp(this);
        notifyPreloader(new MainPreloader.ProgressNotification(0.3));

        LOGGER.log(Level.INFO, "Loading students...");
        studentsModel = DB.getStudents();
        studentsController.setModel(studentsModel);
        notifyPreloader(new MainPreloader.ProgressNotification(0.4));


        // ------------- LOAD CLASSES -----------------
        LOGGER.log(Level.INFO, "Loading Classes.fxml...");
        FXMLLoader classesLoader = new FXMLLoader(getClass().getResource("gui/classes/Classes.fxml"));
        classesLoader.setResources(Settings.bundle);
        classes = classesLoader.load();
        classesController = classesLoader.getController();
        classesController.setMainApp(this);
        notifyPreloader(new MainPreloader.ProgressNotification(0.5));

        LOGGER.log(Level.INFO, "Loading classes...");
        ObservableList<Clazz> classModel = DB.getClasses();
        classesController.setModel(classModel);
        notifyPreloader(new MainPreloader.ProgressNotification(0.6));


        // --------------- LOAD USERS -----------------
        LOGGER.log(Level.INFO, "Loading Users.fxml...");
        FXMLLoader usersLoader = new FXMLLoader(getClass().getResource("gui/users/Users.fxml"));
        usersLoader.setResources(Settings.bundle);
        users = usersLoader.load();
        usersController = usersLoader.getController();
        usersController.setMainApp(this);
        notifyPreloader(new MainPreloader.ProgressNotification(0.7));

        LOGGER.log(Level.INFO, "Loading users...");
        ObservableList<User> usersModel = DB.getUsers();
        usersController.setModel(usersModel);
        notifyPreloader(new MainPreloader.ProgressNotification(0.8));

        // --------------- LOAD REMINDERS -----------------
        LOGGER.log(Level.INFO, "Loading Reminders.fxml...");
        FXMLLoader remindersLoader = new FXMLLoader(getClass().getResource("gui/reminders/Reminders.fxml"));
        remindersLoader.setResources(Settings.bundle);
        reminders = remindersLoader.load();
        remindersController = remindersLoader.getController();
        remindersController.setMainApp(this);
        notifyPreloader(new MainPreloader.ProgressNotification(0.9));

        LOGGER.log(Level.INFO, "Loading users...");
        ObservableList<Reminder> remindersModel = DB.getReminders();
        remindersController.setModel(remindersModel);
        notifyPreloader(new MainPreloader.ProgressNotification(1.0));

        // ----------------- LOAD ABOUT -----------------
        LOGGER.log(Level.INFO, "Loading About.fxml...");
        FXMLLoader aboutLoader = new FXMLLoader(getClass().getResource("gui/about/About.fxml"));
        aboutLoader.setResources(Settings.bundle);
        about = aboutLoader.load();
        aboutController = aboutLoader.getController();
        notifyPreloader(new MainPreloader.ProgressNotification(1.0));
    }

    private void updateNavigation() {
        mainController.setBackDisabled(currentHistory < 1);
        mainController.setNextDisabled(currentHistory == (history.size() - 1));

        root.setCenter(history.get(currentHistory));
        mainController.setPageTitle(historyTitles.get(currentHistory));
    }

    public void back() {
        currentHistory--;
        updateNavigation();
    }

    public void next() {
        currentHistory++;
        updateNavigation();
    }

    public void showStudents() {
        history.clear();
        historyTitles.clear();
        history.add(students);
        historyTitles.add(studentsController.getTitle());
        currentHistory = 0;
        updateNavigation();
    }

    public void showEnrollees(Clazz c) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/classes/Enrollees.fxml"));
        loader.setResources(Settings.bundle);
        SplitPane enrollees = loader.load();
        EnrolleesController controller = loader.getController();
        controller.setMainApp(this);
        controller.setUser(currentUser);
        controller.showClass(c);

        trimHistoryThenAdd(enrollees, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public Student showStudentChooserDialog(Clazz c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/students/StudentChooser.fxml"));
            SplitPane studentChooser = loader.load();
            StudentChooserController studentChooserController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Choose student");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            Scene scene = new Scene(studentChooser);
            if (Settings.isDark)
                scene.getStylesheets().add(getClass().getResource("res/modena_dark.css").toExternalForm());
            dialogStage.setScene(scene);

            studentChooserController.setDialogStage(dialogStage);
            studentChooserController.setClass(c);
            studentChooserController.setModel(studentsModel);

            dialogStage.showAndWait();
            return studentChooserController.getResult();
        } catch (IOException e) {
            Dialogs.exception(e);
        }
        return null;
    }

    public void showEnrolledClasses(Student s) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/students/StudentEnrolledIn.fxml"));
        loader.setResources(Settings.bundle);
        SplitPane enrolledIn = loader.load();
        StudentEnrolledInController controller = loader.getController();
        controller.setMainApp(this);
        controller.showEnrolled(s);

        trimHistoryThenAdd(enrolledIn, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }

    private void trimHistoryThenAdd(SplitPane p, String title) {
        while ((history.size() - 1) > currentHistory) {
            history.remove(history.size() - 1);
            historyTitles.remove(historyTitles.size() - 1);
        }
        history.add(p);
        historyTitles.add(title);
    }

    public void showAbout() {
        history.clear();
        historyTitles.clear();
        history.add(about);
        historyTitles.add(aboutController.getTitle());
        currentHistory = 0;
        mainController.clearToggle();
        updateNavigation();
    }

    public void showReminders() {
        history.clear();
        historyTitles.clear();
        history.add(reminders);
        historyTitles.add(remindersController.getTitle());
        currentHistory = 0;
        mainController.clearToggle();
        remindersController.refreshSchedule();
        updateNavigation();
    }

    public void showClasses() {
        history.clear();
        historyTitles.clear();
        history.add(classes);
        historyTitles.add(classesController.getTitle());
        currentHistory = 0;
        updateNavigation();
    }

    public void showDashboard() {
        history.clear();
        historyTitles.clear();
        history.add(dashboard);
        historyTitles.add(dashboardController.getTitle());
        currentHistory = 0;
        updateNavigation();
        dashboardController.clear();
        dashboardController.refresh();
    }

    public void showClass(Clazz c) {
        classesController.showClass(c);

        trimHistoryThenAdd(classes, classesController.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public void showUsers() {
        history.clear();
        historyTitles.clear();
        history.add(users);
        historyTitles.add(usersController.getTitle());
        currentHistory = 0;
        updateNavigation();
    }

    public void showStudent(Student student) {
        studentsController.showStudent(student);

        trimHistoryThenAdd(students, studentsController.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public void showCriteria(Clazz currentClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/classes/Criteria.fxml"));
        loader.setResources(Settings.bundle);
        SplitPane enrollees = loader.load();
        CriteriaController controller = loader.getController();
        controller.setMainApp(this);
        controller.setUser(currentUser);
        controller.setClass(currentClass);

        trimHistoryThenAdd(enrollees, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public void showAttendance(Clazz currentClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/classes/Attendance.fxml"));
        loader.setResources(Settings.bundle);
        SplitPane attendance = loader.load();
        AttendanceController controller = loader.getController();
        controller.setMainApp(this);
        controller.setUser(currentUser);
        controller.setClass(currentClass);

        trimHistoryThenAdd(attendance, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public void addAttendance(Clazz currentClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/classes/Attendance.fxml"));
        loader.setResources(Settings.bundle);
        SplitPane attendance = loader.load();
        AttendanceController controller = loader.getController();
        controller.setMainApp(this);
        controller.setUser(currentUser);
        controller.setClass(currentClass);
        controller.quickAdd();

        trimHistoryThenAdd(attendance, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public void showAttendanceStatistics(Clazz currentClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/classes/AttendanceStatistics.fxml"));
        loader.setResources(Settings.bundle);
        SplitPane attendance = loader.load();
        AttendanceStatisticsController controller = loader.getController();
        controller.setClass(currentClass);

        trimHistoryThenAdd(attendance, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public void showTasks(Clazz currentClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/classes/Tasks.fxml"));
        loader.setResources(Settings.bundle);
        SplitPane tasks = loader.load();
        TasksController controller = loader.getController();
        controller.setMainApp(this);
        controller.setUser(currentUser);
        controller.setClass(currentClass);

        trimHistoryThenAdd(tasks, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public Enrollee showEnrolleeChooserDialog(Clazz c, Task t) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/classes/EnrolleeChooser.fxml"));
            SplitPane enrolleeChooser = loader.load();
            EnrolleeChooserController enrolleeChooserController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Choose enrollee");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            Scene scene = new Scene(enrolleeChooser);
            if (Settings.isDark)
                scene.getStylesheets().add(getClass().getResource("res/modena_dark.css").toExternalForm());
            dialogStage.setScene(scene);

            enrolleeChooserController.setDialogStage(dialogStage);
            enrolleeChooserController.setClass(c, t);

            dialogStage.showAndWait();
            return enrolleeChooserController.getResult();
        } catch (IOException e) {
            Dialogs.exception(e);
        }
        return null;
    }

    public void showSettingsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/settings/Settings.fxml"));
            SplitPane enrolleeChooser = loader.load();
            SettingsController settingsController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Settings");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            Scene scene = new Scene(enrolleeChooser);
            if (Settings.isDark)
                scene.getStylesheets().add(getClass().getResource("res/modena_dark.css").toExternalForm());
            dialogStage.setScene(scene);

            settingsController.setDialogStage(dialogStage);
            settingsController.setMainApp(this);

            dialogStage.showAndWait();
        } catch (IOException e) {
            Dialogs.exception(e);
        }
    }

    public BufferedImage chooseImage(int size) {
        FileChooser fc = new FileChooser();
        fc.setTitle(Settings.bundle.getString("main.imagechooser"));
        File f = fc.showOpenDialog(stage);
        if (f == null) return null;

        try {
            BufferedImage bi = ImageIO.read(f);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/students/ImageRefiner.fxml"));
            SplitPane imageRefiner = loader.load();
            ImageRefinerController irController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(Settings.bundle.getString("main.imagerefiner"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            Scene scene = new Scene(imageRefiner);
            if (Settings.isDark)
                scene.getStylesheets().add(getClass().getResource("res/modena_dark.css").toExternalForm());
            dialogStage.setScene(scene);

            irController.setDialogStage(dialogStage);
            irController.setImage(bi);

            dialogStage.showAndWait();
            BufferedImage chosenImage = irController.getResult();

            BufferedImage resizedImage = null;
            try {
                if (chosenImage.getHeight() > chosenImage.getWidth()) {
                    resizedImage = Thumbnails.of(chosenImage).width(size).asBufferedImage();
                } else {
                    resizedImage = Thumbnails.of(chosenImage).height(size).asBufferedImage();
                }
            } catch (IOException ex) {
                Dialogs.exception(ex);
            }

            BufferedImage croppedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics g = croppedImage.createGraphics();

            assert resizedImage != null;
            int width = resizedImage.getWidth();
            int height = resizedImage.getHeight();
            int cropW = (width - height) / 2;
            cropW = (cropW < 0) ? 0 : cropW;
            int cropH = (height - width) / 2;
            cropH = (cropH < 0) ? 0 : cropH;

            g.drawImage(resizedImage, 0, 0, size, size, cropW, cropH, width - cropW, height - cropH, null);
            g.dispose();

            return croppedImage;
        } catch (IOException e) {
            Dialogs.exception(e);
        }
        return null;
    }

    public void showGrades(Clazz currentClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/classes/Grades.fxml"));
        loader.setResources(Settings.bundle);
        SplitPane grades = loader.load();
        GradesController controller = loader.getController();
        controller.setClass(currentClass);

        trimHistoryThenAdd(grades, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public void summarizeAttendance(Clazz currentClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/classes/AttendanceSummary.fxml"));
        loader.setResources(Settings.bundle);
        SplitPane grades = loader.load();
        AttendanceSummaryController controller = loader.getController();
        controller.setClass(currentClass);

        trimHistoryThenAdd(grades, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public void refreshUser() {
        mainController.refreshUser();
    }

    public void detach() {
        Stage stage = new Stage();
        stage.setTitle("Class Record - " + mainController.getTitle());
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("res/Dossier_16px.png")));
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("res/Dossier_30px.png")));
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("res/Dossier_40px.png")));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_80px.png")));

        SplitPane currentCenter = (SplitPane) root.getCenter();
        if ((currentCenter == students) || (currentCenter == classes) ||
                (currentCenter == users) || (currentCenter == about) || (currentCenter == reminders)) {


            currentCenter.getItems().setAll();
        }
        stage.setScene(new Scene(history.get(currentHistory), 800, 600));
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }
}
