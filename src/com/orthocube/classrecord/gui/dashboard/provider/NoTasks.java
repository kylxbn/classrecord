/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.dashboard.provider;

import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Enrollee;
import com.orthocube.classrecord.data.Score;
import com.orthocube.classrecord.data.Task;
import com.orthocube.classrecord.util.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.SQLException;

public class NoTasks implements DashboardProvider {


    @Override
    public String getTitle() {
        return "There seem to be students with missing scores.";
    }

    private String termToWord(int term) {
        switch (term) {
            case 1:
                return "Prelims";
            case 2:
                return "Midterms";
            case 4:
                return "Semis";
            case 8:
                return "Finals";
            default:
                return "UNKNOWN";
        }
    }

    @Override
    public Node getContent() {
        try {
            ObservableList<Clazz> allClasses = DB.getClasses();
            ObservableList<String> students = FXCollections.observableArrayList();
            for (Clazz c : allClasses) {
                ObservableList<Enrollee> allEnrollees = DB.getEnrollees(c);
                for (Enrollee e : allEnrollees) {
                    ObservableList<Task> allTasks = DB.getTasks(c);
                    for (Task t : allTasks) {
                        boolean found = false;
                        for (Score s : t.getScores()) {
                            if (e.getID() == s.getEnrollee().getID()) {
                                found = true;
                            }
                        }
                        if (!found) {
                            students.add(e.getStudent().getFN() + " " + e.getStudent().getLN() + " on " + c.getName() + " / " + termToWord(t.getTerm()) + " / " + t.getName());
                        }
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
