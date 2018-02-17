/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util.grade;

import java.util.ArrayList;

/**
 * @author OrthoCube
 */
public class SHSCriterion {

    private ArrayList<SHSTask> tasks;
    private double percent;

    public SHSCriterion(int p) {
        tasks = new ArrayList<>();
        percent = ((double) p) / 100.0;
    }

    public void addTask(int s, int t) {
        tasks.add(new SHSTask(s, t));
    }

    public double getGrade() {
        double totalTask = 0;
        double totalStudent = 0;
        for (SHSTask t : tasks) {
            totalTask += t.getTotal();
            totalStudent += t.getScore();
        }
        //JOptionPane.showMessageDialog(null, Double.toString(total*percent), "TEST", JOptionPane.WARNING_MESSAGE);
        return (totalStudent / totalTask) * percent * 100.0;
    }
}
