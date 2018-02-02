/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.classes;

/**
 * Controller for the Classes display.
 *
 * @author OrthoCube
 */

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassesController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(ClassesController.class.getName());
    private ResourceBundle bundle;
    private MainApp mainApp;

    private ObservableList<Clazz> classes;
    private Clazz currentClass;

    private ObservableList<String> collegeYears = FXCollections.observableArrayList("1st", "2nd", "3rd", "4th", "5th");
    private ObservableList<String> shsYears = FXCollections.observableArrayList("11", "12");


    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private TextField txtClassSearch;

    @FXML
    private TextField txtSYSearch;

    @FXML
    private TextField txtSemSearch;

    @FXML
    private TextField txtCourseSearch;

    @FXML
    private Button cmdSearch;

    @FXML
    private TitledPane ttlView;

    @FXML
    private Label lblTimeView;

    @FXML
    private Label lblLevelView;

    @FXML
    private Label lblSortingView;

    @FXML
    private RadioButton rdoTimeToday;

    @FXML
    private RadioButton rdoTimeThisSemester;

    @FXML
    private RadioButton rdoTimeAll;

    @FXML
    private RadioButton rdoLevelCollege;

    @FXML
    private RadioButton rdoLevelSHS;

    @FXML
    private RadioButton rdoLevelAll;

    @FXML
    private RadioButton rdoSortingChronological;

    @FXML
    private RadioButton rdoSortingAlphabetical;

    @FXML
    private TableView<Clazz> tblClasses;

    @FXML
    private TableColumn<Clazz, String> colName;

    @FXML
    private TableColumn<Clazz, String> colSY;

    @FXML
    private TableColumn<Clazz, String> colSem;

    @FXML
    private TableColumn<Clazz, String> colRoom;

    @FXML
    private TableColumn<Clazz, String> colCourse;

    @FXML
    private TitledPane ttlInfo;

    @FXML
    private Label lblName;

    @FXML
    private Label lblSY;

    @FXML
    private Label lblSemester;

    @FXML
    private Label lblYear;

    @FXML
    private Label lblCourse;

    @FXML
    private Label lblRoom;

    @FXML
    private Label lblLevel;

    @FXML
    private Label lblNotes;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtSY;

    @FXML
    private TextField txtCourse;

    @FXML
    private TextField txtRoom;

    @FXML
    private TextField txtNotes;

    @FXML
    private ChoiceBox<String> cboLevel;

    @FXML
    private Label lblSY2;

    @FXML
    private ChoiceBox<String> cboSemester;

    @FXML
    private ChoiceBox<String> cboYear;

    @FXML
    private TitledPane ttlSchedule;

    @FXML
    private CheckBox chkSunday;

    @FXML
    private CheckBox chkFriday;

    @FXML
    private CheckBox chkSaturday;

    @FXML
    private CheckBox chkThursday;

    @FXML
    private CheckBox chkWednesday;

    @FXML
    private CheckBox chkTuesday;

    @FXML
    private CheckBox chkMonday;

    @FXML
    private TextField txtSunday;

    @FXML
    private TextField txtMonday;

    @FXML
    private TextField txtTuesday;

    @FXML
    private TextField txtWednesday;

    @FXML
    private TextField txtThursday;

    @FXML
    private TextField txtFriday;

    @FXML
    private TextField txtSaturday;

    @FXML
    private TextField txtSunday2;

    @FXML
    private TextField txtMonday2;

    @FXML
    private TextField txtTuesday2;

    @FXML
    private TextField txtWednesday2;

    @FXML
    private TextField txtThursday2;

    @FXML
    private TextField txtFriday2;

    @FXML
    private TextField txtSaturday2;

    @FXML
    private Button cmdSave;
    // </editor-fold>

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    void cmdSaveAction(ActionEvent event) {
        try {
            currentClass.setName(txtName.getText());
            currentClass.setSY(Integer.parseInt(txtSY.getText()));
            currentClass.setSem(cboSemester.getSelectionModel().getSelectedIndex() + 1);
            if (currentClass.isSHS())
                currentClass.setYear(cboYear.getSelectionModel().getSelectedIndex() + 11);
            else
                currentClass.setYear(cboYear.getSelectionModel().getSelectedIndex() + 1);
            currentClass.setCourse(txtCourse.getText());
            currentClass.setRoom(txtRoom.getText());

            int days = 0;
            days |= (chkSunday.isSelected() ? 1 : 0);
            days |= (chkMonday.isSelected() ? 1 : 0) << 1;
            days |= (chkTuesday.isSelected() ? 1 : 0) << 2;
            days |= (chkWednesday.isSelected() ? 1 : 0) << 3;
            days |= (chkThursday.isSelected() ? 1 : 0) << 4;
            days |= (chkFriday.isSelected() ? 1 : 0) << 5;
            days |= (chkSaturday.isSelected() ? 1 : 0) << 6;

            currentClass.setDays(days);
            currentClass.getTimes().set(0, txtSunday.getText());
            currentClass.getTimes().set(1, txtSunday2.getText());
            currentClass.getTimes().set(2, txtMonday.getText());
            currentClass.getTimes().set(3, txtMonday2.getText());
            currentClass.getTimes().set(4, txtTuesday.getText());
            currentClass.getTimes().set(5, txtTuesday2.getText());
            currentClass.getTimes().set(6, txtWednesday.getText());
            currentClass.getTimes().set(7, txtWednesday2.getText());
            currentClass.getTimes().set(8, txtThursday.getText());
            currentClass.getTimes().set(9, txtThursday2.getText());
            currentClass.getTimes().set(10, txtFriday.getText());
            currentClass.getTimes().set(11, txtFriday2.getText());
            currentClass.getTimes().set(12, txtSaturday.getText());
            currentClass.getTimes().set(13, txtSaturday2.getText());

            currentClass.setSHS(cboLevel.getSelectionModel().getSelectedIndex() > 0);
            currentClass.setNotes(txtNotes.getText());
            //currentClass.setPicture(ImageIO.read(r.getBinaryStream(10)));
            boolean newentry = DB.save(currentClass);
            if (newentry) {
                classes.add(currentClass);
                tblClasses.getSelectionModel().select(currentClass);
                tblClasses.scrollTo(currentClass);
            }
        } catch (Exception e) {
            Dialogs.exception(e);
        }

        cmdSave.setDisable(true);
        cmdSave.setText("Save");
    }

    @FXML
    void cmdSearchAction(ActionEvent event) {
        // TODO: Implement searching
    }

    @FXML
    void cmdAddAction(ActionEvent event) {
        currentClass = new Clazz();
        showClassInfo();
        cmdSave.setText("Save as new");
    }

    @FXML
    void mnuEnrolleesAction(ActionEvent event) {
        try {
            mainApp.showEnrollees(currentClass);
        } catch (Exception e) {
            Dialogs.exception(e);
        }

    }

    @FXML
    void mnuDeleteAction(ActionEvent event) {
        if (Dialogs.confirm("Delete Class", "Are you sure you want to delete this class?", currentClass.getName()) == ButtonType.OK)
            try {
                DB.delete(currentClass);
                classes.remove(currentClass);
            } catch (SQLException e) {
                Dialogs.exception(e);
            }
    }

    public void showClass(Clazz c) {
        for (Clazz test : classes) {
            if (test.getID() == c.getID()) {
                tblClasses.getSelectionModel().select(test);
                tblClasses.scrollTo(test);
            }
        }
        tblClasses.requestFocus();
    }

    private void showClassInfo() {
        if (currentClass == null) {
            txtName.setText("");
            txtSY.setText("");
            cboSemester.getSelectionModel().selectFirst();
            cboYear.getSelectionModel().selectFirst();
            txtCourse.setText("");
            txtRoom.setText("");
            cboLevel.getSelectionModel().selectFirst();
            txtNotes.setText("");

            chkSunday.setSelected(false);
            chkMonday.setSelected(false);
            chkTuesday.setSelected(false);
            chkWednesday.setSelected(false);
            chkThursday.setSelected(false);
            chkFriday.setSelected(false);
            chkSaturday.setSelected(false);

            txtSunday.setText("");
            txtMonday.setText("");
            txtTuesday.setText("");
            txtWednesday.setText("");
            txtThursday.setText("");
            txtFriday.setText("");
            txtSaturday.setText("");
        } else {
            txtName.setText(currentClass.getName());
            txtSY.setText(Integer.toString(currentClass.getSY()));

            if (currentClass.isSHS()) {
                cboLevel.getSelectionModel().select(1);
                cboYear.getSelectionModel().select(currentClass.getYear() - 11);
            } else {
                cboLevel.getSelectionModel().select(0);
                cboYear.getSelectionModel().select(currentClass.getYear() - 1);
            }
            cboSemester.getSelectionModel().select(currentClass.getSem() - 1);
            txtCourse.setText(currentClass.getCourse());
            txtRoom.setText(currentClass.getRoom());

            txtNotes.setText(currentClass.getNotes());

            int days = currentClass.getDays();
            chkSunday.setSelected((days & 1) > 0);
            chkMonday.setSelected((days & 2) > 0);
            chkTuesday.setSelected((days & 4) > 0);
            chkWednesday.setSelected((days & 8) > 0);
            chkThursday.setSelected((days & 16) > 0);
            chkFriday.setSelected((days & 32) > 0);
            chkSaturday.setSelected((days & 64) > 0);

            ArrayList<String> times = currentClass.getTimes();
            txtSunday.setText(times.get(0));
            txtSunday2.setText(times.get(1));
            txtMonday.setText(times.get(2));
            txtMonday2.setText(times.get(3));
            txtTuesday.setText(times.get(4));
            txtTuesday2.setText(times.get(5));
            txtWednesday.setText(times.get(6));
            txtWednesday2.setText(times.get(7));
            txtThursday.setText(times.get(8));
            txtThursday2.setText(times.get(9));
            txtFriday.setText(times.get(10));
            txtFriday2.setText(times.get(11));
            txtSaturday.setText(times.get(12));
            txtSaturday2.setText(times.get(13));
        }

        cmdSave.setDisable(true);
        cmdSave.setText("Save");
    }

    public String getTitle() {
        return "Classes"; //bundle.getString("classes");
    }

    public void setModel(ObservableList<Clazz> model) {
        classes = model;
        tblClasses.setItems(classes);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = rb;

        colName.setCellValueFactory(
                cellData -> cellData.getValue().nameProperty()
        );
        colSY.setCellValueFactory(
                cellData -> cellData.getValue().syProperty().asString()
        );
        colSem.setCellValueFactory(
                cellData -> cellData.getValue().semProperty().asString()
        );
        colRoom.setCellValueFactory(
                cellData -> cellData.getValue().roomProperty()
        );
        colCourse.setCellValueFactory(
                cellData -> cellData.getValue().courseProperty()
        );

        tblClasses.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> {
                    if (cmdSave.isDisable()) {
                        currentClass = newV;
                        showClassInfo();
                    } else {
                        if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another class will discard your current unsaved changes.") == ButtonType.OK) {
                            currentClass = newV;
                            showClassInfo();
                        }
                    }
                }
        );

        cboLevel.setItems(FXCollections.observableArrayList("College", "SHS"));
        cboSemester.setItems(FXCollections.observableArrayList("1st", "2nd", "Summer Class"));

        cboLevel.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldV, newV) -> {
                    if (newV.equals(0)) {
                        cboYear.setItems(collegeYears);
                        lblYear.setText("Year:");
                    } else {
                        cboYear.setItems(shsYears);
                        lblYear.setText("Grade:");
                    }
                }
        );

        txtSY.textProperty().addListener(
                (obs, oldV, newV) -> {
                    try {
                        lblSY2.setText(" - " + (Integer.parseInt(newV) + 1));
                    } catch (Exception e) {
                        lblSY2.setText(" - ????");
                    }
                }
        );

        final ToggleGroup timeGroup = new ToggleGroup();
        final ToggleGroup levelGroup = new ToggleGroup();
        final ToggleGroup sortingGroup = new ToggleGroup();

        rdoTimeToday.setToggleGroup(timeGroup);
        rdoTimeThisSemester.setToggleGroup(timeGroup);
        rdoTimeAll.setToggleGroup(timeGroup);

        rdoLevelCollege.setToggleGroup(levelGroup);
        rdoLevelSHS.setToggleGroup(levelGroup);
        rdoLevelAll.setToggleGroup(levelGroup);

        rdoSortingChronological.setToggleGroup(sortingGroup);
        rdoSortingAlphabetical.setToggleGroup(sortingGroup);

        colSY.setPrefWidth(41);
        colSem.setPrefWidth(36);
        colName.prefWidthProperty().bind(tblClasses.widthProperty().subtract(126).divide(2.0));
        colRoom.prefWidthProperty().bind(tblClasses.widthProperty().subtract(126).divide(5.0));
        colCourse.prefWidthProperty().bind(tblClasses.widthProperty().subtract(126).divide(3.0));

        // <editor-fold defaultstate="collapsed" desc="Change listeners">
        txtName.textProperty().addListener(
                (obs, ov, nv) -> {

                    cmdSave.setDisable(false);
                }
        );

        txtSY.textProperty().addListener(
                (obs, ov, nv) -> {

                    cmdSave.setDisable(false);
                }
        );

        cboSemester.valueProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        cboYear.valueProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtCourse.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtRoom.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        cboLevel.valueProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtNotes.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        chkSunday.selectedProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        chkMonday.selectedProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        chkTuesday.selectedProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        chkWednesday.selectedProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        chkThursday.selectedProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        chkFriday.selectedProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        chkSaturday.selectedProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtSunday.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtMonday.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtTuesday.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtWednesday.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtThursday.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtFriday.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtSaturday.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtSunday2.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtMonday2.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtTuesday2.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtWednesday2.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtThursday2.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtFriday2.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        txtSaturday2.textProperty().addListener(
                (obs, ov, nv) -> {
                    cmdSave.setDisable(false);
                }
        );

        // </editor-fold>
    }
}
