/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord;

import com.orthocube.classrecord.about.AboutController;
import com.orthocube.classrecord.classes.*;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Student;
import com.orthocube.classrecord.students.StudentChooserController;
import com.orthocube.classrecord.students.StudentEnrolledInController;
import com.orthocube.classrecord.students.StudentsController;
import com.orthocube.classrecord.users.UsersController;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.NotificationPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author OrthoCube
 */
public class MainApp extends Application implements MainPreloader.CredentialsConsumer {
    private final static Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    private ArrayList<SplitPane> history = new ArrayList<>();
    private ArrayList<String> historyTitles = new ArrayList<>();
    private int currentHistory = -1;

    private BorderPane root;
    private NotificationPane rootNotification;

    private SplitPane students;
    private SplitPane classes;
    private SplitPane users;
    private SplitPane about;

    private MainController mainController;
    private StudentsController studentsController;
    private ClassesController classesController;
    private UsersController usersController;
    private AboutController aboutController;

    private ObservableList<Clazz> classModel;
    private ObservableList<Student> studentsModel;

    private Locale language = Locale.ENGLISH;
    private ResourceBundle bundle = null; // ResourceBundle.getBundle("com.orthocube.classrecord.bundles.strings", language);
    private Stage stage;
    private Scene scene;

    private String username;
    private String password;

    private boolean isDark = true;

    public NotificationPane getRootNotification() {
        return rootNotification;
    }

    private void mayBeShown() {
        if (stage != null) {//username != null && stage != null) {
            Platform.runLater(() -> {
                stage.show();
                rootNotification.show("Welcome, OrthoCube!");
            });
        }
    }

    public void setCredential(String username, String password) {
        this.username = username;
        this.password = password;
        mayBeShown();
    }

    private static void changeLoggingLevel(Level l) {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(l);
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(l);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            if (args[0].equals("--debug")) {
                System.out.println("Debug mode activated.");
                changeLoggingLevel(Level.INFO);
            } else {
                changeLoggingLevel(Level.OFF);
                System.out.println("Invalid arguments. Starting application normally.");
            }
        } else if (args.length > 1) {
            changeLoggingLevel(Level.OFF);
            System.out.println("Invalid number of arguments. Starting application normally.");
        } else {
            changeLoggingLevel(Level.OFF);
        }

        System.setProperty("javafx.preloader", "com.orthocube.classrecord.MainPreloader");
        Application.launch(MainApp.class, args);
    }

    @Override
    public void init() throws Exception {
        prepareApp();
        showStudents();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        scene = new Scene(rootNotification);
        Platform.setImplicitExit(true);
        setDarkTheme();
        stage.setOnCloseRequest(e -> {
            if (Dialogs.confirm("Close Application", "Are you sure you want to leave?", "Any unsaved changes will be lost.") == ButtonType.CANCEL)
                e.consume();
        });
        this.stage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_16px.png")));
        this.stage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_30px.png")));
        this.stage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_40px.png")));
        this.stage.getIcons().add(new Image(getClass().getResourceAsStream("res/Dossier_80px.png")));

        this.stage.setScene(scene);
        this.stage.setTitle("Class Record"); //bundle.getString("classrecord"));

        mayBeShown();
    }

    public void setDarkTheme() {
        scene.getStylesheets().add(getClass().getResource("res/modena_dark.css").toExternalForm());
        isDark = true;
    }

    public void setLightTheme() {
        scene.getStylesheets().clear();
        isDark = false;
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
        FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
        rootLoader.setResources(bundle);
        root = rootLoader.load();
        rootNotification = new NotificationPane(root);
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
        rootNotification.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
        mainController = rootLoader.getController();
        mainController.setMainApp(this);
        notifyPreloader(new MainPreloader.ProgressNotification(0.2));


        // ------------ LOAD STUDENTS ----------------
        LOGGER.log(Level.INFO, "Loading Students.fxml...");
        FXMLLoader studentLoader = new FXMLLoader(getClass().getResource("students/Students.fxml"));
        studentLoader.setResources(bundle);
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
        FXMLLoader classesLoader = new FXMLLoader(getClass().getResource("classes/Classes.fxml"));
        classesLoader.setResources(bundle);
        classes = classesLoader.load();
        classesController = classesLoader.getController();
        classesController.setMainApp(this);
        notifyPreloader(new MainPreloader.ProgressNotification(0.5));

        LOGGER.log(Level.INFO, "Loading classes...");
        classModel = DB.getClasses();
        classesController.setModel(classModel);
        notifyPreloader(new MainPreloader.ProgressNotification(0.6));


        // --------------- LOAD USERS -----------------
        LOGGER.log(Level.INFO, "Loading Users.fxml...");
        FXMLLoader usersLoader = new FXMLLoader(getClass().getResource("users/Users.fxml"));
        usersLoader.setResources(bundle);
        users = usersLoader.load();
        usersController = usersLoader.getController();
        usersController.setMainApp(this);
        notifyPreloader(new MainPreloader.ProgressNotification(0.8));


        // ----------------- LOAD ABOUT -----------------
        LOGGER.log(Level.INFO, "Loading About.fxml...");
        FXMLLoader aboutLoader = new FXMLLoader(getClass().getResource("about/About.fxml"));
        aboutLoader.setResources(bundle);
        about = aboutLoader.load();
        aboutController = aboutLoader.getController();
        aboutController.setMainApp(this);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("classes/Enrollees.fxml"));
        loader.setResources(bundle);
        SplitPane enrollees = loader.load();
        EnrolleesController controller = loader.getController();
        controller.setMainApp(this);
        controller.showClass(c);

        trimHistoryThenAdd(enrollees, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public Student showStudentChooserDialog(Clazz c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("students/StudentChooser.fxml"));
            SplitPane studentChooser = loader.load();
            StudentChooserController studentChooserController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Choose student");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            Scene scene = new Scene(studentChooser);
            if (isDark) scene.getStylesheets().add(getClass().getResource("res/modena_dark.css").toExternalForm());
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("students/StudentEnrolledIn.fxml"));
        loader.setResources(bundle);
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

    public void showClasses() {
        history.clear();
        historyTitles.clear();
        history.add(classes);
        historyTitles.add(classesController.getTitle());
        currentHistory = 0;
        updateNavigation();
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

    public void showCriterias(Clazz currentClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("classes/Criterias.fxml"));
        loader.setResources(bundle);
        SplitPane enrollees = loader.load();
        CriteriasController controller = loader.getController();
        controller.setMainApp(this);
        controller.setClass(currentClass);

        trimHistoryThenAdd(enrollees, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public void showAttendance(Clazz currentClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("classes/Attendance.fxml"));
        loader.setResources(bundle);
        SplitPane attendance = loader.load();
        AttendanceController controller = loader.getController();
        controller.setMainApp(this);
        controller.setClass(currentClass);

        trimHistoryThenAdd(attendance, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }

    public void showAttendanceStatistics(Clazz currentClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("classes/AttendanceStatistics.fxml"));
        loader.setResources(bundle);
        SplitPane attendance = loader.load();
        AttendanceStatisticsController controller = loader.getController();
        controller.setClass(currentClass);

        trimHistoryThenAdd(attendance, controller.getTitle());
        currentHistory++;
        updateNavigation();
    }
}
