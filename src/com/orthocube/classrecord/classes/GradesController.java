/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.classes;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Grade;
import com.orthocube.classrecord.students.StudentEnrolledInController;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GradesController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(StudentEnrolledInController.class.getName());
    private MainApp mainApp;
    private Clazz currentClass;
    private ObservableList<Grade> grades;
    private ResourceBundle bundle;
    @FXML
    private TableView<Grade> tblGrades;

    public void setClass(Clazz c) {
        currentClass = c;
        try {
            if (currentClass.isSHS()) {
                grades = DB.getSHSGrades(c);
                TableColumn<Grade, String> colStudent = new TableColumn<>("Student");
                TableColumn<Grade, String> colMidterms = new TableColumn<>("Midterms");
                TableColumn<Grade, String> colFinals = new TableColumn<>("Finals");
                TableColumn<Grade, String> colFinal = new TableColumn<>("Final Grade");
                colStudent.setCellValueFactory(cellD -> Bindings.concat(cellD.getValue().lnameProperty(), ", ", cellD.getValue().fnameProperty()));
                colMidterms.setCellValueFactory(cellD -> cellD.getValue().midtermsProperty());
                colFinals.setCellValueFactory(cellD -> cellD.getValue().finalsProperty());
                colFinal.setCellValueFactory(cellD -> cellD.getValue().finalProperty());
                tblGrades.getColumns().addAll(colStudent, colMidterms, colFinals, colFinal);
            } else {
                grades = DB.getCollegeGrades(c);
                TableColumn<Grade, String> colStudent = new TableColumn<>("Student");
                TableColumn<Grade, String> colPrelims = new TableColumn<>("Prelims");
                TableColumn<Grade, String> colMidterms = new TableColumn<>("Midterms");
                TableColumn<Grade, String> colSemis = new TableColumn<>("Semis");
                TableColumn<Grade, String> colFinals = new TableColumn<>("Finals");
                TableColumn<Grade, String> colFinal = new TableColumn<>("Final Grade");
                TableColumn<Grade, Number> colClassCard = new TableColumn<>("Class Card");
                colStudent.setCellValueFactory(cellD -> Bindings.concat(cellD.getValue().lnameProperty(), ", ", cellD.getValue().fnameProperty()));
                colPrelims.setCellValueFactory(cellD -> cellD.getValue().prelimProperty());
                colMidterms.setCellValueFactory(cellD -> cellD.getValue().midtermsProperty());
                colSemis.setCellValueFactory(cellD -> cellD.getValue().semisProperty());
                colFinals.setCellValueFactory(cellD -> cellD.getValue().finalsProperty());
                colFinal.setCellValueFactory(cellD -> cellD.getValue().finalProperty());
                colClassCard.setCellValueFactory(cellD -> cellD.getValue().classCardProperty());
                tblGrades.getColumns().addAll(colStudent, colPrelims, colMidterms, colSemis, colFinals, colFinal, colClassCard);
            }

            tblGrades.setItems(grades);
        } catch (SQLException | IOException e) {
            Dialogs.exception(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = rb;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public String getTitle() {
        return currentClass.getName() + " Grades";
    }
}
