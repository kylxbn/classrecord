/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.classes;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.AttendanceDay;
import com.orthocube.classrecord.data.AttendanceList;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.User;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import com.orthocube.classrecord.util.Settings;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendanceController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(ClassesController.class.getName());

    private MainApp mainApp;
    private Clazz currentClass;
    private AttendanceDay currentDay;
    private AttendanceList currentList;
    private ObservableList<AttendanceDay> attendanceDays;

    private User currentUser = new User();


    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private Label lblTotalDays;
    @FXML
    private Button cmdDaysCancel;
    @FXML
    private Button cmdListCancel;

    @FXML
    private VBox vbxDays;

    @FXML
    private TitledPane pnlDays;

    @FXML
    private TableView<AttendanceDay> tblAttendanceDays;

    @FXML
    private TableColumn<AttendanceDay, Date> colDate;

    @FXML
    private TableColumn<AttendanceDay, String> colDaysNotes;

    @FXML
    private MenuItem mnuRemove;

    @FXML
    private TitledPane pnlDaysInfo;

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
    private VBox vbxList;

    @FXML
    private TitledPane pnlList;

    @FXML
    private TableView<AttendanceList> tblAttendanceList;

    @FXML
    private TableColumn<AttendanceList, String> colName;

    @FXML
    private TableColumn<AttendanceList, String> colRemarks;

    @FXML
    private TableColumn<AttendanceList, String> colListNotes;

    @FXML
    private TitledPane pnlListInfo;

    @FXML
    private ChoiceBox<String> cboRemarks;

    @FXML
    private TextField txtListNotes;

    @FXML
    private Label lblLastMeeting;

    @FXML
    private Button cmdSaveList;

    @FXML
    private TitledPane pnlQuickSet;

    @FXML
    private Button cmdOthers;

    @FXML
    private Button cmdLate;

    @FXML
    private Button cmdAbsent;

    @FXML
    private Button cmdPresent;
    // </editor-fold>

    public void setUser(User u) {
        currentUser = u;
        if (currentUser.getAccessLevel() < 2) {
            cmdAddDay.setDisable(true);
            cmdQuickAdd.setDisable(true);
            cmdAbsent.setDisable(true);
            cmdPresent.setDisable(true);
            cmdLate.setDisable(true);
            cmdOthers.setDisable(true);
            mnuRemove.setDisable(true);

            txtDaysNotes.setEditable(false);
            txtListNotes.setEditable(false);
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public String getTitle() {
        return String.format(Settings.bundle.getString("attendance.attendance"), currentClass.getName());
    }

    public void setClass(Clazz c) {
        try {
            currentClass = c;
            attendanceDays = DB.getAttendanceDays(c);

            tblAttendanceDays.setItems(attendanceDays);
            lblTotalDays.textProperty().bind(Bindings.concat(Bindings.size(attendanceDays), Bindings.when(Bindings.size(attendanceDays).greaterThan(1)).then(" days").otherwise(" day")));

        } catch (SQLException ex) {
            Dialogs.exception(ex);
        }
    }

    @FXML
    void cmdAbsentAction(ActionEvent event) {
        try {
            currentList.setRemarks("A");
            DB.save(currentList);
            tblAttendanceList.getSelectionModel().selectBelowCell();
            tblAttendanceList.scrollTo(tblAttendanceList.getSelectionModel().getSelectedIndex());
            showListInfo();
            listEditMode(false);
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdAddDayAction(ActionEvent event) {
        currentDay = new AttendanceDay();
        currentDay.setClass(currentClass);
        showDayInfo();
        cmdSaveDay.setText(Settings.bundle.getString("attendance.saveasnew"));
        dayEditMode(true);
    }

    private void showDayInfo() {
        if (currentDay != null) {
            dpkDate.setValue(currentDay.getDate().toLocalDate());
            txtDaysNotes.setText(currentDay.getNotes());

            tblAttendanceList.setItems(currentDay.getAttendanceList());

            pnlDaysInfo.setDisable(false);
        } else {
            dpkDate.setValue(null);
            txtDaysNotes.setText("");

            tblAttendanceList.setItems(null);

            pnlDaysInfo.setDisable(true);
        }
    }

    private void showListInfo() {
        if (currentList != null) {
            String remarks = currentList.getRemarks();
            switch (remarks) {
                case "P":
                    cboRemarks.getSelectionModel().select(0);
                    break;
                case "A":
                    cboRemarks.getSelectionModel().select(1);
                    break;
                case "L":
                    cboRemarks.getSelectionModel().select(2);
                    break;
                case "E":
                    cboRemarks.getSelectionModel().select(3);
                    break;
                case "O":
                    cboRemarks.getSelectionModel().select(4);
                    break;
                default:
                    cboRemarks.getSelectionModel().select(-1);
                    break;
            }
            txtListNotes.setText(currentList.getNotes());

            pnlListInfo.setDisable(false);
            pnlQuickSet.setDisable(false);
        } else {
            cboRemarks.getSelectionModel().select(-1);
            txtListNotes.setText("");

            pnlListInfo.setDisable(true);
            pnlQuickSet.setDisable(true);
        }
    }

    @FXML
    void cmdLateAction(ActionEvent event) {
        try {
            currentList.setRemarks("L");
            DB.save(currentList);
            tblAttendanceList.getSelectionModel().selectBelowCell();
            tblAttendanceList.scrollTo(tblAttendanceList.getSelectionModel().getSelectedIndex());
            showListInfo();
            listEditMode(false);
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdOthersAction(ActionEvent event) {
        try {
            currentList.setRemarks("O");
            DB.save(currentList);
            tblAttendanceList.getSelectionModel().selectBelowCell();
            tblAttendanceList.scrollTo(tblAttendanceList.getSelectionModel().getSelectedIndex());
            showListInfo();
            listEditMode(false);
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdPresentAction(ActionEvent event) {
        try {
            currentList.setRemarks("P");
            DB.save(currentList);
            tblAttendanceList.getSelectionModel().selectBelowCell();
            tblAttendanceList.scrollTo(tblAttendanceList.getSelectionModel().getSelectedIndex());
            showListInfo();
            listEditMode(false);
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdQuickAddAction(ActionEvent event) {
        currentDay = new AttendanceDay();
        currentDay.setClass(currentClass);

        try {
            boolean newEntry = false;

            if (currentDay.getID() == -1) {
                long existingDay;
                if ((existingDay = DB.dayAlreadyExists(currentDay)) > 0) {
                    Dialogs.error(Settings.bundle.getString("attendance.dayalreadyexists_title"), Settings.bundle.getString("attendance.dayalreadyexists_header"), Settings.bundle.getString("attendance.dayalreadyexists_content"));
                    for (AttendanceDay ad : attendanceDays) {
                        if (ad.getID() == existingDay) {
                            cmdSaveDay.setDisable(true);
                            cmdSaveDay.setText(Settings.bundle.getString("attendance.save"));
                            cmdAddDay.setDisable(false);
                            tblAttendanceDays.getSelectionModel().select(ad);
                            tblAttendanceDays.scrollTo(ad);
                            return;
                        }
                    }
                } else {
                    newEntry = DB.save(currentDay);
                }
            }


            if (newEntry) {
                attendanceDays.add(currentDay);
                tblAttendanceDays.getSelectionModel().select(currentDay);
                tblAttendanceDays.scrollTo(currentDay);
            }

            mainApp.getRootNotification().show("Attendance Day saved.");
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void mnuRemoveAction(ActionEvent event) {
        if (currentDay == null) return;
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

            boolean newEntry = false;

            if (currentDay.getID() == -1) {
                long existingDay;
                if ((existingDay = DB.dayAlreadyExists(currentDay)) > 0) {
                    Dialogs.error("Day already exists", "The attendance day you are\ntrying to add already exists.", "It will now be automatically selected.");
                    for (AttendanceDay ad : attendanceDays) {
                        if (ad.getID() == existingDay) {
                            cmdSaveDay.setDisable(true);
                            cmdSaveDay.setText("Save");
                            cmdAddDay.setDisable(false);
                            tblAttendanceDays.getSelectionModel().select(ad);
                            tblAttendanceDays.scrollTo(ad);
                            return;
                        }
                    }
                } else {
                    newEntry = DB.save(currentDay);
                }
            }

            cmdSaveDay.setDisable(true);
            if (newEntry) {
                attendanceDays.add(currentDay);
                tblAttendanceDays.getSelectionModel().select(currentDay);
                tblAttendanceDays.scrollTo(currentDay);

                cmdSaveDay.setText("Save");
            }

            dayEditMode(false);
            mainApp.getRootNotification().show("Attendance Day saved.");
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    private void cmdDaysCancelAction(ActionEvent event) {
        currentDay = tblAttendanceDays.getSelectionModel().getSelectedItem();
        cmdSaveDay.setText("Save");
        showDayInfo();
        dayEditMode(false);
    }

    @FXML
    private void cmdListCancelAction(ActionEvent event) {
        currentList = tblAttendanceList.getSelectionModel().getSelectedItem();
        cmdSaveList.setText("Save");
        showListInfo();
        listEditMode(false);
    }

    @FXML
    void cmdSaveListAction(ActionEvent event) {
        try {
            String remarks = cboRemarks.getValue();
            switch (remarks) {
                case "Present":
                    currentList.setRemarks("P");
                    break;
                case "Absent":
                    currentList.setRemarks("A");
                    break;
                case "Late":
                    currentList.setRemarks("L");
                    break;
                case "Excused":
                    currentList.setRemarks("E");
                    break;
                case "Others":
                    currentList.setRemarks("O");
                    break;
            }

            currentList.setNotes(txtListNotes.getText());

            DB.save(currentList);

            listEditMode(false);

            mainApp.getRootNotification().show("Attendance saved.");
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    private void dayEditMode(boolean t) {
        if (t && (currentUser.getAccessLevel() > 1)) {
            pnlDays.setDisable(true);
            cmdSaveDay.setDisable(false);
            cmdDaysCancel.setVisible(true);
            cmdAddDay.setDisable(true);
            cmdQuickAdd.setDisable(true);
            vbxList.setDisable(true);
        } else {
            pnlDays.setDisable(false);
            cmdSaveDay.setDisable(true);
            cmdDaysCancel.setVisible(false);
            vbxList.setDisable(false);
            if (currentUser.getAccessLevel() > 1) {
                cmdAddDay.setDisable(false);
                cmdQuickAdd.setDisable(false);
            }
        }
    }

    private void listEditMode(boolean t) {
        if (t && (currentUser.getAccessLevel() > 1)) {
            vbxDays.setDisable(true);
            pnlList.setDisable(true);
            cmdSaveList.setDisable(false);
            cmdListCancel.setVisible(true);
            pnlQuickSet.setDisable(true);
        } else {
            vbxDays.setDisable(false);
            pnlList.setDisable(false);
            cmdSaveList.setDisable(true);
            cmdListCancel.setVisible(false);
            pnlQuickSet.setDisable(currentList == null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.log(Level.INFO, "Initializing...");

        colDate.setCellValueFactory(cellValue -> cellValue.getValue().dateProperty());
        colDaysNotes.setCellValueFactory(cellValue -> cellValue.getValue().notesProperty());

        tblAttendanceDays.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            currentDay = newv;
            showDayInfo();
            dayEditMode(false);
        });

        colName.setCellValueFactory(cv -> Bindings.concat(cv.getValue().getEnrollee().getStudent().lnProperty(), ", ", cv.getValue().getEnrollee().getStudent().fnProperty()));
        colRemarks.setCellValueFactory(cv -> cv.getValue().remarksProperty());
        colListNotes.setCellValueFactory(cv -> cv.getValue().notesProperty());

        tblAttendanceList.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            currentList = newv;
            showListInfo();
            listEditMode(false);
        });

        colDate.setPrefWidth(100);
        colDaysNotes.prefWidthProperty().bind(tblAttendanceDays.widthProperty().subtract(110));

        colName.prefWidthProperty().bind(tblAttendanceList.widthProperty().divide(2.0).subtract(35));
        colRemarks.setPrefWidth(60);
        colListNotes.prefWidthProperty().bind(tblAttendanceList.widthProperty().divide(2.0).subtract(35));


        cboRemarks.setItems(FXCollections.observableArrayList("Present", "Absent", "Late", "Excused", "Others"));

        // <editor-fold defaultstate="collapsed" desc="Listeners">
        dpkDate.valueProperty().addListener((obs, ov, nv) -> {
            if (currentDay != null) dayEditMode(true);
        });
        txtDaysNotes.textProperty().addListener((obs, ov, nv) -> {
            if (currentDay != null) dayEditMode(true);
        });
        cboRemarks.valueProperty().addListener((obs, ov, nv) -> {
            if (currentDay != null) listEditMode(true);
        });
        txtListNotes.textProperty().addListener((obs, ov, nv) -> {
            if (currentDay != null) listEditMode(true);
        });
        // </editor-fold>

        listEditMode(false);
        dayEditMode(false);
    }

    public void quickAdd() {
        cmdQuickAddAction(null);
    }
}
