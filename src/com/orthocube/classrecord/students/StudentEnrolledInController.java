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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author OrthoCube
 */
public class StudentEnrolledInController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(StudentEnrolledInController.class.getName());
    private MainApp mainApp;
    private ObservableList<Clazz> college;
    private ObservableList<Clazz> shs;
    private ResourceBundle bundle;
    private Student currentStudent;

    @FXML
    private TitledPane ttlCollege;

    @FXML
    private ListView<Clazz> lstCollege;

    @FXML
    private TitledPane ttlSHS;

    @FXML
    private ListView<Clazz> lstSHS;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    void viewClassAction(ActionEvent event) {
        mainApp.showClass(lstCollege.getSelectionModel().getSelectedItem());
    }

    @FXML
    void viewSHSClassAction(ActionEvent event) {
        mainApp.showClass(lstSHS.getSelectionModel().getSelectedItem());
    }


    public void showEnrolled(Student s) {
        currentStudent = s;
        try {
            college = DB.getEnrolledIn(currentStudent);
            shs = DB.getSHSEnrolledIn(currentStudent);

            lstCollege.setItems(college);
            lstSHS.setItems(shs);


        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }

    public String getTitle() {
        return "Classes Enrolled by " + currentStudent.getFN(); //String.format(bundle.getString("classesenrolledby"), currentStudent.getFN());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = rb;


        lstCollege.setCellFactory(new Callback<ListView<Clazz>, ListCell<Clazz>>() {
            @Override
            public ListCell<Clazz> call(ListView<Clazz> p) {

                return new ListCell<Clazz>() {
                    @Override
                    protected void updateItem(Clazz t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getName());
                        }
                    }
                };
            }
        });

        lstSHS.setCellFactory(new Callback<ListView<Clazz>, ListCell<Clazz>>() {
            @Override
            public ListCell<Clazz> call(ListView<Clazz> p) {

                return new ListCell<Clazz>() {
                    @Override
                    protected void updateItem(Clazz t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getName());
                        }
                    }
                };
            }
        });
    }
}
