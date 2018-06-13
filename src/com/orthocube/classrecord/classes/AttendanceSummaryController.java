/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.classes;

import com.orthocube.classrecord.data.AttendanceDay;
import com.orthocube.classrecord.data.AttendanceList;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Enrollee;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Month;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendanceSummaryController implements Initializable {
    private ResourceBundle bundle;
    private final static Logger LOGGER = Logger.getLogger(ClassesController.class.getName());
    private Clazz currentClass;
    private ObservableList<AttendanceDay> attendanceDays;

    // <editor-fold defaultstate="collapsed" desc="controls">
    @FXML
    private ChoiceBox<String> cboYear;

    @FXML
    private ChoiceBox<String> cboMonth;

    @FXML
    private TableView<List<String>> tblAttendance;
    // </editor-fold>

    public String getTitle() {
        return "Attendance Summary for " + currentClass.getName();
    }

    public void setClass(Clazz c) {
        currentClass = c;

        LinkedHashSet<String> years = new LinkedHashSet<>();
        LinkedHashSet<String> months = new LinkedHashSet<>();
        try {
            attendanceDays = DB.getAttendanceDays(currentClass);
            for (AttendanceDay ad : attendanceDays) {
                years.add(String.format("%04d", ad.getDate().toLocalDate().getYear()));
                months.add(monthToWord(ad.getDate().toLocalDate().getMonth()));
            }
            cboMonth.setItems(FXCollections.observableArrayList(months));
            cboYear.setItems(FXCollections.observableArrayList(years));

            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            
            if (month == Calendar.JANUARY) {
                year--;
                month = Calendar.DECEMBER;
            } else {
                month -= 1;
            }
            
            for (int i = 0; i < cboYear.getItems().size(); i++) {
                if (cboYear.getItems().get(i).equals(Integer.toString(year)))
                    cboYear.getSelectionModel().select(i);
            }
            for (int i = 0; i < cboMonth.getItems().size(); i++) {
                if (cboMonth.getItems().get(i).equals(monthToWord(month)))
                    cboMonth.getSelectionModel().select(i);
            }
        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }

    private String monthToWord(Month m) {
        if (m == Month.JANUARY) return "January";
        else if (m == Month.FEBRUARY) return "February";
        else if (m == Month.MARCH) return "March";
        else if (m == Month.APRIL) return "April";
        else if (m == Month.MAY) return "May";
        else if (m == Month.JUNE) return "June";
        else if (m == Month.JULY) return "July";
        else if (m == Month.AUGUST) return "August";
        else if (m == Month.SEPTEMBER) return "September";
        else if (m == Month.OCTOBER) return "October";
        else if (m == Month.NOVEMBER) return "November";
        else if (m == Month.DECEMBER) return "December";
        else return "INVALID MONTH";
    }

    private String monthToWord(int m) {
        switch (m) {
            case (Calendar.JANUARY):
                return "January";
            case (Calendar.FEBRUARY):
                return "February";
            case (Calendar.MARCH):
                return "March";
            case (Calendar.APRIL):
                return "April";
            case (Calendar.MAY):
                return "May";
            case (Calendar.JUNE):
                return "June";
            case (Calendar.JULY):
                return "July";
            case (Calendar.AUGUST):
                return "August";
            case (Calendar.SEPTEMBER):
                return "September";
            case (Calendar.OCTOBER):
                return "October";
            case (Calendar.NOVEMBER):
                return "November";
            case (Calendar.DECEMBER):
                return "December";
            default:
                return "INVALID MONTH";
        }
    }

    private void showSummary(String year, String month) {
        if ((year == null) || (month == null)) return;

        // we get the IDs of all enrollees
        LinkedHashSet<Long> enrollees = new LinkedHashSet<>();
        for (AttendanceList e : attendanceDays.get(0).getAttendanceList()) {
            enrollees.add(e.getEnrollee().getID());
        }

        // and construct the table based on this ID
        ObservableList<AttendanceMatrix> matrix = FXCollections.observableArrayList();
        for (long i : enrollees) matrix.add(new AttendanceMatrix(i));

        // then we fill in the names of the enrollees
        for (AttendanceList e : attendanceDays.get(0).getAttendanceList()) {
            Enrollee currentEnrollee = e.getEnrollee();
            for (AttendanceMatrix m : matrix) {
                if (m.enrolleeID == currentEnrollee.getID()) {
                    m.enrolleeName = currentEnrollee.getStudent().getLN() + ", " + currentEnrollee.getStudent().getFN();
                }
            }
        }

        // we put the desired days into filteredDays
        List<AttendanceDay> filteredDays = new ArrayList<>();
        for (AttendanceDay ad : attendanceDays) {
            String thisMonth = monthToWord(ad.getDate().toLocalDate().getMonth());
            String thisYear = Integer.toString(ad.getDate().toLocalDate().getYear());
            if (thisMonth.equals(month) && thisYear.equals(year)) {
                filteredDays.add(ad);
            }
        }

        List<String> days = new ArrayList<>();
        days.add("Enrollee");
        ObservableList<TableColumn<AttendanceMatrix, String>> tableColumns = FXCollections.observableArrayList();
        // so now we fill in the attendance of each enrollee
        for (AttendanceDay ad : filteredDays) {
            days.add(Integer.toString(ad.getDate().toLocalDate().getDayOfMonth()));
            for (AttendanceList al : ad.getAttendanceList()) {
                // we search for the matching student
                for (AttendanceMatrix m : matrix) {
                    if (m.enrolleeID == al.getEnrollee().getID())
                        m.row.add(al.getRemarks());
                }
            }
            // now we check if a student has been left out of the attendance count
            boolean repeat;
            int dayscount = 0;
            for (AttendanceMatrix m : matrix)
                if (m.row.size() > dayscount) dayscount = m.row.size();
            do {
                repeat = false;
                int culprit = -1;
                for (int i = 1; i < matrix.size(); i++) {
                    if (matrix.get(i).row.size() < dayscount) {
                        repeat = true;
                        culprit = i;
                    }
                }
                if (culprit >= 0) {
                    matrix.get(culprit).row.add(" ");
                }
            } while (repeat);
        }

        // now we build the final table data that will be displayed
        ObservableList<List<String>> data = FXCollections.observableArrayList();
        for (AttendanceMatrix m : matrix) {
            List<String> row = new ArrayList<>();
            row.add(m.enrolleeName);
            row.addAll(m.row);
            data.add(row);
        }

        // now we generate the table columns
        tblAttendance.getColumns().clear();
        for (int i = 0; i < data.get(0).size(); i++) {
            TableColumn tc = new TableColumn(days.get(i));
            final int colNo = i;
            tc.setCellValueFactory((Callback<TableColumn.CellDataFeatures<List<String>, String>, ObservableValue<String>>) p -> new SimpleStringProperty((p.getValue().get(colNo))));
            tc.setPrefWidth(i == 0 ? 250 : 25);
            tc.setMinWidth(25);
            tblAttendance.getColumns().add(tc);
        }

        // now we pray to Quetzalcoatl
        tblAttendance.setItems(data);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.log(Level.INFO, "Initializing");
        bundle = resources;

        cboMonth.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            showSummary(cboYear.getSelectionModel().getSelectedItem(), newv);
        });

        cboYear.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            showSummary(newv, cboMonth.getSelectionModel().getSelectedItem());
        });
    }

    private class AttendanceMatrix {
        long enrolleeID = -1;
        String enrolleeName = "";
        List<String> row = new ArrayList<>();

        public AttendanceMatrix(long id) {
            enrolleeID = id;
        }
    }
}