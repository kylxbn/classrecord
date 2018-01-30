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
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
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
            tblEnrollees.setItems(DB.getEnrollees(currentClass));
        } catch (SQLException | IOException e) {
            Dialogs.exception(e);
        }
    }

    private void showEnrolleeInfo() {
        txtClassCard.setText(Integer.toString(currentEnrollee.getClasscard()));
        txtNotes.setText(currentEnrollee.getNotes());
        txtCourse.setText(currentEnrollee.getCourse());
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
                    currentEnrollee = newV;
                    showEnrolleeInfo();

                }
        );

        colName.prefWidthProperty().bind(tblEnrollees.widthProperty().subtract(19).divide(3.33333333));
        colCourse.prefWidthProperty().bind(tblEnrollees.widthProperty().subtract(19).divide(3.33333333));
        colNotes.prefWidthProperty().bind(tblEnrollees.widthProperty().subtract(19).divide(3.333333333));

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
    }

    @FXML
    private void cmdAddAction(ActionEvent event) {
    }

    @FXML
    private void cmdSaveAction(ActionEvent event) {
    }

}
