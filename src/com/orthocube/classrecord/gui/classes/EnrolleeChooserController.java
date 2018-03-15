/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.classes;

import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Enrollee;
import com.orthocube.classrecord.data.Task;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnrolleeChooserController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(EnrolleeChooserController.class.getName());
    private Enrollee currentEnrollee = null;
    private Enrollee result = null;

    @FXML
    private ChoiceBox<String> cboCourse;

    @FXML
    private TableView<Enrollee> tblEnrollees;

    @FXML
    private TableColumn<Enrollee, String> colName;

    @FXML
    private TableColumn<Enrollee, String> colCourse;

    @FXML
    private TableColumn<Enrollee, String> colNotes;

    @FXML
    private MenuItem mnuViewStudent;

    @FXML
    private MenuItem mnuSearchOnFacebook;

    @FXML
    private MenuItem mnuRemove;

    @FXML
    private Label lblNum;

    @FXML
    private Button cmdChoose;
    private Stage dialogStage;
    private Task currentTask;


    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public Enrollee getResult() {
        return result;
    }

    public void setClass(Clazz c, Task t) {
        currentTask = t;
        try {
            ObservableList<Enrollee> enrollees = DB.getEnrollees(c);

            Set<String> courses = new LinkedHashSet<>();
            courses.add("All");
            for (Enrollee e : enrollees) {
                courses.add(e.getCourse().trim().isEmpty() ? "Default" : e.getCourse());
            }
            cboCourse.setItems(FXCollections.observableArrayList(courses));
            cboCourse.getSelectionModel().select(0);

            FilteredList<Enrollee> filteredEnrollees = new FilteredList<>(enrollees, p -> true);
            cboCourse.valueProperty().addListener((obs, oldv, newv) -> filteredEnrollees.setPredicate(enrollee -> {
                if (newv != null) {
                    switch (newv) {
                        case "All":
                            return true;
                        case "Default":
                            return enrollee.getCourse().trim().isEmpty();
                        default:
                            return enrollee.getCourse().equals(newv);
                    }
                } else
                    return false;
            }));

            SortedList<Enrollee> sortedEnrollees = new SortedList<>(filteredEnrollees);
            sortedEnrollees.comparatorProperty().bind(tblEnrollees.comparatorProperty());

            tblEnrollees.setRowFactory(tv -> {
                TableRow<Enrollee> row = new TableRow<>();
                row.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2 && (!row.isEmpty())) {
                        currentEnrollee = row.getItem();
                        cmdChooseAction(null);
                    }
                });
                return row;
            });

            tblEnrollees.setItems(sortedEnrollees);
        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }


    @FXML
    void cmdChooseAction(ActionEvent event) {
        try {
            if (!DB.enrolleeHasScoreIn(currentEnrollee, currentTask)) {
                result = currentEnrollee;
                dialogStage.close();
            } else {
                Dialogs.error("Already has score", "Enrollee already has score for this task.", "Please choose another enrollee.");
            }
        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void mnuSearchOnFacebookAction(ActionEvent event) {
        if (currentEnrollee == null) return;
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI("https://www.facebook.com/search/top/?q=" + (currentEnrollee.getStudent().getFN() + " " + currentEnrollee.getStudent().getLN()).replace(" ", "%20")));
            } else {
                Dialogs.error("Error", "Desktop Not Supported", "This computer is not able to open a web browser.");
            }
        } catch (IOException | URISyntaxException e) {
            Dialogs.exception(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.log(Level.INFO, "Initializing...");

        colName.setCellValueFactory(
                cellData -> Bindings.concat(cellData.getValue().getStudent().lnProperty(), ", "/*bundle.getString("nameseparator")*/, cellData.getValue().getStudent().fnProperty())
        );
        colCourse.setCellValueFactory(
                cellData -> cellData.getValue().courseProperty()
        );
        colNotes.setCellValueFactory(
                cellData -> cellData.getValue().notesProperty()
        );

        tblEnrollees.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> {
                    currentEnrollee = newV;
                    cmdChoose.setDisable(currentEnrollee == null);
                }
        );

        colName.prefWidthProperty().bind(tblEnrollees.widthProperty().divide(3).subtract(3));
        colCourse.prefWidthProperty().bind(tblEnrollees.widthProperty().divide(3).subtract(3));
        colNotes.prefWidthProperty().bind(tblEnrollees.widthProperty().divide(3).subtract(3));
    }
}
