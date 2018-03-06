/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.classes;

import com.orthocube.classrecord.data.AttendanceDay;
import com.orthocube.classrecord.data.AttendanceList;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Enrollee;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendanceStatisticsController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(ClassesController.class.getName());
    // charts
    private final CategoryAxis xAxis1 = new CategoryAxis();
    private final NumberAxis yAxis1 = new NumberAxis();
    private final NumberAxis xAxis2 = new NumberAxis();
    private final CategoryAxis yAxis2 = new CategoryAxis();
    private ResourceBundle bundle;
    private StackedBarChart<String, Number> chtHistory;
    private StackedBarChart<Number, String> chtStudents;
    private ObservableList<AttendanceDay> attendanceDays;
    private Clazz currentClass;

    @FXML
    private ChoiceBox<String> cboCourse;
    @FXML
    private VBox pnlVbox;
    @FXML
    private VBox pnlVboxStudents;

    public void setClass(Clazz c) {
        try {
            currentClass = c;
            attendanceDays = DB.getAttendanceDays(c);

            Set<String> courses = new LinkedHashSet<>();
            courses.add("All");
            if (attendanceDays.size() > 0) {
                for (AttendanceList al : attendanceDays.get(0).getAttendanceList()) {
                    courses.add(al.getEnrollee().getCourse().trim().isEmpty() ? "Default" : al.getEnrollee().getCourse());
                }
            }
            cboCourse.setItems(FXCollections.observableArrayList(courses));
            cboCourse.getSelectionModel().select(0);
            cboCourse.valueProperty().addListener(e -> updateChart());
            updateChart();
        } catch (SQLException ex) {
            Dialogs.exception(ex);
        }
    }

    public String getTitle() {
        return "Attendance Statistics for " + currentClass.getName();
    }

    private boolean enrolleeMatchesCourse(Enrollee e, String c) {
        switch (c) {
            case "All":
                return true;
            case "Default":
                return e.getCourse().trim().isEmpty();
            default:
                return e.getCourse().equals(c);
        }
    }

    private void updateChart() {
        LOGGER.log(Level.INFO, "Updating chart...");
        final XYChart.Series<String, Number> presents = new XYChart.Series<>();
        final XYChart.Series<String, Number> others = new XYChart.Series<>();

        ObservableList<String> categories = FXCollections.observableArrayList();

        presents.setName("Present");
        others.setName("Others");
        int totalStudents = 0;
        for (AttendanceDay ad : attendanceDays) {
            int p = 0, a = 0;
            String cat = (new SimpleDateFormat("yyyy-MM-dd")).format(ad.getDate());
            categories.add(cat);
            for (AttendanceList al : ad.getAttendanceList()) {
                if (enrolleeMatchesCourse(al.getEnrollee(), cboCourse.getValue())) {
                    if ("LP".contains(al.getRemarks()))
                        p++;
                    else
                        a++;
                }
            }
            if (totalStudents < (a + p))
                totalStudents = a + p;
            presents.getData().add(new XYChart.Data<>(cat, p));
            others.getData().add(new XYChart.Data<>(cat, a));
        }

        chtHistory.setData(FXCollections.observableArrayList(presents, others));

        // -------------------------------------------------------------
        final XYChart.Series<Number, String> presents2 = new XYChart.Series<>();
        final XYChart.Series<Number, String> others2 = new XYChart.Series<>();

        HashMap<String, Integer> students = new HashMap<>();
        ObservableList<AttendanceList> last = attendanceDays.get(attendanceDays.size() - 1).getAttendanceList();
        for (AttendanceList al : last) {
            if (enrolleeMatchesCourse(al.getEnrollee(), cboCourse.getValue()))
                students.put(al.getEnrollee().getStudent().getLN() + ", " + al.getEnrollee().getStudent().getFN(), 0);
        }

        presents2.setName("Present");
        others2.setName("Others");
        for (AttendanceDay ad : attendanceDays) {
            int p = 0;
            for (AttendanceList al : ad.getAttendanceList()) {
                if (enrolleeMatchesCourse(al.getEnrollee(), cboCourse.getValue())) {
                    if ("LP".contains(al.getRemarks()))
                        students.put(al.getEnrollee().getStudent().getLN() + ", " + al.getEnrollee().getStudent().getFN(),
                                students.get(al.getEnrollee().getStudent().getLN() + ", " + al.getEnrollee().getStudent().getFN()) + 1);
                }
            }
        }

        for (String key : students.keySet()) {
            presents2.getData().add(new XYChart.Data<>(students.get(key), key));
            others2.getData().add(new XYChart.Data<>(attendanceDays.size() - students.get(key), key));
        }

        chtStudents.setData(FXCollections.observableArrayList(presents2, others2));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = resources;

        xAxis1.setLabel("Days");
        yAxis1.setLabel("Attendance");
        xAxis1.setAnimated(false);
        chtHistory = new StackedBarChart<>(xAxis1, yAxis1);
        chtHistory.setAnimated(false);
        pnlVbox.getChildren().add(chtHistory);

        xAxis2.setLabel("Attendance");
        yAxis2.setLabel("Students");
        xAxis2.setAnimated(false);
        chtStudents = new StackedBarChart<>(xAxis2, yAxis2);
        chtStudents.setAnimated(false);
        pnlVboxStudents.getChildren().add(chtStudents);
    }
}
