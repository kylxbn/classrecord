/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.classes;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Enrollee;
import com.orthocube.classrecord.data.Student;
import com.orthocube.classrecord.data.User;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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

/**
 * FXML Controller class
 *
 * @author OrthoCube
 */
public class EnrolleesController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(EnrolleesController.class.getName());
    private MainApp mainApp;
    private Clazz currentClass;
    private Enrollee currentEnrollee;
    private ObservableList<String> courses;

    private boolean saveInProgress = false;

    private ObservableList<Enrollee> enrollees;

    private User currentUser = new User();

    // <editor-fold defaultstate="collapsed" desc="Controls">

    @FXML
    private Button cmdCancel;

    @FXML
    private VBox vbxEdit;
    @FXML
    private ChoiceBox<String> cboCourse;
    @FXML
    private TableView<Enrollee> tblEnrollees;
    @FXML
    private MenuItem mnuSearchOnFacebook;
    @FXML
    private MenuItem mnuRemove;
    @FXML
    private TableColumn<Enrollee, String> colName;
    @FXML
    private TableColumn<Enrollee, String> colCourse;
    @FXML
    private TableColumn<Enrollee, String> colNotes;
    @FXML
    private Label lblNum;
    @FXML
    private Button cmdAdd;
    @FXML
    private Label lblClassCard;
    @FXML
    private Label lblCourse;
    @FXML
    private TextField txtCourse;
    @FXML
    private Label lblNotes;
    @FXML
    private TextArea txtNotes;
    @FXML
    private TextField txtClassCard;
    @FXML
    private Button cmdSave;
    // </editor-fold>

    public void setUser(User u) {
        currentUser = u;
        if (currentUser.getAccessLevel() < 2) {
            mnuRemove.setDisable(true);
            cmdAdd.setDisable(true);

            txtClassCard.setEditable(false);
            txtCourse.setEditable(false);
            txtNotes.setEditable(false);
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    private void updateCourses() {
        Set<String> courses = new LinkedHashSet<>();
        courses.add("All");
        for (Enrollee e : enrollees) {
            courses.add(e.getCourse().trim().isEmpty() ? "Default" : e.getCourse());
        }
        int oldi = cboCourse.getSelectionModel().getSelectedIndex();
        String old = cboCourse.getSelectionModel().getSelectedItem();
        cboCourse.setItems(FXCollections.observableArrayList(courses));
        if (oldi < 0)
            cboCourse.getSelectionModel().select(0);
        else {
            for (String i : cboCourse.getItems()) {
                if (i.equals(old))
                    cboCourse.getSelectionModel().select(i);
            }
        }
    }

    public void showClass(Clazz c) {
        currentClass = c;
        try {
            if (currentClass.isSHS()) {
                lblClassCard.setDisable(true);
                txtClassCard.setDisable(true);
                txtClassCard.setPromptText("Not available.");
            }
            enrollees = DB.getEnrollees(currentClass);
            updateCourses();

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

            tblEnrollees.setItems(sortedEnrollees);
            lblNum.textProperty().bind(Bindings.concat(Bindings.size(enrollees), Bindings.when(Bindings.size(enrollees).greaterThan(1)).then(" enrollees").otherwise(" enrollee")));

        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }

    private void showEnrolleeInfo() {
        if (currentEnrollee != null) {
            txtClassCard.setText(Integer.toString(currentEnrollee.getClasscard()));
            txtNotes.setText(currentEnrollee.getNotes());
            txtCourse.setText(currentEnrollee.getCourse());

            vbxEdit.setDisable(false);
        } else {
            txtClassCard.setText("");
            txtNotes.setText("");
            txtCourse.setText("");

            vbxEdit.setDisable(true);
        }
    }

    public String getTitle() {
        return "Enrollees for " + currentClass.getName();
    }

    private void editMode(boolean t) {
        if (t && (currentUser.getAccessLevel() > 1)) {
            cboCourse.setDisable(true);
            tblEnrollees.setDisable(true);
            cmdAdd.setDisable(true);
            cmdCancel.setVisible(true);
            cmdSave.setDisable(false);
        } else {
            cboCourse.setDisable(false);
            tblEnrollees.setDisable(false);
            if (currentUser.getAccessLevel() > 1)
                cmdAdd.setDisable(false);
            cmdCancel.setVisible(false);
            cmdSave.setDisable(true);
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");

        colName.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getStudent().lnProperty(), ", ", cellData.getValue().getStudent().fnProperty()));
        colCourse.setCellValueFactory(cellData -> cellData.getValue().courseProperty());
        colNotes.setCellValueFactory(cellData -> cellData.getValue().notesProperty());

        tblEnrollees.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> {
                    if (!saveInProgress) {
                        currentEnrollee = newV;
                        showEnrolleeInfo();
                        editMode(false);
                    }
                }
        );

        colName.prefWidthProperty().bind(tblEnrollees.widthProperty().subtract(19).divide(3.33333333));
        colCourse.prefWidthProperty().bind(tblEnrollees.widthProperty().subtract(19).divide(3.33333333));
        colNotes.prefWidthProperty().bind(tblEnrollees.widthProperty().subtract(19).divide(3.333333333));

        // <editor-fold defaultstate="collapsed" desc="Change listeners">
        txtClassCard.textProperty().addListener(
                (obs, ov, nv) -> {
                    if (currentEnrollee != null) editMode(true);
                }
        );
        txtCourse.textProperty().addListener(
                (obs, ov, nv) -> {
                    if (currentEnrollee != null) editMode(true);
                }
        );
        txtNotes.textProperty().addListener(
                (obs, ov, nv) -> {
                    if (currentEnrollee != null) editMode(true);
                }
        );
        // </editor-fold>

        editMode(false);
    }

    @FXML
    private void mnuSearchOnFacebookAction(ActionEvent event) {
        if (currentEnrollee == null) return;
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI("https://www.facebook.com/search/top/?q=" + (currentEnrollee.getStudent().getFN() + " " + currentEnrollee.getStudent().getLN()).replace(" ", "%20")));
            } else {
                System.out.println("Desktop not supported.");
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mnuViewStudentAction(ActionEvent event) {
        if (currentEnrollee == null) return;
        mainApp.showStudent(currentEnrollee.getStudent());
    }

    @FXML
    private void mnuRemoveAction(ActionEvent event) {
        if (currentEnrollee == null) return;
        if (Dialogs.confirm("Remove Enrollee", "Are you sure you want to delete this enrollee?", currentEnrollee.getStudent().getFN() + " " + currentEnrollee.getStudent().getLN()) == ButtonType.OK)
            try {
                DB.delete(currentEnrollee);
                enrollees.remove(currentEnrollee);
                updateCourses();
                mainApp.getRootNotification().show("Enrollee removed from class.");
            } catch (SQLException e) {
                Dialogs.exception(e);
            }
    }

    @FXML
    private void cmdCancelAction(ActionEvent event) {
        currentEnrollee = tblEnrollees.getSelectionModel().getSelectedItem();
        cmdSave.setText("Save");
        showEnrolleeInfo();
        editMode(false);
    }

    @FXML
    private void cmdAddAction(ActionEvent event) {
        Student chosenStudent = mainApp.showStudentChooserDialog(currentClass);
        if (chosenStudent != null) {
            currentEnrollee = new Enrollee(-1, currentClass, chosenStudent, 0, "", "");
            showEnrolleeInfo();
            cmdSave.setText("Save as new");
            editMode(true);
            txtClassCard.requestFocus();
        }
    }

    @FXML
    private void cmdSaveAction(ActionEvent event) {
        try {
            try {
                Integer.parseInt(txtClassCard.getText());
            } catch (Exception e) {
                txtClassCard.setText("0");
            }
            currentEnrollee.setClasscard(Integer.parseInt(txtClassCard.getText()));
            currentEnrollee.setCourse(txtCourse.getText());
            currentEnrollee.setNotes(txtNotes.getText());

            boolean newEntry = DB.save(currentEnrollee);

            if (newEntry) {
                enrollees.add(currentEnrollee);
                cmdSave.setText("Save");
            }

            saveInProgress = true;
            updateCourses();
            saveInProgress = false;
            tblEnrollees.getSelectionModel().select(currentEnrollee);
            tblEnrollees.scrollTo(currentEnrollee);

            mainApp.getRootNotification().show("Enrollee saved.");
            editMode(false);
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

}
