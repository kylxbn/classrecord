/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.dashboard.provider;

import com.orthocube.classrecord.data.AttendanceDay;
import com.orthocube.classrecord.data.AttendanceList;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Enrollee;
import com.orthocube.classrecord.util.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConsecutiveAbsences implements DashboardProvider {


    @Override
    public String getTitle() {
        return "There seem to be students who have consecutive absences.";
    }

    @Override
    public Node getContent() {
        try {
            ObservableList<Clazz> allClasses = DB.getClasses();
            ObservableList<String> students = FXCollections.observableArrayList();
            for (Clazz c : allClasses) {
                ObservableList<Enrollee> allEnrollees = DB.getEnrollees(c);
                Map<Long, Integer> enrollees = new HashMap<>();
                for (Enrollee e : allEnrollees) {
                    enrollees.put(e.getID(), 0);
                }
                ObservableList<AttendanceDay> days = DB.getAttendanceDays(c);
                for (AttendanceDay d : days) {
                    for (AttendanceList list : d.getAttendanceList()) {
                        Long enrollee = list.getEnrollee().getID();
                        if (list.getRemarks().equals("A"))
                            enrollees.replace(enrollee, enrollees.get(enrollee) + 1);
                        else
                            enrollees.replace(enrollee, 0);
                    }
                }
                for (Long eID : enrollees.keySet()) {
                    if (enrollees.get(eID) > 1) {
                        Enrollee e = c.isSHS() ? DB.getSHSEnrollee(eID) : DB.getEnrollee(eID);
                        students.add(e.getStudent().getFN() + " " + e.getStudent().getLN() + " on " + c.getName() + " (absent for " + enrollees.get(e.getID()) + " consecutive meetings)");
                    }
                }
            }

            return (students.size() == 0) ? null : new ListView<>(students);
        } catch (SQLException e) {
            return new Label(e.getLocalizedMessage());
        }
    }

    @Override
    public int getAlertLevel() {
        return 0;
    }
}
