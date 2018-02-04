/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.students;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Student;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentChooserController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(StudentsController.class.getName());
    private Student currentStudent = null;
    private ObservableList<Student> students;
    private ResourceBundle bundle;
    private Clazz currentClass;
    private Student result = null;

    private MainApp mainApp;
    private Stage dialogStage;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Student> tblStudents;

    @FXML
    private TableColumn<Student, String> colID;

    @FXML
    private TableColumn<Student, String> colName;

    @FXML
    private Label lblTotalStudents;

    @FXML
    private Button cmdChoose;


    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public Student getResult() {
        return result;
    }

    @FXML
    void cmdChooseAction(ActionEvent event) {
        try {
            if (!DB.studentEnrolledIn(currentStudent, currentClass)) {
                result = currentStudent;
                dialogStage.close();
            } else {
                Dialogs.error("Already enrolled", "Student is already enrolled to this class.", "Please choose another student to enroll.");
            }
        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }


    @FXML
    void mnuSearchOnFacebookAction(ActionEvent event) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI("https://www.facebook.com/search/top/?q=" + (currentStudent.getFN() + " " + currentStudent.getLN()).replace(" ", "%20")));
            } else {
                Dialogs.error("Error", "Desktop Not Supported", "This computer is not able to open a web browser.");
            }
        } catch (IOException | URISyntaxException e) {
            Dialogs.exception(e);
        }
    }

    public void setClass(Clazz c) {
        currentClass = c;
    }

    public void setModel(ObservableList<Student> model) {

        students = model;

        FilteredList<Student> filteredStudents = new FilteredList<>(model, p -> true);
        txtSearch.textProperty().addListener((obs, oldv, newv) -> filteredStudents.setPredicate(student -> {
            if (newv == null || newv.isEmpty())
                return true;
            String lowercasefilter = newv.toLowerCase();
            return student.getFN().toLowerCase().contains(lowercasefilter) || student.getLN().toLowerCase().contains(lowercasefilter);
        }));

        SortedList<Student> sortedStudents = new SortedList<>(filteredStudents);
        sortedStudents.comparatorProperty().bind(tblStudents.comparatorProperty());
        tblStudents.setItems(sortedStudents);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = rb;

        colID.setCellValueFactory(
                cellData -> cellData.getValue().sidProperty()
        );
        colName.setCellValueFactory(
                cellData -> Bindings.concat(cellData.getValue().lnProperty(), ", "/*bundle.getString("nameseparator")*/, cellData.getValue().fnProperty())
        );

        tblStudents.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> {
                    currentStudent = newV;
                    cmdChoose.setDisable(currentStudent == null);
                }
        );


        colID.setPrefWidth(135);
        colName.prefWidthProperty().bind(tblStudents.widthProperty().subtract(154));
    }
}
