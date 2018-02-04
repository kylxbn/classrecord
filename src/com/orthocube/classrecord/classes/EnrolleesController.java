/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.classes;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Enrollee;
import com.orthocube.classrecord.data.Student;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
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
    private ResourceBundle bundle;

    private ObservableList<Enrollee> enrollees;

    // <editor-fold defaultstate="collapsed" desc="Controls">
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

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void showClass(Clazz c) {
        currentClass = c;
        try {
            if (currentClass.isSHS()) {
                lblClassCard.setDisable(true);
                txtClassCard.setDisable(true);
            }
            enrollees = DB.getEnrollees(currentClass);
            tblEnrollees.setItems(enrollees);
        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }

    private void showEnrolleeInfo() {
        txtClassCard.setText(Integer.toString(currentEnrollee.getClasscard()));
        txtNotes.setText(currentEnrollee.getNotes());
        txtCourse.setText(currentEnrollee.getCourse());
        cmdSave.setDisable(true);
        if (currentClass.isSHS()) {
            txtClassCard.setPromptText("Not available");
        }
    }

    public String getTitle() {
        return "Enrollees for " + currentClass.getName();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = rb;

        colName.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getStudent().lnProperty(), ", ", cellData.getValue().getStudent().fnProperty()));
        colCourse.setCellValueFactory(cellData -> cellData.getValue().courseProperty());
        colNotes.setCellValueFactory(cellData -> cellData.getValue().notesProperty());

        tblEnrollees.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> {
                    if (cmdSave.isDisable()) {
                        currentEnrollee = newV;
                        showEnrolleeInfo();
                    } else {
                        if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another enrollee will discard your current unsaved changes.") == ButtonType.OK) {
                            currentEnrollee = newV;
                            showEnrolleeInfo();
                            cmdAdd.setDisable(false);
                            cmdSave.setDisable(true);
                            cmdSave.setText("Save");
                        }
                    }
                }

        );

        colName.prefWidthProperty().bind(tblEnrollees.widthProperty().subtract(19).divide(3.33333333));
        colCourse.prefWidthProperty().bind(tblEnrollees.widthProperty().subtract(19).divide(3.33333333));
        colNotes.prefWidthProperty().bind(tblEnrollees.widthProperty().subtract(19).divide(3.333333333));

        // <editor-fold defaultstate="collapsed" desc="Change listeners">
        txtClassCard.textProperty().addListener(
                (obs, ov, nv) -> cmdSave.setDisable(false)
        );
        txtCourse.textProperty().addListener(
                (obs, ov, nv) -> cmdSave.setDisable(false)
        );
        txtNotes.textProperty().addListener(
                (obs, ov, nv) -> cmdSave.setDisable(false)
        );
        // </editor-fold>

    }

    @FXML
    private void mnuSearchOnFacebookAction(ActionEvent event) {
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
        mainApp.showStudent(currentEnrollee.getStudent());
    }

    @FXML
    private void mnuRemoveAction(ActionEvent event) {
        if (Dialogs.confirm("Remove Enrollee", "Are you sure you want to delete this this enrollee?", currentEnrollee.getStudent().getFN() + " " + currentEnrollee.getStudent().getLN()) == ButtonType.OK)
            try {
                DB.delete(currentEnrollee);
                enrollees.remove(currentEnrollee);
            } catch (SQLException e) {
                Dialogs.exception(e);
            }
    }

    @FXML
    private void cmdAddAction(ActionEvent event) {
        if (!cmdSave.isDisable()) {
            if (Dialogs.confirm("New enrollee", "You have unsaved changes. Discard changes?", "Creating another enrollee will discard\nyour current unsaved changes.") == ButtonType.CANCEL) {
                return;
            }
        }

        Student chosenStudent = mainApp.showStudentChooserDialog(currentClass);
        if (chosenStudent != null) {
            currentEnrollee = new Enrollee(-1, currentClass, chosenStudent, 0, "", "");
            showEnrolleeInfo();
            cmdSave.setDisable(false);
            cmdSave.setText("Save as new");
            cmdAdd.setDisable(true);
        }
    }

    @FXML
    private void cmdSaveAction(ActionEvent event) {
        try {
            currentEnrollee.setClasscard(Integer.parseInt(txtClassCard.getText()));
            currentEnrollee.setCourse(txtCourse.getText());
            currentEnrollee.setNotes(txtNotes.getText());

            boolean newEntry = DB.save(currentEnrollee);

            cmdSave.setDisable(true);
            if (newEntry) {
                enrollees.add(currentEnrollee);
                tblEnrollees.getSelectionModel().select(currentEnrollee);
                tblEnrollees.scrollTo(currentEnrollee);
                cmdAdd.setDisable(false);

                cmdSave.setText("Save");
                cmdAdd.setDisable(false);
            }
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

}
