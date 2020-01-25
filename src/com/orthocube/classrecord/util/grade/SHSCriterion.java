/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util.grade;

import org.apache.commons.math3.fraction.BigFraction;

import java.util.ArrayList;

/**
 * @author OrthoCube
 */
public class SHSCriterion {

    private final ArrayList<SHSTask> tasks;
    private final BigFraction percent;

    public SHSCriterion(int p) {
        tasks = new ArrayList<>();
        percent = new BigFraction(p).divide(100);
    }

    public void addTask(int s, int t) {
        tasks.add(new SHSTask(s, t));
    }

    public BigFraction getGrade() {
        BigFraction totalTask = BigFraction.ZERO;
        BigFraction totalStudent = BigFraction.ZERO;
        for (SHSTask t : tasks) {
            totalTask = totalTask.add(t.getTotal());
            totalStudent = totalStudent.add(t.getScore());
        }
        //JOptionPane.showMessageDialog(null, Double.toString(total*percent), "TEST", JOptionPane.WARNING_MESSAGE);
        return totalStudent.divide(totalTask).multiply(percent).multiply(100);
    }
}
