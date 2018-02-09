/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.classes;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.AttendanceDay;
import com.orthocube.classrecord.data.AttendanceList;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendanceController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(ClassesController.class.getName());
    // charts
    final CategoryAxis xAxis1 = new CategoryAxis();
    final NumberAxis yAxis1 = new NumberAxis();
    final NumberAxis xAxis2 = new NumberAxis();
    final CategoryAxis yAxis2 = new CategoryAxis();
    private ResourceBundle bundle;
    private MainApp mainApp;
    private Clazz currentClass;
    private AttendanceDay currentDay;
    private AttendanceList currentList;
    private ObservableList<AttendanceDay> attendanceDays;
    private ObservableList<AttendanceList> attendanceList;
    private StackedBarChart<String, Number> chtHistory;
    private StackedBarChart<Number, String> chtStudents;


    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private VBox pnlVbox;
    @FXML
    private VBox pnlVboxStudents;

    @FXML
    private TableView<AttendanceDay> tblAttendanceDays;

    @FXML
    private TableColumn<AttendanceDay, Date> colDate;

    @FXML
    private TableColumn<AttendanceDay, String> colDaysNotes;

    @FXML
    private DatePicker dpkDate;

    @FXML
    private TextField txtDaysNotes;

    @FXML
    private Button cmdSaveDay;

    @FXML
    private Button cmdAddDay;

    @FXML
    private Button cmdQuickAdd;

    @FXML
    private TableView<AttendanceList> tblAttendanceList;

    @FXML
    private TableColumn<AttendanceList, String> colName;

    @FXML
    private TableColumn<AttendanceList, String> colRemarks;

    @FXML
    private TableColumn<AttendanceList, String> colListNotes;

    @FXML
    private ChoiceBox<String> cboRemarks;

    @FXML
    private TextField txtListNotes;

    @FXML
    private Label lblLastMeeting;

    @FXML
    private Button cmdSaveList;
    // </editor-fold>

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public String getTitle() {
        return "Attendance for " + currentClass.getName();
    }

    public void setClass(Clazz c) {
        try {
            currentClass = c;
            attendanceDays = DB.getAttendanceDays(c);
            attendanceDays.addListener((ListChangeListener<AttendanceDay>) change -> {
                updateChart();
            });
            for (AttendanceDay ad : attendanceDays) {
                ad.getAttendanceList().addListener((ListChangeListener<AttendanceList>) change -> {
                    updateChart();
                });
            }
            updateChart();

            tblAttendanceDays.setItems(attendanceDays);
        } catch (SQLException ex) {
            Dialogs.exception(ex);
        }
    }

    @FXML
    void cmdAbsentAction(ActionEvent event) {
        if (!cmdSaveList.isDisable()) {
            if (Dialogs.confirm("Set Absent", "You have unsaved changes. Discard changes?", "Setting this student as absent will discard\nyour current unsaved changes.") == ButtonType.CANCEL) {
                return;
            }
        }
        try {
            currentList.setRemarks("A");
            DB.save(currentList);
            tblAttendanceList.getSelectionModel().selectBelowCell();
            tblAttendanceList.scrollTo(tblAttendanceList.getSelectionModel().getSelectedIndex());
            showListInfo();
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdAddDayAction(ActionEvent event) {
        if (!cmdSaveDay.isDisable()) {
            if (Dialogs.confirm("New Attendance Day", "You have unsaved changes. Discard changes?", "Creating another attendance day will discard\nyour current unsaved changes.") == ButtonType.CANCEL) {
                return;
            }
        }

        currentDay = new AttendanceDay();
        currentDay.setClass(currentClass);
        showDayInfo();
        cmdSaveDay.setDisable(false);
        cmdSaveDay.setText("Save as new");
        cmdAddDay.setDisable(true);
    }

    private void showDayInfo() {
        dpkDate.setValue(currentDay.getDate().toLocalDate());
        txtDaysNotes.setText(currentDay.getNotes());

        tblAttendanceList.setItems(currentDay.getAttendanceList());

        dpkDate.setDisable(false);
        txtDaysNotes.setDisable(false);

        cmdSaveDay.setDisable(true);
        cmdAddDay.setDisable(false);
        cmdSaveDay.setText("Save");
    }

    private void showListInfo() {
        if (currentList != null) {
            String remarks = currentList.getRemarks();
            if (remarks.equals("P"))
                cboRemarks.getSelectionModel().select(0);
            else if (remarks.equals("A"))
                cboRemarks.getSelectionModel().select(1);
            else if (remarks.equals("L"))
                cboRemarks.getSelectionModel().select(2);
            else if (remarks.equals("E"))
                cboRemarks.getSelectionModel().select(3);
            else if (remarks.equals("O"))
                cboRemarks.getSelectionModel().select(4);
            else
                cboRemarks.getSelectionModel().select(-1);
            txtListNotes.setText(currentList.getNotes());

            cboRemarks.setDisable(false);
            txtListNotes.setDisable(false);
        } else {
            cboRemarks.getSelectionModel().select(-1);
            txtListNotes.setText("");

            cboRemarks.setDisable(true);
            txtListNotes.setDisable(true);

        }

        cmdSaveList.setDisable(true);
    }

    @FXML
    void cmdLateAction(ActionEvent event) {
        if (!cmdSaveList.isDisable()) {
            if (Dialogs.confirm("Set Late", "You have unsaved changes. Discard changes?", "Setting this student as late will discard\nyour current unsaved changes.") == ButtonType.CANCEL) {
                return;
            }
        }
        try {
            currentList.setRemarks("L");
            DB.save(currentList);
            tblAttendanceList.getSelectionModel().selectBelowCell();
            tblAttendanceList.scrollTo(tblAttendanceList.getSelectionModel().getSelectedIndex());
            showListInfo();
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdOthersAction(ActionEvent event) {
        if (!cmdSaveList.isDisable()) {
            if (Dialogs.confirm("Set Others", "You have unsaved changes. Discard changes?", "Setting this student as others will discard\nyour current unsaved changes.") == ButtonType.CANCEL) {
                return;
            }
        }
        try {
            currentList.setRemarks("O");
            DB.save(currentList);
            tblAttendanceList.getSelectionModel().selectBelowCell();
            tblAttendanceList.scrollTo(tblAttendanceList.getSelectionModel().getSelectedIndex());
            showListInfo();
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdPresentAction(ActionEvent event) {
        if (!cmdSaveList.isDisable()) {
            if (Dialogs.confirm("Set Present", "You have unsaved changes. Discard changes?", "Setting this student as present will discard\nyour current unsaved changes.") == ButtonType.CANCEL) {
                return;
            }
        }
        try {
            currentList.setRemarks("P");
            DB.save(currentList);
            tblAttendanceList.getSelectionModel().selectBelowCell();
            tblAttendanceList.scrollTo(tblAttendanceList.getSelectionModel().getSelectedIndex());
            showListInfo();
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdQuickAddAction(ActionEvent event) {
        if (!cmdSaveDay.isDisable()) {
            if (Dialogs.confirm("New Attendance Day", "You have unsaved changes. Discard changes?", "Creating another attendance day will discard\nyour current unsaved changes.") == ButtonType.CANCEL) {
                return;
            }
        }

        currentDay = new AttendanceDay();
        currentDay.setClass(currentClass);
        try {
            boolean newEntry = DB.save(currentDay);

            cmdSaveDay.setDisable(true);
            if (newEntry) {
                attendanceDays.add(currentDay);
                tblAttendanceDays.getSelectionModel().select(currentDay);
                tblAttendanceDays.scrollTo(currentDay);

                cmdSaveDay.setText("Save");
                cmdAddDay.setDisable(false);
            }

            mainApp.getRootNotification().show("Attendance Day saved.");
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    private void updateChart() {
        LOGGER.log(Level.INFO, "Updating chart...");
        final XYChart.Series<String, Number> presents = new XYChart.Series<String, Number>();
        final XYChart.Series<String, Number> others = new XYChart.Series<String, Number>();

        ObservableList categories = FXCollections.observableArrayList();

        presents.setName("Present");
        others.setName("Others");
        int totalStudents = 0;
        for (AttendanceDay ad : attendanceDays) {
            int p = 0, a = 0;
            String cat = (new SimpleDateFormat("yyyy-MM-dd")).format(ad.getDate());
            categories.add(cat);
            for (AttendanceList al : ad.getAttendanceList()) {
                if ("LP".contains(al.getRemarks()))
                    p++;
                else
                    a++;
            }
            if (totalStudents < (a + p))
                totalStudents = a + p;
            presents.getData().add(new XYChart.Data<String, Number>(cat, p));
            others.getData().add(new XYChart.Data<String, Number>(cat, a));
        }

        chtHistory.setData(FXCollections.observableArrayList(presents, others));

        // -------------------------------------------------------------
        final XYChart.Series<Number, String> presents2 = new XYChart.Series<Number, String>();
        final XYChart.Series<Number, String> others2 = new XYChart.Series<Number, String>();

        HashMap<String, Integer> students = new HashMap<>();
        ObservableList<AttendanceList> last = attendanceDays.get(attendanceDays.size() - 1).getAttendanceList();
        for (AttendanceList al : last) {
            students.put(al.getEnrollee().getStudent().getLN() + ", " + al.getEnrollee().getStudent().getFN(), 0);
        }

        presents2.setName("Present");
        others2.setName("Others");
        for (AttendanceDay ad : attendanceDays) {
            int p = 0;
            for (AttendanceList al : ad.getAttendanceList()) {
                if ("LP".contains(al.getRemarks()))
                    students.put(al.getEnrollee().getStudent().getLN() + ", " + al.getEnrollee().getStudent().getFN(),
                            students.get(al.getEnrollee().getStudent().getLN() + ", " + al.getEnrollee().getStudent().getFN()) + 1);
            }
        }

        for (String key : students.keySet()) {
            presents2.getData().add(new XYChart.Data<Number, String>(students.get(key), key));
            others2.getData().add(new XYChart.Data<Number, String>(attendanceDays.size() - students.get(key), key));
        }

        chtStudents.setData(FXCollections.observableArrayList(presents2, others2));

    }

    @FXML
    void mnuRemoveAction(ActionEvent event) {
        if (Dialogs.confirm("Delete Attendance day", "Are you sure you want to delete this Attendance day?", (new SimpleDateFormat("yyyy-MM-dd")).format(currentDay.getDate())) == ButtonType.OK)
            try {
                DB.delete(currentDay);
                attendanceDays.remove(currentDay);
                mainApp.getRootNotification().show("Attendance day deleted.");
            } catch (SQLException e) {
                Dialogs.exception(e);
            }
    }

    @FXML
    void cmdSaveDayAction(ActionEvent event) {
        try {
            currentDay.setDate(dpkDate.getValue());
            currentDay.setNotes(txtDaysNotes.getText());

            boolean newEntry = DB.save(currentDay);
            currentDay.getAttendanceList().addListener((ListChangeListener<AttendanceList>) change -> {
                updateChart();
            });


            cmdSaveDay.setDisable(true);
            if (newEntry) {
                attendanceDays.add(currentDay);
                tblAttendanceDays.getSelectionModel().select(currentDay);
                tblAttendanceDays.scrollTo(currentDay);

                cmdSaveDay.setText("Save");
                cmdAddDay.setDisable(false);
            }

            mainApp.getRootNotification().show("Attendance Day saved.");
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdSaveListAction(ActionEvent event) {
        try {
            String remarks = cboRemarks.getValue();
            if (remarks.equals("Present"))
                currentList.setRemarks("P");
            else if (remarks.equals("Absent"))
                currentList.setRemarks("A");
            else if (remarks.equals("Late"))
                currentList.setRemarks("L");
            else if (remarks.equals("Excused"))
                currentList.setRemarks("E");
            else if (remarks.equals("Others"))
                currentList.setRemarks("O");

            currentList.setNotes(txtListNotes.getText());

            DB.save(currentList);

            cmdSaveList.setDisable(true);

            mainApp.getRootNotification().show("Attendance saved.");
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = resources;

        colDate.setCellValueFactory(cellValue -> cellValue.getValue().dateProperty());
        colDaysNotes.setCellValueFactory(cellValue -> cellValue.getValue().notesProperty());

        tblAttendanceDays.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if (cmdSaveDay.isDisable()) {
                currentDay = newv;
                showDayInfo();
            } else {
                if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another attendance day will discard your current unsaved changes.") == ButtonType.OK) {
                    currentDay = newv;
                    showDayInfo();
                    cmdAddDay.setDisable(false);
                    cmdSaveDay.setDisable(true);
                    cmdSaveDay.setText("Save");
                }
            }
        });

        colName.setCellValueFactory(cv -> Bindings.concat(cv.getValue().getEnrollee().getStudent().lnProperty(), ", ", cv.getValue().getEnrollee().getStudent().fnProperty()));
        colRemarks.setCellValueFactory(cv -> cv.getValue().remarksProperty());
        colListNotes.setCellValueFactory(cv -> cv.getValue().notesProperty());

        tblAttendanceList.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if (cmdSaveList.isDisable()) {
                currentList = newv;
                showListInfo();
            } else {
                if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another student will discard your current unsaved changes.") == ButtonType.OK) {
                    currentList = newv;
                    showListInfo();
                    cmdSaveList.setDisable(true);
                }
            }
        });


        cboRemarks.setItems(FXCollections.observableArrayList("Present", "Absent", "Late", "Excused", "Others"));

        xAxis1.setLabel("Days");
        yAxis1.setLabel("Attendance");
        xAxis1.setAnimated(false);
        chtHistory = new StackedBarChart<String, Number>(xAxis1, yAxis1);
        chtHistory.setAnimated(false);
        pnlVbox.getChildren().add(chtHistory);

        xAxis2.setLabel("Attendance");
        yAxis2.setLabel("Students");
        xAxis2.setAnimated(false);
        chtStudents = new StackedBarChart<Number, String>(xAxis2, yAxis2);
        chtStudents.setAnimated(false);
        pnlVboxStudents.getChildren().add(chtStudents);

        // <editor-fold defaultstate="collapsed" desc="Listeners">
        dpkDate.valueProperty().addListener((obs, ov, nv) -> {
            if (currentDay != null) cmdSaveDay.setDisable(false);
        });
        txtDaysNotes.textProperty().addListener((obs, ov, nv) -> {
            if (currentDay != null) cmdSaveDay.setDisable(false);
        });
        cboRemarks.valueProperty().addListener((obs, ov, nv) -> {
            if (currentDay != null) cmdSaveList.setDisable(false);
        });
        txtListNotes.textProperty().addListener((obs, ov, nv) -> {
            if (currentDay != null) cmdSaveList.setDisable(false);
        });
        // </editor-fold>
    }
}
